package se.sundsvall.rtjmanagement.statistics.service;

import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.statistics.api.model.HandlaggareCount;
import se.sundsvall.rtjmanagement.statistics.api.model.StatusCount;
import se.sundsvall.rtjmanagement.statushistory.integration.db.StatusHistoryRepository;
import se.sundsvall.rtjmanagement.statushistory.integration.db.model.StatusHistoryEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final OffsetDateTime T0 = OffsetDateTime.parse("2026-06-01T08:00:00Z");

	@Mock
	private ErrandRepository errandRepositoryMock;
	@Mock
	private StatusHistoryRepository statusHistoryRepositoryMock;

	@InjectMocks
	private StatisticsService service;

	private static ErrandEntity errand(final String id, final String status, final String assignee) {
		return ErrandEntity.create().withId(id).withStatus(status).withAssignedUserId(assignee).withCreated(T0);
	}

	private static StatusHistoryEntity transition(final String from, final String to, final OffsetDateTime at) {
		return StatusHistoryEntity.create().withFromStatus(from).withToStatus(to).withChangedAt(at);
	}

	@Test
	void aggregatesCountsAndPauseAwareHandlaggningstid() {
		final var e1 = errand("e1", "DECIDED", "bsk1");
		final var e2 = errand("e2", "DECIDED", "bsk1");
		final var e3 = errand("e3", "UNDER_MANUAL_REVIEW", null);
		final var e4 = errand("e4", "REGISTERED", "bsk2");
		when(errandRepositoryMock.findAll(any(Specification.class))).thenReturn(List.of(e1, e2, e3, e4));

		// e1: active 1h (REGISTERED) + paused 2h (AWAITING_SUPPLEMENTATION, excluded) + active 1h (UNDER_MANUAL_REVIEW) = 2h
		when(statusHistoryRepositoryMock.findByErrandIdOrderByChangedAtDesc("e1")).thenReturn(List.of(
			transition("UNDER_MANUAL_REVIEW", "DECIDED", T0.plusHours(4)),
			transition("AWAITING_SUPPLEMENTATION", "UNDER_MANUAL_REVIEW", T0.plusHours(3)),
			transition("REGISTERED", "AWAITING_SUPPLEMENTATION", T0.plusHours(1))));
		// e2: terminal but no history recorded → no computable duration (excluded from the average)
		when(statusHistoryRepositoryMock.findByErrandIdOrderByChangedAtDesc("e2")).thenReturn(List.of());

		final var result = service.compute(MUNICIPALITY_ID, NAMESPACE, null, null, null);

		assertThat(result.total()).isEqualTo(4);
		assertThat(result.decidedCount()).isEqualTo(2);
		assertThat(result.unassigned()).isEqualTo(1);
		assertThat(result.byStatus()).containsExactly(
			new StatusCount("DECIDED", 2),
			new StatusCount("REGISTERED", 1),
			new StatusCount("UNDER_MANUAL_REVIEW", 1));
		assertThat(result.byHandlaggare()).containsExactly(
			new HandlaggareCount("bsk1", 2),
			new HandlaggareCount("bsk2", 1));
		// Only e1 has a computable active duration: 2 hours = 7200 seconds.
		assertThat(result.averageHandlaggningstidSeconds()).isEqualTo(7200L);
	}

	@Test
	void averageIsNullWhenNoTerminalErrandHasHistory() {
		final var open = errand("e1", "REGISTERED", "bsk1");
		when(errandRepositoryMock.findAll(any(Specification.class))).thenReturn(List.of(open));

		final var result = service.compute(MUNICIPALITY_ID, NAMESPACE, "EGENSOTNING", T0, T0.plusDays(1));

		assertThat(result.total()).isEqualTo(1);
		assertThat(result.decidedCount()).isZero();
		assertThat(result.averageHandlaggningstidSeconds()).isNull();
	}

	@Test
	void emptySelectionYieldsZeroes() {
		when(errandRepositoryMock.findAll(any(Specification.class))).thenReturn(List.of());

		final var result = service.compute(MUNICIPALITY_ID, NAMESPACE, null, null, null);

		assertThat(result.total()).isZero();
		assertThat(result.unassigned()).isZero();
		assertThat(result.decidedCount()).isZero();
		assertThat(result.byStatus()).isEmpty();
		assertThat(result.byHandlaggare()).isEmpty();
		assertThat(result.averageHandlaggningstidSeconds()).isNull();
	}

	@Test
	void terminalErrandWithoutCreatedIsSkippedFromAverage() {
		final var noCreated = ErrandEntity.create().withId("e1").withStatus("DECIDED").withAssignedUserId("bsk1");
		when(errandRepositoryMock.findAll(any(Specification.class))).thenReturn(List.of(noCreated));

		final var result = service.compute(MUNICIPALITY_ID, NAMESPACE, null, null, null);

		assertThat(result.decidedCount()).isEqualTo(1);
		assertThat(result.averageHandlaggningstidSeconds()).isNull();
	}

	@Test
	void unknownStatusIsLabelledUnknown() {
		final var noStatus = ErrandEntity.create().withId("e1").withCreated(T0);
		when(errandRepositoryMock.findAll(any(Specification.class))).thenReturn(List.of(noStatus));

		final var result = service.compute(MUNICIPALITY_ID, NAMESPACE, null, null, null);

		assertThat(result.byStatus()).containsExactly(new StatusCount("UNKNOWN", 1));
		assertThat(result.unassigned()).isEqualTo(1);
	}

	@Test
	void terminalErrandWithoutTerminalTransitionIsSkipped() {
		// Status is DECIDED but the history holds no transition INTO a terminal status → not computable.
		final var e1 = errand("e1", "DECIDED", "bsk1");
		when(errandRepositoryMock.findAll(any(Specification.class))).thenReturn(List.of(e1));
		when(statusHistoryRepositoryMock.findByErrandIdOrderByChangedAtDesc(eq("e1"))).thenReturn(List.of(
			transition("REGISTERED", "UNDER_MANUAL_REVIEW", T0.plusHours(1))));

		final var result = service.compute(MUNICIPALITY_ID, NAMESPACE, null, null, null);

		assertThat(result.averageHandlaggningstidSeconds()).isNull();
	}
}
