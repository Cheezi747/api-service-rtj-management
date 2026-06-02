package se.sundsvall.rtjmanagement.types.egensotning.details.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Objects;
import se.sundsvall.rtjmanagement.core.api.validation.groups.OnCreate;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(
	description = "Type-specific extension fields for EGENSOTNING errands. One per errand (1:1). Holds the application data the automated checks need (applicant personnummer + the property the application concerns). The check-result fields are read-only and written by the verification step.")
public class EgensotningDetails {

	@Schema(description = "Sökandes personnummer. Används för folkbokföringskontroll och för att identifiera återansökningar.", examples = "199001011234")
	@Size(max = 16)
	private String personnummer;

	@Schema(description = "Fastighetsbeteckning för fastigheten ansökan gäller", examples = "Sundsvall Stenstaden 1:23")
	@Size(max = 255)
	private String fastighetsbeteckning;

	@Schema(description = "Adress till fastigheten ansökan gäller", examples = "Storgatan 5")
	@Size(max = 255)
	private String propertyAddress;

	@Schema(description = "Om en bilaga finns på ärendet (beräknad av verifieringssteget)", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private Boolean bilagaPresent;

	@Schema(description = "Om sökande är folkbokförd på fastigheten (beräknad av verifieringssteget)", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private Boolean registeredAtProperty;

	@Schema(description = "Om återansökan-statusen tillåter auto-godkännande (beräknad av verifieringssteget)", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private Boolean reapplicationOk;

	@Schema(description = "Senaste routing-utfall: AUTO_APPROVE, NEEDS_SUPPLEMENT eller NEEDS_MANUAL_REVIEW", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private String lastOutcome;

	@Schema(description = "Anledning till manuell granskning, t.ex. NOT_REGISTERED, REAPPLICATION_REJECTED, REAPPLICATION_ONGOING", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private String manualReviewReason;

	@Schema(description = "När verifieringen senast kördes", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime lastVerifiedAt;

	@Schema(description = "Created timestamp", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime created;

	@Schema(description = "Modified timestamp", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime modified;

	public static EgensotningDetails create() {
		return new EgensotningDetails();
	}

	public String getPersonnummer() {
		return personnummer;
	}

	public void setPersonnummer(final String v) {
		this.personnummer = v;
	}

	public EgensotningDetails withPersonnummer(final String v) {
		this.personnummer = v;
		return this;
	}

	public String getFastighetsbeteckning() {
		return fastighetsbeteckning;
	}

	public void setFastighetsbeteckning(final String v) {
		this.fastighetsbeteckning = v;
	}

	public EgensotningDetails withFastighetsbeteckning(final String v) {
		this.fastighetsbeteckning = v;
		return this;
	}

	public String getPropertyAddress() {
		return propertyAddress;
	}

	public void setPropertyAddress(final String v) {
		this.propertyAddress = v;
	}

	public EgensotningDetails withPropertyAddress(final String v) {
		this.propertyAddress = v;
		return this;
	}

	public Boolean getBilagaPresent() {
		return bilagaPresent;
	}

	public void setBilagaPresent(final Boolean v) {
		this.bilagaPresent = v;
	}

	public EgensotningDetails withBilagaPresent(final Boolean v) {
		this.bilagaPresent = v;
		return this;
	}

	public Boolean getRegisteredAtProperty() {
		return registeredAtProperty;
	}

	public void setRegisteredAtProperty(final Boolean v) {
		this.registeredAtProperty = v;
	}

	public EgensotningDetails withRegisteredAtProperty(final Boolean v) {
		this.registeredAtProperty = v;
		return this;
	}

	public Boolean getReapplicationOk() {
		return reapplicationOk;
	}

	public void setReapplicationOk(final Boolean v) {
		this.reapplicationOk = v;
	}

	public EgensotningDetails withReapplicationOk(final Boolean v) {
		this.reapplicationOk = v;
		return this;
	}

	public String getLastOutcome() {
		return lastOutcome;
	}

	public void setLastOutcome(final String v) {
		this.lastOutcome = v;
	}

	public EgensotningDetails withLastOutcome(final String v) {
		this.lastOutcome = v;
		return this;
	}

	public String getManualReviewReason() {
		return manualReviewReason;
	}

	public void setManualReviewReason(final String v) {
		this.manualReviewReason = v;
	}

	public EgensotningDetails withManualReviewReason(final String v) {
		this.manualReviewReason = v;
		return this;
	}

	public OffsetDateTime getLastVerifiedAt() {
		return lastVerifiedAt;
	}

	public void setLastVerifiedAt(final OffsetDateTime v) {
		this.lastVerifiedAt = v;
	}

	public EgensotningDetails withLastVerifiedAt(final OffsetDateTime v) {
		this.lastVerifiedAt = v;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime v) {
		this.created = v;
	}

	public EgensotningDetails withCreated(final OffsetDateTime v) {
		this.created = v;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime v) {
		this.modified = v;
	}

	public EgensotningDetails withModified(final OffsetDateTime v) {
		this.modified = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final EgensotningDetails that = (EgensotningDetails) o;
		return Objects.equals(personnummer, that.personnummer) && Objects.equals(fastighetsbeteckning, that.fastighetsbeteckning)
			&& Objects.equals(propertyAddress, that.propertyAddress) && Objects.equals(bilagaPresent, that.bilagaPresent)
			&& Objects.equals(registeredAtProperty, that.registeredAtProperty) && Objects.equals(reapplicationOk, that.reapplicationOk)
			&& Objects.equals(lastOutcome, that.lastOutcome) && Objects.equals(manualReviewReason, that.manualReviewReason)
			&& Objects.equals(lastVerifiedAt, that.lastVerifiedAt) && Objects.equals(created, that.created)
			&& Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(personnummer, fastighetsbeteckning, propertyAddress, bilagaPresent, registeredAtProperty,
			reapplicationOk, lastOutcome, manualReviewReason, lastVerifiedAt, created, modified);
	}

	@Override
	public String toString() {
		return "EgensotningDetails{fastighetsbeteckning='" + fastighetsbeteckning + "', propertyAddress='" + propertyAddress
			+ "', bilagaPresent=" + bilagaPresent + ", registeredAtProperty=" + registeredAtProperty + ", reapplicationOk=" + reapplicationOk
			+ ", lastOutcome='" + lastOutcome + "', manualReviewReason='" + manualReviewReason + "', lastVerifiedAt=" + lastVerifiedAt
			+ ", created=" + created + ", modified=" + modified + '}';
	}
}
