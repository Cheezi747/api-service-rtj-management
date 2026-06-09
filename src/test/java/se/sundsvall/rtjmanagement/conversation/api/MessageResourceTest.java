package se.sundsvall.rtjmanagement.conversation.api;

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
import se.sundsvall.rtjmanagement.conversation.api.model.CreateMessage;
import se.sundsvall.rtjmanagement.conversation.api.model.Message;
import se.sundsvall.rtjmanagement.conversation.service.MessageService;

import static java.util.UUID.randomUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class MessageResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = randomUUID().toString();
	private static final String PATH = "/{municipalityId}/{namespace}/errands/{errandId}/messages";

	@MockitoBean
	private MessageService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void post() {
		when(serviceMock.post(eq(ERRAND_ID), any(CreateMessage.class))).thenReturn("m1");

		webTestClient.post()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.bodyValue(new CreateMessage("OUTBOUND", "Komplettera tack", "bsk1"))
			.exchange()
			.expectStatus().isCreated();

		verify(serviceMock).post(eq(ERRAND_ID), any(CreateMessage.class));
	}

	@Test
	void list() {
		when(serviceMock.listForErrand(ERRAND_ID)).thenReturn(List.of(Message.create().withId("m1").withDirection("OUTBOUND")));

		webTestClient.get()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$[0].direction").isEqualTo("OUTBOUND");
	}

	@Test
	void postInvalidDirectionIsBadRequest() {
		webTestClient.post()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.bodyValue(new CreateMessage("SIDEWAYS", "body", "bsk1"))
			.exchange()
			.expectStatus().isBadRequest();
	}

	@Test
	void postBlankBodyIsBadRequest() {
		webTestClient.post()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.bodyValue(new CreateMessage("OUTBOUND", "", "bsk1"))
			.exchange()
			.expectStatus().isBadRequest();
	}
}
