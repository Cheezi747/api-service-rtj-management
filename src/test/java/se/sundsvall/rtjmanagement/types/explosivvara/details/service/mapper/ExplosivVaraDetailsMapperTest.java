package se.sundsvall.rtjmanagement.types.explosivvara.details.service.mapper;

import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.types.explosivvara.details.api.model.ExplosivVaraDetails;

import static org.assertj.core.api.Assertions.assertThat;

class ExplosivVaraDetailsMapperTest {

	private static ExplosivVaraDetails fullDetails() {
		return ExplosivVaraDetails.create()
			.withTypAvHantering("STORAGE")
			.withProxy(true)
			.withFastighetsbeteckning("Fast 1:1")
			.withHandlingLocationAddress("Gatan 1")
			.withHandlingLocationZipCode("85230")
			.withHandlingLocationCity("Sundsvall");
	}

	@Test
	void toEntityMapsAllFields() {
		final var entity = ExplosivVaraDetailsMapper.toEntity(fullDetails(), "errand-1");

		assertThat(entity).isNotNull();
		assertThat(entity.getErrandId()).isEqualTo("errand-1");
		assertThat(entity.getTypAvHantering()).isEqualTo("STORAGE");
		assertThat(entity.isProxy()).isTrue();
		assertThat(entity.getFastighetsbeteckning()).isEqualTo("Fast 1:1");
		assertThat(entity.getHandlingLocationAddress()).isEqualTo("Gatan 1");
		assertThat(entity.getHandlingLocationZipCode()).isEqualTo("85230");
		assertThat(entity.getHandlingLocationCity()).isEqualTo("Sundsvall");
	}

	@Test
	void toEntityNullReturnsNull() {
		assertThat(ExplosivVaraDetailsMapper.toEntity(null, "errand-1")).isNull();
	}

	@Test
	void toDetailsMapsAllFields() {
		final var entity = ExplosivVaraDetailsMapper.toEntity(fullDetails(), "errand-1").withId(7L);

		final var dto = ExplosivVaraDetailsMapper.toDetails(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getTypAvHantering()).isEqualTo("STORAGE");
		assertThat(dto.isProxy()).isTrue();
		assertThat(dto.getFastighetsbeteckning()).isEqualTo("Fast 1:1");
		assertThat(dto.getHandlingLocationAddress()).isEqualTo("Gatan 1");
		assertThat(dto.getHandlingLocationZipCode()).isEqualTo("85230");
		assertThat(dto.getHandlingLocationCity()).isEqualTo("Sundsvall");
	}

	@Test
	void toDetailsNullReturnsNull() {
		assertThat(ExplosivVaraDetailsMapper.toDetails(null)).isNull();
	}

	@Test
	void applyPatchOnlyAppliesNonNullFields() {
		final var target = ExplosivVaraDetailsMapper.toEntity(fullDetails(), "errand-1");
		final var patch = ExplosivVaraDetails.create()
			.withTypAvHantering("TRADE")
			.withHandlingLocationCity("Timrå");

		ExplosivVaraDetailsMapper.applyPatch(target, patch);

		assertThat(target.getTypAvHantering()).isEqualTo("TRADE");
		assertThat(target.getHandlingLocationCity()).isEqualTo("Timrå");
		// Untouched fields keep their original values
		assertThat(target.getFastighetsbeteckning()).isEqualTo("Fast 1:1");
		assertThat(target.getHandlingLocationAddress()).isEqualTo("Gatan 1");
	}

	@Test
	void applyPatchNullTargetOrPatchReturnsTarget() {
		assertThat(ExplosivVaraDetailsMapper.applyPatch(null, fullDetails())).isNull();
		final var target = ExplosivVaraDetailsMapper.toEntity(fullDetails(), "errand-1");
		assertThat(ExplosivVaraDetailsMapper.applyPatch(target, null)).isSameAs(target);
	}
}
