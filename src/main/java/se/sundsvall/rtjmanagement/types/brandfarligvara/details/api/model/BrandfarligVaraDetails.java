package se.sundsvall.rtjmanagement.types.brandfarligvara.details.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Objects;
import se.sundsvall.dept44.common.validators.annotation.OneOf;
import se.sundsvall.rtjmanagement.core.api.validation.groups.OnCreate;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(
	description = "Type-specific extension fields for BRANDFARLIG_VARA errands. One per errand (1:1). Holds the scalar fields from the ansökningsblankett that don't belong on the core Errand envelope.")
public class BrandfarligVaraDetails {

	@Schema(description = "Verksamhetstyp", examples = "RESTAURANT", allowableValues = {
		"RESTAURANT", "FUEL_STATION", "RETAIL", "OTHER"
	})
	@OneOf(value = {
		"RESTAURANT", "FUEL_STATION", "RETAIL", "OTHER"
	}, nullable = true)
	private String verksamhetstyp;

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

	public static BrandfarligVaraDetails create() {
		return new BrandfarligVaraDetails();
	}

	public String getVerksamhetstyp() {
		return verksamhetstyp;
	}

	public void setVerksamhetstyp(final String v) {
		this.verksamhetstyp = v;
	}

	public BrandfarligVaraDetails withVerksamhetstyp(final String v) {
		this.verksamhetstyp = v;
		return this;
	}

	public boolean isProxy() {
		return isProxy;
	}

	public void setProxy(final boolean v) {
		this.isProxy = v;
	}

	public BrandfarligVaraDetails withProxy(final boolean v) {
		this.isProxy = v;
		return this;
	}

	public String getFastighetsbeteckning() {
		return fastighetsbeteckning;
	}

	public void setFastighetsbeteckning(final String v) {
		this.fastighetsbeteckning = v;
	}

	public BrandfarligVaraDetails withFastighetsbeteckning(final String v) {
		this.fastighetsbeteckning = v;
		return this;
	}

	public String getHandlingLocationAddress() {
		return handlingLocationAddress;
	}

	public void setHandlingLocationAddress(final String v) {
		this.handlingLocationAddress = v;
	}

	public BrandfarligVaraDetails withHandlingLocationAddress(final String v) {
		this.handlingLocationAddress = v;
		return this;
	}

	public String getHandlingLocationZipCode() {
		return handlingLocationZipCode;
	}

	public void setHandlingLocationZipCode(final String v) {
		this.handlingLocationZipCode = v;
	}

	public BrandfarligVaraDetails withHandlingLocationZipCode(final String v) {
		this.handlingLocationZipCode = v;
		return this;
	}

	public String getHandlingLocationCity() {
		return handlingLocationCity;
	}

	public void setHandlingLocationCity(final String v) {
		this.handlingLocationCity = v;
	}

	public BrandfarligVaraDetails withHandlingLocationCity(final String v) {
		this.handlingLocationCity = v;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime v) {
		this.created = v;
	}

	public BrandfarligVaraDetails withCreated(final OffsetDateTime v) {
		this.created = v;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime v) {
		this.modified = v;
	}

	public BrandfarligVaraDetails withModified(final OffsetDateTime v) {
		this.modified = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final BrandfarligVaraDetails that = (BrandfarligVaraDetails) o;
		return isProxy == that.isProxy && Objects.equals(verksamhetstyp, that.verksamhetstyp)
			&& Objects.equals(fastighetsbeteckning, that.fastighetsbeteckning)
			&& Objects.equals(handlingLocationAddress, that.handlingLocationAddress)
			&& Objects.equals(handlingLocationZipCode, that.handlingLocationZipCode)
			&& Objects.equals(handlingLocationCity, that.handlingLocationCity)
			&& Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(verksamhetstyp, isProxy, fastighetsbeteckning, handlingLocationAddress,
			handlingLocationZipCode, handlingLocationCity, created, modified);
	}

	@Override
	public String toString() {
		return "BrandfarligVaraDetails{verksamhetstyp='" + verksamhetstyp + "', proxy=" + isProxy
			+ ", fastighetsbeteckning='" + fastighetsbeteckning + "', handlingLocationAddress='" + handlingLocationAddress
			+ "', handlingLocationZipCode='" + handlingLocationZipCode + "', handlingLocationCity='" + handlingLocationCity
			+ "', created=" + created + ", modified=" + modified + '}';
	}
}
