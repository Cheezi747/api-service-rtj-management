package se.sundsvall.rtjmanagement.types.explosivvara.details.api;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.rtjmanagement.Application;
import se.sundsvall.rtjmanagement.types.explosivvara.details.api.model.ExplosivVaraDetails;
import se.sundsvall.rtjmanagement.types.explosivvara.details.service.ExplosivVaraDetailsService;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class ExplosivVaraDetailsResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "explosiv-vara";
	private static final String ERRAND_ID = randomUUID().toString();
	private static final String PATH = "/{municipalityId}/{namespace}/errands/{errandId}/explosiv-vara-details";

	@MockitoBean
	private ExplosivVaraDetailsService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void upsertDetails() {
		webTestClient.put()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.bodyValue(ExplosivVaraDetails.create().withTypAvHantering("STORAGE").withProxy(true))
			.exchange()
			.expectStatus().isNoContent();

		verify(serviceMock).upsert(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), any(ExplosivVaraDetails.class));
	}

	@Test
	void readDetails() {
		when(serviceMock.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID))
			.thenReturn(ExplosivVaraDetails.create().withTypAvHantering("TRADE"));

		final var response = webTestClient.get()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(ExplosivVaraDetails.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTypAvHantering()).isEqualTo("TRADE");
		verify(serviceMock).read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);
	}

	@Test
	void deleteDetails() {
		webTestClient.delete()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.exchange()
			.expectStatus().isNoContent();

		verify(serviceMock).delete(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);
	}
}
