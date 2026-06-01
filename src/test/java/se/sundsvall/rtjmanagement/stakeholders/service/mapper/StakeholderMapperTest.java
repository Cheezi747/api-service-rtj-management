package se.sundsvall.rtjmanagement.stakeholders.service.mapper;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.stakeholders.api.model.ContactChannel;
import se.sundsvall.rtjmanagement.stakeholders.api.model.Stakeholder;
import se.sundsvall.rtjmanagement.stakeholders.integration.db.model.StakeholderEntity;

import static org.assertj.core.api.Assertions.assertThat;

class StakeholderMapperTest {

	private static Stakeholder fullStakeholder() {
		return Stakeholder.create()
			.withExternalId("ext-1")
			.withExternalIdType("PRIVATE")
			.withRole("BSK")
			.withFirstName("BSK")
			.withLastName("Handläggare")
			.withOrganizationName("Org")
			.withAddress("Street 1")
			.withCareOf("c/o")
			.withZipCode("85130")
			.withCity("Sundsvall")
			.withCountry("SE")
			.withContactChannels(List.of(ContactChannel.create().withKey("PHONE").withValue("0701234567")));
	}

	@Test
	void toStakeholderEntityMapsAllFields() {
		final var entity = StakeholderMapper.toStakeholderEntity(fullStakeholder(), "errand-1");

		assertThat(entity).isNotNull();
		assertThat(entity.getErrandId()).isEqualTo("errand-1");
		assertThat(entity.getExternalId()).isEqualTo("ext-1");
		assertThat(entity.getExternalIdType()).isEqualTo("PRIVATE");
		assertThat(entity.getRole()).isEqualTo("BSK");
		assertThat(entity.getFirstName()).isEqualTo("BSK");
		assertThat(entity.getLastName()).isEqualTo("Handläggare");
		assertThat(entity.getOrganizationName()).isEqualTo("Org");
		assertThat(entity.getAddress()).isEqualTo("Street 1");
		assertThat(entity.getCareOf()).isEqualTo("c/o");
		assertThat(entity.getZipCode()).isEqualTo("85130");
		assertThat(entity.getCity()).isEqualTo("Sundsvall");
		assertThat(entity.getCountry()).isEqualTo("SE");
		assertThat(entity.getContactChannels()).hasSize(1);
	}

	@Test
	void toStakeholderEntityNullReturnsNull() {
		assertThat(StakeholderMapper.toStakeholderEntity(null, "errand-1")).isNull();
	}

	@Test
	void toStakeholderMapsAllFields() {
		final var entity = StakeholderMapper.toStakeholderEntity(fullStakeholder(), "errand-1").withId("s1");

		final var dto = StakeholderMapper.toStakeholder(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo("s1");
		assertThat(dto.getRole()).isEqualTo("BSK");
		assertThat(dto.getFirstName()).isEqualTo("BSK");
		assertThat(dto.getLastName()).isEqualTo("Handläggare");
		assertThat(dto.getCity()).isEqualTo("Sundsvall");
		assertThat(dto.getContactChannels()).hasSize(1);
		assertThat(dto.getContactChannels().getFirst().getKey()).isEqualTo("PHONE");
	}

	@Test
	void toStakeholderNullReturnsNull() {
		assertThat(StakeholderMapper.toStakeholder(null)).isNull();
	}

	@Test
	void updateStakeholderEntityAppliesOnlyNonNullFields() {
		final var entity = StakeholderEntity.create().withRole("OLD").withFirstName("OldName").withCity("OldCity");

		final var result = StakeholderMapper.updateStakeholderEntity(entity, Stakeholder.create()
			.withRole("NEW")
			.withCity("NewCity")
			.withContactChannels(List.of(ContactChannel.create().withKey("EMAIL").withValue("a@b.c"))));

		assertThat(result.getRole()).isEqualTo("NEW");
		assertThat(result.getFirstName()).isEqualTo("OldName");
		assertThat(result.getCity()).isEqualTo("NewCity");
		assertThat(result.getContactChannels()).hasSize(1);
	}

	@Test
	void updateStakeholderEntityNullEntityOrSourceReturnsEntity() {
		assertThat(StakeholderMapper.updateStakeholderEntity(null, Stakeholder.create())).isNull();

		final var entity = StakeholderEntity.create().withRole("KEEP");
		final var result = StakeholderMapper.updateStakeholderEntity(entity, null);
		assertThat(result.getRole()).isEqualTo("KEEP");
	}

	@Test
	void toStakeholderListMapsAndNullReturnsEmpty() {
		final var list = StakeholderMapper.toStakeholderList(List.of(
			StakeholderEntity.create().withId("s1").withRole("APPLICANT"),
			StakeholderEntity.create().withId("s2").withRole("BSK")));

		assertThat(list).hasSize(2).extracting(Stakeholder::getRole).containsExactly("APPLICANT", "BSK");
		assertThat(StakeholderMapper.toStakeholderList(null)).isEmpty();
	}

	@Test
	void toStakeholderEntityListMapsAndNullReturnsEmpty() {
		final var list = StakeholderMapper.toStakeholderEntityList(List.of(fullStakeholder()), "errand-9");

		assertThat(list).hasSize(1);
		assertThat(list.getFirst().getErrandId()).isEqualTo("errand-9");
		assertThat(StakeholderMapper.toStakeholderEntityList(null, "errand-9")).isEmpty();
	}
}
