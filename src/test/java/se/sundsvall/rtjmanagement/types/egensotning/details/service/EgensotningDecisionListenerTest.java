package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import generated.se.sundsvall.templating.RenderRequest;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.rtjmanagement.attachments.service.AttachmentService;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.shared.DecisionRecorded;
import se.sundsvall.rtjmanagement.stakeholders.api.model.Stakeholder;
import se.sundsvall.rtjmanagement.stakeholders.service.StakeholderService;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.EgensotningDetailsRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.templating.TemplatingIntegration;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.templating.TemplatingMapper;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.SotningsobjektRepository;

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
class EgensotningDecisionListenerTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String DECISION_ID = "22222222-2222-2222-2222-222222222222";

	@Mock
	private ErrandRepository errandRepositoryMock;
	@Mock
	private EgensotningDetailsRepository egensotningDetailsRepositoryMock;
	@Mock
	private SotningsobjektRepository sotningsobjektRepositoryMock;
	@Mock
	private StakeholderService stakeholderServiceMock;
	@Mock
	private TemplatingMapper templatingMapperMock;
	@Mock
	private TemplatingIntegration templatingIntegrationMock;
	@Mock
	private AttachmentService attachmentServiceMock;

	@InjectMocks
	private EgensotningDecisionListener listener;

	@Test
	void rendersAndStoresPdfForEgensotningDecision() {
		final var pdf = "the-pdf".getBytes();
		final var request = new RenderRequest();
		final var event = new DecisionRecorded(DECISION_ID, ERRAND_ID, "EGENSOTNING", "APPROVED", "Godkänd.", "operaton", OffsetDateTime.now());
		final var errand = ErrandEntity.create().withId(ERRAND_ID).withMunicipalityId(MUNICIPALITY_ID).withNamespace(NAMESPACE).withErrandNumber("RTJ-2026-001");

		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(errand));
		when(egensotningDetailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.of(EgensotningDetailsEntity.create()));
		when(sotningsobjektRepositoryMock.findByErrandIdOrderByTypAscFabrikatAsc(ERRAND_ID)).thenReturn(List.of());
		when(stakeholderServiceMock.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID)).thenReturn(List.of(
			Stakeholder.create().withRole("BSK").withFirstName("Bertil"),
			Stakeholder.create().withRole("APPLICANT").withFirstName("Test").withLastName("Testsson")));
		when(templatingMapperMock.toRenderRequest(any(), any(), any(), any(), any())).thenReturn(request);
		when(templatingIntegrationMock.renderPdf(MUNICIPALITY_ID, request)).thenReturn(pdf);

		listener.on(event);

		final ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
		verify(attachmentServiceMock).createAttachment(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), eq(pdf), fileNameCaptor.capture(), eq("application/pdf"), eq("DECISION"));
		assertThat(fileNameCaptor.getValue()).isEqualTo("beslut-egensotning-RTJ-2026-001.pdf");
	}

	@Test
	void fileNameFallsBackToDecisionIdWhenNoErrandNumber() {
		final var pdf = "the-pdf".getBytes();
		final var request = new RenderRequest();
		final var event = new DecisionRecorded(DECISION_ID, ERRAND_ID, "EGENSOTNING", "REJECTED", "Avslag.", "bsk01", OffsetDateTime.now());
		final var errand = ErrandEntity.create().withId(ERRAND_ID).withMunicipalityId(MUNICIPALITY_ID).withNamespace(NAMESPACE);

		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(errand));
		when(egensotningDetailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.empty());
		when(sotningsobjektRepositoryMock.findByErrandIdOrderByTypAscFabrikatAsc(ERRAND_ID)).thenReturn(List.of());
		when(stakeholderServiceMock.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID)).thenReturn(List.of());
		when(templatingMapperMock.toRenderRequest(any(), any(), any(), any(), any())).thenReturn(request);
		when(templatingIntegrationMock.renderPdf(MUNICIPALITY_ID, request)).thenReturn(pdf);

		listener.on(event);

		verify(attachmentServiceMock).createAttachment(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), eq(pdf), eq("beslut-egensotning-" + DECISION_ID + ".pdf"), eq("application/pdf"), eq("DECISION"));
	}

	@Test
	void ignoresNonEgensotningDecision() {
		final var event = new DecisionRecorded(DECISION_ID, ERRAND_ID, "BRANDFARLIGVARA", "APPROVED", "x", "operaton", OffsetDateTime.now());

		listener.on(event);

		verifyNoInteractions(errandRepositoryMock, egensotningDetailsRepositoryMock, sotningsobjektRepositoryMock,
			stakeholderServiceMock, templatingMapperMock, templatingIntegrationMock, attachmentServiceMock);
	}

	@Test
	void skipsWhenErrandMissing() {
		final var event = new DecisionRecorded(DECISION_ID, ERRAND_ID, "EGENSOTNING", "APPROVED", "x", "operaton", OffsetDateTime.now());
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.empty());

		listener.on(event);

		verifyNoInteractions(templatingIntegrationMock, attachmentServiceMock);
	}

	@Test
	void templatingFailureIsSwallowed() {
		final var event = new DecisionRecorded(DECISION_ID, ERRAND_ID, "EGENSOTNING", "APPROVED", "x", "operaton", OffsetDateTime.now());
		final var errand = ErrandEntity.create().withId(ERRAND_ID).withMunicipalityId(MUNICIPALITY_ID).withNamespace(NAMESPACE).withErrandNumber("RTJ-1");

		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(errand));
		when(egensotningDetailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.empty());
		when(sotningsobjektRepositoryMock.findByErrandIdOrderByTypAscFabrikatAsc(ERRAND_ID)).thenReturn(List.of());
		when(stakeholderServiceMock.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID)).thenReturn(List.of());
		when(templatingMapperMock.toRenderRequest(any(), any(), any(), any(), any())).thenReturn(new RenderRequest());
		when(templatingIntegrationMock.renderPdf(any(), any())).thenThrow(Problem.valueOf(BAD_GATEWAY, "boom"));

		assertThatNoException().isThrownBy(() -> listener.on(event));

		verify(attachmentServiceMock, never()).createAttachment(any(), any(), any(), any(byte[].class), any(), any(), any());
	}
}
