package se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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

	public OffsetDateTime getCreated() {
		return created;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setId(final Long v) {
		this.id = v;
	}

	public void setErrandId(final String v) {
		this.errandId = v;
	}

	public void setPersonnummer(final String v) {
		this.personnummer = v;
	}

	public void setFastighetsbeteckning(final String v) {
		this.fastighetsbeteckning = v;
	}

	public void setPropertyAddress(final String v) {
		this.propertyAddress = v;
	}

	public void setBilagaPresent(final Boolean v) {
		this.bilagaPresent = v;
	}

	public void setRegisteredAtProperty(final Boolean v) {
		this.registeredAtProperty = v;
	}

	public void setReapplicationOk(final Boolean v) {
		this.reapplicationOk = v;
	}

	public void setLastOutcome(final String v) {
		this.lastOutcome = v;
	}

	public void setManualReviewReason(final String v) {
		this.manualReviewReason = v;
	}

	public void setLastVerifiedAt(final OffsetDateTime v) {
		this.lastVerifiedAt = v;
	}

	@Override
	public void setCreated(final OffsetDateTime v) {
		this.created = v;
	}

	@Override
	public void setModified(final OffsetDateTime v) {
		this.modified = v;
	}

	public EgensotningDetailsEntity withId(final Long v) {
		this.id = v;
		return this;
	}

	public EgensotningDetailsEntity withErrandId(final String v) {
		this.errandId = v;
		return this;
	}

	public EgensotningDetailsEntity withPersonnummer(final String v) {
		this.personnummer = v;
		return this;
	}

	public EgensotningDetailsEntity withFastighetsbeteckning(final String v) {
		this.fastighetsbeteckning = v;
		return this;
	}

	public EgensotningDetailsEntity withPropertyAddress(final String v) {
		this.propertyAddress = v;
		return this;
	}

	public EgensotningDetailsEntity withBilagaPresent(final Boolean v) {
		this.bilagaPresent = v;
		return this;
	}

	public EgensotningDetailsEntity withRegisteredAtProperty(final Boolean v) {
		this.registeredAtProperty = v;
		return this;
	}

	public EgensotningDetailsEntity withReapplicationOk(final Boolean v) {
		this.reapplicationOk = v;
		return this;
	}

	public EgensotningDetailsEntity withLastOutcome(final String v) {
		this.lastOutcome = v;
		return this;
	}

	public EgensotningDetailsEntity withManualReviewReason(final String v) {
		this.manualReviewReason = v;
		return this;
	}

	public EgensotningDetailsEntity withLastVerifiedAt(final OffsetDateTime v) {
		this.lastVerifiedAt = v;
		return this;
	}

	public EgensotningDetailsEntity withCreated(final OffsetDateTime v) {
		this.created = v;
		return this;
	}

	public EgensotningDetailsEntity withModified(final OffsetDateTime v) {
		this.modified = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final EgensotningDetailsEntity that = (EgensotningDetailsEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(errandId, that.errandId)
			&& Objects.equals(personnummer, that.personnummer) && Objects.equals(fastighetsbeteckning, that.fastighetsbeteckning)
			&& Objects.equals(propertyAddress, that.propertyAddress) && Objects.equals(bilagaPresent, that.bilagaPresent)
			&& Objects.equals(registeredAtProperty, that.registeredAtProperty) && Objects.equals(reapplicationOk, that.reapplicationOk)
			&& Objects.equals(lastOutcome, that.lastOutcome) && Objects.equals(manualReviewReason, that.manualReviewReason)
			&& Objects.equals(lastVerifiedAt, that.lastVerifiedAt)
			&& Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, errandId, personnummer, fastighetsbeteckning, propertyAddress, bilagaPresent,
			registeredAtProperty, reapplicationOk, lastOutcome, manualReviewReason, lastVerifiedAt, created, modified);
	}

	@Override
	public String toString() {
		return "EgensotningDetailsEntity{id=" + id + ", errandId='" + errandId + "', fastighetsbeteckning='" + fastighetsbeteckning
			+ "', propertyAddress='" + propertyAddress + "', bilagaPresent=" + bilagaPresent + ", registeredAtProperty=" + registeredAtProperty
			+ ", reapplicationOk=" + reapplicationOk + ", lastOutcome='" + lastOutcome + "', manualReviewReason='" + manualReviewReason
			+ "', lastVerifiedAt=" + lastVerifiedAt + ", created=" + created + ", modified=" + modified + '}';
	}
}
