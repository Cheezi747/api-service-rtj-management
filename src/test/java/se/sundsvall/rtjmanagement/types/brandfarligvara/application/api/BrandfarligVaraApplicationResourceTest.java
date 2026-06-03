package se.sundsvall.rtjmanagement.types.brandfarligvara.application.api;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.rtjmanagement.Application;
import se.sundsvall.rtjmanagement.types.brandfarligvara.application.api.model.ApplicantResponsiblePerson;
import se.sundsvall.rtjmanagement.types.brandfarligvara.application.api.model.BrandfarligVaraApplication;
import se.sundsvall.rtjmanagement.types.brandfarligvara.application.service.BrandfarligVaraApplicationService;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.api.model.HazardousGoodsProduct;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.web.reactive.function.BodyInserters.fromMultipartData;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class BrandfarligVaraApplicationResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "BRANDFARLIG_VARA";
	private static final String PATH = "/{municipalityId}/{namespace}/brandfarlig-vara/applications";

	@MockitoBean
	private BrandfarligVaraApplicationService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	private static BrandfarligVaraApplication validApplication() {
		return BrandfarligVaraApplication.create()
			.withApplicantEmail("kontakt@foretaget.se")
			.withOrganizationNumber("5560123456")
			.withCompanyName("Restaurang Ankaret AB")
			.withFastighetsbeteckning("Sundsvall Stenstaden 1:23")
			.withProducts(List.of(HazardousGoodsProduct.create().withCategory("GAS").withProductName("Gasol")))
			.withResponsiblePersons(List.of(ApplicantResponsiblePerson.create().withFirstName("Anna")));
	}

	private static MultipartBodyBuilder body(final BrandfarligVaraApplication application, final boolean withFile) {
		final var builder = new MultipartBodyBuilder();
		builder.part("application", application, APPLICATION_JSON);
		if (withFile) {
			builder.part("files", new ByteArrayResource("riskutredning".getBytes())).filename("bilaga.txt");
		}
		return builder;
	}

	@Test
	void submitApplicationCreated() {
		when(serviceMock.submit(eq(MUNICIPALITY_ID), eq(NAMESPACE), any(BrandfarligVaraApplication.class), anyList()))
			.thenReturn("11111111-1111-1111-1111-111111111111");

		webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE)))
			.contentType(MULTIPART_FORM_DATA)
			.body(fromMultipartData(body(validApplication(), true).build()))
			.exchange()
			.expectStatus().isCreated();

		verify(serviceMock).submit(eq(MUNICIPALITY_ID), eq(NAMESPACE), any(BrandfarligVaraApplication.class), anyList());
	}

	@Test
	void submitApplicationWithoutFileIsBadRequest() {
		webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE)))
			.contentType(MULTIPART_FORM_DATA)
			.body(fromMultipartData(body(validApplication(), false).build()))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void submitInvalidApplicationIsBadRequest() {
		// Missing required fields
		// (applicantEmail/organizationNumber/companyName/fastighetsbeteckning/products/responsiblePersons)
		webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE)))
			.contentType(MULTIPART_FORM_DATA)
			.body(fromMultipartData(body(BrandfarligVaraApplication.create(), true).build()))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}
}
