package se.sundsvall.rtjmanagement.conversation.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.UuidGenerator;

import static org.hibernate.Length.LONG32;
import static org.hibernate.annotations.TimeZoneStorageType.NORMALIZE;

@Entity
@Table(name = "errand_message",
	indexes = {
		@Index(name = "idx_message_errand_id", columnList = "errand_id"),
		@Index(name = "idx_message_created", columnList = "created")
	})
public class MessageEntity {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "errand_id", nullable = false, length = 255)
	private String errandId;

	@Column(name = "direction", nullable = false, length = 16)
	private String direction;

	@Column(name = "body", nullable = false, length = LONG32)
	private String body;

	@Column(name = "author", length = 64)
	private String author;

	@Column(name = "created", nullable = false)
	@TimeZoneStorage(NORMALIZE)
	private OffsetDateTime created;

	public static MessageEntity create() {
		return new MessageEntity();
	}

	public String getId() {
		return id;
	}

	public String getErrandId() {
		return errandId;
	}

	public String getDirection() {
		return direction;
	}

	public String getBody() {
		return body;
	}

	public String getAuthor() {
		return author;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public MessageEntity withId(final String v) {
		this.id = v;
		return this;
	}

	public MessageEntity withErrandId(final String v) {
		this.errandId = v;
		return this;
	}

	public MessageEntity withDirection(final String v) {
		this.direction = v;
		return this;
	}

	public MessageEntity withBody(final String v) {
		this.body = v;
		return this;
	}

	public MessageEntity withAuthor(final String v) {
		this.author = v;
		return this;
	}

	public MessageEntity withCreated(final OffsetDateTime v) {
		this.created = v;
		return this;
	}
}
