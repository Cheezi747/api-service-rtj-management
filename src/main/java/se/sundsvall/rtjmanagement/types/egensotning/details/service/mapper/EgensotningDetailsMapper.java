package se.sundsvall.rtjmanagement.types.egensotning.details.service.mapper;

import se.sundsvall.rtjmanagement.types.egensotning.details.api.model.EgensotningDetails;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;

import static java.util.Optional.ofNullable;

public final class EgensotningDetailsMapper {

	private EgensotningDetailsMapper() {}

	public static EgensotningDetails toDetails(final EgensotningDetailsEntity entity) {
		return ofNullable(entity)
			.map(e -> EgensotningDetails.create()
				.withPersonnummer(e.getPersonnummer())
				.withFastighetsbeteckning(e.getFastighetsbeteckning())
				.withPropertyAddress(e.getPropertyAddress())
				.withBilagaPresent(e.getBilagaPresent())
				.withRegisteredAtProperty(e.getRegisteredAtProperty())
				.withReapplicationOk(e.getReapplicationOk())
				.withLastOutcome(e.getLastOutcome())
				.withManualReviewReason(e.getManualReviewReason())
				.withLastVerifiedAt(e.getLastVerifiedAt())
				.withValidFrom(e.getValidFrom())
				.withValidUntil(e.getValidUntil())
				.withReminderSentAt(e.getReminderSentAt())
				.withCreated(e.getCreated())
				.withModified(e.getModified()))
			.orElse(null);
	}

	/**
	 * Maps only the frontend-supplied fields. The check-result fields are computed and
	 * persisted by the verification step, never set from the API payload.
	 */
	public static EgensotningDetailsEntity toEntity(final EgensotningDetails details, final String errandId) {
		return ofNullable(details)
			.map(d -> EgensotningDetailsEntity.create()
				.withErrandId(errandId)
				.withPersonnummer(d.getPersonnummer())
				.withFastighetsbeteckning(d.getFastighetsbeteckning())
				.withPropertyAddress(d.getPropertyAddress()))
			.orElse(null);
	}

	/**
	 * PATCH semantics: only non-null frontend fields on {@code patch} are applied to
	 * {@code target}. Computed check-result fields are left untouched (they are owned by
	 * the verification step).
	 */
	public static EgensotningDetailsEntity applyPatch(final EgensotningDetailsEntity target, final EgensotningDetails patch) {
		if (target == null || patch == null) {
			return target;
		}
		ofNullable(patch.getPersonnummer()).ifPresent(target::setPersonnummer);
		ofNullable(patch.getFastighetsbeteckning()).ifPresent(target::setFastighetsbeteckning);
		ofNullable(patch.getPropertyAddress()).ifPresent(target::setPropertyAddress);
		return target;
	}
}
