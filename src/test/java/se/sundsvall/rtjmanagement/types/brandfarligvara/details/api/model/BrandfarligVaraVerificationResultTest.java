package se.sundsvall.rtjmanagement.types.brandfarligvara.details.api.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

class BrandfarligVaraVerificationResultTest {

	@Test
	void testBean() {
		assertThat(BrandfarligVaraVerificationResult.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = BrandfarligVaraVerificationResult.create()
			.withOutcome("NEEDS_MANUAL_REVIEW")
			.withBilagaPresent(true)
			.withProductsPresent(true)
			.withSupplementReason("bilaga")
			.withDecisionDescription("text");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getOutcome()).isEqualTo("NEEDS_MANUAL_REVIEW");
		assertThat(result.getBilagaPresent()).isTrue();
		assertThat(result.getProductsPresent()).isTrue();
		assertThat(result.getSupplementReason()).isEqualTo("bilaga");
		assertThat(result.getDecisionDescription()).isEqualTo("text");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(BrandfarligVaraVerificationResult.create()).hasAllNullFieldsOrProperties();
	}
}
