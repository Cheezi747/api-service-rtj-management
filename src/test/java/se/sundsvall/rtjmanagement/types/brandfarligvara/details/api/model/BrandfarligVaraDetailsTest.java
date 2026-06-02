package se.sundsvall.rtjmanagement.types.brandfarligvara.details.api.model;

import java.time.OffsetDateTime;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

class BrandfarligVaraDetailsTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt(1000)), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(BrandfarligVaraDetails.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var created = OffsetDateTime.now();
		final var modified = OffsetDateTime.now();

		final var result = BrandfarligVaraDetails.create()
			.withVerksamhetstyp("RESTAURANT")
			.withProxy(true)
			.withFastighetsbeteckning("Sundsvall Stenstaden 1:23")
			.withHandlingLocationAddress("Storgatan 5")
			.withHandlingLocationZipCode("85230")
			.withHandlingLocationCity("Sundsvall")
			.withCreated(created)
			.withModified(modified);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getVerksamhetstyp()).isEqualTo("RESTAURANT");
		assertThat(result.isProxy()).isTrue();
		assertThat(result.getFastighetsbeteckning()).isEqualTo("Sundsvall Stenstaden 1:23");
		assertThat(result.getHandlingLocationAddress()).isEqualTo("Storgatan 5");
		assertThat(result.getHandlingLocationZipCode()).isEqualTo("85230");
		assertThat(result.getHandlingLocationCity()).isEqualTo("Sundsvall");
		assertThat(result.getCreated()).isEqualTo(created);
		assertThat(result.getModified()).isEqualTo(modified);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		// isProxy is a primitive boolean (defaults to false) so it can never be null.
		assertThat(BrandfarligVaraDetails.create()).hasAllNullFieldsOrPropertiesExcept("isProxy");
	}
}
