package se.sundsvall.rtjmanagement.statistics.service;

import jakarta.persistence.criteria.Predicate;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.statistics.api.model.HandlaggareCount;
import se.sundsvall.rtjmanagement.statistics.api.model.StatisticsResponse;
import se.sundsvall.rtjmanagement.statistics.api.model.StatusCount;
import se.sundsvall.rtjmanagement.statushistory.integration.db.StatusHistoryRepository;
import se.sundsvall.rtjmanagement.statushistory.integration.db.model.StatusHistoryEntity;

import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.springframework.util.StringUtils.hasText;

/**
 * Computes ärendestatistik on read. Loads the errands for the namespace (optionally scoped by
 * typeSlug and a {@code created} date range) and aggregates them in memory — counts per status and
 * per handläggare plus an active handläggningstid.
 *
 * <p>
 * The handläggningstid is derived from the {@code statushistory} timeline: walking each terminal
 * errand's transitions from {@code created} to the terminal change, summing the time spent in active
 * statuses. Any {@code AWAITING_*} status is treated as paused (the ärende is waiting on the sökande
 * or an external party, not on active handläggning) and excluded — this is R5's "paus-funktion"
 * realised from the existing transition log, so no separate time registration is needed.
 * </p>
 */
@Service
@Transactional(readOnly = true)
public class StatisticsService {

	private static final String STATUS_REGISTERED = "REGISTERED";
	private static final String PAUSED_STATUS_PREFIX = "AWAITING_";
	private static final String STATUS_DECIDED = "DECIDED";
	private static final Set<String> TERMINAL_STATUSES = Set.of("DECIDED", "REJECTED", "REVOKED");
	private static final String UNKNOWN_STATUS = "UNKNOWN";

	private final ErrandRepository errandRepository;
	private final StatusHistoryRepository statusHistoryRepository;

	StatisticsService(final ErrandRepository errandRepository, final StatusHistoryRepository statusHistoryRepository) {
		this.errandRepository = errandRepository;
		this.statusHistoryRepository = statusHistoryRepository;
	}

	public StatisticsResponse compute(final String municipalityId, final String namespace, final String typeSlug,
		final OffsetDateTime from, final OffsetDateTime to) {

		final var errands = errandRepository.findAll(selection(municipalityId, namespace, typeSlug, from, to));

		final var byStatus = errands.stream()
			.collect(groupingBy(errand -> ofNullable(errand.getStatus()).orElse(UNKNOWN_STATUS), counting()))
			.entrySet().stream()
			.map(entry -> new StatusCount(entry.getKey(), entry.getValue()))
			.sorted(comparing(StatusCount::status))
			.toList();

		final var byHandlaggare = errands.stream()
			.filter(errand -> hasText(errand.getAssignedUserId()))
			.collect(groupingBy(ErrandEntity::getAssignedUserId, counting()))
			.entrySet().stream()
			.map(entry -> new HandlaggareCount(entry.getKey(), entry.getValue()))
			.sorted(comparing(HandlaggareCount::handlaggare))
			.toList();

		final var unassigned = errands.stream().filter(errand -> !hasText(errand.getAssignedUserId())).count();
		final var decidedCount = errands.stream().filter(errand -> STATUS_DECIDED.equals(errand.getStatus())).count();

		final var activeDurations = errands.stream()
			.filter(errand -> errand.getStatus() != null && TERMINAL_STATUSES.contains(errand.getStatus()))
			.map(this::activeHandlaggningstid)
			.flatMap(Optional::stream)
			.toList();
		final var averageSeconds = activeDurations.isEmpty()
			? null
			: Math.round(activeDurations.stream().mapToLong(Duration::getSeconds).average().orElse(0));

		return new StatisticsResponse(errands.size(), byStatus, byHandlaggare, unassigned, decidedCount, averageSeconds);
	}

	/**
	 * Active handläggningstid for a single terminal errand: from {@code created} to the terminal
	 * transition, summing only the segments spent in non-paused statuses. Empty when the errand has no
	 * created timestamp or no terminal transition recorded.
	 */
	private Optional<Duration> activeHandlaggningstid(final ErrandEntity errand) {
		if (errand.getCreated() == null) {
			return Optional.empty();
		}
		final var transitions = statusHistoryRepository.findByErrandIdOrderByChangedAtDesc(errand.getId()).stream()
			.sorted(comparing(StatusHistoryEntity::getChangedAt))
			.toList();
		final var terminalAt = transitions.stream()
			.filter(transition -> transition.getToStatus() != null && TERMINAL_STATUSES.contains(transition.getToStatus()))
			.map(StatusHistoryEntity::getChangedAt)
			.reduce((first, second) -> second); // last terminal transition
		if (terminalAt.isEmpty()) {
			return Optional.empty();
		}

		final var end = terminalAt.get();
		var total = Duration.ZERO;
		var cursor = errand.getCreated();
		var currentStatus = STATUS_REGISTERED;
		for (final var transition : transitions) {
			var segmentEnd = transition.getChangedAt();
			if (segmentEnd.isAfter(end)) {
				segmentEnd = end;
			}
			if (segmentEnd.isAfter(cursor)) {
				if (!isPaused(currentStatus)) {
					total = total.plus(Duration.between(cursor, segmentEnd));
				}
				cursor = segmentEnd;
			}
			currentStatus = transition.getToStatus();
			if (!transition.getChangedAt().isBefore(end)) {
				break;
			}
		}
		return Optional.of(total);
	}

	private static boolean isPaused(final String status) {
		return status != null && status.startsWith(PAUSED_STATUS_PREFIX);
	}

	/**
	 * Selection spec built locally over the (exposed) {@code ErrandEntity} — namespace + municipality,
	 * optionally narrowed by typeSlug and a {@code created} range. Kept self-contained so the statistics
	 * module doesn't reach into core's internal specification package.
	 */
	private static Specification<ErrandEntity> selection(final String municipalityId, final String namespace,
		final String typeSlug, final OffsetDateTime from, final OffsetDateTime to) {
		return (root, _, cb) -> {
			final var predicates = new ArrayList<Predicate>();
			predicates.add(cb.equal(root.get("namespace"), namespace));
			predicates.add(cb.equal(root.get("municipalityId"), municipalityId));
			if (hasText(typeSlug)) {
				predicates.add(cb.equal(root.get("typeSlug"), typeSlug));
			}
			ofNullable(from).ifPresent(value -> predicates.add(cb.greaterThanOrEqualTo(root.get("created"), value)));
			ofNullable(to).ifPresent(value -> predicates.add(cb.lessThanOrEqualTo(root.get("created"), value)));
			return cb.and(predicates.toArray(Predicate[]::new));
		};
	}
}
