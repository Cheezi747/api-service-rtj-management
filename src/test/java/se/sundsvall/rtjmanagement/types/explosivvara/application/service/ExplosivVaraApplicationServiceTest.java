package se.sundsvall.rtjmanagement.types.explosivvara.application.service;

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
import se.sundsvall.rtjmanagement.types.explosivvara.application.api.model.ExplosivApplicantPerson;
import se.sundsvall.rtjmanagement.types.explosivvara.application.api.model.ExplosivVaraApplication;
import se.sundsvall.rtjmanagement.types.explosivvara.details.api.model.ExplosivVaraDetails;
import se.sundsvall.rtjmanagement.types.explosivvara.details.service.ExplosivVaraDetailsService;
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.api.model.ExplosivGoodsProduct;
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.service.ExplosivGoodsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExplosivVaraApplicationServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EXPLOSIV_VARA";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String PROCESS_NAME = "Hantera ansökan om tillstånd för explosiv vara";

	@Mock
	private ErrandService errandServiceMock;
	@Mock
	private ExplosivVaraDetailsService detailsServiceMock;
	@Mock
	private ExplosivGoodsService explosivGoodsServiceMock;
	@Mock
	private StakeholderService stakeholderServiceMock;
	@Mock
	private AttachmentService attachmentServiceMock;

	@InjectMocks
	private ExplosivVaraApplicationService service;

	private static ExplosivVaraApplication sampleApplication() {
		return ExplosivVaraApplication.create()
			.withApplicantEmail("kontakt@foretaget.se")
			.withOrganizationNumber("5560123456")
			.withCompanyName("Sprängbolaget AB")
			.withCompanyAddress("Storgatan 5")
			.withCompanyZipCode("85230")
			.withCompanyCity("Sundsvall")
			.withContactPersonName("Anna Karlsson")
			.withContactPersonEmail("anna@foretaget.se")
			.withContactPersonPhone("+46701234567")
			.withTypAvHantering("STORAGE")
			.withFastighetsbeteckning("Sundsvall Stenstaden 1:23")
			.withProducts(List.of(
				ExplosivGoodsProduct.create().withHazardClass("1.1").withProductName("Dynamit"),
				ExplosivGoodsProduct.create().withHazardClass("1.4").withProductName("Sprängkapslar")))
			.withPersons(List.of(
				ExplosivApplicantPerson.create().withRole("RESPONSIBLE_PERSON").withFirstName("Anna").withPersonnummer("198507231234"),
				ExplosivApplicantPerson.create().withRole("PARTICIPANT").withFirstName("Bertil").withPersonnummer("197003031234")));
	}

	@Test
	void submitCreatesEverythingAndStartsProcessLast() {
		when(errandServiceMock.createErrand(eq(MUNICIPALITY_ID), eq(NAMESPACE), any(Errand.class))).thenReturn(ERRAND_ID);
		final List<MultipartFile> files = List.of(new MockMultipartFile("files", "riskutredning.pdf", "application/pdf", "data".getBytes()));

		final var result = service.submit(MUNICIPALITY_ID, NAMESPACE, sampleApplication(), files);

		assertThat(result).isEqualTo(ERRAND_ID);
		verify(detailsServiceMock).upsert(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), any(ExplosivVaraDetails.class));
		verify(explosivGoodsServiceMock, times(2)).create(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), any(ExplosivGoodsProduct.class));
		verify(attachmentServiceMock).createAttachment(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), any(MultipartFile.class), eq("OTHER"));

		// applicant (1) + contact person (1) + personer (2) = 4 stakeholders
		final var stakeholderCaptor = ArgumentCaptor.forClass(Stakeholder.class);
		verify(stakeholderServiceMock, times(4)).create(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), stakeholderCaptor.capture());
		final var roles = stakeholderCaptor.getAllValues().stream().map(Stakeholder::getRole).toList();
		assertThat(roles).containsExactlyInAnyOrder("APPLICANT", "CONTACT_PERSON", "RESPONSIBLE_PERSON", "PARTICIPANT");
		final var applicant = stakeholderCaptor.getAllValues().stream().filter(s -> "APPLICANT".equals(s.getRole())).findFirst().orElseThrow();
		assertThat(applicant.getExternalId()).isEqualTo("5560123456");
		assertThat(applicant.getExternalIdType()).isEqualTo("ORGANIZATION");
		assertThat(applicant.getOrganizationName()).isEqualTo("Sprängbolaget AB");
		// Each person maps to a stakeholder carrying its own role (externalIdType PERSON)
		final var participant = stakeholderCaptor.getAllValues().stream().filter(s -> "PARTICIPANT".equals(s.getRole())).findFirst().orElseThrow();
		assertThat(participant.getExternalIdType()).isEqualTo("PERSON");
		assertThat(participant.getFirstName()).isEqualTo("Bertil");
		assertThat(participant.getExternalId()).isEqualTo("197003031234");

		// Process must start LAST
		final var ordered = inOrder(errandServiceMock, attachmentServiceMock);
		ordered.verify(errandServiceMock).createErrand(any(), any(), any());
		ordered.verify(attachmentServiceMock).createAttachment(any(), any(), any(), any(), any());
		ordered.verify(errandServiceMock).startProcess(any(), any(), any(), any(), any());

		verify(errandServiceMock).startProcess(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PROCESS_NAME, null);
	}

	@Test
	void submitWithNullFilesAndNoContactPersonDoesNotCreateContactStakeholder() {
		final var application = ExplosivVaraApplication.create()
			.withApplicantEmail("kontakt@foretaget.se")
			.withOrganizationNumber("5560123456")
			.withCompanyName("Sprängbolaget AB")
			.withFastighetsbeteckning("Fast 1:1")
			.withProducts(List.of(ExplosivGoodsProduct.create().withHazardClass("1.1").withProductName("Dynamit")))
			.withPersons(List.of(ExplosivApplicantPerson.create().withRole("RESPONSIBLE_PERSON").withFirstName("Anna")));
		when(errandServiceMock.createErrand(any(), any(), any())).thenReturn(ERRAND_ID);

		final var result = service.submit(MUNICIPALITY_ID, NAMESPACE, application, null);

		assertThat(result).isEqualTo(ERRAND_ID);
		// applicant (1) + person (1) = 2 stakeholders; no contact person
		final var stakeholderCaptor = ArgumentCaptor.forClass(Stakeholder.class);
		verify(stakeholderServiceMock, times(2)).create(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), stakeholderCaptor.capture());
		assertThat(stakeholderCaptor.getAllValues().stream().map(Stakeholder::getRole).toList())
			.containsExactlyInAnyOrder("APPLICANT", "RESPONSIBLE_PERSON");
		verify(errandServiceMock).startProcess(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PROCESS_NAME, null);
	}
}
