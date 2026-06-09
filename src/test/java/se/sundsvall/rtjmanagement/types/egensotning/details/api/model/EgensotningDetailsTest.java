package se.sundsvall.rtjmanagement.types.egensotning.details.api.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

class EgensotningDetailsTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt(1000)), OffsetDateTime.class);
		registerValueGenerator(() -> LocalDate.now().plusDays(new Random().nextInt(1000)), LocalDate.class);
	}

	@Test
	void testBean() {
		// toString is asserted separately — it deliberately omits personnummer (PII).
		assertThat(EgensotningDetails.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals()));
	}

	@Test
	void testBuilderMethods() {
		final var verifiedAt = OffsetDateTime.now();
		final var validFrom = LocalDate.now();
		final var validUntil = LocalDate.now().plusYears(6);
		final var reminderSentAt = OffsetDateTime.now();
		final var revokedAt = OffsetDateTime.now();
		final var created = OffsetDateTime.now();
		final var modified = OffsetDateTime.now();

		final var result = EgensotningDetails.create()
			.withPersonnummer("199001011234")
			.withFastighetsbeteckning("Sundsvall Stenstaden 1:23")
			.withPropertyAddress("Storgatan 5")
			.withOwnsProperty(true)
			.withOwnershipMotivation("Arrenderar fastigheten")
			.withAppliesForOtherProperty(false)
			.withMotivering("Egen motivering")
			.withBilagaPresent(true)
			.withRegisteredAtProperty(true)
			.withReapplicationOk(false)
			.withLastOutcome("AUTO_APPROVE")
			.withManualReviewReason("NOT_REGISTERED")
			.withSupplementNeeds(List.of("MISSING_BRANDSKYDDSKONTROLL"))
			.withLastVerifiedAt(verifiedAt)
			.withValidFrom(validFrom)
			.withValidUntil(validUntil)
			.withReminderSentAt(reminderSentAt)
			.withRevokedAt(revokedAt)
			.withRevocationReason("ADDRESS_CHANGED")
			.withCreated(created)
			.withModified(modified);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getPersonnummer()).isEqualTo("199001011234");
		assertThat(result.getFastighetsbeteckning()).isEqualTo("Sundsvall Stenstaden 1:23");
		assertThat(result.getPropertyAddress()).isEqualTo("Storgatan 5");
		assertThat(result.getOwnsProperty()).isTrue();
		assertThat(result.getOwnershipMotivation()).isEqualTo("Arrenderar fastigheten");
		assertThat(result.getAppliesForOtherProperty()).isFalse();
		assertThat(result.getMotivering()).isEqualTo("Egen motivering");
		assertThat(result.getBilagaPresent()).isTrue();
		assertThat(result.getRegisteredAtProperty()).isTrue();
		assertThat(result.getReapplicationOk()).isFalse();
		assertThat(result.getLastOutcome()).isEqualTo("AUTO_APPROVE");
		assertThat(result.getManualReviewReason()).isEqualTo("NOT_REGISTERED");
		assertThat(result.getLastVerifiedAt()).isEqualTo(verifiedAt);
		assertThat(result.getValidFrom()).isEqualTo(validFrom);
		assertThat(result.getValidUntil()).isEqualTo(validUntil);
		assertThat(result.getReminderSentAt()).isEqualTo(reminderSentAt);
		assertThat(result.getRevokedAt()).isEqualTo(revokedAt);
		assertThat(result.getRevocationReason()).isEqualTo("ADDRESS_CHANGED");
		assertThat(result.getCreated()).isEqualTo(created);
		assertThat(result.getModified()).isEqualTo(modified);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(EgensotningDetails.create()).hasAllNullFieldsOrProperties();
	}

	@Test
	void toStringOmitsPersonnummer() {
		final var details = EgensotningDetails.create()
			.withPersonnummer("199001011234")
			.withFastighetsbeteckning("Sundsvall Stenstaden 1:23");

		assertThat(details.toString())
			.contains("Sundsvall Stenstaden 1:23")
			.doesNotContain("199001011234");
	}
}
