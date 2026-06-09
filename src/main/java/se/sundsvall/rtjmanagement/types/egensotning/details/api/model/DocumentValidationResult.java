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

	public void setValid(final Boolean valid) {
		this.valid = valid;
	}

	public DocumentValidationResult withValid(final Boolean valid) {
		this.valid = valid;
		return this;
	}

	public Boolean getDocumentTypeOk() {
		return documentTypeOk;
	}

	public void setDocumentTypeOk(final Boolean documentTypeOk) {
		this.documentTypeOk = documentTypeOk;
	}

	public DocumentValidationResult withDocumentTypeOk(final Boolean documentTypeOk) {
		this.documentTypeOk = documentTypeOk;
		return this;
	}

	public Boolean getIdentityMatch() {
		return identityMatch;
	}

	public void setIdentityMatch(final Boolean identityMatch) {
		this.identityMatch = identityMatch;
	}

	public DocumentValidationResult withIdentityMatch(final Boolean identityMatch) {
		this.identityMatch = identityMatch;
		return this;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(final String reason) {
		this.reason = reason;
	}

	public DocumentValidationResult withReason(final String reason) {
		this.reason = reason;
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
