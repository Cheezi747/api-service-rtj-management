package se.sundsvall.rtjmanagement.types.egensotning.details.api.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

class EgensotningVerificationResultTest {

	@Test
	void testBean() {
		assertThat(EgensotningVerificationResult.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = EgensotningVerificationResult.create()
			.withOutcome("NEEDS_MANUAL_REVIEW")
			.withBilagaPresent(true)
			.withRegisteredAtProperty(false)
			.withReapplicationOk(true)
			.withManualReviewReason("NOT_REGISTERED")
			.withDecisionDescription("Ansökan om egensotning godkänd. ...");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getOutcome()).isEqualTo("NEEDS_MANUAL_REVIEW");
		assertThat(result.getBilagaPresent()).isTrue();
		assertThat(result.getRegisteredAtProperty()).isFalse();
		assertThat(result.getReapplicationOk()).isTrue();
		assertThat(result.getManualReviewReason()).isEqualTo("NOT_REGISTERED");
		assertThat(result.getDecisionDescription()).isEqualTo("Ansökan om egensotning godkänd. ...");
	}
}
