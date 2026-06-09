package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import generated.eneo.AskResponse;
import generated.eneo.FilePublic;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.sql.rowset.serial.SerialBlob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.attachments.integration.db.AttachmentRepository;
import se.sundsvall.rtjmanagement.attachments.integration.db.model.AttachmentDataEntity;
import se.sundsvall.rtjmanagement.attachments.integration.db.model.AttachmentEntity;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.stakeholders.api.model.Stakeholder;
import se.sundsvall.rtjmanagement.stakeholders.service.StakeholderService;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.EgensotningDetailsRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.eneo.EneoIntegration;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.eneo.EneoProperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.CATEGORY_BRANDSKYDDSKONTROLL;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.CATEGORY_UTBILDNINGSINTYG;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.ROLE_APPLICANT;

@ExtendWith(MockitoExtension.class)
class EgensotningDocumentValidationServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String PNR = "199001011234";
	private static final String FASTIGHET = "Sundsvall Stenstaden 1:23";
	private static final UUID BRAND_ASSISTANT = UUID.fromString("2aaf52d5-cd24-4ac1-af60-8b5bcd664252");
	private static final UUID EGEN_ASSISTANT = UUID.fromString("aeb476c0-01dd-4b2d-a83e-bdca5d911f52");
	private static final UUID SPACE_ID = UUID.fromString("3357cada-c641-4100-b813-af3f943b9de1");
	private static final UUID FILE_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

	private static final String VALID_JSON = "{\"valid\": true, \"documentTypeOk\": true, \"identityMatch\": true, \"reason\": \"OK\"}";

	@Mock
	private ErrandRepository errandRepositoryMock;
	@Mock
	private EgensotningDetailsRepository detailsRepositoryMock;
	@Mock
	private AttachmentRepository attachmentRepositoryMock;
	@Mock
	private StakeholderService stakeholderServiceMock;
	@Mock
	private EneoIntegration eneoIntegrationMock;

	private EgensotningDocumentValidationService service;

	@BeforeEach
	void setUp() {
		final var properties = new EneoProperties("http://eneo.url", "the-api-key",
			new EneoProperties.Oauth2("http://token.url", "id", "secret", "client_credentials"),
			new EneoProperties.Assistants(BRAND_ASSISTANT, EGEN_ASSISTANT), SPACE_ID, 5, 30);
		service = new EgensotningDocumentValidationService(errandRepositoryMock, detailsRepositoryMock,
			attachmentRepositoryMock, stakeholderServiceMock, eneoIntegrationMock, properties);
	}

	private static ErrandEntity egensotningErrand() {
		return ErrandEntity.create().withId(ERRAND_ID).withTypeSlug("EGENSOTNING");
	}

	private static EgensotningDetailsEntity details() {
		return EgensotningDetailsEntity.create().withErrandId(ERRAND_ID).withPersonnummer(PNR)
			.withFastighetsbeteckning(FASTIGHET).withPropertyAddress("Storgatan 1");
	}

	private static AttachmentEntity attachment(final String category) throws SQLException {
		return AttachmentEntity.create().withErrandId(ERRAND_ID).withCategory(category)
			.withFileName(category + ".pdf").withMimeType("application/pdf")
			.withAttachmentData(AttachmentDataEntity.create().withFile(new SerialBlob("pdf-bytes".getBytes())));
	}

	private void stubErrandDetailsAndAttachments() throws SQLException {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(egensotningErrand()));
		when(detailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.of(details()));
		when(attachmentRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(List.of(attachment(CATEGORY_BRANDSKYDDSKONTROLL), attachment(CATEGORY_UTBILDNINGSINTYG)));
	}

	private void stubApplicant() {
		when(stakeholderServiceMock.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID))
			.thenReturn(List.of(Stakeholder.create().withRole(ROLE_APPLICANT).withFirstName("Anna").withLastName("Andersson")));
	}

	private void stubUpload() {
		when(eneoIntegrationMock.uploadFile(any(MultipartFile.class))).thenReturn(new FilePublic().id(FILE_ID));
	}

	private void stubAssistant(final UUID assistantId, final String answer) {
		when(eneoIntegrationMock.askAssistant(eq(assistantId), any())).thenReturn(new AskResponse().answer(answer));
	}

	@Test
	void bothDocumentsValidAutoApprovesAndPersists() throws SQLException {
		stubErrandDetailsAndAttachments();
		stubApplicant();
		stubUpload();
		stubAssistant(BRAND_ASSISTANT, VALID_JSON);
		stubAssistant(EGEN_ASSISTANT, VALID_JSON);

		final var result = service.validateDocuments(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getValid()).isTrue();
		assertThat(result.getDocumentTypeOk()).isTrue();
		assertThat(result.getIdentityMatch()).isTrue();
		// each PDF goes to its own assistant — two uploads, two asks, two deletes
		verify(eneoIntegrationMock, times(2)).uploadFile(any(MultipartFile.class));
		verify(eneoIntegrationMock).askAssistant(eq(BRAND_ASSISTANT), any());
		verify(eneoIntegrationMock).askAssistant(eq(EGEN_ASSISTANT), any());
		verify(eneoIntegrationMock, times(2)).deleteFile(FILE_ID);
		verify(detailsRepositoryMock).save(any(EgensotningDetailsEntity.class));
	}

	@Test
	void oneDocumentInvalidMakesOverallInvalid() throws SQLException {
		stubErrandDetailsAndAttachments();
		stubApplicant();
		stubUpload();
		stubAssistant(BRAND_ASSISTANT, VALID_JSON);
		stubAssistant(EGEN_ASSISTANT, "{\"valid\": false, \"documentTypeOk\": true, \"identityMatch\": false, \"reason\": \"Namn matchar inte\"}");

		final var result = service.validateDocuments(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getValid()).isFalse();
		assertThat(result.getIdentityMatch()).isFalse();
		assertThat(result.getReason()).contains("Namn matchar inte");
	}

	@Test
	void missingOneBilagaIsNotValidAndSkipsEneo() throws SQLException {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(egensotningErrand()));
		when(detailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.of(details()));
		when(attachmentRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(List.of(attachment(CATEGORY_BRANDSKYDDSKONTROLL)));

		final var result = service.validateDocuments(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getValid()).isFalse();
		assertThat(result.getReason()).contains("bilagorna");
		verifyNoInteractions(eneoIntegrationMock);
		verify(detailsRepositoryMock).save(any(EgensotningDetailsEntity.class));
	}

	@Test
	void eneoUnavailableRoutesToManualReview() throws SQLException {
		stubErrandDetailsAndAttachments();
		stubApplicant();
		when(eneoIntegrationMock.uploadFile(any(MultipartFile.class)))
			.thenThrow(Problem.valueOf(BAD_GATEWAY, "Error uploading file to Eneo"));

		final var result = service.validateDocuments(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getValid()).isFalse();
		assertThat(result.getReason()).contains("Eneo");
	}

	@Test
	void unparseableAnswerRoutesToManualReview() throws SQLException {
		stubErrandDetailsAndAttachments();
		stubApplicant();
		stubUpload();
		stubAssistant(BRAND_ASSISTANT, "Tyvärr, jag kan inte avgöra detta.");
		stubAssistant(EGEN_ASSISTANT, VALID_JSON);

		final var result = service.validateDocuments(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getValid()).isFalse();
		assertThat(result.getReason()).contains("tolkas");
	}

	@Test
	void malformedJsonAnswerRoutesToManualReview() throws SQLException {
		stubErrandDetailsAndAttachments();
		stubApplicant();
		stubUpload();
		stubAssistant(BRAND_ASSISTANT, "{ this is not valid json }");
		stubAssistant(EGEN_ASSISTANT, VALID_JSON);

		final var result = service.validateDocuments(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getValid()).isFalse();
		assertThat(result.getReason()).contains("tolkas");
	}

	@Test
	void errandMissingThrowsNotFound() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.validateDocuments(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
		verifyNoInteractions(eneoIntegrationMock);
	}

	@Test
	void wrongTypeThrowsBadRequest() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(ErrandEntity.create().withId(ERRAND_ID).withTypeSlug("BRANDFARLIG_VARA")));

		assertThatThrownBy(() -> service.validateDocuments(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST);
		verify(eneoIntegrationMock, never()).askAssistant(any(), any());
	}
}
