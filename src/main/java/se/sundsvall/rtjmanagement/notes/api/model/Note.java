package se.sundsvall.rtjmanagement.notes.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Objects;

@Schema(description = "Note attached to an errand")
public class Note {

	@Schema(description = "Unique identifier")
	private String id;

	@Schema(description = "Errand id this note belongs to")
	private String errandId;

	@Schema(description = "Note body", example = "Spoke to family today, awaiting docs.")
	private String body;

	@Schema(description = "Author user id", example = "jane01doe")
	private String author;

	@Schema(description = "Created timestamp")
	private OffsetDateTime created;

	public static Note create() {
		return new Note();
	}

	public String getId() {
		return id;
	}

	public String getErrandId() {
		return errandId;
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

	public void setId(final String id) {
		this.id = id;
	}

	public void setErrandId(final String errandId) {
		this.errandId = errandId;
	}

	public void setBody(final String body) {
		this.body = body;
	}

	public void setAuthor(final String author) {
		this.author = author;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public Note withId(final String id) {
		this.id = id;
		return this;
	}

	public Note withErrandId(final String errandId) {
		this.errandId = errandId;
		return this;
	}

	public Note withBody(final String body) {
		this.body = body;
		return this;
	}

	public Note withAuthor(final String author) {
		this.author = author;
		return this;
	}

	public Note withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Note that = (Note) o;
		return Objects.equals(id, that.id) && Objects.equals(errandId, that.errandId)
			&& Objects.equals(body, that.body) && Objects.equals(author, that.author)
			&& Objects.equals(created, that.created);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, errandId, body, author, created);
	}
}
