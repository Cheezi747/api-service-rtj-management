package se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;
import org.hibernate.annotations.UuidGenerator;
import se.sundsvall.rtjmanagement.shared.Auditable;
import se.sundsvall.rtjmanagement.shared.AuditableListener;

/**
 * One sotningsobjekt (eldstad/anläggning) covered by an EGENSOTNING errand. N rows per
 * errand — mirrors the per-objekt-tabell in the formal beslut.
 */
@Entity
@Table(name = "egensotning_sotningsobjekt",
	indexes = {
		@Index(name = "idx_egensotning_sotningsobjekt_errand_id", columnList = "errand_id")
	})
@EntityListeners(AuditableListener.class)
public class SotningsobjektEntity implements Auditable {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "errand_id", nullable = false, length = 255)
	private String errandId;

	@Column(name = "fabrikat")
	private String fabrikat;

	@Column(name = "typ", length = 64)
	private String typ;

	@Column(name = "tillverkningsar")
	private Integer tillverkningsar;

	@Column(name = "bransleslag", length = 64)
	private String bransleslag;

	@Column(name = "branslemangd", length = 64)
	private String branslemangd;

	@Column(name = "sotningsintervall_veckor")
	private Integer sotningsintervallVeckor;

	@Column(name = "created")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	private OffsetDateTime created;

	@Column(name = "modified")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	private OffsetDateTime modified;

	public static SotningsobjektEntity create() {
		return new SotningsobjektEntity();
	}

	public String getId() {
		return id;
	}

	public String getErrandId() {
		return errandId;
	}

	public String getFabrikat() {
		return fabrikat;
	}

	public String getTyp() {
		return typ;
	}

	public Integer getTillverkningsar() {
		return tillverkningsar;
	}

	public String getBransleslag() {
		return bransleslag;
	}

	public String getBranslemangd() {
		return branslemangd;
	}

	public Integer getSotningsintervallVeckor() {
		return sotningsintervallVeckor;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setErrandId(final String errandId) {
		this.errandId = errandId;
	}

	public void setFabrikat(final String fabrikat) {
		this.fabrikat = fabrikat;
	}

	public void setTyp(final String typ) {
		this.typ = typ;
	}

	public void setTillverkningsar(final Integer tillverkningsar) {
		this.tillverkningsar = tillverkningsar;
	}

	public void setBransleslag(final String bransleslag) {
		this.bransleslag = bransleslag;
	}

	public void setBranslemangd(final String branslemangd) {
		this.branslemangd = branslemangd;
	}

	public void setSotningsintervallVeckor(final Integer sotningsintervallVeckor) {
		this.sotningsintervallVeckor = sotningsintervallVeckor;
	}

	@Override
	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	@Override
	public void setModified(final OffsetDateTime modified) {
		this.modified = modified;
	}

	public SotningsobjektEntity withId(final String id) {
		this.id = id;
		return this;
	}

	public SotningsobjektEntity withErrandId(final String errandId) {
		this.errandId = errandId;
		return this;
	}

	public SotningsobjektEntity withFabrikat(final String fabrikat) {
		this.fabrikat = fabrikat;
		return this;
	}

	public SotningsobjektEntity withTyp(final String typ) {
		this.typ = typ;
		return this;
	}

	public SotningsobjektEntity withTillverkningsar(final Integer tillverkningsar) {
		this.tillverkningsar = tillverkningsar;
		return this;
	}

	public SotningsobjektEntity withBransleslag(final String bransleslag) {
		this.bransleslag = bransleslag;
		return this;
	}

	public SotningsobjektEntity withBranslemangd(final String branslemangd) {
		this.branslemangd = branslemangd;
		return this;
	}

	public SotningsobjektEntity withSotningsintervallVeckor(final Integer sotningsintervallVeckor) {
		this.sotningsintervallVeckor = sotningsintervallVeckor;
		return this;
	}

	public SotningsobjektEntity withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public SotningsobjektEntity withModified(final OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final SotningsobjektEntity that = (SotningsobjektEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(errandId, that.errandId) && Objects.equals(fabrikat, that.fabrikat)
			&& Objects.equals(typ, that.typ) && Objects.equals(tillverkningsar, that.tillverkningsar)
			&& Objects.equals(bransleslag, that.bransleslag) && Objects.equals(branslemangd, that.branslemangd)
			&& Objects.equals(sotningsintervallVeckor, that.sotningsintervallVeckor)
			&& Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, errandId, fabrikat, typ, tillverkningsar, bransleslag, branslemangd, sotningsintervallVeckor, created, modified);
	}

	@Override
	public String toString() {
		return "SotningsobjektEntity{id='" + id + "', errandId='" + errandId + "', fabrikat='" + fabrikat + "', typ='" + typ
			+ "', tillverkningsar=" + tillverkningsar + ", bransleslag='" + bransleslag + "', branslemangd='" + branslemangd
			+ "', sotningsintervallVeckor=" + sotningsintervallVeckor + ", created=" + created + ", modified=" + modified + '}';
	}
}
