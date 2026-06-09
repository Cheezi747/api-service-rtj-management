package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import generated.se.sundsvall.messaging.EmailRequest;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.shared.MessagePosted;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.messaging.MessagingClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EgensotningMessageNotificationListenerTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String MESSAGE_ID = "msg-1";
	// Det handläggaren faktiskt skriver lever bara i in-app-tråden — det får ALDRIG dyka upp i mejlet.
	private static final String FREE_TEXT_THAT_MUST_NOT_LEAK = "Vänligen komplettera med utbildningsintyg för panna X.";

	@Mock
	private ErrandRepository errandRepositoryMock;
	@Mock
	private MessagingClient messagingClientMock;

	@InjectMocks
	private EgensotningMessageNotificationListener listener;

	private static MessagePosted event(final String direction) {
		return new MessagePosted(MESSAGE_ID, ERRAND_ID, direction, "bsk01", OffsetDateTime.now());
	}

	private static ErrandEntity errand(final String typeSlug, final String email) {
		return ErrandEntity.create().withId(ERRAND_ID).withMunicipalityId(MUNICIPALITY_ID).withNamespace(NAMESPACE)
			.withTypeSlug(typeSlug).withApplicantEmail(email);
	}

	@Test
	void sendsContentFreeNotificationOnOutboundEgensotningMessage() {
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(errand("EGENSOTNING", "sokande@example.com")));

		listener.on(event("OUTBOUND"));

		final var captor = ArgumentCaptor.forClass(EmailRequest.class);
		verify(messagingClientMock).sendEmail(eq(MUNICIPALITY_ID), captor.capture());
		final var sent = captor.getValue();
		assertThat(sent.getEmailAddress()).isEqualTo("sokande@example.com");
		assertThat(sent.getSubject()).contains("nytt meddelande");
		assertThat(sent.getMessage()).contains("Logga in på Mina sidor");
		// Kärnan i R1: e-posten aviserar ATT ett meddelande finns, aldrig innehållet.
		assertThat(sent.getMessage()).doesNotContain(FREE_TEXT_THAT_MUST_NOT_LEAK);
		assertThat(sent.getAttachments()).isNullOrEmpty();
		// Enhetlig avsändare med BPMN-flödets mejl.
		assertThat(sent.getSender()).isNotNull();
		assertThat(sent.getSender().getName()).isEqualTo("Räddningstjänsten Medelpad");
		assertThat(sent.getSender().getAddress()).isEqualTo("noreply@sundsvall.se");
	}

	@Test
	void ignoresInboundMessage() {
		// Sökandens egna svar mejlar vi inte tillbaka — handläggaren ser dem i admin-vyns notiser.
		listener.on(event("INBOUND"));

		verifyNoInteractions(errandRepositoryMock, messagingClientMock);
	}

	@Test
	void ignoresNonEgensotningErrand() {
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(errand("BRANDFARLIGVARA", "sokande@example.com")));

		listener.on(event("OUTBOUND"));

		verify(messagingClientMock, never()).sendEmail(any(), any());
	}

	@Test
	void skipsWhenErrandMissing() {
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.empty());

		listener.on(event("OUTBOUND"));

		verify(messagingClientMock, never()).sendEmail(any(), any());
	}

	@Test
	void skipsWhenApplicantEmailMissing() {
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(errand("EGENSOTNING", null)));

		listener.on(event("OUTBOUND"));

		verify(messagingClientMock, never()).sendEmail(any(), any());
	}

	@Test
	void messagingFailureIsSwallowed() {
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(errand("EGENSOTNING", "sokande@example.com")));
		when(messagingClientMock.sendEmail(eq(MUNICIPALITY_ID), any())).thenThrow(new RuntimeException("messaging down"));

		assertThatNoException().isThrownBy(() -> listener.on(event("OUTBOUND")));
	}
}
