package se.sundsvall.rtjmanagement.remiss.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.springframework.format.annotation.DateTimeFormat;
import se.sundsvall.dept44.common.validators.annotation.OneOf;
import se.sundsvall.rtjmanagement.core.api.validation.groups.OnCreate;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;
import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

/**
 * En remiss/samråd som skickats på ett ärende (14 § FBE — t.ex. miljökontor; för explosiv vara
 * polisens yttrande) och dess svar. {@code status} följer livscykeln SENT → RESPONDED.
 */
@Schema(description = "Remiss/samråd på ett ärende (14 § FBE) med mottagande instans, svarsdatum och status.")
public class Remiss {

	@Schema(description = "Unikt id", examples = "cb20c51f-fcf3-42c0-b613-de563634a8ec", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private String id;

	@Schema(description = "Mottagande instans", examples = "MILJOKONTOR", allowableValues = {
		"MILJOKONTOR", "POLIS", "LANSSTYRELSE", "MCF", "KOMMUN", "OVRIGT"
	})
	@NotBlank(groups = OnCreate.class)
	@OneOf(value = {
		"MILJOKONTOR", "POLIS", "LANSSTYRELSE", "MCF", "KOMMUN", "OVRIGT"
	}, nullable = true)
	private String instans;

	@Schema(description = "Mottagare (namn/enhet)", examples = "Miljökontoret Sundsvall")
	@Size(max = 255)
	private String recipient;

	@Schema(description = "Datum då remissen skickades. Default: dagens datum.", examples = "2026-06-03")
	@DateTimeFormat(iso = DATE)
	private LocalDate sentAt;

	@Schema(description = "Sista svarsdatum", examples = "2026-07-01")
	@DateTimeFormat(iso = DATE)
	private LocalDate dueAt;

	@Schema(description = "Svar på remissen", examples = "Miljökontoret har inget att invända.")
	@Size(max = 4096)
	private String responseText;

	@Schema(description = "Status", examples = "SENT", allowableValues = {
		"SENT", "RESPONDED"
	})
	@OneOf(value = {
		"SENT", "RESPONDED"
	}, nullable = true)
	private String status;

	@Schema(description = "Skapad", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime created;

	@Schema(description = "Ändrad", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime modified;

	public static Remiss create() {
		return new Remiss();
	}

	public String getId() {
		return id;
	}

	public void setId(final String v) {
		this.id = v;
	}

	public Remiss withId(final String v) {
		this.id = v;
		return this;
	}

	public String getInstans() {
		return instans;
	}

	public void setInstans(final String v) {
		this.instans = v;
	}

	public Remiss withInstans(final String v) {
		this.instans = v;
		return this;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(final String v) {
		this.recipient = v;
	}

	public Remiss withRecipient(final String v) {
		this.recipient = v;
		return this;
	}

	public LocalDate getSentAt() {
		return sentAt;
	}

	public void setSentAt(final LocalDate v) {
		this.sentAt = v;
	}

	public Remiss withSentAt(final LocalDate v) {
		this.sentAt = v;
		return this;
	}

	public LocalDate getDueAt() {
		return dueAt;
	}

	public void setDueAt(final LocalDate v) {
		this.dueAt = v;
	}

	public Remiss withDueAt(final LocalDate v) {
		this.dueAt = v;
		return this;
	}

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(final String v) {
		this.responseText = v;
	}

	public Remiss withResponseText(final String v) {
		this.responseText = v;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String v) {
		this.status = v;
	}

	public Remiss withStatus(final String v) {
		this.status = v;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime v) {
		this.created = v;
	}

	public Remiss withCreated(final OffsetDateTime v) {
		this.created = v;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime v) {
		this.modified = v;
	}

	public Remiss withModified(final OffsetDateTime v) {
		this.modified = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Remiss that = (Remiss) o;
		return Objects.equals(id, that.id) && Objects.equals(instans, that.instans) && Objects.equals(recipient, that.recipient)
			&& Objects.equals(sentAt, that.sentAt) && Objects.equals(dueAt, that.dueAt) && Objects.equals(responseText, that.responseText)
			&& Objects.equals(status, that.status) && Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, instans, recipient, sentAt, dueAt, responseText, status, created, modified);
	}

	@Override
	public String toString() {
		return "Remiss{id='" + id + "', instans='" + instans + "', recipient='" + recipient + "', sentAt=" + sentAt
			+ ", dueAt=" + dueAt + ", responseText='" + responseText + "', status='" + status + "', created=" + created
			+ ", modified=" + modified + '}';
	}
}
