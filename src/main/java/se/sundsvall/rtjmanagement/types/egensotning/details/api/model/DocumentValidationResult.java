package se.sundsvall.rtjmanagement.types.egensotning.details.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Result of the Eneo (LLM) validation of the egensotning attachments (brandskyddskontroll + utbildningsintyg). Gates auto-approval: a non-valid result diverts the BPMN to manual review.")
public class DocumentValidationResult {

	@Schema(description = "True only when the documents are of the correct types, valid, and match the applicant. A non-valid result (or an Eneo outage) routes the errand to manual review — the LLM never auto-rejects.", examples = "true")
	private Boolean valid;

	@Schema(description = "True if both attachments are of the expected document types (brandskyddskontroll resp. utbildningsintyg)", examples = "true")
	private Boolean documentTypeOk;

	@Schema(description = "True if the name / personnummer / fastighet in the documents match the applicant", examples = "true")
	private Boolean identityMatch;

	@Schema(description = "Short human-readable motivation from the LLM (or the reason validation could not be completed)", examples = "Dokumenten är giltiga och uppgifterna stämmer med sökanden.")
	private String reason;

	public static DocumentValidationResult create() {
		return new DocumentValidationResult();
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(final Boolean v) {
		this.valid = v;
	}

	public DocumentValidationResult withValid(final Boolean v) {
		this.valid = v;
		return this;
	}

	public Boolean getDocumentTypeOk() {
		return documentTypeOk;
	}

	public void setDocumentTypeOk(final Boolean v) {
		this.documentTypeOk = v;
	}

	public DocumentValidationResult withDocumentTypeOk(final Boolean v) {
		this.documentTypeOk = v;
		return this;
	}

	public Boolean getIdentityMatch() {
		return identityMatch;
	}

	public void setIdentityMatch(final Boolean v) {
		this.identityMatch = v;
	}

	public DocumentValidationResult withIdentityMatch(final Boolean v) {
		this.identityMatch = v;
		return this;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(final String v) {
		this.reason = v;
	}

	public DocumentValidationResult withReason(final String v) {
		this.reason = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final DocumentValidationResult that = (DocumentValidationResult) o;
		return Objects.equals(valid, that.valid) && Objects.equals(documentTypeOk, that.documentTypeOk)
			&& Objects.equals(identityMatch, that.identityMatch) && Objects.equals(reason, that.reason);
	}

	@Override
	public int hashCode() {
		return Objects.hash(valid, documentTypeOk, identityMatch, reason);
	}

	@Override
	public String toString() {
		return "DocumentValidationResult{valid=" + valid + ", documentTypeOk=" + documentTypeOk
			+ ", identityMatch=" + identityMatch + ", reason='" + reason + "'}";
	}
}
