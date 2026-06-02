package se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.api;

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
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.api.model.Sotningsobjekt;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.service.SotningsobjektService;

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
class SotningsobjektResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = randomUUID().toString();
	private static final String OBJEKT_ID = randomUUID().toString();
	private static final String PATH = "/{municipalityId}/{namespace}/errands/{errandId}/sotningsobjekt";

	@MockitoBean
	private SotningsobjektService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void createSotningsobjekt() {
		when(serviceMock.create(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), any(Sotningsobjekt.class))).thenReturn(OBJEKT_ID);

		webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.bodyValue(Sotningsobjekt.create().withTyp("Värmepanna").withSotningsintervallVeckor(8))
			.exchange()
			.expectStatus().isCreated();

		verify(serviceMock).create(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), any(Sotningsobjekt.class));
	}

	@Test
	void readSotningsobjekt() {
		when(serviceMock.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID)).thenReturn(List.of(Sotningsobjekt.create().withTyp("Vedspis")));

		final var response = webTestClient.get()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(Sotningsobjekt.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		verify(serviceMock).readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);
	}

	@Test
	void readSotningsobjektById() {
		when(serviceMock.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, OBJEKT_ID)).thenReturn(Sotningsobjekt.create().withId(OBJEKT_ID));

		final var response = webTestClient.get()
			.uri(uri -> uri.path(PATH + "/{objektId}").build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID, "objektId", OBJEKT_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(Sotningsobjekt.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		verify(serviceMock).read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, OBJEKT_ID);
	}

	@Test
	void updateSotningsobjekt() {
		webTestClient.patch()
			.uri(uri -> uri.path(PATH + "/{objektId}").build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID, "objektId", OBJEKT_ID)))
			.bodyValue(Sotningsobjekt.create().withSotningsintervallVeckor(12))
			.exchange()
			.expectStatus().isNoContent();

		verify(serviceMock).update(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), eq(OBJEKT_ID), any(Sotningsobjekt.class));
	}

	@Test
	void deleteSotningsobjekt() {
		webTestClient.delete()
			.uri(uri -> uri.path(PATH + "/{objektId}").build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID, "objektId", OBJEKT_ID)))
			.exchange()
			.expectStatus().isNoContent();

		verify(serviceMock).delete(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, OBJEKT_ID);
	}
}
