package se.sundsvall.rtjmanagement.remiss.api;

import java.util.List;
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
import se.sundsvall.rtjmanagement.remiss.api.model.RemissResponseRequest;
import se.sundsvall.rtjmanagement.remiss.service.RemissService;

import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class RemissResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "BRANDFARLIG_VARA";
	private static final String ERRAND_ID = randomUUID().toString();
	private static final String REMISS_ID = randomUUID().toString();
	private static final String BASE = "/{municipalityId}/{namespace}/errands/{errandId}/remisser";

	@MockitoBean
	private RemissService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void createRemissCreated() {
		when(serviceMock.create(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), any(Remiss.class))).thenReturn(REMISS_ID);

		webTestClient.post()
			.uri(uri -> uri.path(BASE).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(Remiss.create().withInstans("MILJOKONTOR"))
			.exchange()
			.expectStatus().isCreated();

		verify(serviceMock).create(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), any(Remiss.class));
	}

	@Test
	void readRemisserOk() {
		when(serviceMock.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID)).thenReturn(List.of(Remiss.create().withId(REMISS_ID)));

		webTestClient.get()
			.uri(uri -> uri.path(BASE).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(Remiss.class);

		verify(serviceMock).readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);
	}

	@Test
	void readRemissOk() {
		when(serviceMock.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, REMISS_ID)).thenReturn(Remiss.create().withId(REMISS_ID));

		webTestClient.get()
			.uri(uri -> uri.path(BASE + "/{remissId}").build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID, "remissId", REMISS_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(Remiss.class);

		verify(serviceMock).read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, REMISS_ID);
	}

	@Test
	void registerResponseNoContent() {
		webTestClient.post()
			.uri(uri -> uri.path(BASE + "/{remissId}/response").build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID, "remissId", REMISS_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(new RemissResponseRequest("Inget att invända"))
			.exchange()
			.expectStatus().isNoContent();

		verify(serviceMock).registerResponse(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, REMISS_ID, "Inget att invända");
	}

	@Test
	void deleteRemissNoContent() {
		webTestClient.delete()
			.uri(uri -> uri.path(BASE + "/{remissId}").build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID, "remissId", REMISS_ID)))
			.exchange()
			.expectStatus().isNoContent();

		verify(serviceMock).delete(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, REMISS_ID);
	}
}
