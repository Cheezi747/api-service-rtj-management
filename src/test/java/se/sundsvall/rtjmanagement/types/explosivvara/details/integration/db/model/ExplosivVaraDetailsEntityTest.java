package se.sundsvall.rtjmanagement.types.explosivvara.details.integration.db.model;

import com.google.code.beanmatchers.BeanMatchers;
import java.time.OffsetDateTime;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

class ExplosivVaraDetailsEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(ExplosivVaraDetailsEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals()));
	}

	@Test
	void testToString() {
		final var entity = ExplosivVaraDetailsEntity.create().withId(1L).withErrandId("e1").withTypAvHantering("STORAGE");
		org.assertj.core.api.Assertions.assertThat(entity.toString())
			.contains("ExplosivVaraDetailsEntity{").contains("errandId='e1'").contains("typAvHantering='STORAGE'");
	}

	@Test
	void testBuilderMethods() {
		final var created = now();
		final var modified = now();

		final var entity = ExplosivVaraDetailsEntity.create()
			.withId(1L)
			.withErrandId("errand")
			.withTypAvHantering("USE")
			.withAnlaggningTyp("NEW")
			.withProxy(true)
			.withFastighetsbeteckning("Fast 1:1")
			.withHandlingLocationAddress("Gatan 1")
			.withHandlingLocationZipCode("85230")
			.withHandlingLocationCity("Sundsvall")
			.withCreated(created)
			.withModified(modified);

		assertThat(entity).hasNoNullFieldsOrProperties();
		org.assertj.core.api.Assertions.assertThat(entity.getId()).isEqualTo(1L);
		org.assertj.core.api.Assertions.assertThat(entity.getErrandId()).isEqualTo("errand");
		org.assertj.core.api.Assertions.assertThat(entity.getTypAvHantering()).isEqualTo("USE");
		org.assertj.core.api.Assertions.assertThat(entity.getAnlaggningTyp()).isEqualTo("NEW");
		org.assertj.core.api.Assertions.assertThat(entity.isProxy()).isTrue();
		org.assertj.core.api.Assertions.assertThat(entity.getFastighetsbeteckning()).isEqualTo("Fast 1:1");
		org.assertj.core.api.Assertions.assertThat(entity.getHandlingLocationAddress()).isEqualTo("Gatan 1");
		org.assertj.core.api.Assertions.assertThat(entity.getHandlingLocationZipCode()).isEqualTo("85230");
		org.assertj.core.api.Assertions.assertThat(entity.getHandlingLocationCity()).isEqualTo("Sundsvall");
		org.assertj.core.api.Assertions.assertThat(entity.getCreated()).isEqualTo(created);
		org.assertj.core.api.Assertions.assertThat(entity.getModified()).isEqualTo(modified);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		org.assertj.core.api.Assertions.assertThat(ExplosivVaraDetailsEntity.create()).hasAllNullFieldsOrPropertiesExcept("isProxy");
		org.assertj.core.api.Assertions.assertThat(new ExplosivVaraDetailsEntity()).hasAllNullFieldsOrPropertiesExcept("isProxy");
	}
}
