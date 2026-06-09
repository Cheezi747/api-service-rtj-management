package se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model;

import com.google.code.beanmatchers.BeanMatchers;
import java.time.LocalDate;
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

class EgensotningDetailsEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
		BeanMatchers.registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt(3650)), LocalDate.class);
	}

	@Test
	void testBean() {
		assertThat(EgensotningDetailsEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals()));
	}

	@Test
	void testToStringOmitsPersonnummer() {
		final var entity = EgensotningDetailsEntity.create().withId(1L).withErrandId("e1")
			.withPersonnummer("199001011234").withFastighetsbeteckning("Fast 1:1");

		org.assertj.core.api.Assertions.assertThat(entity.toString())
			.contains("EgensotningDetailsEntity{").contains("errandId='e1'").contains("fastighetsbeteckning='Fast 1:1'")
			.doesNotContain("199001011234");
	}

	@Test
	void testBuilderMethods() {
		final var verifiedAt = now();
		final var validFrom = LocalDate.now();
		final var validUntil = LocalDate.now().plusYears(6);
		final var reminderSentAt = now();
		final var documentValidatedAt = now();
		final var created = now();
		final var modified = now();

		final var entity = EgensotningDetailsEntity.create()
			.withId(1L)
			.withErrandId("errand")
			.withPersonnummer("199001011234")
			.withFastighetsbeteckning("Fast 1:1")
			.withPropertyAddress("Gatan 1")
			.withOwnsProperty(true)
			.withOwnershipMotivation("Arrenderar")
			.withAppliesForOtherProperty(false)
			.withMotivering("Egen motivering")
			.withBilagaPresent(true)
			.withRegisteredAtProperty(false)
			.withReapplicationOk(true)
			.withLastOutcome("NEEDS_SUPPLEMENT")
			.withManualReviewReason("NOT_REGISTERED")
			.withLastVerifiedAt(verifiedAt)
			.withValidFrom(validFrom)
			.withValidUntil(validUntil)
			.withReminderSentAt(reminderSentAt)
			.withDocumentsValid(true)
			.withDocumentValidationDetail("Dokumenten är giltiga")
			.withDocumentValidatedAt(documentValidatedAt)
			.withCreated(created)
			.withModified(modified);

		assertThat(entity).hasNoNullFieldsOrProperties();
		org.assertj.core.api.Assertions.assertThat(entity.getId()).isEqualTo(1L);
		org.assertj.core.api.Assertions.assertThat(entity.getErrandId()).isEqualTo("errand");
		org.assertj.core.api.Assertions.assertThat(entity.getPersonnummer()).isEqualTo("199001011234");
		org.assertj.core.api.Assertions.assertThat(entity.getFastighetsbeteckning()).isEqualTo("Fast 1:1");
		org.assertj.core.api.Assertions.assertThat(entity.getPropertyAddress()).isEqualTo("Gatan 1");
		org.assertj.core.api.Assertions.assertThat(entity.getOwnsProperty()).isTrue();
		org.assertj.core.api.Assertions.assertThat(entity.getOwnershipMotivation()).isEqualTo("Arrenderar");
		org.assertj.core.api.Assertions.assertThat(entity.getAppliesForOtherProperty()).isFalse();
		org.assertj.core.api.Assertions.assertThat(entity.getMotivering()).isEqualTo("Egen motivering");
		org.assertj.core.api.Assertions.assertThat(entity.getBilagaPresent()).isTrue();
		org.assertj.core.api.Assertions.assertThat(entity.getRegisteredAtProperty()).isFalse();
		org.assertj.core.api.Assertions.assertThat(entity.getReapplicationOk()).isTrue();
		org.assertj.core.api.Assertions.assertThat(entity.getLastOutcome()).isEqualTo("NEEDS_SUPPLEMENT");
		org.assertj.core.api.Assertions.assertThat(entity.getManualReviewReason()).isEqualTo("NOT_REGISTERED");
		org.assertj.core.api.Assertions.assertThat(entity.getLastVerifiedAt()).isEqualTo(verifiedAt);
		org.assertj.core.api.Assertions.assertThat(entity.getValidFrom()).isEqualTo(validFrom);
		org.assertj.core.api.Assertions.assertThat(entity.getValidUntil()).isEqualTo(validUntil);
		org.assertj.core.api.Assertions.assertThat(entity.getReminderSentAt()).isEqualTo(reminderSentAt);
		org.assertj.core.api.Assertions.assertThat(entity.getDocumentsValid()).isTrue();
		org.assertj.core.api.Assertions.assertThat(entity.getDocumentValidationDetail()).isEqualTo("Dokumenten är giltiga");
		org.assertj.core.api.Assertions.assertThat(entity.getDocumentValidatedAt()).isEqualTo(documentValidatedAt);
		org.assertj.core.api.Assertions.assertThat(entity.getCreated()).isEqualTo(created);
		org.assertj.core.api.Assertions.assertThat(entity.getModified()).isEqualTo(modified);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		org.assertj.core.api.Assertions.assertThat(EgensotningDetailsEntity.create()).hasAllNullFieldsOrProperties();
		org.assertj.core.api.Assertions.assertThat(new EgensotningDetailsEntity()).hasAllNullFieldsOrProperties();
	}
}
