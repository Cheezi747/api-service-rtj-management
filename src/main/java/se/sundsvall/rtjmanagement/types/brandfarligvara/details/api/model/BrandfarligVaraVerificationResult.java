package se.sundsvall.rtjmanagement.types.brandfarligvara.details.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

/**
 * Result of the automated brandfarlig-vara completeness check. Drives the BPMN routing gateway.
 *
 * <p>
 * Unlike egensotning, a brandfarlig-vara permit can <b>never</b> be auto-approved — LBE requires
 * manual handläggning (samråd, intern prövning, ev. tillsyn/avsyning). The check therefore only
 * routes between {@code NEEDS_SUPPLEMENT} (something is missing → kompletteringsslinga) and
 * {@code NEEDS_MANUAL_REVIEW} (ansökan är komplett nog att handläggas).
 * </p>
 */
@Schema(description = "Resultat av den automatiska fullständighetskontrollen för brandfarlig vara. Styr BPMN-routingen.")
public class BrandfarligVaraVerificationResult {

	@Schema(description = "Routing-utfall. Brandfarlig vara auto-godkänns aldrig.", examples = "NEEDS_MANUAL_REVIEW", allowableValues = {
		"NEEDS_SUPPLEMENT", "NEEDS_MANUAL_REVIEW"
	})
	private String outcome;

	@Schema(description = "True om minst en bilaga finns på ärendet", examples = "true")
	private Boolean bilagaPresent;

	@Schema(description = "True om minst en produkt (brandfarlig vara) finns registrerad på ärendet", examples = "true")
	private Boolean productsPresent;

	@Schema(description = "Sammanfattning av vad som saknas när outcome är NEEDS_SUPPLEMENT", examples = "bilaga, minst en brandfarlig vara")
	private String supplementReason;

	@Schema(description = "Genererad formell beslutstext för tillståndsbeslut (omfattning + lagstöd + villkor + tillståndstid + överklagande). Används som beslutets description vid godkännande.")
	private String decisionDescription;

	public static BrandfarligVaraVerificationResult create() {
		return new BrandfarligVaraVerificationResult();
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(final String v) {
		this.outcome = v;
	}

	public BrandfarligVaraVerificationResult withOutcome(final String v) {
		this.outcome = v;
		return this;
	}

	public Boolean getBilagaPresent() {
		return bilagaPresent;
	}

	public void setBilagaPresent(final Boolean v) {
		this.bilagaPresent = v;
	}

	public BrandfarligVaraVerificationResult withBilagaPresent(final Boolean v) {
		this.bilagaPresent = v;
		return this;
	}

	public Boolean getProductsPresent() {
		return productsPresent;
	}

	public void setProductsPresent(final Boolean v) {
		this.productsPresent = v;
	}

	public BrandfarligVaraVerificationResult withProductsPresent(final Boolean v) {
		this.productsPresent = v;
		return this;
	}

	public String getSupplementReason() {
		return supplementReason;
	}

	public void setSupplementReason(final String v) {
		this.supplementReason = v;
	}

	public BrandfarligVaraVerificationResult withSupplementReason(final String v) {
		this.supplementReason = v;
		return this;
	}

	public String getDecisionDescription() {
		return decisionDescription;
	}

	public void setDecisionDescription(final String v) {
		this.decisionDescription = v;
	}

	public BrandfarligVaraVerificationResult withDecisionDescription(final String v) {
		this.decisionDescription = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final BrandfarligVaraVerificationResult that = (BrandfarligVaraVerificationResult) o;
		return Objects.equals(outcome, that.outcome) && Objects.equals(bilagaPresent, that.bilagaPresent)
			&& Objects.equals(productsPresent, that.productsPresent) && Objects.equals(supplementReason, that.supplementReason)
			&& Objects.equals(decisionDescription, that.decisionDescription);
	}

	@Override
	public int hashCode() {
		return Objects.hash(outcome, bilagaPresent, productsPresent, supplementReason, decisionDescription);
	}

	@Override
	public String toString() {
		return "BrandfarligVaraVerificationResult{outcome='" + outcome + "', bilagaPresent=" + bilagaPresent
			+ ", productsPresent=" + productsPresent + ", supplementReason='" + supplementReason
			+ "', decisionDescription='" + decisionDescription + "'}";
	}
}
