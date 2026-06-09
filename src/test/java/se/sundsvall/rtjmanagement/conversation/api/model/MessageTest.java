package se.sundsvall.rtjmanagement.conversation.api.model;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageTest {

	@Test
	void builderMethods() {
		final var created = OffsetDateTime.now();

		final var message = Message.create()
			.withId("m1")
			.withErrandId("e1")
			.withDirection("OUTBOUND")
			.withBody("body")
			.withAuthor("bsk1")
			.withCreated(created);

		assertThat(message.getId()).isEqualTo("m1");
		assertThat(message.getErrandId()).isEqualTo("e1");
		assertThat(message.getDirection()).isEqualTo("OUTBOUND");
		assertThat(message.getBody()).isEqualTo("body");
		assertThat(message.getAuthor()).isEqualTo("bsk1");
		assertThat(message.getCreated()).isEqualTo(created);
	}

	@Test
	void setters() {
		final var created = OffsetDateTime.now();
		final var message = Message.create();
		message.setId("m1");
		message.setErrandId("e1");
		message.setDirection("INBOUND");
		message.setBody("body");
		message.setAuthor("anna");
		message.setCreated(created);

		assertThat(message.getId()).isEqualTo("m1");
		assertThat(message.getErrandId()).isEqualTo("e1");
		assertThat(message.getDirection()).isEqualTo("INBOUND");
		assertThat(message.getBody()).isEqualTo("body");
		assertThat(message.getAuthor()).isEqualTo("anna");
		assertThat(message.getCreated()).isEqualTo(created);
	}

	@Test
	void createReturnsBlankInstance() {
		assertThat(Message.create()).hasAllNullFieldsOrProperties();
	}

	@Test
	void equalsHashCodeAndToString() {
		final var created = OffsetDateTime.parse("2026-06-09T10:00:00Z");
		final var a = Message.create().withId("m1").withErrandId("e1").withDirection("OUTBOUND").withBody("b").withAuthor("x").withCreated(created);
		final var b = Message.create().withId("m1").withErrandId("e1").withDirection("OUTBOUND").withBody("b").withAuthor("x").withCreated(created);

		assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
		assertThat(a).isNotEqualTo(Message.create().withId("other"));
		assertThat(a.toString()).contains("OUTBOUND").contains("e1");
	}
}
