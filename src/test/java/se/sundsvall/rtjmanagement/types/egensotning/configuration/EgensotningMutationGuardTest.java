package se.sundsvall.rtjmanagement.types.egensotning.configuration;

import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.CONFLICT;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_AWAITING_SUPPLEMENTATION;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_DECIDED;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_REGISTERED;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_REJECTED;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_UNDER_MANUAL_REVIEW;

class EgensotningMutationGuardTest {

	private static ErrandEntity errand(final String status) {
		return ErrandEntity.create().withId("err-1").withStatus(status);
	}

	@Test
	void allowsNonTerminalStatuses() {
		assertThatCode(() -> {
			EgensotningMutationGuard.assertMutable(errand(STATUS_REGISTERED));
			EgensotningMutationGuard.assertMutable(errand(STATUS_AWAITING_SUPPLEMENTATION));
			EgensotningMutationGuard.assertMutable(errand(STATUS_UNDER_MANUAL_REVIEW));
		}).doesNotThrowAnyException();
	}

	@Test
	void allowsNullErrandAndNullStatus() {
		assertThatCode(() -> {
			EgensotningMutationGuard.assertMutable(null);
			EgensotningMutationGuard.assertMutable(errand(null));
		}).doesNotThrowAnyException();
	}

	@Test
	void blocksDecidedErrand() {
		assertThatThrownBy(() -> EgensotningMutationGuard.assertMutable(errand(STATUS_DECIDED)))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", CONFLICT);
	}

	@Test
	void blocksRejectedErrand() {
		assertThatThrownBy(() -> EgensotningMutationGuard.assertMutable(errand(STATUS_REJECTED)))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", CONFLICT);
	}
}
