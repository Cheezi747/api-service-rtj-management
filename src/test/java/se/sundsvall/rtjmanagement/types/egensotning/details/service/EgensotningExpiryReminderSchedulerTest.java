package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import generated.se.sundsvall.messaging.EmailRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.EgensotningDetailsRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.messaging.MessagingClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EgensotningExpiryReminderSchedulerTest {

	private static final long LEAD_TIME_DAYS = 90;
	private static final String MUNICIPALITY_ID = "2281";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";

	@Mock
	private EgensotningDetailsRepository egensotningDetailsRepositoryMock;
	@Mock
	private ErrandRepository errandRepositoryMock;
	@Mock
	private MessagingClient messagingClientMock;

	private EgensotningExpiryReminderScheduler scheduler;

	@BeforeEach
	void setUp() {
		scheduler = new EgensotningExpiryReminderScheduler(egensotningDetailsRepositoryMock, errandRepositoryMock, messagingClientMock, LEAD_TIME_DAYS);
	}

	@Test
	void sendsReminderAndStampsReminderSentAt() {
		final var details = EgensotningDetailsEntity.create().withId(1L).withErrandId(ERRAND_ID)
			.withFastighetsbeteckning("Sundsvall Stenstaden 1:23").withValidUntil(LocalDate.now().plusDays(30));
		final var errand = ErrandEntity.create().withId(ERRAND_ID).withMunicipalityId(MUNICIPALITY_ID).withApplicantEmail("agaren@example.com");

		when(egensotningDetailsRepositoryMock.findByValidUntilBetweenAndReminderSentAtIsNull(any(), any())).thenReturn(List.of(details));
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(errand));

		scheduler.sendExpiryReminders();

		final var emailCaptor = ArgumentCaptor.forClass(EmailRequest.class);
		verify(messagingClientMock).sendEmail(eq(MUNICIPALITY_ID), emailCaptor.capture());
		assertThat(emailCaptor.getValue().getEmailAddress()).isEqualTo("agaren@example.com");
		assertThat(emailCaptor.getValue().getSubject()).contains("egensotning");
		assertThat(emailCaptor.getValue().getMessage()).contains("Sundsvall Stenstaden 1:23");
		verify(egensotningDetailsRepositoryMock).save(details);
		assertThat(details.getReminderSentAt()).isNotNull();
	}

	@Test
	void queriesWindowFromTodayToTodayPlusLeadTime() {
		when(egensotningDetailsRepositoryMock.findByValidUntilBetweenAndReminderSentAtIsNull(any(), any())).thenReturn(List.of());

		scheduler.sendExpiryReminders();

		final var fromCaptor = ArgumentCaptor.forClass(LocalDate.class);
		final var toCaptor = ArgumentCaptor.forClass(LocalDate.class);
		verify(egensotningDetailsRepositoryMock).findByValidUntilBetweenAndReminderSentAtIsNull(fromCaptor.capture(), toCaptor.capture());
		assertThat(fromCaptor.getValue()).isEqualTo(LocalDate.now());
		assertThat(toCaptor.getValue()).isEqualTo(LocalDate.now().plusDays(LEAD_TIME_DAYS));
	}

	@Test
	void skipsRowWithoutApplicantEmailButStillProcessesOthers() {
		final var noEmail = EgensotningDetailsEntity.create().withId(1L).withErrandId("errand-no-email")
			.withFastighetsbeteckning("Fast 1:1").withValidUntil(LocalDate.now().plusDays(10));
		final var withEmail = EgensotningDetailsEntity.create().withId(2L).withErrandId(ERRAND_ID)
			.withFastighetsbeteckning("Fast 2:2").withValidUntil(LocalDate.now().plusDays(20));
		final var errandNoEmail = ErrandEntity.create().withId("errand-no-email").withMunicipalityId(MUNICIPALITY_ID);
		final var errandWithEmail = ErrandEntity.create().withId(ERRAND_ID).withMunicipalityId(MUNICIPALITY_ID).withApplicantEmail("agaren@example.com");

		when(egensotningDetailsRepositoryMock.findByValidUntilBetweenAndReminderSentAtIsNull(any(), any())).thenReturn(List.of(noEmail, withEmail));
		when(errandRepositoryMock.findById("errand-no-email")).thenReturn(Optional.of(errandNoEmail));
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(errandWithEmail));

		scheduler.sendExpiryReminders();

		verify(messagingClientMock).sendEmail(eq(MUNICIPALITY_ID), any());
		verify(egensotningDetailsRepositoryMock).save(withEmail);
		verify(egensotningDetailsRepositoryMock, never()).save(noEmail);
		assertThat(noEmail.getReminderSentAt()).isNull();
	}

	@Test
	void sendFailureDoesNotAbortBatchAndDoesNotStamp() {
		final var first = EgensotningDetailsEntity.create().withId(1L).withErrandId("errand-1")
			.withFastighetsbeteckning("Fast 1:1").withValidUntil(LocalDate.now().plusDays(10));
		final var second = EgensotningDetailsEntity.create().withId(2L).withErrandId("errand-2")
			.withFastighetsbeteckning("Fast 2:2").withValidUntil(LocalDate.now().plusDays(20));
		final var errand1 = ErrandEntity.create().withId("errand-1").withMunicipalityId(MUNICIPALITY_ID).withApplicantEmail("a@example.com");
		final var errand2 = ErrandEntity.create().withId("errand-2").withMunicipalityId(MUNICIPALITY_ID).withApplicantEmail("b@example.com");

		when(egensotningDetailsRepositoryMock.findByValidUntilBetweenAndReminderSentAtIsNull(any(), any())).thenReturn(List.of(first, second));
		when(errandRepositoryMock.findById("errand-1")).thenReturn(Optional.of(errand1));
		when(errandRepositoryMock.findById("errand-2")).thenReturn(Optional.of(errand2));
		when(messagingClientMock.sendEmail(eq(MUNICIPALITY_ID), any()))
			.thenThrow(new RuntimeException("messaging down"))
			.thenReturn(null);

		scheduler.sendExpiryReminders();

		// Första misslyckas (ingen stämpel), andra lyckas (stämpel) — batchen avbryts inte.
		verify(messagingClientMock, times(2)).sendEmail(eq(MUNICIPALITY_ID), any());
		verify(egensotningDetailsRepositoryMock, never()).save(first);
		verify(egensotningDetailsRepositoryMock).save(second);
		assertThat(first.getReminderSentAt()).isNull();
		assertThat(second.getReminderSentAt()).isNotNull();
	}

	@Test
	void noDueDecisionsSendsNothing() {
		when(egensotningDetailsRepositoryMock.findByValidUntilBetweenAndReminderSentAtIsNull(any(), any())).thenReturn(List.of());

		scheduler.sendExpiryReminders();

		verifyNoInteractions(messagingClientMock, errandRepositoryMock);
	}
}
