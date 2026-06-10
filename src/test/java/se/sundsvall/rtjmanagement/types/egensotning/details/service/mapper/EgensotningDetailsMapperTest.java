package se.sundsvall.rtjmanagement.types.egensotning.details.service.mapper;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.types.egensotning.details.api.model.EgensotningDetails;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;

import static org.assertj.core.api.Assertions.assertThat;

class EgensotningDetailsMapperTest {

	private static EgensotningDetails frontendDetails() {
		return EgensotningDetails.create()
			.withPersonnummer("199001011234")
			.withFastighetsbeteckning("Fast 1:1")
			.withPropertyAddress("Gatan 1")
			.withOwnsProperty(true)
			.withOwnershipMotivation("Arrenderar")
			.withAppliesForOtherProperty(false);
	}

	@Test
	void toEntityMapsFrontendFieldsOnly() {
		final var entity = EgensotningDetailsMapper.toEntity(frontendDetails(), "errand-1");

		assertThat(entity).isNotNull();
		assertThat(entity.getErrandId()).isEqualTo("errand-1");
		assertThat(entity.getPersonnummer()).isEqualTo("199001011234");
		assertThat(entity.getFastighetsbeteckning()).isEqualTo("Fast 1:1");
		assertThat(entity.getPropertyAddress()).isEqualTo("Gatan 1");
		assertThat(entity.getOwnsProperty()).isTrue();
		assertThat(entity.getOwnershipMotivation()).isEqualTo("Arrenderar");
		assertThat(entity.getAppliesForOtherProperty()).isFalse();
		// motivering is handläggare-owned (set during manual review), never from the create payload
		assertThat(entity.getMotivering()).isNull();
		// Computed fields are never set from the payload
		assertThat(entity.getBilagaPresent()).isNull();
		assertThat(entity.getRegisteredAtProperty()).isNull();
		assertThat(entity.getReapplicationOk()).isNull();
		assertThat(entity.getLastOutcome()).isNull();
		assertThat(entity.getManualReviewReason()).isNull();
		assertThat(entity.getLastVerifiedAt()).isNull();
		assertThat(entity.getValidFrom()).isNull();
		assertThat(entity.getValidUntil()).isNull();
		assertThat(entity.getReminderSentAt()).isNull();
	}

	@Test
	void toEntityNullReturnsNull() {
		assertThat(EgensotningDetailsMapper.toEntity(null, "errand-1")).isNull();
	}

	@Test
	void toDetailsMapsAllFields() {
		final var validFrom = LocalDate.of(2026, 6, 3);
		final var validUntil = LocalDate.of(2032, 6, 1);
		final var reminderSentAt = OffsetDateTime.parse("2032-03-01T00:00:00Z");
		final var revokedAt = OffsetDateTime.parse("2027-01-15T08:00:00Z");
		final var documentValidatedAt = OffsetDateTime.parse("2026-06-10T09:00:00Z");
		final var entity = EgensotningDetailsMapper.toEntity(frontendDetails(), "errand-1")
			.withId(7L)
			.withMotivering("Handläggarens motivering")
			.withBilagaPresent(true)
			.withRegisteredAtProperty(false)
			.withReapplicationOk(true)
			.withLastOutcome("NEEDS_MANUAL_REVIEW")
			.withManualReviewReason("NOT_REGISTERED")
			.withDocumentsValid(false)
			.withDocumentValidationDetail("Brandskyddskontroll: fastighet stämmer inte. | Utbildningsintyg: personnummer saknas.")
			.withDocumentValidatedAt(documentValidatedAt)
			.withValidFrom(validFrom)
			.withValidUntil(validUntil)
			.withReminderSentAt(reminderSentAt)
			.withRevokedAt(revokedAt)
			.withRevocationReason("ADDRESS_CHANGED");

		final var dto = EgensotningDetailsMapper.toDetails(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getPersonnummer()).isEqualTo("199001011234");
		assertThat(dto.getFastighetsbeteckning()).isEqualTo("Fast 1:1");
		assertThat(dto.getPropertyAddress()).isEqualTo("Gatan 1");
		assertThat(dto.getOwnsProperty()).isTrue();
		assertThat(dto.getOwnershipMotivation()).isEqualTo("Arrenderar");
		assertThat(dto.getAppliesForOtherProperty()).isFalse();
		assertThat(dto.getMotivering()).isEqualTo("Handläggarens motivering");
		assertThat(dto.getBilagaPresent()).isTrue();
		assertThat(dto.getRegisteredAtProperty()).isFalse();
		assertThat(dto.getReapplicationOk()).isTrue();
		assertThat(dto.getLastOutcome()).isEqualTo("NEEDS_MANUAL_REVIEW");
		assertThat(dto.getManualReviewReason()).isEqualTo("NOT_REGISTERED");
		assertThat(dto.getDocumentsValid()).isFalse();
		assertThat(dto.getDocumentValidationDetail()).isEqualTo("Brandskyddskontroll: fastighet stämmer inte. | Utbildningsintyg: personnummer saknas.");
		assertThat(dto.getDocumentValidatedAt()).isEqualTo(documentValidatedAt);
		assertThat(dto.getValidFrom()).isEqualTo(validFrom);
		assertThat(dto.getValidUntil()).isEqualTo(validUntil);
		assertThat(dto.getReminderSentAt()).isEqualTo(reminderSentAt);
		assertThat(dto.getRevokedAt()).isEqualTo(revokedAt);
		assertThat(dto.getRevocationReason()).isEqualTo("ADDRESS_CHANGED");
	}

	@Test
	void toDetailsNullReturnsNull() {
		assertThat(EgensotningDetailsMapper.toDetails(null)).isNull();
	}

	@Test
	void applyPatchOnlyAppliesNonNullFrontendFields() {
		final var target = EgensotningDetailsMapper.toEntity(frontendDetails(), "errand-1")
			.withBilagaPresent(true)
			.withLastOutcome("AUTO_APPROVE");
		final var patch = EgensotningDetails.create().withFastighetsbeteckning("Fast 2:2").withMotivering("Handläggarens motivering");

		EgensotningDetailsMapper.applyPatch(target, patch);

		assertThat(target.getFastighetsbeteckning()).isEqualTo("Fast 2:2");
		// Handläggaren kan sätta motivering via patch
		assertThat(target.getMotivering()).isEqualTo("Handläggarens motivering");
		// Untouched frontend field keeps its value
		assertThat(target.getPersonnummer()).isEqualTo("199001011234");
		// Computed fields are not disturbed by a details patch
		assertThat(target.getBilagaPresent()).isTrue();
		assertThat(target.getLastOutcome()).isEqualTo("AUTO_APPROVE");
	}

	@Test
	void applyPatchNullTargetOrPatchReturnsTarget() {
		assertThat(EgensotningDetailsMapper.applyPatch(null, frontendDetails())).isNull();
		final var target = EgensotningDetailsMapper.toEntity(frontendDetails(), "errand-1");
		assertThat(EgensotningDetailsMapper.applyPatch(target, null)).isSameAs(target);
	}

	@Test
	void toEntityEmptyDetailsLeavesErrandIdOnly() {
		final var entity = EgensotningDetailsMapper.toEntity(EgensotningDetails.create(), "errand-9");

		assertThat(entity).isNotNull();
		assertThat(entity.getErrandId()).isEqualTo("errand-9");
		assertThat(entity).extracting(EgensotningDetailsEntity::getPersonnummer, EgensotningDetailsEntity::getFastighetsbeteckning,
			EgensotningDetailsEntity::getPropertyAddress).containsOnlyNulls();
	}
}
