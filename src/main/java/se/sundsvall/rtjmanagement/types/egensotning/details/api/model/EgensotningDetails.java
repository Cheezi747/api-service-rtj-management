package se.sundsvall.rtjmanagement.types.egensotning.details.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import se.sundsvall.dept44.common.validators.annotation.ValidPersonalNumber;
import se.sundsvall.rtjmanagement.core.api.validation.groups.OnCreate;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(
	description = "Type-specific extension fields for EGENSOTNING errands. One per errand (1:1). Holds the application data the automated checks need (applicant personnummer + the property the application concerns). The check-result fields are read-only and written by the verification step.")
public class EgensotningDetails {

	@Schema(description = "Sökandes personnummer på formatet ÅÅÅÅMMDDNNNN. Används för folkbokföringskontroll och för att identifiera återansökningar.", examples = "199001011234")
	@ValidPersonalNumber(nullable = true)
	private String personnummer;

	@Schema(description = "Fastighetsbeteckning för fastigheten ansökan gäller", examples = "Sundsvall Stenstaden 1:23")
	@Size(max = 255)
	private String fastighetsbeteckning;

	@Schema(description = "Adress till fastigheten ansökan gäller", examples = "Storgatan 5")
	@Size(max = 255)
	private String propertyAddress;

	@Schema(description = "Om sökanden äger fastigheten ansökan gäller. Underlag för manuell granskning när sökanden inte är folkbokförd på fastigheten.", examples = "true")
	private Boolean ownsProperty;

	@Schema(description = "Motivering när sökanden inte äger fastigheten (t.ex. nyttjanderätt till annans fastighet)", examples = "Arrenderar fastigheten av ägaren sedan 2019.")
	@Size(max = 2048)
	private String ownershipMotivation;

	@Schema(description = "Om ansökan gäller en annan fastighet än sökandens folkbokföringsadress (t.ex. sommarstuga)", examples = "false")
	private Boolean appliesForOtherProperty;

	@Schema(description = "Beslutsmotivering. Kan redigeras av handläggaren vid manuell granskning; lämnas den tom används den förifyllda standardtexten.", examples = "Sökanden bedöms ha tillräcklig kunskap om anläggningen.")
	@Size(max = 4096)
	private String motivering;

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

