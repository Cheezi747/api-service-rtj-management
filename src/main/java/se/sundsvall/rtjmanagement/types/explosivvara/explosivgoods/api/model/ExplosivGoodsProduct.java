package se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.api.model;

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
	description = "One product row in the explosive-goods inventory for an EXPLOSIV_VARA errand. N rows per errand. Hazard class groups them into the six MSBFS 2010:4 riskklasser (1.1–1.6). Quantities and storage characteristics drive whether the application is approved/inspected.")
public class ExplosivGoodsProduct {

	@Schema(description = "Unique identifier", examples = "cb20c51f-fcf3-42c0-b613-de563634a8ec", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private String id;

	@Schema(description = "Riskklass enligt MSBFS 2010:4", examples = "1.1", allowableValues = {
		"1.1", "1.2", "1.3", "1.4", "1.5", "1.6"
	})
	@NotBlank(groups = OnCreate.class)
	@OneOf(value = {
		"1.1", "1.2", "1.3", "1.4", "1.5", "1.6"
	}, nullable = true)
	private String hazardClass;

	@Schema(description = "Product / trade name (varunamn)", examples = "Dynamit")
	@NotBlank(groups = OnCreate.class)
	private String productName;

	@Schema(description = "Largest handled quantity in the unit given by quantityUnit", examples = "250.000")
	private BigDecimal quantity;

	@Schema(description = "Unit of the quantity field", examples = "KG", allowableValues = {
		"KG", "STK", "L"
	})
	@OneOf(value = {
		"KG", "STK", "L"
	}, nullable = true)
	private String quantityUnit;

	@Schema(description = "Storage type (cistern, IBC, loose container, barrel, magazine)", examples = "MAGAZINE", allowableValues = {
		"CISTERN", "IBC", "LOOSE_CONTAINER", "BARREL", "MAGAZINE"
	})
	@OneOf(value = {
		"CISTERN", "IBC", "LOOSE_CONTAINER", "BARREL", "MAGAZINE"
	}, nullable = true)
	private String storageType;

	@Schema(description = "Storage location", examples = "INDOOR", allowableValues = {
		"INDOOR", "OUTDOOR", "UNDERGROUND"
	})
	@OneOf(value = {
		"INDOOR", "OUTDOOR", "UNDERGROUND"
	}, nullable = true)
	private String storageLocation;

	@Schema(description = "Created timestamp", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime created;

	@Schema(description = "Modified timestamp", accessMode = READ_ONLY)
	@Null(groups = OnCreate.class)
	private OffsetDateTime modified;

	public static ExplosivGoodsProduct create() {
		return new ExplosivGoodsProduct();
	}

	public String getId() {
		return id;
	}

	public void setId(final String v) {
		this.id = v;
	}

	public ExplosivGoodsProduct withId(final String v) {
		this.id = v;
		return this;
	}

	public String getHazardClass() {
		return hazardClass;
	}

	public void setHazardClass(final String v) {
		this.hazardClass = v;
	}

	public ExplosivGoodsProduct withHazardClass(final String v) {
		this.hazardClass = v;
		return this;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(final String v) {
		this.productName = v;
	}

	public ExplosivGoodsProduct withProductName(final String v) {
		this.productName = v;
		return this;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(final BigDecimal v) {
		this.quantity = v;
	}

	public ExplosivGoodsProduct withQuantity(final BigDecimal v) {
		this.quantity = v;
		return this;
	}

	public String getQuantityUnit() {
		return quantityUnit;
	}

	public void setQuantityUnit(final String v) {
		this.quantityUnit = v;
	}

	public ExplosivGoodsProduct withQuantityUnit(final String v) {
		this.quantityUnit = v;
		return this;
	}

	public String getStorageType() {
		return storageType;
	}

	public void setStorageType(final String v) {
		this.storageType = v;
	}

	public ExplosivGoodsProduct withStorageType(final String v) {
		this.storageType = v;
		return this;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(final String v) {
		this.storageLocation = v;
	}

	public ExplosivGoodsProduct withStorageLocation(final String v) {
		this.storageLocation = v;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime v) {
		this.created = v;
	}

	public ExplosivGoodsProduct withCreated(final OffsetDateTime v) {
		this.created = v;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(final OffsetDateTime v) {
		this.modified = v;
	}

	public ExplosivGoodsProduct withModified(final OffsetDateTime v) {
		this.modified = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final ExplosivGoodsProduct that = (ExplosivGoodsProduct) o;
		return Objects.equals(id, that.id) && Objects.equals(hazardClass, that.hazardClass)
			&& Objects.equals(productName, that.productName) && Objects.equals(quantity, that.quantity)
			&& Objects.equals(quantityUnit, that.quantityUnit) && Objects.equals(storageType, that.storageType)
			&& Objects.equals(storageLocation, that.storageLocation)
			&& Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, hazardClass, productName, quantity, quantityUnit, storageType, storageLocation,
			created, modified);
	}

	@Override
	public String toString() {
		return "ExplosivGoodsProduct{id='" + id + "', hazardClass='" + hazardClass + "', productName='" + productName
			+ "', quantity=" + quantity + ", quantityUnit='" + quantityUnit + "', storageType='" + storageType
			+ "', storageLocation='" + storageLocation + "', created=" + created + ", modified=" + modified + '}';
	}
}
