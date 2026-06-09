package se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import se.sundsvall.dept44.common.validators.annotation.OneOf;
import se.sundsvall.rtjmanagement.core.api.validation.groups.OnCreate;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

@Schema(
	description = "One product row in the hazardous-goods inventory for a BRANDFARLIG_VARA errand. N rows per errand. Category groups them into the four buckets defined in LBE: gas, liquids, aerosols, reactive substances. Quantities and storage characteristics drive whether the application is approved/inspected.")
public class HazardousGoodsProduct {

	@Schema(description = "Unique identifier", examples = "cb20c51f-fcf3-42c0-b613-de563634a8ec", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private String id;

	@Schema(description = "Hazard category bucket", examples = "LIQUID", allowableValues = {
		"GAS", "LIQUID", "AEROSOL", "REACTIVE"
	})
	@NotBlank(groups = OnCreate.class)
	@OneOf(value = {
		"GAS", "LIQUID", "AEROSOL", "REACTIVE"
	}, nullable = true)
	private String category;

	@Schema(description = "Product / trade name (varunamn)", examples = "Aspen 4")
	@NotBlank(groups = OnCreate.class)
	private String productName;

	@Schema(description = "Largest handled quantity in the unit given by quantityUnit", examples = "1500.000")
	private BigDecimal quantity;

	@Schema(description = "Unit of the quantity field", examples = "L", allowableValues = {
		"L", "KG", "M3"
	})
	@OneOf(value = {
		"L", "KG", "M3"
	}, nullable = true)
	private String quantityUnit;

	@Schema(description = "Storage type (cistern, IBC, loose container, barrel, pipeline)", examples = "CISTERN", allowableValues = {
		"CISTERN", "IBC", "LOOSE_CONTAINER", "BARREL", "PIPELINE"
	})
	@OneOf(value = {
		"CISTERN", "IBC", "LOOSE_CONTAINER", "BARREL", "PIPELINE"
	}, nullable = true)
	private String storageType;

	@Schema(description = "Storage location", examples = "INDOOR", allowableValues = {
		"INDOOR", "OUTDOOR", "UNDERGROUND"
	})
	@OneOf(value = {
		"INDOOR", "OUTDOOR", "UNDERGROUND"
	}, nullable = true)
	private String storageLocation;

	@Schema(description = "Flash point in degrees Celsius. Only meaningful for LIQUID category.", examples = "23.00")
	private BigDecimal flashPoint;

	@Schema(description = "Created timestamp", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime created;

	@Schema(description = "Modified timestamp", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime modified;

	public static HazardousGoodsProduct create() {
		return new HazardousGoodsProduct();
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public HazardousGoodsProduct withId(final String id) {
		this.id = id;
		return this;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(final String category) {
		this.category = category;
	}

	public HazardousGoodsProduct withCategory(final String category) {
		this.category = category;
		return this;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(final String productName) {
		this.productName = productName;
	}

	public HazardousGoodsProduct withProductName(final String productName) {
		this.productName = productName;
		return this;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(final BigDecimal quantity) {
		this.quantity = quantity;
	}

	public HazardousGoodsProduct withQuantity(final BigDecimal quantity) {
		this.quantity = quantity;
		return this;
	}

	public String getQuantityUnit() {
		return quantityUnit;
	}

	public void setQuantityUnit(final String quantityUnit) {
		this.quantityUnit = quantityUnit;
	}

	public HazardousGoodsProduct withQuantityUnit(final String quantityUnit) {
		this.quantityUnit = quantityUnit;
		return this;
	}

	public String getStorageType() {
		return storageType;
	}

	public void setStorageType(final String storageType) {
		this.storageType = storageType;
	}

	public HazardousGoodsProduct withStorageType(final String storageType) {
		this.storageType = storageType;
		return this;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(final String storageLocation) {
		this.storageLocation = storageLocation;
	}

	public HazardousGoodsProduct withStorageLocation(final String storageLocation) {
		this.storageLocation = storageLocation;
		return this;
	}

	public BigDecimal getFlashPoint() {
		return flashPoint;
	}

	public void setFlashPoint(final BigDecimal flashPoint) {
		this.flashPoint = flashPoint;
	}

	public HazardousGoodsProduct withFlashPoint(final BigDecimal flashPoint) {
		this.flashPoint = flashPoint;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public HazardousGoodsProduct withCreated(final OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime modified) {
		this.modified = modified;
	}

	public HazardousGoodsProduct withModified(final OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final HazardousGoodsProduct that = (HazardousGoodsProduct) o;
		return Objects.equals(id, that.id) && Objects.equals(category, that.category)
			&& Objects.equals(productName, that.productName) && Objects.equals(quantity, that.quantity)
			&& Objects.equals(quantityUnit, that.quantityUnit) && Objects.equals(storageType, that.storageType)
			&& Objects.equals(storageLocation, that.storageLocation) && Objects.equals(flashPoint, that.flashPoint)
			&& Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, category, productName, quantity, quantityUnit, storageType, storageLocation, flashPoint,
			created, modified);
	}

	@Override
	public String toString() {
		return "HazardousGoodsProduct{id='" + id + "', category='" + category + "', productName='" + productName
			+ "', quantity=" + quantity + ", quantityUnit='" + quantityUnit + "', storageType='" + storageType
			+ "', storageLocation='" + storageLocation + "', flashPoint=" + flashPoint
			+ ", created=" + created + ", modified=" + modified + '}';
	}
}
