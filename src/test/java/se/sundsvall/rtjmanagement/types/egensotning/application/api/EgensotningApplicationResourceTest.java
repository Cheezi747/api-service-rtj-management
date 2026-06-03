package se.sundsvall.rtjmanagement.types.egensotning.application.api;

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
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.rtjmanagement.Application;
import se.sundsvall.rtjmanagement.types.egensotning.application.api.model.EgensotningApplication;
import se.sundsvall.rtjmanagement.types.egensotning.application.service.EgensotningApplicationService;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.api.model.Sotningsobjekt;

import static org.mockito.ArgumentMatchers.any;
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
class EgensotningApplicationResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String PATH = "/{municipalityId}/{namespace}/egensotning/applications";

	@MockitoBean
	private EgensotningApplicationService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	private static EgensotningApplication validApplication() {
		final var application = EgensotningApplication.create();
		application.setApplicantEmail("anna@example.se");
		application.setPersonnummer("198507231234");
		application.setFastighetsbeteckning("Sundsvall Stenstaden 1:23");
		application.setSotningsobjekt(List.of(Sotningsobjekt.create().withTyp("Värmepanna")));
		return application;
	}

	private static MultipartBodyBuilder body(final EgensotningApplication application, final boolean withProtokoll, final boolean withIntyg) {
		final var builder = new MultipartBodyBuilder();
		builder.part("application", application, APPLICATION_JSON);
		if (withProtokoll) {
			builder.part("brandskyddskontroll", new ByteArrayResource("brandskyddskontroll".getBytes())).filename("protokoll.pdf");
		}
		if (withIntyg) {
			builder.part("utbildningsintyg", new ByteArrayResource("utbildningsintyg".getBytes())).filename("intyg.pdf");
		}
		return builder;
	}

	@Test
	void submitApplicationCreated() {
		when(serviceMock.submit(eq(MUNICIPALITY_ID), eq(NAMESPACE), any(EgensotningApplication.class), any(MultipartFile.class), any(MultipartFile.class)))
			.thenReturn("11111111-1111-1111-1111-111111111111");

		webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE)))
			.contentType(MULTIPART_FORM_DATA)
			.body(fromMultipartData(body(validApplication(), true, true).build()))
			.exchange()
			.expectStatus().isCreated();

		verify(serviceMock).submit(eq(MUNICIPALITY_ID), eq(NAMESPACE), any(EgensotningApplication.class), any(MultipartFile.class), any(MultipartFile.class));
	}

	@Test
	void submitApplicationMissingBrandskyddskontrollIsBadRequest() {
		webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE)))
			.contentType(MULTIPART_FORM_DATA)
			.body(fromMultipartData(body(validApplication(), false, true).build()))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void submitApplicationMissingUtbildningsintygIsBadRequest() {
		webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE)))
			.contentType(MULTIPART_FORM_DATA)
			.body(fromMultipartData(body(validApplication(), true, false).build()))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void submitApplicationMissingBothBilagorIsBadRequest() {
		webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE)))
			.contentType(MULTIPART_FORM_DATA)
			.body(fromMultipartData(body(validApplication(), false, false).build()))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void submitInvalidApplicationIsBadRequest() {
		// Missing required fields (applicantEmail/personnummer/fastighetsbeteckning/sotningsobjekt)
		webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE)))
			.contentType(MULTIPART_FORM_DATA)
			.body(fromMultipartData(body(EgensotningApplication.create(), true, true).build()))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void submitApplicationWithInvalidPersonnummerIsBadRequest() {
		final var application = validApplication();
		application.setPersonnummer("123"); // ogiltigt personnummer-format

		webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE)))
			.contentType(MULTIPART_FORM_DATA)
			.body(fromMultipartData(body(application, true, true).build()))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}
}
