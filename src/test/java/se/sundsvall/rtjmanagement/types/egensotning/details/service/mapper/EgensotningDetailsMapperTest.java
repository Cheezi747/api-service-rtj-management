package se.sundsvall.rtjmanagement.types.egensotning.details.service.mapper;

import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.types.egensotning.details.api.model.EgensotningDetails;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;

import static org.assertj.core.api.Assertions.assertThat;

class EgensotningDetailsMapperTest {

	private static EgensotningDetails frontendDetails() {
		return EgensotningDetails.create()
			.withPersonnummer("199001011234")
			.withFastighetsbeteckning("Fast 1:1")
			.withPropertyAddress("Gatan 1");
	}

	@Test
	void toEntityMapsFrontendFieldsOnly() {
		final var entity = EgensotningDetailsMapper.toEntity(frontendDetails(), "errand-1");

		assertThat(entity).isNotNull();
		assertThat(entity.getErrandId()).isEqualTo("errand-1");
		assertThat(entity.getPersonnummer()).isEqualTo("199001011234");
		assertThat(entity.getFastighetsbeteckning()).isEqualTo("Fast 1:1");
		assertThat(entity.getPropertyAddress()).isEqualTo("Gatan 1");
		// Computed fields are never set from the payload
		assertThat(entity.getBilagaPresent()).isNull();
		assertThat(entity.getRegisteredAtProperty()).isNull();
		assertThat(entity.getReapplicationOk()).isNull();
		assertThat(entity.getLastOutcome()).isNull();
		assertThat(entity.getManualReviewReason()).isNull();
		assertThat(entity.getLastVerifiedAt()).isNull();
	}

	@Test
	void toEntityNullReturnsNull() {
		assertThat(EgensotningDetailsMapper.toEntity(null, "errand-1")).isNull();
	}

	@Test
	void toDetailsMapsAllFields() {
		final var entity = EgensotningDetailsMapper.toEntity(frontendDetails(), "errand-1")
			.withId(7L)
			.withBilagaPresent(true)
			.withRegisteredAtProperty(false)
			.withReapplicationOk(true)
			.withLastOutcome("NEEDS_MANUAL_REVIEW")
			.withManualReviewReason("NOT_REGISTERED");

		final var dto = EgensotningDetailsMapper.toDetails(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getPersonnummer()).isEqualTo("199001011234");
		assertThat(dto.getFastighetsbeteckning()).isEqualTo("Fast 1:1");
		assertThat(dto.getPropertyAddress()).isEqualTo("Gatan 1");
		assertThat(dto.getBilagaPresent()).isTrue();
		assertThat(dto.getRegisteredAtProperty()).isFalse();
		assertThat(dto.getReapplicationOk()).isTrue();
		assertThat(dto.getLastOutcome()).isEqualTo("NEEDS_MANUAL_REVIEW");
		assertThat(dto.getManualReviewReason()).isEqualTo("NOT_REGISTERED");
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
		final var patch = EgensotningDetails.create().withFastighetsbeteckning("Fast 2:2");

		EgensotningDetailsMapper.applyPatch(target, patch);

		assertThat(target.getFastighetsbeteckning()).isEqualTo("Fast 2:2");
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
