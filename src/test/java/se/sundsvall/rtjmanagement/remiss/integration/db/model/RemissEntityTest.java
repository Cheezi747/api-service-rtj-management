package se.sundsvall.rtjmanagement.remiss.integration.db.model;

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

class RemissEntityTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt(1000)), OffsetDateTime.class);
		registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt(1000)), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(RemissEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var sentAt = LocalDate.of(2026, 6, 3);
		final var dueAt = LocalDate.of(2026, 7, 1);
		final var created = OffsetDateTime.now();
		final var modified = OffsetDateTime.now();

		final var result = RemissEntity.create()
			.withId("id")
			.withErrandId("errand-1")
			.withInstans("POLIS")
			.withRecipient("Polismyndigheten")
			.withSentAt(sentAt)
			.withDueAt(dueAt)
			.withResponseText("svar")
			.withStatus("SENT")
			.withCreated(created)
			.withModified(modified);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getErrandId()).isEqualTo("errand-1");
		assertThat(result.getInstans()).isEqualTo("POLIS");
		assertThat(result.getRecipient()).isEqualTo("Polismyndigheten");
		assertThat(result.getSentAt()).isEqualTo(sentAt);
		assertThat(result.getDueAt()).isEqualTo(dueAt);
		assertThat(result.getStatus()).isEqualTo("SENT");
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(RemissEntity.create()).hasAllNullFieldsOrProperties();
	}
}
