package se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.model;

import com.google.code.beanmatchers.BeanMatchers;
import java.time.OffsetDateTime;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

class SotningsobjektEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(SotningsobjektEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var created = now();
		final var modified = now();

		final var entity = SotningsobjektEntity.create()
			.withId("obj-1")
			.withErrandId("errand")
			.withFabrikat("CTC")
			.withTyp("Värmepanna")
			.withTillverkningsar(1998)
			.withBransleslag("Ved")
			.withBranslemangd("12 m³")
			.withSotningsintervallVeckor(8)
			.withCreated(created)
			.withModified(modified);

		assertThat(entity).hasNoNullFieldsOrProperties();
		org.assertj.core.api.Assertions.assertThat(entity.getId()).isEqualTo("obj-1");
		org.assertj.core.api.Assertions.assertThat(entity.getErrandId()).isEqualTo("errand");
		org.assertj.core.api.Assertions.assertThat(entity.getFabrikat()).isEqualTo("CTC");
		org.assertj.core.api.Assertions.assertThat(entity.getTyp()).isEqualTo("Värmepanna");
		org.assertj.core.api.Assertions.assertThat(entity.getTillverkningsar()).isEqualTo(1998);
		org.assertj.core.api.Assertions.assertThat(entity.getBransleslag()).isEqualTo("Ved");
		org.assertj.core.api.Assertions.assertThat(entity.getBranslemangd()).isEqualTo("12 m³");
		org.assertj.core.api.Assertions.assertThat(entity.getSotningsintervallVeckor()).isEqualTo(8);
		org.assertj.core.api.Assertions.assertThat(entity.getCreated()).isEqualTo(created);
		org.assertj.core.api.Assertions.assertThat(entity.getModified()).isEqualTo(modified);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		org.assertj.core.api.Assertions.assertThat(SotningsobjektEntity.create()).hasAllNullFieldsOrProperties();
		org.assertj.core.api.Assertions.assertThat(new SotningsobjektEntity()).hasAllNullFieldsOrProperties();
	}
}
