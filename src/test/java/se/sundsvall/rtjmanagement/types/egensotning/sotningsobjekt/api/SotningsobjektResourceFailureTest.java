package se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.api;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.rtjmanagement.Application;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.api.model.Sotningsobjekt;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.service.SotningsobjektService;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class SotningsobjektResourceFailureTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = randomUUID().toString();
	private static final String PATH = "/{municipalityId}/{namespace}/errands/{errandId}/sotningsobjekt";

	@MockitoBean
	private SotningsobjektService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void createSotningsobjekt_badMunicipalityId() {
		webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", "abc", "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.bodyValue(Sotningsobjekt.create().withTyp("Värmepanna"))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void createSotningsobjekt_badNamespace() {
		webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", "bad namespace", "errandId", ERRAND_ID)))
			.bodyValue(Sotningsobjekt.create().withTyp("Värmepanna"))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void createSotningsobjekt_badErrandIdUuid() {
		webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", "not-a-uuid")))
			.bodyValue(Sotningsobjekt.create().withTyp("Värmepanna"))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void createSotningsobjekt_blankTyp() {
		webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.bodyValue(Sotningsobjekt.create().withFabrikat("CTC"))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void readSotningsobjektById_badObjektIdUuid() {
		webTestClient.get()
			.uri(uri -> uri.path(PATH + "/{objektId}").build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID, "objektId", "not-a-uuid")))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}
}
