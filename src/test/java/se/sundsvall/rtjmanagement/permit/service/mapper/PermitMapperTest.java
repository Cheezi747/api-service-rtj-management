package se.sundsvall.rtjmanagement.permit.service.mapper;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.permit.api.model.Permit;
import se.sundsvall.rtjmanagement.permit.integration.db.model.PermitEntity;

import static org.assertj.core.api.Assertions.assertThat;

class PermitMapperTest {

	@Test
	void toPermitMapsAllFields() {
		final var entity = PermitEntity.create()
			.withId("id")
			.withErrandId("errand-1")
			.withPermitType("BRANDFARLIG_VARA")
			.withValidFrom(LocalDate.of(2026, 6, 3))
			.withValidUntil(LocalDate.of(2031, 9, 1))
			.withConditions("villkor")
			.withStatus("ACTIVE")
			.withCreated(OffsetDateTime.now())
			.withModified(OffsetDateTime.now());

		final var permit = PermitMapper.toPermit(entity);

		assertThat(permit.getId()).isEqualTo("id");
		assertThat(permit.getPermitType()).isEqualTo("BRANDFARLIG_VARA");
		assertThat(permit.getValidFrom()).isEqualTo(LocalDate.of(2026, 6, 3));
		assertThat(permit.getValidUntil()).isEqualTo(LocalDate.of(2031, 9, 1));
		assertThat(permit.getConditions()).isEqualTo("villkor");
		assertThat(permit.getStatus()).isEqualTo("ACTIVE");
		assertThat(permit.getCreated()).isEqualTo(entity.getCreated());
		assertThat(permit.getModified()).isEqualTo(entity.getModified());
	}

	@Test
	void toPermitEntityMapsAllFieldsAndErrandId() {
		final var permit = Permit.create()
			.withPermitType("EXPLOSIV_VARA")
			.withValidFrom(LocalDate.of(2026, 6, 3))
			.withValidUntil(LocalDate.of(2029, 3, 1))
			.withConditions("villkor")
			.withStatus("ACTIVE");

		final var entity = PermitMapper.toPermitEntity(permit, "errand-9");

		assertThat(entity.getErrandId()).isEqualTo("errand-9");
		assertThat(entity.getPermitType()).isEqualTo("EXPLOSIV_VARA");
		assertThat(entity.getValidFrom()).isEqualTo(LocalDate.of(2026, 6, 3));
		assertThat(entity.getValidUntil()).isEqualTo(LocalDate.of(2029, 3, 1));
		assertThat(entity.getStatus()).isEqualTo("ACTIVE");
	}

	@Test
	void nullsReturnNull() {
		assertThat(PermitMapper.toPermit(null)).isNull();
		assertThat(PermitMapper.toPermitEntity(null, "errand-1")).isNull();
	}

	@Test
	void toPermitListMapsAllAndNullToEmpty() {
		assertThat(PermitMapper.toPermitList(null)).isEmpty();
		assertThat(PermitMapper.toPermitList(List.of(PermitEntity.create().withId("a"), PermitEntity.create().withId("b"))))
			.extracting(Permit::getId).containsExactly("a", "b");
	}
}
