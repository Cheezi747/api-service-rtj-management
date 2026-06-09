package se.sundsvall.rtjmanagement.types.explosivvara.details.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

/**
 * Result of the automated explosiv-vara completeness check. Drives the BPMN routing gateway.
 *
 * <p>
 * Unlike egensotning, an explosiv-vara permit can <b>never</b> be auto-approved — LBE requires
 * manual handläggning (polisyttrande, intern prövning, ev. tillsyn). The check therefore only
 * routes between {@code NEEDS_SUPPLEMENT} (something is missing → kompletteringsslinga) and
 * {@code NEEDS_MANUAL_REVIEW} (ansökan är komplett nog att handläggas).
 * </p>
 */
@Schema(description = "Resultat av den automatiska fullständighetskontrollen för explosiv vara. Styr BPMN-routingen.")
public class ExplosivVaraVerificationResult {

	@Schema(description = "Routing-utfall. Explosiv vara auto-godkänns aldrig.", examples = "NEEDS_MANUAL_REVIEW", allowableValues = {
		"NEEDS_SUPPLEMENT", "NEEDS_MANUAL_REVIEW"
	})
	private String outcome;

	@Schema(description = "True om minst en bilaga finns på ärendet", examples = "true")
	private Boolean bilagaPresent;

	@Schema(description = "True om minst en produkt (explosiv vara) finns registrerad på ärendet", examples = "true")
	private Boolean productsPresent;

	@Schema(description = "Sammanfattning av vad som saknas när outcome är NEEDS_SUPPLEMENT", examples = "bilaga, minst en explosiv vara")
	private String supplementReason;

	@Schema(description = "Genererad formell beslutstext för tillståndsbeslut (omfattning + lagstöd + villkor + tillståndstid + överklagande). Används som beslutets description vid godkännande.")
	private String decisionDescription;

	public static ExplosivVaraVerificationResult create() {
		return new ExplosivVaraVerificationResult();
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(final String outcome) {
		this.outcome = outcome;
	}

	public ExplosivVaraVerificationResult withOutcome(final String outcome) {
		this.outcome = outcome;
		return this;
	}

	public Boolean getBilagaPresent() {
		return bilagaPresent;
	}

	public void setBilagaPresent(final Boolean bilagaPresent) {
		this.bilagaPresent = bilagaPresent;
	}

	public ExplosivVaraVerificationResult withBilagaPresent(final Boolean bilagaPresent) {
		this.bilagaPresent = bilagaPresent;
		return this;
	}

	public Boolean getProductsPresent() {
		return productsPresent;
	}

	public void setProductsPresent(final Boolean productsPresent) {
		this.productsPresent = productsPresent;
	}

	public ExplosivVaraVerificationResult withProductsPresent(final Boolean productsPresent) {
		this.productsPresent = productsPresent;
		return this;
	}

	public String getSupplementReason() {
		return supplementReason;
	}

	public void setSupplementReason(final String supplementReason) {
		this.supplementReason = supplementReason;
	}

	public ExplosivVaraVerificationResult withSupplementReason(final String supplementReason) {
		this.supplementReason = supplementReason;
		return this;
	}

	public String getDecisionDescription() {
		return decisionDescription;
	}

	public void setDecisionDescription(final String decisionDescription) {
		this.decisionDescription = decisionDescription;
	}

	public ExplosivVaraVerificationResult withDecisionDescription(final String decisionDescription) {
		this.decisionDescription = decisionDescription;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final ExplosivVaraVerificationResult that = (ExplosivVaraVerificationResult) o;
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
		return "ExplosivVaraVerificationResult{outcome='" + outcome + "', bilagaPresent=" + bilagaPresent
			+ ", productsPresent=" + productsPresent + ", supplementReason='" + supplementReason
			+ "', decisionDescription='" + decisionDescription + "'}";
	}
}
