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
import se.sundsvall.rtjmanagement.types.egensotning.details.api.model.DocumentValidationResult;
import se.sundsvall.rtjmanagement.types.egensotning.details.service.EgensotningDocumentValidationService;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class EgensotningDocumentValidationResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = randomUUID().toString();
	private static final String PATH = "/{municipalityId}/{namespace}/errands/{errandId}/egensotning-details/validate-documents";

	@MockitoBean
	private EgensotningDocumentValidationService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void validateReturnsResult() {
		when(serviceMock.validateDocuments(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID))
			.thenReturn(DocumentValidationResult.create().withValid(true).withDocumentTypeOk(true).withIdentityMatch(true).withReason("OK"));

		final var response = webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(DocumentValidationResult.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getValid()).isTrue();
		verify(serviceMock).validateDocuments(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);
	}
}
