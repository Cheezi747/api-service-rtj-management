package se.sundsvall.rtjmanagement.conversation.service;

import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.rtjmanagement.conversation.api.model.CreateMessage;
import se.sundsvall.rtjmanagement.conversation.api.model.Message;
import se.sundsvall.rtjmanagement.conversation.integration.db.MessageRepository;
import se.sundsvall.rtjmanagement.conversation.integration.db.model.MessageEntity;
import se.sundsvall.rtjmanagement.shared.MessagePosted;

import static java.time.OffsetDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Per-errand message thread between handläggare and sökande. Persists the message och publicerar ett
 * {@link MessagePosted}-event så att intresserade typmoduler kan avisera. All meddelandetext lever
 * bara i den här in-app-tråden; egensotningsmodulen mejlar enbart en innehållslös notis (logga in på
 * Mina sidor) vid OUTBOUND — texten lämnar aldrig tråden.
 */
@Service
@Transactional
public class MessageService {

	private final MessageRepository repository;
	private final ApplicationEventPublisher eventPublisher;

	MessageService(final MessageRepository repository, final ApplicationEventPublisher eventPublisher) {
		this.repository = repository;
		this.eventPublisher = eventPublisher;
	}

	public String post(final String errandId, final CreateMessage request) {
		final var saved = repository.save(MessageEntity.create()
			.withErrandId(errandId)
			.withDirection(request.direction())
			.withBody(request.body())
			.withAuthor(request.author())
			.withCreated(now(systemDefault()).truncatedTo(MILLIS)));
		// Avisera att ett meddelande postats. Konsumenter (t.ex. egensotning) avgör själva om/hur de
		// notifierar — typiskt en innehållslös e-postnotis vid OUTBOUND. Eventet bär aldrig body:n.
		eventPublisher.publishEvent(new MessagePosted(saved.getId(), errandId, saved.getDirection(), saved.getAuthor(), saved.getCreated()));
		return saved.getId();
	}

	@Transactional(readOnly = true)
	public List<Message> listForErrand(final String errandId) {
		return repository.findByErrandIdOrderByCreatedAsc(errandId).stream()
			.map(MessageService::toMessage)
			.toList();
	}

	@Transactional(readOnly = true)
	public Message read(final String messageId) {
		return toMessage(repository.findById(messageId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, "No message with id '" + messageId + "'")));
	}

	private static Message toMessage(final MessageEntity entity) {
		return Message.create()
			.withId(entity.getId())
			.withErrandId(entity.getErrandId())
			.withDirection(entity.getDirection())
			.withBody(entity.getBody())
			.withAuthor(entity.getAuthor())
			.withCreated(entity.getCreated());
	}
}
