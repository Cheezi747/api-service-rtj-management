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

class DocumentValidationResultTest {

	@Test
	void testBean() {
		assertThat(DocumentValidationResult.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = DocumentValidationResult.create()
			.withValid(true)
			.withDocumentTypeOk(true)
			.withIdentityMatch(false)
			.withReason("Namn matchar inte");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getValid()).isTrue();
		assertThat(result.getDocumentTypeOk()).isTrue();
		assertThat(result.getIdentityMatch()).isFalse();
		assertThat(result.getReason()).isEqualTo("Namn matchar inte");
	}
}
