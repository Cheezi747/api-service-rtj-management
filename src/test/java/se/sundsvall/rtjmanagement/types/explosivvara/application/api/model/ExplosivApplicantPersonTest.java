package se.sundsvall.rtjmanagement.types.explosivvara.application.api.model;

import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

class ExplosivApplicantPersonTest {

	@Test
	void testBean() {
		assertThat(ExplosivApplicantPerson.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var result = ExplosivApplicantPerson.create()
			.withRole("RESPONSIBLE_PERSON")
			.withFirstName("Anna")
			.withLastName("Karlsson")
			.withPersonnummer("198507231234")
			.withEmail("anna.karlsson@example.se")
			.withPhone("+46701234567");

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getRole()).isEqualTo("RESPONSIBLE_PERSON");
		assertThat(result.getFirstName()).isEqualTo("Anna");
		assertThat(result.getLastName()).isEqualTo("Karlsson");
		assertThat(result.getPersonnummer()).isEqualTo("198507231234");
		assertThat(result.getEmail()).isEqualTo("anna.karlsson@example.se");
		assertThat(result.getPhone()).isEqualTo("+46701234567");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ExplosivApplicantPerson.create()).hasAllNullFieldsOrProperties();
	}
}
