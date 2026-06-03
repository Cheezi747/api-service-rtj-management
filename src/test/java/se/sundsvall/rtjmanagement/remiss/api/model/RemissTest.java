package se.sundsvall.rtjmanagement.remiss.api.model;

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

class RemissTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt(1000)), OffsetDateTime.class);
		registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt(1000)), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(Remiss.class, allOf(
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

		final var result = Remiss.create()
			.withId("id")
			.withInstans("MILJOKONTOR")
			.withRecipient("Miljökontoret Sundsvall")
			.withSentAt(sentAt)
			.withDueAt(dueAt)
			.withResponseText("svar")
			.withStatus("SENT")
			.withCreated(created)
			.withModified(modified);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo("id");
		assertThat(result.getInstans()).isEqualTo("MILJOKONTOR");
		assertThat(result.getRecipient()).isEqualTo("Miljökontoret Sundsvall");
		assertThat(result.getSentAt()).isEqualTo(sentAt);
		assertThat(result.getDueAt()).isEqualTo(dueAt);
		assertThat(result.getResponseText()).isEqualTo("svar");
		assertThat(result.getStatus()).isEqualTo("SENT");
		assertThat(result.getCreated()).isEqualTo(created);
		assertThat(result.getModified()).isEqualTo(modified);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(Remiss.create()).hasAllNullFieldsOrProperties();
	}
}
