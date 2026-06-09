package se.sundsvall.rtjmanagement.conversation.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.conversation.api.model.CreateMessage;
import se.sundsvall.rtjmanagement.conversation.integration.db.MessageRepository;
import se.sundsvall.rtjmanagement.conversation.integration.db.model.MessageEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";

	@Mock
	private MessageRepository repositoryMock;

	@InjectMocks
	private MessageService service;

	@Test
	void postPersistsMessageWithTimestamp() {
		when(repositoryMock.save(any(MessageEntity.class))).thenReturn(MessageEntity.create().withId("m1"));

		final var id = service.post(ERRAND_ID, new CreateMessage("OUTBOUND", "Komplettera tack", "bsk1"));

		assertThat(id).isEqualTo("m1");
		final var captor = ArgumentCaptor.forClass(MessageEntity.class);
		verify(repositoryMock).save(captor.capture());
		assertThat(captor.getValue().getErrandId()).isEqualTo(ERRAND_ID);
		assertThat(captor.getValue().getDirection()).isEqualTo("OUTBOUND");
		assertThat(captor.getValue().getBody()).isEqualTo("Komplettera tack");
		assertThat(captor.getValue().getAuthor()).isEqualTo("bsk1");
		assertThat(captor.getValue().getCreated()).isNotNull();
	}

	@Test
	void listForErrandReturnsMappedMessages() {
		when(repositoryMock.findByErrandIdOrderByCreatedAsc(ERRAND_ID)).thenReturn(List.of(
			MessageEntity.create().withId("m1").withErrandId(ERRAND_ID).withDirection("INBOUND").withBody("hej").withCreated(OffsetDateTime.now())));

		final var result = service.listForErrand(ERRAND_ID);

		assertThat(result).singleElement().satisfies(message -> {
			assertThat(message.getId()).isEqualTo("m1");
			assertThat(message.getDirection()).isEqualTo("INBOUND");
			assertThat(message.getBody()).isEqualTo("hej");
		});
	}

	@Test
	void readReturnsMappedMessage() {
		when(repositoryMock.findById("m1")).thenReturn(Optional.of(
			MessageEntity.create().withId("m1").withErrandId(ERRAND_ID).withDirection("OUTBOUND").withBody("body")));

		final var result = service.read("m1");

		assertThat(result.getId()).isEqualTo("m1");
		assertThat(result.getDirection()).isEqualTo("OUTBOUND");
	}

	@Test
	void readWhenMissingThrowsNotFound() {
		when(repositoryMock.findById("missing")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.read("missing"))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}
}
