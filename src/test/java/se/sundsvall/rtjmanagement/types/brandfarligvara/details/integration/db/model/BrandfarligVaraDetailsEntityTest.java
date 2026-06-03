package se.sundsvall.rtjmanagement.types.brandfarligvara.details.integration.db.model;

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

class BrandfarligVaraDetailsEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(BrandfarligVaraDetailsEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals()));
	}

	@Test
	void testToString() {
		final var entity = BrandfarligVaraDetailsEntity.create().withId(1L).withErrandId("e1").withVerksamhetstyp("RETAIL");
		org.assertj.core.api.Assertions.assertThat(entity.toString())
			.contains("BrandfarligVaraDetailsEntity{").contains("errandId='e1'").contains("verksamhetstyp='RETAIL'");
	}

	@Test
	void testBuilderMethods() {
		final var created = now();
		final var modified = now();

		final var entity = BrandfarligVaraDetailsEntity.create()
			.withId(1L)
			.withErrandId("errand")
			.withVerksamhetstyp("FUEL_STATION")
			.withAnlaggningTyp("EXISTING")
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
		org.assertj.core.api.Assertions.assertThat(entity.getVerksamhetstyp()).isEqualTo("FUEL_STATION");
		org.assertj.core.api.Assertions.assertThat(entity.getAnlaggningTyp()).isEqualTo("EXISTING");
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
		org.assertj.core.api.Assertions.assertThat(BrandfarligVaraDetailsEntity.create()).hasAllNullFieldsOrPropertiesExcept("isProxy");
		org.assertj.core.api.Assertions.assertThat(new BrandfarligVaraDetailsEntity()).hasAllNullFieldsOrPropertiesExcept("isProxy");
	}
}
