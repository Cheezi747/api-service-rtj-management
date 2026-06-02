package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.attachments.integration.db.AttachmentRepository;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig;
import se.sundsvall.rtjmanagement.types.egensotning.details.api.model.EgensotningVerificationResult;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.citizen.CitizenClient;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.EgensotningDetailsRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.SotningsobjektRepository;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_DECIDED;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_REJECTED;

/**
 * Runs the three automated egensotning checks and computes the routing {@code outcome}:
 * <ol>
 * <li><b>bilaga present</b> — at least one attachment exists on the errand.</li>
 * <li><b>folkbokförd på fastigheten</b> — applicant's population-registration address
 * matches the property (via the citizen integration).</li>
 * <li><b>återansökan</b> — prior egensotning applications for the same personnummer.
 * A prior DECIDED errand means a previous approval (renewal, OK to auto-approve); a prior
 * REJECTED or still-open errand requires manual review.</li>
 * </ol>
 *
 * Precedence (hardest constraint wins): not registered OR reapplication-needs-review →
 * NEEDS_MANUAL_REVIEW; else bilaga missing → NEEDS_SUPPLEMENT; else AUTO_APPROVE.
 *
 * The computed result is persisted on the details row (for the handläggare UI / audit) and
 * returned so the BPMN worker can copy it into process variables.
 */
@Service
@Transactional
public class EgensotningVerificationService {

	static final String OUTCOME_AUTO_APPROVE = "AUTO_APPROVE";
	static final String OUTCOME_NEEDS_SUPPLEMENT = "NEEDS_SUPPLEMENT";
	static final String OUTCOME_NEEDS_MANUAL_REVIEW = "NEEDS_MANUAL_REVIEW";

	static final String REASON_NOT_REGISTERED = "NOT_REGISTERED";
	static final String REASON_REAPPLICATION_REJECTED = "REAPPLICATION_REJECTED";
	static final String REASON_REAPPLICATION_ONGOING = "REAPPLICATION_ONGOING";

	private static final String ERRAND_NOT_FOUND_MESSAGE = "No errand with id '%s' found in namespace '%s' for municipality id '%s'";
	private static final String WRONG_TYPE_MESSAGE = "Errand '%s' has typeSlug '%s'; egensotning verification requires typeSlug '%s'";

	private static final Logger LOG = LoggerFactory.getLogger(EgensotningVerificationService.class);

	private final ErrandRepository errandRepository;
	private final EgensotningDetailsRepository detailsRepository;
	private final AttachmentRepository attachmentRepository;
	private final SotningsobjektRepository sotningsobjektRepository;
	private final CitizenClient citizenClient;

	EgensotningVerificationService(final ErrandRepository errandRepository, final EgensotningDetailsRepository detailsRepository,
		final AttachmentRepository attachmentRepository, final SotningsobjektRepository sotningsobjektRepository, final CitizenClient citizenClient) {
		this.errandRepository = errandRepository;
		this.detailsRepository = detailsRepository;
		this.attachmentRepository = attachmentRepository;
		this.sotningsobjektRepository = sotningsobjektRepository;
		this.citizenClient = citizenClient;
	}

