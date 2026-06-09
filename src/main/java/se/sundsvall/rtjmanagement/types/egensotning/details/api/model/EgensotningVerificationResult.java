package se.sundsvall.rtjmanagement.types.egensotning.details.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

@Schema(description = "Result of the automated egensotning checks. Drives the BPMN routing gateway.")
public class EgensotningVerificationResult {

	@Schema(description = "Routing outcome", examples = "AUTO_APPROVE", allowableValues = {
		"AUTO_APPROVE", "NEEDS_SUPPLEMENT", "NEEDS_MANUAL_REVIEW"
	})
	private String outcome;

	@Schema(description = "True if at least one attachment (bilaga) exists on the errand", examples = "true")
	private Boolean bilagaPresent;

	@Schema(description = "True if the applicant is folkbokförd at the property the application concerns", examples = "true")
	private Boolean registeredAtProperty;

	@Schema(description = "True if the återansökan status allows auto-approval", examples = "true")
	private Boolean reapplicationOk;

	@Schema(description = "Reason a human review is needed, when outcome is NEEDS_MANUAL_REVIEW", examples = "NOT_REGISTERED", allowableValues = {
		"NOT_REGISTERED", "OWNER_NOT_REGISTERED", "REAPPLICATION_REJECTED", "REAPPLICATION_ONGOING", "ACTIVE_PERMIT_EXISTS"
	})
	private String manualReviewReason;

	@Schema(description = "Genererad formell beslutstext för godkännande (objekttabell + lagstöd + villkor + överklagande). Används som beslutets description vid godkännande.")
	private String decisionDescription;

	public static EgensotningVerificationResult create() {
		return new EgensotningVerificationResult();
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(final String outcome) {
		this.outcome = outcome;
	}

	public EgensotningVerificationResult withOutcome(final String outcome) {
		this.outcome = outcome;
		return this;
	}

	public Boolean getBilagaPresent() {
		return bilagaPresent;
	}

	public void setBilagaPresent(final Boolean bilagaPresent) {
		this.bilagaPresent = bilagaPresent;
	}

	public EgensotningVerificationResult withBilagaPresent(final Boolean bilagaPresent) {
		this.bilagaPresent = bilagaPresent;
		return this;
	}

	public Boolean getRegisteredAtProperty() {
		return registeredAtProperty;
	}

	public void setRegisteredAtProperty(final Boolean registeredAtProperty) {
		this.registeredAtProperty = registeredAtProperty;
	}

	public EgensotningVerificationResult withRegisteredAtProperty(final Boolean registeredAtProperty) {
		this.registeredAtProperty = registeredAtProperty;
		return this;
	}

	public Boolean getReapplicationOk() {
		return reapplicationOk;
	}

	public void setReapplicationOk(final Boolean reapplicationOk) {
		this.reapplicationOk = reapplicationOk;
	}

	public EgensotningVerificationResult withReapplicationOk(final Boolean reapplicationOk) {
		this.reapplicationOk = reapplicationOk;
		return this;
	}

	public String getManualReviewReason() {
		return manualReviewReason;
	}

	public void setManualReviewReason(final String manualReviewReason) {
		this.manualReviewReason = manualReviewReason;
	}

	public EgensotningVerificationResult withManualReviewReason(final String manualReviewReason) {
		this.manualReviewReason = manualReviewReason;
		return this;
	}

	public String getDecisionDescription() {
		return decisionDescription;
	}

	public void setDecisionDescription(final String decisionDescription) {
		this.decisionDescription = decisionDescription;
	}

	public EgensotningVerificationResult withDecisionDescription(final String decisionDescription) {
		this.decisionDescription = decisionDescription;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final EgensotningVerificationResult that = (EgensotningVerificationResult) o;
		return Objects.equals(outcome, that.outcome) && Objects.equals(bilagaPresent, that.bilagaPresent)
			&& Objects.equals(registeredAtProperty, that.registeredAtProperty) && Objects.equals(reapplicationOk, that.reapplicationOk)
			&& Objects.equals(manualReviewReason, that.manualReviewReason) && Objects.equals(decisionDescription, that.decisionDescription);
	}

	@Override
	public int hashCode() {
		return Objects.hash(outcome, bilagaPresent, registeredAtProperty, reapplicationOk, manualReviewReason, decisionDescription);
	}

	@Override
	public String toString() {
		return "EgensotningVerificationResult{outcome='" + outcome + "', bilagaPresent=" + bilagaPresent
			+ ", registeredAtProperty=" + registeredAtProperty + ", reapplicationOk=" + reapplicationOk
			+ ", manualReviewReason='" + manualReviewReason + "', decisionDescription='" + decisionDescription + "'}";
	}
}
