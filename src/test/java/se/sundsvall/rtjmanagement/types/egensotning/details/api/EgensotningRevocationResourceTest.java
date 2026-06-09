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
import se.sundsvall.rtjmanagement.types.egensotning.details.api.model.RevokeRequest;
import se.sundsvall.rtjmanagement.types.egensotning.details.service.EgensotningRevocationService;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class EgensotningRevocationResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = randomUUID().toString();
	private static final String PATH = "/{municipalityId}/{namespace}/errands/{errandId}/egensotning-details/revoke";

	@MockitoBean
	private EgensotningRevocationService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void revoke() {
		webTestClient.post()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.bodyValue(new RevokeRequest("BSK_FAILED"))
			.exchange()
			.expectStatus().isNoContent();

		verify(serviceMock).revoke(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, "BSK_FAILED");
	}

	@Test
	void revokeBlankReasonIsBadRequest() {
		webTestClient.post()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.bodyValue(new RevokeRequest(" "))
			.exchange()
			.expectStatus().isBadRequest();
	}
}
