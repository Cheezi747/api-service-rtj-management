package se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.integration.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;
import org.hibernate.annotations.UuidGenerator;
import se.sundsvall.rtjmanagement.shared.Auditable;
import se.sundsvall.rtjmanagement.shared.AuditableListener;

@Entity
@Table(name = "hazardous_goods_product",
	indexes = {
		@Index(name = "idx_hazardous_goods_errand_id", columnList = "errand_id"),
		@Index(name = "idx_hazardous_goods_category", columnList = "category")
	})
@EntityListeners(AuditableListener.class)
public class HazardousGoodsProductEntity implements Auditable {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "errand_id", nullable = false, length = 255)
	private String errandId;

	@Column(name = "category", nullable = false, length = 32)
	private String category;

	@Column(name = "product_name", nullable = false)
	private String productName;

	@Column(name = "quantity", precision = 18, scale = 3)
	private BigDecimal quantity;

	@Column(name = "quantity_unit", length = 32)
	private String quantityUnit;

	@Column(name = "storage_type", length = 64)
	private String storageType;

	@Column(name = "storage_location", length = 64)
	private String storageLocation;

	@Column(name = "flash_point", precision = 8, scale = 2)
	private BigDecimal flashPoint;

	@Column(name = "created")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	private OffsetDateTime created;

	@Column(name = "modified")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	private OffsetDateTime modified;

	public static HazardousGoodsProductEntity create() {
		return new HazardousGoodsProductEntity();
	}

	public String getId() {
		return id;
	}

	public String getErrandId() {
		return errandId;
	}

	public String getCategory() {
		return category;
	}

	public String getProductName() {
		return productName;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public String getQuantityUnit() {
		return quantityUnit;
	}

	public String getStorageType() {
		return storageType;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public BigDecimal getFlashPoint() {
		return flashPoint;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setId(final String v) {
		this.id = v;
	}

	public void setErrandId(final String v) {
		this.errandId = v;
	}

	public void setCategory(final String v) {
		this.category = v;
	}

	public void setProductName(final String v) {
		this.productName = v;
	}

	public void setQuantity(final BigDecimal v) {
		this.quantity = v;
	}

	public void setQuantityUnit(final String v) {
		this.quantityUnit = v;
	}

	public void setStorageType(final String v) {
		this.storageType = v;
	}

	public void setStorageLocation(final String v) {
		this.storageLocation = v;
	}

	public void setFlashPoint(final BigDecimal v) {
		this.flashPoint = v;
	}

	@Override
	public void setCreated(final OffsetDateTime v) {
		this.created = v;
	}

	@Override
	public void setModified(final OffsetDateTime v) {
		this.modified = v;
	}

	public HazardousGoodsProductEntity withId(final String v) {
		this.id = v;
		return this;
	}

	public HazardousGoodsProductEntity withErrandId(final String v) {
		this.errandId = v;
		return this;
	}

	public HazardousGoodsProductEntity withCategory(final String v) {
		this.category = v;
		return this;
	}

	public HazardousGoodsProductEntity withProductName(final String v) {
		this.productName = v;
		return this;
	}

	public HazardousGoodsProductEntity withQuantity(final BigDecimal v) {
		this.quantity = v;
		return this;
	}

	public HazardousGoodsProductEntity withQuantityUnit(final String v) {
		this.quantityUnit = v;
		return this;
	}

	public HazardousGoodsProductEntity withStorageType(final String v) {
		this.storageType = v;
		return this;
	}

	public HazardousGoodsProductEntity withStorageLocation(final String v) {
		this.storageLocation = v;
		return this;
	}

	public HazardousGoodsProductEntity withFlashPoint(final BigDecimal v) {
		this.flashPoint = v;
		return this;
	}

	public HazardousGoodsProductEntity withCreated(final OffsetDateTime v) {
		this.created = v;
		return this;
	}

	public HazardousGoodsProductEntity withModified(final OffsetDateTime v) {
		this.modified = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final HazardousGoodsProductEntity that = (HazardousGoodsProductEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(errandId, that.errandId)
			&& Objects.equals(category, that.category) && Objects.equals(productName, that.productName)
			&& Objects.equals(quantity, that.quantity) && Objects.equals(quantityUnit, that.quantityUnit)
			&& Objects.equals(storageType, that.storageType) && Objects.equals(storageLocation, that.storageLocation)
			&& Objects.equals(flashPoint, that.flashPoint) && Objects.equals(created, that.created)
			&& Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, errandId, category, productName, quantity, quantityUnit, storageType, storageLocation,
			flashPoint, created, modified);
	}

	@Override
	public String toString() {
		return "HazardousGoodsProductEntity{id='" + id + "', errandId='" + errandId + "', category='" + category
			+ "', productName='" + productName + "', quantity=" + quantity + " " + quantityUnit
			+ ", storageType='" + storageType + "', storageLocation='" + storageLocation + "', flashPoint=" + flashPoint
			+ ", created=" + created + ", modified=" + modified + '}';
	}
}
