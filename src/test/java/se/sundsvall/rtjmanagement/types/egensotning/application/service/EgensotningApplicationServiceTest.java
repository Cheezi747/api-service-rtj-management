package se.sundsvall.rtjmanagement.types.egensotning.application.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.rtjmanagement.attachments.service.AttachmentService;
import se.sundsvall.rtjmanagement.core.api.model.Errand;
import se.sundsvall.rtjmanagement.core.service.ErrandService;
import se.sundsvall.rtjmanagement.stakeholders.api.model.Stakeholder;
import se.sundsvall.rtjmanagement.stakeholders.service.StakeholderService;
import se.sundsvall.rtjmanagement.types.egensotning.application.api.model.EgensotningApplication;
import se.sundsvall.rtjmanagement.types.egensotning.details.api.model.EgensotningDetails;
import se.sundsvall.rtjmanagement.types.egensotning.details.service.EgensotningDetailsService;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.api.model.Sotningsobjekt;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.service.SotningsobjektService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EgensotningApplicationServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String PROCESS_NAME = "Hantera ansökan om egensotning";

	@Mock
	private ErrandService errandServiceMock;
	@Mock
	private EgensotningDetailsService detailsServiceMock;
	@Mock
	private SotningsobjektService sotningsobjektServiceMock;
	@Mock
	private StakeholderService stakeholderServiceMock;
	@Mock
	private AttachmentService attachmentServiceMock;

	@InjectMocks
	private EgensotningApplicationService service;

	private static MultipartFile protokoll() {
		return new MockMultipartFile("brandskyddskontroll", "protokoll.pdf", "application/pdf", "data".getBytes());
	}

	private static MultipartFile intyg() {
		return new MockMultipartFile("utbildningsintyg", "intyg.pdf", "application/pdf", "data".getBytes());
	}

	private static EgensotningApplication sampleApplication() {
		final var application = EgensotningApplication.create();
		application.setApplicantEmail("anna@example.se");
		application.setPersonnummer("198507231234");
		application.setFastighetsbeteckning("Sundsvall Stenstaden 1:23");
		application.setApplicantFirstName("Anna");
		application.setApplicantLastName("Karlsson");
		application.setApplicantPhone("+46701234567");
		application.setSotningsobjekt(List.of(
			Sotningsobjekt.create().withTyp("Värmepanna"),
			Sotningsobjekt.create().withTyp("Vedspis")));
		return application;
	}

	@Test
	void submitCreatesEverythingAndStartsProcessLast() {
		when(errandServiceMock.createErrand(eq(MUNICIPALITY_ID), eq(NAMESPACE), any(Errand.class))).thenReturn(ERRAND_ID);

		final var result = service.submit(MUNICIPALITY_ID, NAMESPACE, sampleApplication(), protokoll(), intyg());

		assertThat(result).isEqualTo(ERRAND_ID);
		verify(detailsServiceMock).upsert(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), any(EgensotningDetails.class));
		verify(sotningsobjektServiceMock, times(2)).create(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), any(Sotningsobjekt.class));
		// Each bilaga stored with its specific category
		verify(attachmentServiceMock).createAttachment(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), any(MultipartFile.class), eq("BRANDSKYDDSKONTROLL"));
		verify(attachmentServiceMock).createAttachment(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), any(MultipartFile.class), eq("UTBILDNINGSINTYG"));

		final var stakeholderCaptor = ArgumentCaptor.forClass(Stakeholder.class);
		verify(stakeholderServiceMock).create(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), stakeholderCaptor.capture());
		assertThat(stakeholderCaptor.getValue().getRole()).isEqualTo("APPLICANT");
		assertThat(stakeholderCaptor.getValue().getExternalId()).isEqualTo("198507231234");
		assertThat(stakeholderCaptor.getValue().getContactChannels()).hasSize(2);

		// Process must start LAST, after the errand + attachments are persisted
		final var ordered = inOrder(errandServiceMock, attachmentServiceMock);
		ordered.verify(errandServiceMock).createErrand(any(), any(), any());
		ordered.verify(attachmentServiceMock, times(2)).createAttachment(any(), any(), any(), any(), any());
		ordered.verify(errandServiceMock).startProcess(any(), any(), any(), any(), any());

		verify(errandServiceMock).startProcess(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PROCESS_NAME, null);
	}

	@Test
	void submitWithNullObjektDoesNotFail() {
		final var application = EgensotningApplication.create();
		application.setApplicantEmail("a@b.c");
		application.setPersonnummer("198501010000");
		application.setFastighetsbeteckning("Fast 1:1");
		when(errandServiceMock.createErrand(any(), any(), any())).thenReturn(ERRAND_ID);

		final var result = service.submit(MUNICIPALITY_ID, NAMESPACE, application, protokoll(), intyg());

		assertThat(result).isEqualTo(ERRAND_ID);
		verify(errandServiceMock).startProcess(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PROCESS_NAME, null);
	}
}