	@Schema(description = "Beslutets giltighet börjar (sätts vid godkännande)", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private LocalDate validFrom;

	@Schema(description = "Beslutets giltighet upphör — sex år från beslutsdatum, framflyttat till nästa fasta datum (1 mar/jun/sep/dec)", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private LocalDate validUntil;

	@Schema(description = "När påminnelse om utgång senast skickades (null tills den gått ut)", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime reminderSentAt;

	@Schema(description = "När beslutet återkallades (null om gällande)", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime revokedAt;

	@Schema(description = "Anledning till återkallelse, t.ex. ADDRESS_CHANGED eller BSK_FAILED", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private String revocationReason;

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

	public void setPersonnummer(final String personnummer) {
		this.personnummer = personnummer;
	}

	public EgensotningDetails withPersonnummer(final String personnummer) {
		this.personnummer = personnummer;
		return this;
	}

	public String getFastighetsbeteckning() {
		return fastighetsbeteckning;
	}

	public void setFastighetsbeteckning(final String fastighetsbeteckning) {
		this.fastighetsbeteckning = fastighetsbeteckning;
	}

	public EgensotningDetails withFastighetsbeteckning(final String fastighetsbeteckning) {
		this.fastighetsbeteckning = fastighetsbeteckning;
		return this;
	}

	public String getPropertyAddress() {
		return propertyAddress;
	}

	public void setPropertyAddress(final String propertyAddress) {
		this.propertyAddress = propertyAddress;
	}

	public EgensotningDetails withPropertyAddress(final String propertyAddress) {
		this.propertyAddress = propertyAddress;
		return this;
	}

	public Boolean getOwnsProperty() {
		return ownsProperty;
	}

	public void setOwnsProperty(final Boolean ownsProperty) {
		this.ownsProperty = ownsProperty;
	}

	public EgensotningDetails withOwnsProperty(final Boolean ownsProperty) {
		this.ownsProperty = ownsProperty;
		return this;
	}

	public String getOwnershipMotivation() {
		return ownershipMotivation;
	}

	public void setOwnershipMotivation(final String ownershipMotivation) {
		this.ownershipMotivation = ownershipMotivation;
	}

	public EgensotningDetails withOwnershipMotivation(final String ownershipMotivation) {
		this.ownershipMotivation = ownershipMotivation;
		return this;
	}

	public Boolean getAppliesForOtherProperty() {
		return appliesForOtherProperty;
	}

	public void setAppliesForOtherProperty(final Boolean appliesForOtherProperty) {
		this.appliesForOtherProperty = appliesForOtherProperty;
	}

	public EgensotningDetails withAppliesForOtherProperty(final Boolean appliesForOtherProperty) {
		this.appliesForOtherProperty = appliesForOtherProperty;
		return this;
	}

	public String getMotivering() {
		return motivering;
	}

	public void setMotivering(final String motivering) {
		this.motivering = motivering;
	}

	public EgensotningDetails withMotivering(final String motivering) {
		this.motivering = motivering;
		return this;
	}

	public Boolean getBilagaPresent() {
		return bilagaPresent;
	}

	public void setBilagaPresent(final Boolean bilagaPresent) {
		this.bilagaPresent = bilagaPresent;
	}

	public EgensotningDetails withBilagaPresent(final Boolean bilagaPresent) {
		this.bilagaPresent = bilagaPresent;
		return this;
	}

	public Boolean getRegisteredAtProperty() {
		return registeredAtProperty;
	}

	public void setRegisteredAtProperty(final Boolean registeredAtProperty) {
		this.registeredAtProperty = registeredAtProperty;
	}

	public EgensotningDetails withRegisteredAtProperty(final Boolean registeredAtProperty) {
		this.registeredAtProperty = registeredAtProperty;
		return this;
	}

	public Boolean getReapplicationOk() {
		return reapplicationOk;
	}

	public void setReapplicationOk(final Boolean reapplicationOk) {
		this.reapplicationOk = reapplicationOk;
	}

	public EgensotningDetails withReapplicationOk(final Boolean reapplicationOk) {
		this.reapplicationOk = reapplicationOk;
		return this;
	}

	public String getLastOutcome() {
		return lastOutcome;
	}

	public void setLastOutcome(final String lastOutcome) {
		this.lastOutcome = lastOutcome;
	}

	public EgensotningDetails withLastOutcome(final String lastOutcome) {
		this.lastOutcome = lastOutcome;
		return this;
	}

	public String getManualReviewReason() {
		return manualReviewReason;
	}

	public void setManualReviewReason(final String manualReviewReason) {
		this.manualReviewReason = manualReviewReason;
	}

	public EgensotningDetails withManualReviewReason(final String manualReviewReason) {
		this.manualReviewReason = manualReviewReason;
		return this;
	}

	public OffsetDateTime getLastVerifiedAt() {
		return lastVerifiedAt;
	}

	public void setLastVerifiedAt(final OffsetDateTime lastVerifiedAt) {
		this.lastVerifiedAt = lastVerifiedAt;
	}

	public EgensotningDetails withLastVerifiedAt(final OffsetDateTime lastVerifiedAt) {
		this.lastVerifiedAt = lastVerifiedAt;
		return this;
	}

	public LocalDate getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(final LocalDate validFrom) {
		this.validFrom = validFrom;
	}

	public EgensotningDetails withValidFrom(final LocalDate validFrom) {
		this.validFrom = validFrom;
		return this;
	}

	public LocalDate getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(final LocalDate validUntil) {
		this.validUntil = validUntil;
	}

	public EgensotningDetails withValidUntil(final LocalDate validUntil) {
		this.validUntil = validUntil;
		return this;
	}

	public OffsetDateTime getReminderSentAt() {
		return reminderSentAt;
	}

	public void setReminderSentAt(final OffsetDateTime reminderSentAt) {
		this.reminderSentAt = reminderSentAt;
	}

	public EgensotningDetails withReminderSentAt(final OffsetDateTime reminderSentAt) {
		this.reminderSentAt = reminderSentAt;
		return this;
	}

	public OffsetDateTime getRevokedAt() {
		return revokedAt;
	}

	public void setRevokedAt(final OffsetDateTime revokedAt) {
		this.revokedAt = revokedAt;
	}

	public EgensotningDetails withRevokedAt(final OffsetDateTime revokedAt) {
		this.revokedAt = revokedAt;
		return this;
	}

	public String getRevocationReason() {
		return revocationReason;
	}

	public void setRevocationReason(final String revocationReason) {
		this.revocationReason = revocationReason;
	}

	public EgensotningDetails withRevocationReason(final String revocationReason) {
		this.revocationReason = revocationReason;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public EgensotningDetails withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime modified) {
		this.modified = modified;
	}

	public EgensotningDetails withModified(final OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final EgensotningDetails that = (EgensotningDetails) o;
		return Objects.equals(personnummer, that.personnummer) && Objects.equals(fastighetsbeteckning, that.fastighetsbeteckning)
			&& Objects.equals(propertyAddress, that.propertyAddress) && Objects.equals(ownsProperty, that.ownsProperty)
			&& Objects.equals(ownershipMotivation, that.ownershipMotivation) && Objects.equals(appliesForOtherProperty, that.appliesForOtherProperty)
			&& Objects.equals(motivering, that.motivering) && Objects.equals(bilagaPresent, that.bilagaPresent)
			&& Objects.equals(registeredAtProperty, that.registeredAtProperty) && Objects.equals(reapplicationOk, that.reapplicationOk)
			&& Objects.equals(lastOutcome, that.lastOutcome) && Objects.equals(manualReviewReason, that.manualReviewReason)
			&& Objects.equals(lastVerifiedAt, that.lastVerifiedAt) && Objects.equals(validFrom, that.validFrom)
			&& Objects.equals(validUntil, that.validUntil) && Objects.equals(reminderSentAt, that.reminderSentAt)
			&& Objects.equals(revokedAt, that.revokedAt) && Objects.equals(revocationReason, that.revocationReason)
			&& Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(personnummer, fastighetsbeteckning, propertyAddress, ownsProperty, ownershipMotivation, appliesForOtherProperty,
			motivering, bilagaPresent, registeredAtProperty, reapplicationOk, lastOutcome, manualReviewReason, lastVerifiedAt, validFrom,
			validUntil, reminderSentAt, revokedAt, revocationReason, created, modified);
	}

	@Override
	public String toString() {
		return "EgensotningDetails{fastighetsbeteckning='" + fastighetsbeteckning + "', propertyAddress='" + propertyAddress
			+ "', ownsProperty=" + ownsProperty + ", ownershipMotivation='" + ownershipMotivation + "', appliesForOtherProperty=" + appliesForOtherProperty
			+ ", motivering='" + motivering + "', bilagaPresent=" + bilagaPresent + ", registeredAtProperty=" + registeredAtProperty + ", reapplicationOk=" + reapplicationOk
			+ ", lastOutcome='" + lastOutcome + "', manualReviewReason='" + manualReviewReason + "', lastVerifiedAt=" + lastVerifiedAt
			+ ", validFrom=" + validFrom + ", validUntil=" + validUntil + ", reminderSentAt=" + reminderSentAt
			+ ", revokedAt=" + revokedAt + ", revocationReason='" + revocationReason + "', created=" + created + ", modified=" + modified + '}';
	}
}
