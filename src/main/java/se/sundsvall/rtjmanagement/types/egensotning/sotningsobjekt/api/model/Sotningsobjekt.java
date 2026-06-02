package se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import java.time.OffsetDateTime;
import java.util.Objects;
import se.sundsvall.rtjmanagement.core.api.validation.groups.OnCreate;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(
	description = "Ett sotningsobjekt (eldstad/anläggning) som omfattas av en EGENSOTNING-ansökan. N rader per ärende — speglar per-objekt-tabellen i beslutet.")
public class Sotningsobjekt {

	@Schema(description = "Unikt id", examples = "cb20c51f-fcf3-42c0-b613-de563634a8ec", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private String id;

	@Schema(description = "Typ av objekt", examples = "Värmepanna")
	@NotBlank(groups = OnCreate.class)
	private String typ;

	@Schema(description = "Fabrikat", examples = "CTC")
	private String fabrikat;

	@Schema(description = "Tillverkningsår", examples = "1998")
	private Integer tillverkningsar;

	@Schema(description = "Bränsleslag", examples = "Ved")
	private String bransleslag;

	@Schema(description = "Bränslemängd per år", examples = "12 m³")
	private String branslemangd;

	@Schema(description = "Sotningsintervall i veckor", examples = "8")
	private Integer sotningsintervallVeckor;

	@Schema(description = "Created timestamp", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime created;

	@Schema(description = "Modified timestamp", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime modified;

	public static Sotningsobjekt create() {
		return new Sotningsobjekt();
	}

	public String getId() {
		return id;
	}

	public void setId(final String v) {
		this.id = v;
	}

	public Sotningsobjekt withId(final String v) {
		this.id = v;
		return this;
	}

	public String getTyp() {
		return typ;
	}

	public void setTyp(final String v) {
		this.typ = v;
	}

	public Sotningsobjekt withTyp(final String v) {
		this.typ = v;
		return this;
	}

	public String getFabrikat() {
		return fabrikat;
	}

	public void setFabrikat(final String v) {
		this.fabrikat = v;
	}

	public Sotningsobjekt withFabrikat(final String v) {
		this.fabrikat = v;
		return this;
	}

	public Integer getTillverkningsar() {
		return tillverkningsar;
	}

	public void setTillverkningsar(final Integer v) {
		this.tillverkningsar = v;
	}

	public Sotningsobjekt withTillverkningsar(final Integer v) {
		this.tillverkningsar = v;
		return this;
	}

	public String getBransleslag() {
		return bransleslag;
	}

	public void setBransleslag(final String v) {
		this.bransleslag = v;
	}

	public Sotningsobjekt withBransleslag(final String v) {
		this.bransleslag = v;
		return this;
	}

	public String getBranslemangd() {
		return branslemangd;
	}

	public void setBranslemangd(final String v) {
		this.branslemangd = v;
	}

	public Sotningsobjekt withBranslemangd(final String v) {
		this.branslemangd = v;
		return this;
	}

	public Integer getSotningsintervallVeckor() {
		return sotningsintervallVeckor;
	}

	public void setSotningsintervallVeckor(final Integer v) {
		this.sotningsintervallVeckor = v;
	}

	public Sotningsobjekt withSotningsintervallVeckor(final Integer v) {
		this.sotningsintervallVeckor = v;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime v) {
		this.created = v;
	}

	public Sotningsobjekt withCreated(final OffsetDateTime v) {
		this.created = v;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime v) {
		this.modified = v;
	}

	public Sotningsobjekt withModified(final OffsetDateTime v) {
		this.modified = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final Sotningsobjekt that = (Sotningsobjekt) o;
		return Objects.equals(id, that.id) && Objects.equals(typ, that.typ) && Objects.equals(fabrikat, that.fabrikat)
			&& Objects.equals(tillverkningsar, that.tillverkningsar) && Objects.equals(bransleslag, that.bransleslag)
			&& Objects.equals(branslemangd, that.branslemangd) && Objects.equals(sotningsintervallVeckor, that.sotningsintervallVeckor)
			&& Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, typ, fabrikat, tillverkningsar, bransleslag, branslemangd, sotningsintervallVeckor, created, modified);
	}

	@Override
	public String toString() {
		return "Sotningsobjekt{id='" + id + "', typ='" + typ + "', fabrikat='" + fabrikat + "', tillverkningsar=" + tillverkningsar
			+ ", bransleslag='" + bransleslag + "', branslemangd='" + branslemangd + "', sotningsintervallVeckor=" + sotningsintervallVeckor
			+ ", created=" + created + ", modified=" + modified + '}';
	}
}
