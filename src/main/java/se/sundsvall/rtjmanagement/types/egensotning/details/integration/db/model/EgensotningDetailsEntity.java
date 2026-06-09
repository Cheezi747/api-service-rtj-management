package se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;
import se.sundsvall.rtjmanagement.shared.Auditable;
import se.sundsvall.rtjmanagement.shared.AuditableListener;

/**
 * 1:1 child of the errand envelope for EGENSOTNING. Holds the frontend-supplied
 * application data the automated checks need (applicant personnummer + the property
 * the application concerns) plus the computed check results and the last routing
 * outcome (written by the verify step) so the handläggare UI and audit trail can see
 * why the process auto-approved or escalated.
 */
@Entity
@Table(name = "egensotning_details",
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_egensotning_details_errand_id", columnNames = "errand_id")
	})
@EntityListeners(AuditableListener.class)
public class EgensotningDetailsEntity implements Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "errand_id", nullable = false, length = 255)
	private String errandId;

	@Column(name = "personnummer", length = 16)
	private String personnummer;

	@Column(name = "fastighetsbeteckning")
	private String fastighetsbeteckning;

	@Column(name = "property_address")
	private String propertyAddress;

	@Column(name = "owns_property")
	private Boolean ownsProperty;

	@Column(name = "ownership_motivation", length = 2048)
	private String ownershipMotivation;

	@Column(name = "applies_for_other_property")
	private Boolean appliesForOtherProperty;

	@Column(name = "motivering", length = 4096)
	private String motivering;

	@Column(name = "bilaga_present")
	private Boolean bilagaPresent;

	@Column(name = "registered_at_property")
	private Boolean registeredAtProperty;

	@Column(name = "reapplication_ok")
	private Boolean reapplicationOk;

	@Column(name = "last_outcome", length = 32)
	private String lastOutcome;

	@Column(name = "manual_review_reason", length = 64)
	private String manualReviewReason;

	@Column(name = "last_verified_at")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	private OffsetDateTime lastVerifiedAt;

	@Column(name = "valid_from")
	private LocalDate validFrom;

	@Column(name = "valid_until")
	private LocalDate validUntil;

	@Column(name = "reminder_sent_at")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	private OffsetDateTime reminderSentAt;

	@Column(name = "documents_valid")
	private Boolean documentsValid;

	@Column(name = "document_validation_detail", length = 2048)
	private String documentValidationDetail;

	@Column(name = "document_validated_at")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	private OffsetDateTime documentValidatedAt;

	@Column(name = "revoked_at")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	private OffsetDateTime revokedAt;

	@Column(name = "revocation_reason", length = 2048)
	private String revocationReason;

	@Column(name = "created")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	private OffsetDateTime created;

	@Column(name = "modified")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	private OffsetDateTime modified;

	public static EgensotningDetailsEntity create() {
		return new EgensotningDetailsEntity();
	}

	public Long getId() {
		return id;
	}

	public String getErrandId() {
		return errandId;
	}

	public String getPersonnummer() {
		return personnummer;
	}

	public String getFastighetsbeteckning() {
		return fastighetsbeteckning;
	}

	public String getPropertyAddress() {
		return propertyAddress;
	}

	public Boolean getOwnsProperty() {
		return ownsProperty;
	}

	public String getOwnershipMotivation() {
		return ownershipMotivation;
	}

	public Boolean getAppliesForOtherProperty() {
		return appliesForOtherProperty;
	}

	public String getMotivering() {
		return motivering;
	}

	public Boolean getBilagaPresent() {
		return bilagaPresent;
	}

	public Boolean getRegisteredAtProperty() {
		return registeredAtProperty;
	}

	public Boolean getReapplicationOk() {
		return reapplicationOk;
	}

	public String getLastOutcome() {
		return lastOutcome;
	}

	public String getManualReviewReason() {
		return manualReviewReason;
	}

	public OffsetDateTime getLastVerifiedAt() {
		return lastVerifiedAt;
	}

	public LocalDate getValidFrom() {
		return validFrom;
	}

	public LocalDate getValidUntil() {
		return validUntil;
	}

	public OffsetDateTime getReminderSentAt() {
		return reminderSentAt;
	}

	public Boolean getDocumentsValid() {
		return documentsValid;
	}

	public String getDocumentValidationDetail() {
		return documentValidationDetail;
	}

	public OffsetDateTime getDocumentValidatedAt() {
		return documentValidatedAt;
	}

	public OffsetDateTime getRevokedAt() {
		return revokedAt;
	}

	public String getRevocationReason() {
		return revocationReason;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setErrandId(final String errandId) {
		this.errandId = errandId;
	}

	public void setPersonnummer(final String personnummer) {
		this.personnummer = personnummer;
	}

	public void setFastighetsbeteckning(final String fastighetsbeteckning) {
		this.fastighetsbeteckning = fastighetsbeteckning;
	}

	public void setPropertyAddress(final String propertyAddress) {
		this.propertyAddress = propertyAddress;
	}

	public void setOwnsProperty(final Boolean ownsProperty) {
		this.ownsProperty = ownsProperty;
	}

	public void setOwnershipMotivation(final String ownershipMotivation) {
		this.ownershipMotivation = ownershipMotivation;
	}

	public void setAppliesForOtherProperty(final Boolean appliesForOtherProperty) {
		this.appliesForOtherProperty = appliesForOtherProperty;
	}

	public void setMotivering(final String motivering) {
		this.motivering = motivering;
	}

	public void setBilagaPresent(final Boolean bilagaPresent) {
		this.bilagaPresent = bilagaPresent;
	}

	public void setRegisteredAtProperty(final Boolean registeredAtProperty) {
		this.registeredAtProperty = registeredAtProperty;
	}

	public void setReapplicationOk(final Boolean reapplicationOk) {
		this.reapplicationOk = reapplicationOk;
	}

	public void setLastOutcome(final String lastOutcome) {
		this.lastOutcome = lastOutcome;
	}

	public void setManualReviewReason(final String manualReviewReason) {
		this.manualReviewReason = manualReviewReason;
	}

	public void setLastVerifiedAt(final OffsetDateTime lastVerifiedAt) {
		this.lastVerifiedAt = lastVerifiedAt;
	}

	public void setValidFrom(final LocalDate validFrom) {
		this.validFrom = validFrom;
	}

	public void setValidUntil(final LocalDate validUntil) {
		this.validUntil = validUntil;
	}

	public void setReminderSentAt(final OffsetDateTime reminderSentAt) {
		this.reminderSentAt = reminderSentAt;
	}

	public void setDocumentsValid(final Boolean documentsValid) {
		this.documentsValid = documentsValid;
	}

	public void setDocumentValidationDetail(final String documentValidationDetail) {
		this.documentValidationDetail = documentValidationDetail;
	}

	public void setDocumentValidatedAt(final OffsetDateTime documentValidatedAt) {
		this.documentValidatedAt = documentValidatedAt;
	}

	public void setRevokedAt(final OffsetDateTime revokedAt) {
		this.revokedAt = revokedAt;
	}

	public void setRevocationReason(final String revocationReason) {
		this.revocationReason = revocationReason;
	}

	@Override
	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	@Override
	public void setModified(final OffsetDateTime modified) {
		this.modified = modified;
	}

	public EgensotningDetailsEntity withId(final Long id) {
		this.id = id;
		return this;
	}

	public EgensotningDetailsEntity withErrandId(final String errandId) {
		this.errandId = errandId;
		return this;
	}

	public EgensotningDetailsEntity withPersonnummer(final String personnummer) {
		this.personnummer = personnummer;
		return this;
	}

	public EgensotningDetailsEntity withFastighetsbeteckning(final String fastighetsbeteckning) {
		this.fastighetsbeteckning = fastighetsbeteckning;
		return this;
	}

	public EgensotningDetailsEntity withPropertyAddress(final String propertyAddress) {
		this.propertyAddress = propertyAddress;
		return this;
	}

	public EgensotningDetailsEntity withOwnsProperty(final Boolean ownsProperty) {
		this.ownsProperty = ownsProperty;
		return this;
	}

	public EgensotningDetailsEntity withOwnershipMotivation(final String ownershipMotivation) {
		this.ownershipMotivation = ownershipMotivation;
		return this;
	}

	public EgensotningDetailsEntity withAppliesForOtherProperty(final Boolean appliesForOtherProperty) {
		this.appliesForOtherProperty = appliesForOtherProperty;
		return this;
	}

	public EgensotningDetailsEntity withMotivering(final String motivering) {
		this.motivering = motivering;
		return this;
	}

	public EgensotningDetailsEntity withBilagaPresent(final Boolean bilagaPresent) {
		this.bilagaPresent = bilagaPresent;
		return this;
	}

	public EgensotningDetailsEntity withRegisteredAtProperty(final Boolean registeredAtProperty) {
		this.registeredAtProperty = registeredAtProperty;
		return this;
	}

	public EgensotningDetailsEntity withReapplicationOk(final Boolean reapplicationOk) {
		this.reapplicationOk = reapplicationOk;
		return this;
	}

	public EgensotningDetailsEntity withLastOutcome(final String lastOutcome) {
		this.lastOutcome = lastOutcome;
		return this;
	}

	public EgensotningDetailsEntity withManualReviewReason(final String manualReviewReason) {
		this.manualReviewReason = manualReviewReason;
		return this;
	}

	public EgensotningDetailsEntity withLastVerifiedAt(final OffsetDateTime lastVerifiedAt) {
		this.lastVerifiedAt = lastVerifiedAt;
		return this;
	}

	public EgensotningDetailsEntity withValidFrom(final LocalDate validFrom) {
		this.validFrom = validFrom;
		return this;
	}

	public EgensotningDetailsEntity withValidUntil(final LocalDate validUntil) {
		this.validUntil = validUntil;
		return this;
	}

	public EgensotningDetailsEntity withReminderSentAt(final OffsetDateTime reminderSentAt) {
		this.reminderSentAt = reminderSentAt;
		return this;
	}

	public EgensotningDetailsEntity withDocumentsValid(final Boolean documentsValid) {
		this.documentsValid = documentsValid;
		return this;
	}

	public EgensotningDetailsEntity withDocumentValidationDetail(final String documentValidationDetail) {
		this.documentValidationDetail = documentValidationDetail;
		return this;
	}

	public EgensotningDetailsEntity withDocumentValidatedAt(final OffsetDateTime documentValidatedAt) {
		this.documentValidatedAt = documentValidatedAt;
		return this;
	}

	public EgensotningDetailsEntity withRevokedAt(final OffsetDateTime revokedAt) {
		this.revokedAt = revokedAt;
		return this;
	}

	public EgensotningDetailsEntity withRevocationReason(final String revocationReason) {
		this.revocationReason = revocationReason;
		return this;
	}

	public EgensotningDetailsEntity withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public EgensotningDetailsEntity withModified(final OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final EgensotningDetailsEntity that = (EgensotningDetailsEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(errandId, that.errandId)
			&& Objects.equals(personnummer, that.personnummer) && Objects.equals(fastighetsbeteckning, that.fastighetsbeteckning)
			&& Objects.equals(propertyAddress, that.propertyAddress) && Objects.equals(ownsProperty, that.ownsProperty)
			&& Objects.equals(ownershipMotivation, that.ownershipMotivation) && Objects.equals(appliesForOtherProperty, that.appliesForOtherProperty)
			&& Objects.equals(motivering, that.motivering) && Objects.equals(bilagaPresent, that.bilagaPresent)
			&& Objects.equals(registeredAtProperty, that.registeredAtProperty) && Objects.equals(reapplicationOk, that.reapplicationOk)
			&& Objects.equals(lastOutcome, that.lastOutcome) && Objects.equals(manualReviewReason, that.manualReviewReason)
			&& Objects.equals(lastVerifiedAt, that.lastVerifiedAt) && Objects.equals(validFrom, that.validFrom)
			&& Objects.equals(validUntil, that.validUntil) && Objects.equals(reminderSentAt, that.reminderSentAt)
			&& Objects.equals(documentsValid, that.documentsValid) && Objects.equals(documentValidationDetail, that.documentValidationDetail)
			&& Objects.equals(documentValidatedAt, that.documentValidatedAt) && Objects.equals(revokedAt, that.revokedAt)
			&& Objects.equals(revocationReason, that.revocationReason)
			&& Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, errandId, personnummer, fastighetsbeteckning, propertyAddress, ownsProperty, ownershipMotivation,
			appliesForOtherProperty, motivering, bilagaPresent, registeredAtProperty, reapplicationOk, lastOutcome, manualReviewReason,
			lastVerifiedAt, validFrom, validUntil, reminderSentAt, documentsValid, documentValidationDetail, documentValidatedAt,
			revokedAt, revocationReason, created, modified);
	}

	@Override
	public String toString() {
		return "EgensotningDetailsEntity{id=" + id + ", errandId='" + errandId + "', fastighetsbeteckning='" + fastighetsbeteckning
			+ "', propertyAddress='" + propertyAddress + "', ownsProperty=" + ownsProperty + ", ownershipMotivation='" + ownershipMotivation
			+ "', appliesForOtherProperty=" + appliesForOtherProperty + ", motivering='" + motivering
			+ "', bilagaPresent=" + bilagaPresent + ", registeredAtProperty=" + registeredAtProperty
			+ ", reapplicationOk=" + reapplicationOk + ", lastOutcome='" + lastOutcome + "', manualReviewReason='" + manualReviewReason
			+ "', lastVerifiedAt=" + lastVerifiedAt + ", validFrom=" + validFrom + ", validUntil=" + validUntil
			+ ", reminderSentAt=" + reminderSentAt + ", documentsValid=" + documentsValid + ", documentValidationDetail='" + documentValidationDetail
			+ "', documentValidatedAt=" + documentValidatedAt + ", revokedAt=" + revokedAt + ", revocationReason='" + revocationReason
			+ "', created=" + created + ", modified=" + modified + '}';
	}
}
