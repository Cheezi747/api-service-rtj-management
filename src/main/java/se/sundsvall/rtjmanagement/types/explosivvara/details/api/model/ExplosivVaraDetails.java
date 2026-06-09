package se.sundsvall.rtjmanagement.types.explosivvara.details.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Objects;
import se.sundsvall.dept44.common.validators.annotation.OneOf;
import se.sundsvall.rtjmanagement.core.api.validation.groups.OnCreate;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(
	description = "Type-specific extension fields for EXPLOSIV_VARA errands. One per errand (1:1). Holds the scalar fields from the ansökningsblankett that don't belong on the core Errand envelope.")
public class ExplosivVaraDetails {

	@Schema(description = "Typ av hantering", examples = "STORAGE", allowableValues = {
		"USE", "STORAGE", "TRADE", "TRANSFER"
	})
	@OneOf(value = {
		"USE", "STORAGE", "TRADE", "TRANSFER"
	}, nullable = true)
	private String typAvHantering;

	@Schema(description = "Anläggningstyp — befintlig eller ny anläggning", examples = "EXISTING", allowableValues = {
		"EXISTING", "NEW"
	})
	@OneOf(value = {
		"EXISTING", "NEW"
	}, nullable = true)
	private String anlaggningTyp;

	@Schema(description = "True if the applicant is acting as a proxy (ombud) for another party. When true, an attachment with category=DELEGATION (fullmakt) is expected.",
		examples = "false",
		defaultValue = "false")
	private boolean isProxy;

	@Schema(description = "Fastighetsbeteckning för platsen där varorna hanteras", examples = "Sundsvall Stenstaden 1:23")
	@Size(max = 255)
	private String fastighetsbeteckning;

	@Schema(description = "Adress till hanteringsplats", examples = "Storgatan 5")
	@Size(max = 255)
	private String handlingLocationAddress;

	@Schema(description = "Postnummer för hanteringsplats", examples = "85230")
	@Size(max = 16)
	private String handlingLocationZipCode;

	@Schema(description = "Ort för hanteringsplats", examples = "Sundsvall")
	@Size(max = 255)
	private String handlingLocationCity;

	@Schema(description = "Created timestamp", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime created;

	@Schema(description = "Modified timestamp", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime modified;

	public static ExplosivVaraDetails create() {
		return new ExplosivVaraDetails();
	}

	public String getTypAvHantering() {
		return typAvHantering;
	}

	public void setTypAvHantering(final String typAvHantering) {
		this.typAvHantering = typAvHantering;
	}

	public ExplosivVaraDetails withTypAvHantering(final String typAvHantering) {
		this.typAvHantering = typAvHantering;
		return this;
	}

	public String getAnlaggningTyp() {
		return anlaggningTyp;
	}

	public void setAnlaggningTyp(final String anlaggningTyp) {
		this.anlaggningTyp = anlaggningTyp;
	}

	public ExplosivVaraDetails withAnlaggningTyp(final String anlaggningTyp) {
		this.anlaggningTyp = anlaggningTyp;
		return this;
	}

	public boolean isProxy() {
		return isProxy;
	}

	public void setProxy(final boolean isProxy) {
		this.isProxy = isProxy;
	}

	public ExplosivVaraDetails withProxy(final boolean isProxy) {
		this.isProxy = isProxy;
		return this;
	}

	public String getFastighetsbeteckning() {
		return fastighetsbeteckning;
	}

	public void setFastighetsbeteckning(final String fastighetsbeteckning) {
		this.fastighetsbeteckning = fastighetsbeteckning;
	}

	public ExplosivVaraDetails withFastighetsbeteckning(final String fastighetsbeteckning) {
		this.fastighetsbeteckning = fastighetsbeteckning;
		return this;
	}

	public String getHandlingLocationAddress() {
		return handlingLocationAddress;
	}

	public void setHandlingLocationAddress(final String handlingLocationAddress) {
		this.handlingLocationAddress = handlingLocationAddress;
	}

	public ExplosivVaraDetails withHandlingLocationAddress(final String handlingLocationAddress) {
		this.handlingLocationAddress = handlingLocationAddress;
		return this;
	}

	public String getHandlingLocationZipCode() {
		return handlingLocationZipCode;
	}

	public void setHandlingLocationZipCode(final String handlingLocationZipCode) {
		this.handlingLocationZipCode = handlingLocationZipCode;
	}

	public ExplosivVaraDetails withHandlingLocationZipCode(final String handlingLocationZipCode) {
		this.handlingLocationZipCode = handlingLocationZipCode;
		return this;
	}

	public String getHandlingLocationCity() {
		return handlingLocationCity;
	}

	public void setHandlingLocationCity(final String handlingLocationCity) {
		this.handlingLocationCity = handlingLocationCity;
	}

	public ExplosivVaraDetails withHandlingLocationCity(final String handlingLocationCity) {
		this.handlingLocationCity = handlingLocationCity;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public ExplosivVaraDetails withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime modified) {
		this.modified = modified;
	}

	public ExplosivVaraDetails withModified(final OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final ExplosivVaraDetails that = (ExplosivVaraDetails) o;
		return isProxy == that.isProxy && Objects.equals(typAvHantering, that.typAvHantering)
			&& Objects.equals(anlaggningTyp, that.anlaggningTyp)
			&& Objects.equals(fastighetsbeteckning, that.fastighetsbeteckning)
			&& Objects.equals(handlingLocationAddress, that.handlingLocationAddress)
			&& Objects.equals(handlingLocationZipCode, that.handlingLocationZipCode)
			&& Objects.equals(handlingLocationCity, that.handlingLocationCity)
			&& Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(typAvHantering, anlaggningTyp, isProxy, fastighetsbeteckning, handlingLocationAddress,
			handlingLocationZipCode, handlingLocationCity, created, modified);
	}

	@Override
	public String toString() {
		return "ExplosivVaraDetails{typAvHantering='" + typAvHantering + "', anlaggningTyp='" + anlaggningTyp + "', proxy=" + isProxy
			+ ", fastighetsbeteckning='" + fastighetsbeteckning + "', handlingLocationAddress='" + handlingLocationAddress
			+ "', handlingLocationZipCode='" + handlingLocationZipCode + "', handlingLocationCity='" + handlingLocationCity
			+ "', created=" + created + ", modified=" + modified + '}';
	}
}
