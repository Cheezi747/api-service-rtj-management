package se.sundsvall.rtjmanagement.conversation.integration.db.model;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageEntityTest {

	@Test
	void builderMethods() {
		final var created = OffsetDateTime.now();

		final var entity = MessageEntity.create()
			.withId("m1")
			.withErrandId("e1")
			.withDirection("OUTBOUND")
			.withBody("body")
			.withAuthor("bsk1")
			.withCreated(created);

		assertThat(entity.getId()).isEqualTo("m1");
		assertThat(entity.getErrandId()).isEqualTo("e1");
		assertThat(entity.getDirection()).isEqualTo("OUTBOUND");
		assertThat(entity.getBody()).isEqualTo("body");
		assertThat(entity.getAuthor()).isEqualTo("bsk1");
		assertThat(entity.getCreated()).isEqualTo(created);
	}

	@Test
	void createReturnsBlankInstance() {
		assertThat(MessageEntity.create()).hasAllNullFieldsOrProperties();
		assertThat(new MessageEntity()).hasAllNullFieldsOrProperties();
	}
}
