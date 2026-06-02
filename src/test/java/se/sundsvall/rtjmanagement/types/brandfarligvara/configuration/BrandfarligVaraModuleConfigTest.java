package se.sundsvall.rtjmanagement.types.brandfarligvara.configuration;

import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.stakeholders.api.model.RoleDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.rtjmanagement.types.brandfarligvara.configuration.BrandfarligVaraModuleConfig.ROLE_APPLICANT;
import static se.sundsvall.rtjmanagement.types.brandfarligvara.configuration.BrandfarligVaraModuleConfig.ROLE_CONTACT_PERSON;
import static se.sundsvall.rtjmanagement.types.brandfarligvara.configuration.BrandfarligVaraModuleConfig.ROLE_INSPECTION_OFFICER;
import static se.sundsvall.rtjmanagement.types.brandfarligvara.configuration.BrandfarligVaraModuleConfig.ROLE_INVOICEE;
import static se.sundsvall.rtjmanagement.types.brandfarligvara.configuration.BrandfarligVaraModuleConfig.ROLE_RESPONSIBLE_PERSON;
import static se.sundsvall.rtjmanagement.types.brandfarligvara.configuration.BrandfarligVaraModuleConfig.STATUS_AWAITING_SUPPLEMENTATION;
import static se.sundsvall.rtjmanagement.types.brandfarligvara.configuration.BrandfarligVaraModuleConfig.STATUS_DECIDED;
import static se.sundsvall.rtjmanagement.types.brandfarligvara.configuration.BrandfarligVaraModuleConfig.STATUS_INSPECTION_SCHEDULED;
import static se.sundsvall.rtjmanagement.types.brandfarligvara.configuration.BrandfarligVaraModuleConfig.STATUS_REGISTERED;
import static se.sundsvall.rtjmanagement.types.brandfarligvara.configuration.BrandfarligVaraModuleConfig.STATUS_REJECTED;
import static se.sundsvall.rtjmanagement.types.brandfarligvara.configuration.BrandfarligVaraModuleConfig.TYPE_SLUG;

class BrandfarligVaraModuleConfigTest {

	private final BrandfarligVaraModuleConfig config = new BrandfarligVaraModuleConfig();

	@Test
	void typeContributionDeclaresSlugAndDisplayName() {
		final var type = config.brandfarligVaraType();

		assertThat(type.typeSlug()).isEqualTo(TYPE_SLUG);
		assertThat(type.displayName()).isEqualTo("Ansökan om tillstånd för brandfarlig vara");
		assertThat(type.allowedStatuses()).contains(STATUS_REGISTERED, STATUS_AWAITING_SUPPLEMENTATION,
			STATUS_INSPECTION_SCHEDULED, STATUS_DECIDED, STATUS_REJECTED);
	}

	@Test
	void typeContributionAllowsExpectedTransitions() {
		final var type = config.brandfarligVaraType();

		assertThat(type.isValidTransition(STATUS_REGISTERED, STATUS_DECIDED)).isTrue();
		assertThat(type.isValidTransition(STATUS_REGISTERED, STATUS_AWAITING_SUPPLEMENTATION)).isTrue();
		assertThat(type.isValidTransition(STATUS_REGISTERED, STATUS_INSPECTION_SCHEDULED)).isTrue();
		assertThat(type.isValidTransition(STATUS_AWAITING_SUPPLEMENTATION, STATUS_INSPECTION_SCHEDULED)).isTrue();
		assertThat(type.isValidTransition(STATUS_INSPECTION_SCHEDULED, STATUS_REJECTED)).isTrue();

		// A nonsensical transition is rejected
		assertThat(type.isValidTransition(STATUS_DECIDED, STATUS_REGISTERED)).isFalse();
	}

	@Test
	void rolesContributionRegistersAllFiveRoles() {
		final var roles = config.brandfarligVaraRoles();

		assertThat(roles.typeSlug()).isEqualTo(TYPE_SLUG);
		assertThat(roles.roles()).extracting(RoleDefinition::code)
			.containsExactlyInAnyOrder(ROLE_APPLICANT, ROLE_CONTACT_PERSON, ROLE_INVOICEE, ROLE_RESPONSIBLE_PERSON, ROLE_INSPECTION_OFFICER);
	}

	@Test
	void applicantRoleIsRequiredAndSingular() {
		final var roles = config.brandfarligVaraRoles();

		final var applicant = roles.roles().stream().filter(r -> r.code().equals(ROLE_APPLICANT)).findFirst().orElseThrow();
		assertThat(applicant.required()).isTrue();
		assertThat(applicant.maxOccurrences()).isEqualTo(1);
	}

	@Test
	void responsiblePersonRoleAllowsMany() {
		final var roles = config.brandfarligVaraRoles();

		final var responsible = roles.roles().stream().filter(r -> r.code().equals(ROLE_RESPONSIBLE_PERSON)).findFirst().orElseThrow();
		assertThat(responsible.required()).isTrue();
		// 0 = unbounded per RoleDefinition's convention
		assertThat(responsible.maxOccurrences()).isZero();
	}
}
