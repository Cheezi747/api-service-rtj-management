package se.sundsvall.rtjmanagement.permit.api;

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
import se.sundsvall.rtjmanagement.permit.api.model.Permit;
import se.sundsvall.rtjmanagement.permit.service.PermitService;

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
class PermitResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "BRANDFARLIG_VARA";
	private static final String ERRAND_ID = randomUUID().toString();
	private static final String PERMIT_ID = randomUUID().toString();
	private static final String BASE = "/{municipalityId}/{namespace}/errands/{errandId}/permits";

	@MockitoBean
	private PermitService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void issuePermitCreated() {
		when(serviceMock.issue(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), any(Permit.class))).thenReturn(PERMIT_ID);

		webTestClient.post()
			.uri(uri -> uri.path(BASE).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(Permit.create().withPermitType("BRANDFARLIG_VARA"))
			.exchange()
			.expectStatus().isCreated();

		verify(serviceMock).issue(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), any(Permit.class));
	}

	@Test
	void readPermitsOk() {
		when(serviceMock.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID)).thenReturn(List.of(Permit.create().withId(PERMIT_ID)));

		webTestClient.get()
			.uri(uri -> uri.path(BASE).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(Permit.class);

		verify(serviceMock).readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);
	}

	@Test
	void readPermitOk() {
		when(serviceMock.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PERMIT_ID)).thenReturn(Permit.create().withId(PERMIT_ID));

		webTestClient.get()
			.uri(uri -> uri.path(BASE + "/{permitId}").build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID, "permitId", PERMIT_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(Permit.class);

		verify(serviceMock).read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PERMIT_ID);
	}

	@Test
	void revokePermitNoContent() {
		webTestClient.post()
			.uri(uri -> uri.path(BASE + "/{permitId}/revoke").build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID, "permitId", PERMIT_ID)))
			.exchange()
			.expectStatus().isNoContent();

		verify(serviceMock).revoke(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PERMIT_ID);
	}

	@Test
	void deletePermitNoContent() {
		webTestClient.delete()
			.uri(uri -> uri.path(BASE + "/{permitId}").build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID, "permitId", PERMIT_ID)))
			.exchange()
			.expectStatus().isNoContent();

		verify(serviceMock).delete(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PERMIT_ID);
	}
}
