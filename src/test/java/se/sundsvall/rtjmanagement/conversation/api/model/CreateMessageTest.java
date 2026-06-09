package se.sundsvall.rtjmanagement.conversation.api.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CreateMessageTest {

	@Test
	void accessors() {
		final var request = new CreateMessage("OUTBOUND", "Komplettera tack", "bsk1");

		assertThat(request.direction()).isEqualTo("OUTBOUND");
		assertThat(request.body()).isEqualTo("Komplettera tack");
		assertThat(request.author()).isEqualTo("bsk1");
	}
}