	public EgensotningVerificationResult verify(final String municipalityId, final String namespace, final String errandId) {
		findErrandWithType(municipalityId, namespace, errandId);

		final var bilagaPresent = attachmentRepository.countByErrandId(errandId) > 0;

		final var detailsOpt = detailsRepository.findByErrandId(errandId);
		if (detailsOpt.isEmpty()) {
			// Application data (personnummer, fastighetsbeteckning) not submitted yet. Route to
			// NEEDS_SUPPLEMENT so the process waits and re-verifies instead of raising a BPMN
			// incident — the frontend normally PUTs details before the first verify poll, so this
			// is a safety net for a not-yet-complete submission.
			LOG.info("No egensotning details on errand {} yet; routing to NEEDS_SUPPLEMENT", errandId);
			return EgensotningVerificationResult.create()
				.withOutcome(OUTCOME_NEEDS_SUPPLEMENT)
				.withBilagaPresent(bilagaPresent)
				.withRegisteredAtProperty(false)
				.withReapplicationOk(true);
		}
		final var details = detailsOpt.get();
		final var sotningsobjekt = sotningsobjektRepository.findByErrandIdOrderByTypAscFabrikatAsc(errandId);
		final var hasSotningsobjekt = !sotningsobjekt.isEmpty();

		final var registeredAtProperty = isRegisteredAtProperty(municipalityId, details);
		final var reapplication = checkReapplication(details);

		final String outcome;
		final String manualReviewReason;
		if (!registeredAtProperty) {
			outcome = OUTCOME_NEEDS_MANUAL_REVIEW;
			manualReviewReason = REASON_NOT_REGISTERED;
		} else if (!reapplication.ok()) {
			outcome = OUTCOME_NEEDS_MANUAL_REVIEW;
			manualReviewReason = reapplication.reason();
		} else if (!bilagaPresent || !hasSotningsobjekt) {
			// A complete application needs both the bilaga and at least one sotningsobjekt (so the
			// auto-issued beslut actually lists the objekt + intervall it covers).
			outcome = OUTCOME_NEEDS_SUPPLEMENT;
			manualReviewReason = null;
		} else {
			outcome = OUTCOME_AUTO_APPROVE;
			manualReviewReason = null;
		}

		final var decisionDescription = EgensotningDecisionTextBuilder.buildApprovalDescription(details, sotningsobjekt);

		details.setBilagaPresent(bilagaPresent);
		details.setRegisteredAtProperty(registeredAtProperty);
		details.setReapplicationOk(reapplication.ok());
		details.setLastOutcome(outcome);
		details.setManualReviewReason(manualReviewReason);
		details.setLastVerifiedAt(OffsetDateTime.now(ZoneId.systemDefault()));
		detailsRepository.save(details);

		LOG.info("Verified egensotning errand {}: outcome={} (bilaga={}, objekt={}, registered={}, reapplicationOk={})",
			errandId, outcome, bilagaPresent, hasSotningsobjekt, registeredAtProperty, reapplication.ok());

		return EgensotningVerificationResult.create()
			.withOutcome(outcome)
			.withBilagaPresent(bilagaPresent)
			.withRegisteredAtProperty(registeredAtProperty)
			.withReapplicationOk(reapplication.ok())
			.withManualReviewReason(manualReviewReason)
			.withDecisionDescription(decisionDescription);
	}

	private boolean isRegisteredAtProperty(final String municipalityId, final EgensotningDetailsEntity details) {
		final var personnummer = details.getPersonnummer();
		if (personnummer == null || personnummer.isBlank()) {
			return false;
		}
		try {
			final var personId = citizenClient.getGuid(municipalityId, personnummer);
			if (personId == null || personId.isBlank()) {
				return false;
			}
			final var citizen = citizenClient.getCitizen(municipalityId, personId);
			return EgensotningCheckUtil.isRegisteredAtProperty(citizen, municipalityId, details.getFastighetsbeteckning());
		} catch (final ThrowableProblem e) {
			if (NOT_FOUND.equals(e.getStatus())) {
				// Unknown person/personnummer → not registered (route to manual review), not a server error.
				return false;
			}
			throw e;
		}
	}

	/**
	 * Classifies prior egensotning applications for the same applicant. The prior errand's
	 * STATUS encodes the previous outcome: this BPMN sets DECIDED only on approval and
	 * REJECTED only on rejection; anything non-terminal is an in-flight application.
	 */
	private Reapplication checkReapplication(final EgensotningDetailsEntity details) {
		final var personnummer = details.getPersonnummer();
		if (personnummer == null || personnummer.isBlank()) {
			return new Reapplication(true, null);
		}
		final var priors = detailsRepository.findByPersonnummerAndErrandIdNot(personnummer, details.getErrandId());
		var anyRejected = false;
		var anyOngoing = false;
		for (final var prior : priors) {
			final var status = errandRepository.findById(prior.getErrandId()).map(ErrandEntity::getStatus).orElse(null);
			if (STATUS_REJECTED.equals(status)) {
				anyRejected = true;
			} else if (status != null && !STATUS_DECIDED.equals(status)) {
				anyOngoing = true;
			}
			// STATUS_DECIDED → a previous approval (renewal) → does not block auto-approval.
		}
		if (anyRejected) {
			return new Reapplication(false, REASON_REAPPLICATION_REJECTED);
		}
		if (anyOngoing) {
			return new Reapplication(false, REASON_REAPPLICATION_ONGOING);
		}
		return new Reapplication(true, null);
	}

	private ErrandEntity findErrandWithType(final String municipalityId, final String namespace, final String errandId) {
		final var errand = errandRepository.findByIdAndNamespaceAndMunicipalityId(errandId, namespace, municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERRAND_NOT_FOUND_MESSAGE.formatted(errandId, namespace, municipalityId)));
		if (!EgensotningModuleConfig.TYPE_SLUG.equals(errand.getTypeSlug())) {
			throw Problem.valueOf(BAD_REQUEST, WRONG_TYPE_MESSAGE.formatted(errandId, errand.getTypeSlug(), EgensotningModuleConfig.TYPE_SLUG));
		}
		return errand;
	}

	private record Reapplication(boolean ok, String reason) {
	}
}
