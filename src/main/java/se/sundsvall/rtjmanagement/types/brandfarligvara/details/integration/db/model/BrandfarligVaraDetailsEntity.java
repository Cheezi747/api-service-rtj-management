package se.sundsvall.rtjmanagement.types.brandfarligvara.details.integration.db.model;

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
@Table(name = "brandfarlig_vara_details",
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_brandfarlig_vara_details_errand_id", columnNames = "errand_id")
	})
@EntityListeners(AuditableListener.class)
public class BrandfarligVaraDetailsEntity implements Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "errand_id", nullable = false, length = 255)
	private String errandId;

	@Column(name = "verksamhetstyp", length = 64)
	private String verksamhetstyp;

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

	public static BrandfarligVaraDetailsEntity create() {
		return new BrandfarligVaraDetailsEntity();
	}

	public Long getId() {
		return id;
	}

	public String getErrandId() {
		return errandId;
	}

	public String getVerksamhetstyp() {
		return verksamhetstyp;
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

	public void setVerksamhetstyp(final String v) {
		this.verksamhetstyp = v;
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

	public BrandfarligVaraDetailsEntity withId(final Long v) {
		this.id = v;
		return this;
	}

	public BrandfarligVaraDetailsEntity withErrandId(final String v) {
		this.errandId = v;
		return this;
	}

	public BrandfarligVaraDetailsEntity withVerksamhetstyp(final String v) {
		this.verksamhetstyp = v;
		return this;
	}

	public BrandfarligVaraDetailsEntity withProxy(final boolean v) {
		this.isProxy = v;
		return this;
	}

	public BrandfarligVaraDetailsEntity withFastighetsbeteckning(final String v) {
		this.fastighetsbeteckning = v;
		return this;
	}

	public BrandfarligVaraDetailsEntity withHandlingLocationAddress(final String v) {
		this.handlingLocationAddress = v;
		return this;
	}

	public BrandfarligVaraDetailsEntity withHandlingLocationZipCode(final String v) {
		this.handlingLocationZipCode = v;
		return this;
	}

	public BrandfarligVaraDetailsEntity withHandlingLocationCity(final String v) {
		this.handlingLocationCity = v;
		return this;
	}

	public BrandfarligVaraDetailsEntity withCreated(final OffsetDateTime v) {
		this.created = v;
		return this;
	}

	public BrandfarligVaraDetailsEntity withModified(final OffsetDateTime v) {
		this.modified = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final BrandfarligVaraDetailsEntity that = (BrandfarligVaraDetailsEntity) o;
		return isProxy == that.isProxy && Objects.equals(id, that.id) && Objects.equals(errandId, that.errandId)
			&& Objects.equals(verksamhetstyp, that.verksamhetstyp) && Objects.equals(fastighetsbeteckning, that.fastighetsbeteckning)
			&& Objects.equals(handlingLocationAddress, that.handlingLocationAddress)
			&& Objects.equals(handlingLocationZipCode, that.handlingLocationZipCode)
			&& Objects.equals(handlingLocationCity, that.handlingLocationCity)
			&& Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, errandId, verksamhetstyp, isProxy, fastighetsbeteckning,
			handlingLocationAddress, handlingLocationZipCode, handlingLocationCity, created, modified);
	}

	@Override
	public String toString() {
		return "BrandfarligVaraDetailsEntity{id=" + id + ", errandId='" + errandId + "', verksamhetstyp='" + verksamhetstyp
			+ "', isProxy=" + isProxy + ", fastighetsbeteckning='" + fastighetsbeteckning
			+ "', handlingLocation=" + handlingLocationAddress + " " + handlingLocationZipCode + " " + handlingLocationCity
			+ ", created=" + created + ", modified=" + modified + '}';
	}
}
