package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.templating.RenderRequest;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.core.service.event.ErrandAssigned;
import se.sundsvall.rtjmanagement.stakeholders.api.model.Stakeholder;
import se.sundsvall.rtjmanagement.stakeholders.service.StakeholderService;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.EgensotningDetailsRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.messaging.MessagingClient;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.templating.MottagningsbevisTemplatingMapper;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.templating.TemplatingIntegration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@ExtendWith(MockitoExtension.class)
class EgensotningMottagningsbevisListenerTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";

	@Mock
	private ErrandRepository errandRepositoryMock;
	@Mock
	private EgensotningDetailsRepository egensotningDetailsRepositoryMock;
	@Mock
	private StakeholderService stakeholderServiceMock;
	@Mock
	private MottagningsbevisTemplatingMapper mottagningsbevisTemplatingMapperMock;
	@Mock
	private TemplatingIntegration templatingIntegrationMock;
	@Mock
	private MessagingClient messagingClientMock;

	@InjectMocks
	private EgensotningMottagningsbevisListener listener;

	private static ErrandAssigned event(final String typeSlug, final String previousAssignee, final String newAssignee) {
		return new ErrandAssigned(ERRAND_ID, typeSlug, MUNICIPALITY_ID, NAMESPACE, previousAssignee, newAssignee, null, OffsetDateTime.now());
	}

	private static ErrandEntity errand(final String status, final String email) {
		return ErrandEntity.create().withId(ERRAND_ID).withMunicipalityId(MUNICIPALITY_ID).withNamespace(NAMESPACE)
			.withErrandNumber("RTJ-2026-001").withStatus(status).withApplicantEmail(email);
	}

	@Test
	void rendersAndEmailsMottagningsbevisOnFirstAssignmentUnderManualReview() {
		final var pdf = "the-pdf".getBytes();
		final var request = new RenderRequest();
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(errand("UNDER_MANUAL_REVIEW", "sokande@example.com")));
		when(egensotningDetailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.of(EgensotningDetailsEntity.create()));
		when(stakeholderServiceMock.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID)).thenReturn(List.of(
			Stakeholder.create().withRole("BSK").withFirstName("Bertil"),
			Stakeholder.create().withRole("APPLICANT").withFirstName("Test").withLastName("Testsson")));
		when(mottagningsbevisTemplatingMapperMock.toRenderRequest(any(), any(), any())).thenReturn(request);
		when(templatingIntegrationMock.renderPdf(MUNICIPALITY_ID, request)).thenReturn(pdf);

		listener.on(event("EGENSOTNING", null, "bsk01"));

		final var emailCaptor = ArgumentCaptor.forClass(EmailRequest.class);
		verify(messagingClientMock).sendEmail(eq(MUNICIPALITY_ID), emailCaptor.capture());
		final var sent = emailCaptor.getValue();
		assertThat(sent.getEmailAddress()).isEqualTo("sokande@example.com");
		assertThat(sent.getSubject()).isEqualTo("Mottagningsbevis – ansökan om egensotning");
		assertThat(sent.getMessage()).contains("handläggningen har påbörjats");
		assertThat(sent.getAttachments()).singleElement().satisfies(attachment -> {
			assertThat(attachment.getName()).isEqualTo("Mottagningsbevis egensotning.pdf");
			assertThat(attachment.getContentType()).isEqualTo("application/pdf");
			assertThat(attachment.getContent()).isEqualTo(Base64.getEncoder().encodeToString(pdf));
		});
	}

	@Test
	void ignoresNonEgensotningAssignment() {
		listener.on(event("BRANDFARLIGVARA", null, "bsk01"));

		verifyNoInteractions(errandRepositoryMock, egensotningDetailsRepositoryMock, stakeholderServiceMock,
			mottagningsbevisTemplatingMapperMock, templatingIntegrationMock, messagingClientMock);
	}

	@Test
	void ignoresReassignment() {
		listener.on(event("EGENSOTNING", "bsk00", "bsk01"));

		verifyNoInteractions(errandRepositoryMock, egensotningDetailsRepositoryMock, stakeholderServiceMock,
			mottagningsbevisTemplatingMapperMock, templatingIntegrationMock, messagingClientMock);
	}

	@Test
	void skipsWhenErrandMissing() {
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.empty());

		listener.on(event("EGENSOTNING", null, "bsk01"));

		verifyNoInteractions(templatingIntegrationMock, messagingClientMock);
	}

	@Test
	void skipsWhenNotUnderManualReview() {
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(errand("REGISTERED", "sokande@example.com")));

		listener.on(event("EGENSOTNING", null, "bsk01"));

		verifyNoInteractions(templatingIntegrationMock, messagingClientMock);
	}

	@Test
	void skipsWhenApplicantEmailMissing() {
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(errand("UNDER_MANUAL_REVIEW", null)));

		listener.on(event("EGENSOTNING", null, "bsk01"));

		verifyNoInteractions(templatingIntegrationMock, messagingClientMock);
	}

	@Test
	void templatingFailureIsSwallowed() {
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(errand("UNDER_MANUAL_REVIEW", "sokande@example.com")));
		when(egensotningDetailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.empty());
		when(stakeholderServiceMock.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID)).thenReturn(List.of());
		when(mottagningsbevisTemplatingMapperMock.toRenderRequest(any(), any(), any())).thenReturn(new RenderRequest());
		when(templatingIntegrationMock.renderPdf(any(), any())).thenThrow(Problem.valueOf(BAD_GATEWAY, "boom"));

		assertThatNoException().isThrownBy(() -> listener.on(event("EGENSOTNING", null, "bsk01")));

		verify(messagingClientMock, never()).sendEmail(any(), any());
	}

	@Test
	void messagingFailureIsSwallowed() {
		final var request = new RenderRequest();
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(errand("UNDER_MANUAL_REVIEW", "sokande@example.com")));
		when(egensotningDetailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.empty());
		when(stakeholderServiceMock.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID)).thenReturn(List.of());
		when(mottagningsbevisTemplatingMapperMock.toRenderRequest(any(), any(), any())).thenReturn(request);
		when(templatingIntegrationMock.renderPdf(MUNICIPALITY_ID, request)).thenReturn("pdf".getBytes());
		when(messagingClientMock.sendEmail(eq(MUNICIPALITY_ID), any())).thenThrow(new RuntimeException("messaging down"));

		assertThatNoException().isThrownBy(() -> listener.on(event("EGENSOTNING", null, "bsk01")));
	}
}
