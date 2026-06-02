package se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.api.model.Sotningsobjekt;

import static org.assertj.core.api.Assertions.assertThat;

class SotningsobjektMapperTest {

	private static Sotningsobjekt fullObjekt() {
		return Sotningsobjekt.create()
			.withTyp("Värmepanna")
			.withFabrikat("CTC")
			.withTillverkningsar(1998)
			.withBransleslag("Ved")
			.withBranslemangd("12 m³")
			.withSotningsintervallVeckor(8);
	}

	@Test
	void toEntityMapsAllFields() {
		final var entity = SotningsobjektMapper.toEntity(fullObjekt(), "errand-1");

		assertThat(entity).isNotNull();
		assertThat(entity.getErrandId()).isEqualTo("errand-1");
		assertThat(entity.getTyp()).isEqualTo("Värmepanna");
		assertThat(entity.getFabrikat()).isEqualTo("CTC");
		assertThat(entity.getTillverkningsar()).isEqualTo(1998);
		assertThat(entity.getBransleslag()).isEqualTo("Ved");
		assertThat(entity.getBranslemangd()).isEqualTo("12 m³");
		assertThat(entity.getSotningsintervallVeckor()).isEqualTo(8);
	}

	@Test
	void toEntityNullReturnsNull() {
		assertThat(SotningsobjektMapper.toEntity(null, "errand-1")).isNull();
	}

	@Test
	void toSotningsobjektMapsAllFields() {
		final var entity = SotningsobjektMapper.toEntity(fullObjekt(), "errand-1").withId("obj-1");

		final var dto = SotningsobjektMapper.toSotningsobjekt(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo("obj-1");
		assertThat(dto.getTyp()).isEqualTo("Värmepanna");
		assertThat(dto.getSotningsintervallVeckor()).isEqualTo(8);
	}

	@Test
	void toSotningsobjektNullReturnsNull() {
		assertThat(SotningsobjektMapper.toSotningsobjekt(null)).isNull();
	}

	@Test
	void toSotningsobjektListMapsEntries() {
		final var list = SotningsobjektMapper.toSotningsobjektList(List.of(SotningsobjektMapper.toEntity(fullObjekt(), "e1")));
		assertThat(list).hasSize(1).first().hasFieldOrPropertyWithValue("typ", "Värmepanna");
	}

	@Test
	void toSotningsobjektListNullReturnsEmpty() {
		assertThat(SotningsobjektMapper.toSotningsobjektList(null)).isEmpty();
	}

	@Test
	void applyPatchOnlyAppliesNonNullFields() {
		final var target = SotningsobjektMapper.toEntity(fullObjekt(), "errand-1");
		final var patch = Sotningsobjekt.create().withBransleslag("Olja").withSotningsintervallVeckor(12);

		SotningsobjektMapper.applyPatch(target, patch);

		assertThat(target.getBransleslag()).isEqualTo("Olja");
		assertThat(target.getSotningsintervallVeckor()).isEqualTo(12);
		// Untouched fields keep their original values
		assertThat(target.getTyp()).isEqualTo("Värmepanna");
		assertThat(target.getFabrikat()).isEqualTo("CTC");
	}

	@Test
	void applyPatchNullTargetOrPatchReturnsTarget() {
		assertThat(SotningsobjektMapper.applyPatch(null, fullObjekt())).isNull();
		final var target = SotningsobjektMapper.toEntity(fullObjekt(), "errand-1");
		assertThat(SotningsobjektMapper.applyPatch(target, null)).isSameAs(target);
	}
}
