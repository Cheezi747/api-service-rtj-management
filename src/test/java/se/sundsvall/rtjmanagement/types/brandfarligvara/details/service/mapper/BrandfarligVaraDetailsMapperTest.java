package se.sundsvall.rtjmanagement.types.brandfarligvara.details.service.mapper;

import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.types.brandfarligvara.details.api.model.BrandfarligVaraDetails;

import static org.assertj.core.api.Assertions.assertThat;

class BrandfarligVaraDetailsMapperTest {

	private static BrandfarligVaraDetails fullDetails() {
		return BrandfarligVaraDetails.create()
			.withVerksamhetstyp("RESTAURANT")
			.withProxy(true)
			.withFastighetsbeteckning("Fast 1:1")
			.withHandlingLocationAddress("Gatan 1")
			.withHandlingLocationZipCode("85230")
			.withHandlingLocationCity("Sundsvall");
	}

	@Test
	void toEntityMapsAllFields() {
		final var entity = BrandfarligVaraDetailsMapper.toEntity(fullDetails(), "errand-1");

		assertThat(entity).isNotNull();
		assertThat(entity.getErrandId()).isEqualTo("errand-1");
		assertThat(entity.getVerksamhetstyp()).isEqualTo("RESTAURANT");
		assertThat(entity.isProxy()).isTrue();
		assertThat(entity.getFastighetsbeteckning()).isEqualTo("Fast 1:1");
		assertThat(entity.getHandlingLocationAddress()).isEqualTo("Gatan 1");
		assertThat(entity.getHandlingLocationZipCode()).isEqualTo("85230");
		assertThat(entity.getHandlingLocationCity()).isEqualTo("Sundsvall");
	}

	@Test
	void toEntityNullReturnsNull() {
		assertThat(BrandfarligVaraDetailsMapper.toEntity(null, "errand-1")).isNull();
	}

	@Test
	void toDetailsMapsAllFields() {
		final var entity = BrandfarligVaraDetailsMapper.toEntity(fullDetails(), "errand-1").withId(7L);

		final var dto = BrandfarligVaraDetailsMapper.toDetails(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getVerksamhetstyp()).isEqualTo("RESTAURANT");
		assertThat(dto.isProxy()).isTrue();
		assertThat(dto.getFastighetsbeteckning()).isEqualTo("Fast 1:1");
		assertThat(dto.getHandlingLocationAddress()).isEqualTo("Gatan 1");
		assertThat(dto.getHandlingLocationZipCode()).isEqualTo("85230");
		assertThat(dto.getHandlingLocationCity()).isEqualTo("Sundsvall");
	}

	@Test
	void toDetailsNullReturnsNull() {
		assertThat(BrandfarligVaraDetailsMapper.toDetails(null)).isNull();
	}

	@Test
	void applyPatchOnlyAppliesNonNullFields() {
		final var target = BrandfarligVaraDetailsMapper.toEntity(fullDetails(), "errand-1");
		final var patch = BrandfarligVaraDetails.create()
			.withVerksamhetstyp("OTHER")
			.withHandlingLocationCity("Timrå");

		BrandfarligVaraDetailsMapper.applyPatch(target, patch);

		assertThat(target.getVerksamhetstyp()).isEqualTo("OTHER");
		assertThat(target.getHandlingLocationCity()).isEqualTo("Timrå");
		// Untouched fields keep their original values
		assertThat(target.getFastighetsbeteckning()).isEqualTo("Fast 1:1");
		assertThat(target.getHandlingLocationAddress()).isEqualTo("Gatan 1");
	}

	@Test
	void applyPatchNullTargetOrPatchReturnsTarget() {
		assertThat(BrandfarligVaraDetailsMapper.applyPatch(null, fullDetails())).isNull();
		final var target = BrandfarligVaraDetailsMapper.toEntity(fullDetails(), "errand-1");
		assertThat(BrandfarligVaraDetailsMapper.applyPatch(target, null)).isSameAs(target);
	}
}
