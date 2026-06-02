package se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.api.model;

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

class SotningsobjektTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt(1000)), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(Sotningsobjekt.class, allOf(
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

		final var result = Sotningsobjekt.create()
			.withId("obj-1")
			.withTyp("Värmepanna")
			.withFabrikat("CTC")
			.withTillverkningsar(1998)
			.withBransleslag("Ved")
			.withBranslemangd("12 m³")
			.withSotningsintervallVeckor(8)
			.withCreated(created)
			.withModified(modified);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getTyp()).isEqualTo("Värmepanna");
		assertThat(result.getFabrikat()).isEqualTo("CTC");
		assertThat(result.getTillverkningsar()).isEqualTo(1998);
		assertThat(result.getBransleslag()).isEqualTo("Ved");
		assertThat(result.getBranslemangd()).isEqualTo("12 m³");
		assertThat(result.getSotningsintervallVeckor()).isEqualTo(8);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Sotningsobjekt.create()).hasAllNullFieldsOrProperties();
	}
}
