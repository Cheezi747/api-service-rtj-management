package se.sundsvall.rtjmanagement.types.egensotning.configuration;

import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.stakeholders.api.model.RoleDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.ROLE_APPLICANT;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.ROLE_BSK;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_AWAITING_SUPPLEMENTATION;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_DECIDED;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_REGISTERED;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_REJECTED;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_UNDER_MANUAL_REVIEW;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.TYPE_SLUG;

class EgensotningModuleConfigTest {

	private final EgensotningModuleConfig config = new EgensotningModuleConfig();

	@Test
	void typeContributionDeclaresSlugAndStatuses() {
		final var type = config.egensotningType();

		assertThat(type.typeSlug()).isEqualTo(TYPE_SLUG);
		assertThat(type.displayName()).isEqualTo("Ansökan om egen sotning");
		assertThat(type.allowedStatuses()).contains(STATUS_REGISTERED, STATUS_AWAITING_SUPPLEMENTATION,
			STATUS_UNDER_MANUAL_REVIEW, STATUS_DECIDED, STATUS_REJECTED);
	}

	@Test
	void typeContributionAllowsExpectedTransitions() {
		final var type = config.egensotningType();

		assertThat(type.isValidTransition(STATUS_REGISTERED, STATUS_DECIDED)).isTrue();
		assertThat(type.isValidTransition(STATUS_REGISTERED, STATUS_AWAITING_SUPPLEMENTATION)).isTrue();
		assertThat(type.isValidTransition(STATUS_REGISTERED, STATUS_UNDER_MANUAL_REVIEW)).isTrue();
		assertThat(type.isValidTransition(STATUS_AWAITING_SUPPLEMENTATION, STATUS_DECIDED)).isTrue();
		assertThat(type.isValidTransition(STATUS_AWAITING_SUPPLEMENTATION, STATUS_UNDER_MANUAL_REVIEW)).isTrue();
		assertThat(type.isValidTransition(STATUS_UNDER_MANUAL_REVIEW, STATUS_DECIDED)).isTrue();
		assertThat(type.isValidTransition(STATUS_UNDER_MANUAL_REVIEW, STATUS_REJECTED)).isTrue();

		// A nonsensical transition is rejected
		assertThat(type.isValidTransition(STATUS_DECIDED, STATUS_REGISTERED)).isFalse();
	}

	@Test
	void rolesContributionRegistersApplicantAndBsk() {
		final var roles = config.egensotningRoles();

		assertThat(roles.typeSlug()).isEqualTo(TYPE_SLUG);
		assertThat(roles.roles()).extracting(RoleDefinition::code).containsExactlyInAnyOrder(ROLE_APPLICANT, ROLE_BSK);
	}

	@Test
	void applicantRoleIsRequiredAndSingular() {
		final var roles = config.egensotningRoles();

		final var applicant = roles.roles().stream().filter(r -> r.code().equals(ROLE_APPLICANT)).findFirst().orElseThrow();
		assertThat(applicant.required()).isTrue();
		assertThat(applicant.maxOccurrences()).isEqualTo(1);
	}

	@Test
	void bskRoleIsOptionalAndSingular() {
		final var roles = config.egensotningRoles();

		final var bsk = roles.roles().stream().filter(r -> r.code().equals(ROLE_BSK)).findFirst().orElseThrow();
		assertThat(bsk.required()).isFalse();
		assertThat(bsk.maxOccurrences()).isEqualTo(1);
	}
}
