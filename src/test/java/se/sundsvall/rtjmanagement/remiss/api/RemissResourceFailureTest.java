package se.sundsvall.rtjmanagement.remiss.api;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.rtjmanagement.Application;
import se.sundsvall.rtjmanagement.remiss.api.model.Remiss;
import se.sundsvall.rtjmanagement.remiss.service.RemissService;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class RemissResourceFailureTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "BRANDFARLIG_VARA";
	private static final String ERRAND_ID = randomUUID().toString();
	private static final String BASE = "/{municipalityId}/{namespace}/errands/{errandId}/remisser";

	@MockitoBean
	private RemissService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	// Path-variable validation is exercised via GET: the POST/create method carries
	// @Validated(OnCreate.class), which switches method-parameter validation to the OnCreate group,
	// so the default-group @ValidMunicipalityId/@Pattern path constraints only fire on the read paths.
	@Test
	void readBadMunicipalityId() {
		webTestClient.get()
			.uri(uri -> uri.path(BASE).build(Map.of("municipalityId", "abc", "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void readBadNamespace() {
		webTestClient.get()
			.uri(uri -> uri.path(BASE).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", "bad namespace", "errandId", ERRAND_ID)))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void createMissingInstansIsBadRequest() {
		webTestClient.post()
			.uri(uri -> uri.path(BASE).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(Remiss.create())
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void createInvalidInstansIsBadRequest() {
		webTestClient.post()
			.uri(uri -> uri.path(BASE).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(Remiss.create().withInstans("NOT_A_VALUE"))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}

	@Test
	void readBadErrandIdUuid() {
		webTestClient.get()
			.uri(uri -> uri.path(BASE).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", "not-a-uuid")))
			.exchange()
			.expectStatus().isBadRequest();

		verifyNoInteractions(serviceMock);
	}
}
