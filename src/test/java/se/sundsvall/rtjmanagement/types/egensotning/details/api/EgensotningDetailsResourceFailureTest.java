package se.sundsvall.rtjmanagement.types.egensotning.details.api;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.rtjmanagement.Application;
import se.sundsvall.rtjmanagement.types.egensotning.details.api.model.EgensotningDetails;
import se.sundsvall.rtjmanagement.types.egensotning.details.service.EgensotningDetailsService;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class EgensotningDetailsResourceFailureTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = randomUUID().toString();
	private static final String PATH = "/{municipalityId}/{namespace}/errands/{errandId}/egensotning-details";

	@MockitoBean
	private EgensotningDetailsService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void upsertDetails_badMunicipalityId() {
		webTestClient.put()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", "abc", "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.bodyValue(EgensotningDetails.create().withFastighetsbeteckning("Fast 1:1"))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void upsertDetails_badNamespace() {
		webTestClient.put()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", "bad namespace", "errandId", ERRAND_ID)))
			.bodyValue(EgensotningDetails.create().withFastighetsbeteckning("Fast 1:1"))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void upsertDetails_badErrandIdUuid() {
		webTestClient.put()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", "not-a-uuid")))
			.bodyValue(EgensotningDetails.create().withFastighetsbeteckning("Fast 1:1"))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void upsertDetails_tooLongPersonnummer() {
		webTestClient.put()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.bodyValue(EgensotningDetails.create().withPersonnummer("1234567890123456789"))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void readDetails_badErrandIdUuid() {
		webTestClient.get()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", "not-a-uuid")))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}
}
