package se.sundsvall.rtjmanagement.conversation.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Objects;

@Schema(description = "Ett meddelande i ärendets konversation")
public class Message {

	@Schema(description = "Unik identifierare")
	private String id;

	@Schema(description = "Ärendet meddelandet hör till")
	private String errandId;

	@Schema(description = "Riktning", allowableValues = {
		"INBOUND", "OUTBOUND"
	}, examples = "OUTBOUND")
	private String direction;

	@Schema(description = "Meddelandetext", examples = "Vänligen komplettera med ett giltigt utbildningsintyg.")
	private String body;

	@Schema(description = "Avsändarens id", examples = "bsk-anders-svensson")
	private String author;

	@Schema(description = "Skapad tidpunkt")
	private OffsetDateTime created;

	public static Message create() {
		return new Message();
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

	public void setId(final String v) {
		this.id = v;
	}

	public void setErrandId(final String v) {
		this.errandId = v;
	}

	public void setDirection(final String v) {
		this.direction = v;
	}

	public void setBody(final String v) {
		this.body = v;
	}

	public void setAuthor(final String v) {
		this.author = v;
	}

	public void setCreated(final OffsetDateTime v) {
		this.created = v;
	}

	public Message withId(final String v) {
		this.id = v;
		return this;
	}

	public Message withErrandId(final String v) {
		this.errandId = v;
		return this;
	}

	public Message withDirection(final String v) {
		this.direction = v;
		return this;
	}

	public Message withBody(final String v) {
		this.body = v;
		return this;
	}

	public Message withAuthor(final String v) {
		this.author = v;
		return this;
	}

	public Message withCreated(final OffsetDateTime v) {
		this.created = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Message that = (Message) o;
		return Objects.equals(id, that.id) && Objects.equals(errandId, that.errandId) && Objects.equals(direction, that.direction)
			&& Objects.equals(body, that.body) && Objects.equals(author, that.author) && Objects.equals(created, that.created);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, errandId, direction, body, author, created);
	}

	@Override
	public String toString() {
		return "Message{id='" + id + "', errandId='" + errandId + "', direction='" + direction + "', body='" + body
			+ "', author='" + author + "', created=" + created + '}';
	}
}
