package se.sundsvall.rtjmanagement.types.brandfarligvara.details.api;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.rtjmanagement.Application;
import se.sundsvall.rtjmanagement.types.brandfarligvara.details.api.model.BrandfarligVaraVerificationResult;
import se.sundsvall.rtjmanagement.types.brandfarligvara.details.service.BrandfarligVaraVerificationService;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class BrandfarligVaraVerificationResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "BRANDFARLIG_VARA";
	private static final String ERRAND_ID = randomUUID().toString();
	private static final String PATH = "/{municipalityId}/{namespace}/errands/{errandId}/brandfarlig-vara-details/verify";

	@MockitoBean
	private BrandfarligVaraVerificationService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void verifyReturnsOutcome() {
		when(serviceMock.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID))
			.thenReturn(BrandfarligVaraVerificationResult.create().withOutcome("NEEDS_MANUAL_REVIEW").withBilagaPresent(true).withProductsPresent(true));

		final var response = webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(BrandfarligVaraVerificationResult.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getOutcome()).isEqualTo("NEEDS_MANUAL_REVIEW");
		verify(serviceMock).verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);
	}
}
