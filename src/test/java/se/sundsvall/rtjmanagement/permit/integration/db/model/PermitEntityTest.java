package se.sundsvall.rtjmanagement.permit.integration.db.model;

import java.time.LocalDate;
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

class PermitEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt(1000)), OffsetDateTime.class);
		registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt(1000)), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(PermitEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var validFrom = LocalDate.of(2026, 6, 3);
		final var validUntil = LocalDate.of(2029, 3, 1);
		final var created = OffsetDateTime.now();
		final var modified = OffsetDateTime.now();

		final var result = PermitEntity.create()
			.withId("id")
			.withErrandId("errand-1")
			.withPermitType("EXPLOSIV_VARA")
			.withValidFrom(validFrom)
			.withValidUntil(validUntil)
			.withConditions("villkor")
			.withStatus("ACTIVE")
			.withCreated(created)
			.withModified(modified);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getErrandId()).isEqualTo("errand-1");
		assertThat(result.getPermitType()).isEqualTo("EXPLOSIV_VARA");
		assertThat(result.getValidFrom()).isEqualTo(validFrom);
		assertThat(result.getValidUntil()).isEqualTo(validUntil);
		assertThat(result.getStatus()).isEqualTo("ACTIVE");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(PermitEntity.create()).hasAllNullFieldsOrProperties();
	}
}
