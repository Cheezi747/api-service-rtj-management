package se.sundsvall.rtjmanagement.types.explosivvara.details.integration.db.model;

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

@Entity
@Table(name = "explosiv_vara_details",
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_explosiv_vara_details_errand_id", columnNames = "errand_id")
	})
@EntityListeners(AuditableListener.class)
public class ExplosivVaraDetailsEntity implements Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "errand_id", nullable = false, length = 255)
	private String errandId;

	@Column(name = "typ_av_hantering", length = 64)
	private String typAvHantering;

	@Column(name = "is_proxy", nullable = false)
	private boolean isProxy;

	@Column(name = "fastighetsbeteckning")
	private String fastighetsbeteckning;

	@Column(name = "handling_location_address")
	private String handlingLocationAddress;

	@Column(name = "handling_location_zip_code", length = 16)
	private String handlingLocationZipCode;

	@Column(name = "handling_location_city")
	private String handlingLocationCity;

	@Column(name = "created")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	private OffsetDateTime created;

	@Column(name = "modified")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	private OffsetDateTime modified;

	public static ExplosivVaraDetailsEntity create() {
		return new ExplosivVaraDetailsEntity();
	}

	public Long getId() {
		return id;
	}

	public String getErrandId() {
		return errandId;
	}

	public String getTypAvHantering() {
		return typAvHantering;
	}

	public boolean isProxy() {
		return isProxy;
	}

	public String getFastighetsbeteckning() {
		return fastighetsbeteckning;
	}

	public String getHandlingLocationAddress() {
		return handlingLocationAddress;
	}

	public String getHandlingLocationZipCode() {
		return handlingLocationZipCode;
	}

	public String getHandlingLocationCity() {
		return handlingLocationCity;
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

	public void setTypAvHantering(final String v) {
		this.typAvHantering = v;
	}

	public void setProxy(final boolean v) {
		this.isProxy = v;
	}

	public void setFastighetsbeteckning(final String v) {
		this.fastighetsbeteckning = v;
	}

	public void setHandlingLocationAddress(final String v) {
		this.handlingLocationAddress = v;
	}

	public void setHandlingLocationZipCode(final String v) {
		this.handlingLocationZipCode = v;
	}

	public void setHandlingLocationCity(final String v) {
		this.handlingLocationCity = v;
	}

	@Override
	public void setCreated(final OffsetDateTime v) {
		this.created = v;
	}

	@Override
	public void setModified(final OffsetDateTime v) {
		this.modified = v;
	}

	public ExplosivVaraDetailsEntity withId(final Long v) {
		this.id = v;
		return this;
	}

	public ExplosivVaraDetailsEntity withErrandId(final String v) {
		this.errandId = v;
		return this;
	}

	public ExplosivVaraDetailsEntity withTypAvHantering(final String v) {
		this.typAvHantering = v;
		return this;
	}

	public ExplosivVaraDetailsEntity withProxy(final boolean v) {
		this.isProxy = v;
		return this;
	}

	public ExplosivVaraDetailsEntity withFastighetsbeteckning(final String v) {
		this.fastighetsbeteckning = v;
		return this;
	}

	public ExplosivVaraDetailsEntity withHandlingLocationAddress(final String v) {
		this.handlingLocationAddress = v;
		return this;
	}

	public ExplosivVaraDetailsEntity withHandlingLocationZipCode(final String v) {
		this.handlingLocationZipCode = v;
		return this;
	}

	public ExplosivVaraDetailsEntity withHandlingLocationCity(final String v) {
		this.handlingLocationCity = v;
		return this;
	}

	public ExplosivVaraDetailsEntity withCreated(final OffsetDateTime v) {
		this.created = v;
		return this;
	}

	public ExplosivVaraDetailsEntity withModified(final OffsetDateTime v) {
		this.modified = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final ExplosivVaraDetailsEntity that = (ExplosivVaraDetailsEntity) o;
		return isProxy == that.isProxy && Objects.equals(id, that.id) && Objects.equals(errandId, that.errandId)
			&& Objects.equals(typAvHantering, that.typAvHantering) && Objects.equals(fastighetsbeteckning, that.fastighetsbeteckning)
			&& Objects.equals(handlingLocationAddress, that.handlingLocationAddress)
			&& Objects.equals(handlingLocationZipCode, that.handlingLocationZipCode)
			&& Objects.equals(handlingLocationCity, that.handlingLocationCity)
			&& Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, errandId, typAvHantering, isProxy, fastighetsbeteckning,
			handlingLocationAddress, handlingLocationZipCode, handlingLocationCity, created, modified);
	}

	@Override
	public String toString() {
		return "ExplosivVaraDetailsEntity{id=" + id + ", errandId='" + errandId + "', typAvHantering='" + typAvHantering
			+ "', isProxy=" + isProxy + ", fastighetsbeteckning='" + fastighetsbeteckning
			+ "', handlingLocation=" + handlingLocationAddress + " " + handlingLocationZipCode + " " + handlingLocationCity
			+ ", created=" + created + ", modified=" + modified + '}';
	}
}
