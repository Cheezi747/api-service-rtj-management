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

	public void setTypAvHantering(final String v) {
		this.typAvHantering = v;
	}

	public ExplosivVaraDetails withTypAvHantering(final String v) {
		this.typAvHantering = v;
		return this;
	}

	public boolean isProxy() {
		return isProxy;
	}

	public void setProxy(final boolean v) {
		this.isProxy = v;
	}

	public ExplosivVaraDetails withProxy(final boolean v) {
		this.isProxy = v;
		return this;
	}

	public String getFastighetsbeteckning() {
		return fastighetsbeteckning;
	}

	public void setFastighetsbeteckning(final String v) {
		this.fastighetsbeteckning = v;
	}

	public ExplosivVaraDetails withFastighetsbeteckning(final String v) {
		this.fastighetsbeteckning = v;
		return this;
	}

	public String getHandlingLocationAddress() {
		return handlingLocationAddress;
	}

	public void setHandlingLocationAddress(final String v) {
		this.handlingLocationAddress = v;
	}

	public ExplosivVaraDetails withHandlingLocationAddress(final String v) {
		this.handlingLocationAddress = v;
		return this;
	}

	public String getHandlingLocationZipCode() {
		return handlingLocationZipCode;
	}

	public void setHandlingLocationZipCode(final String v) {
		this.handlingLocationZipCode = v;
	}

	public ExplosivVaraDetails withHandlingLocationZipCode(final String v) {
		this.handlingLocationZipCode = v;
		return this;
	}

	public String getHandlingLocationCity() {
		return handlingLocationCity;
	}

	public void setHandlingLocationCity(final String v) {
		this.handlingLocationCity = v;
	}

	public ExplosivVaraDetails withHandlingLocationCity(final String v) {
		this.handlingLocationCity = v;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime v) {
		this.created = v;
	}

	public ExplosivVaraDetails withCreated(final OffsetDateTime v) {
		this.created = v;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime v) {
		this.modified = v;
	}

	public ExplosivVaraDetails withModified(final OffsetDateTime v) {
		this.modified = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final ExplosivVaraDetails that = (ExplosivVaraDetails) o;
		return isProxy == that.isProxy && Objects.equals(typAvHantering, that.typAvHantering)
			&& Objects.equals(fastighetsbeteckning, that.fastighetsbeteckning)
			&& Objects.equals(handlingLocationAddress, that.handlingLocationAddress)
			&& Objects.equals(handlingLocationZipCode, that.handlingLocationZipCode)
			&& Objects.equals(handlingLocationCity, that.handlingLocationCity)
			&& Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(typAvHantering, isProxy, fastighetsbeteckning, handlingLocationAddress,
			handlingLocationZipCode, handlingLocationCity, created, modified);
	}

	@Override
	public String toString() {
		return "ExplosivVaraDetails{typAvHantering='" + typAvHantering + "', proxy=" + isProxy
			+ ", fastighetsbeteckning='" + fastighetsbeteckning + "', handlingLocationAddress='" + handlingLocationAddress
			+ "', handlingLocationZipCode='" + handlingLocationZipCode + "', handlingLocationCity='" + handlingLocationCity
			+ "', created=" + created + ", modified=" + modified + '}';
	}
}
