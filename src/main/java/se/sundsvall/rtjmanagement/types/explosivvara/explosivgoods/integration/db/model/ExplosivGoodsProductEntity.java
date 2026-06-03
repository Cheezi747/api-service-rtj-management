package se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.integration.db.model;

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
@Table(name = "explosiv_goods_product",
	indexes = {
		@Index(name = "idx_explosiv_goods_errand_id", columnList = "errand_id"),
		@Index(name = "idx_explosiv_goods_hazard_class", columnList = "hazard_class")
	})
@EntityListeners(AuditableListener.class)
public class ExplosivGoodsProductEntity implements Auditable {

	@Id
	@UuidGenerator
	@Column(name = "id")
	private String id;

	@Column(name = "errand_id", nullable = false, length = 255)
	private String errandId;

	@Column(name = "hazard_class", nullable = false, length = 8)
	private String hazardClass;

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

	@Column(name = "created")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	private OffsetDateTime created;

	@Column(name = "modified")
	@TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
	private OffsetDateTime modified;

	public static ExplosivGoodsProductEntity create() {
		return new ExplosivGoodsProductEntity();
	}

	public String getId() {
		return id;
	}

	public String getErrandId() {
		return errandId;
	}

	public String getHazardClass() {
		return hazardClass;
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

	public void setHazardClass(final String v) {
		this.hazardClass = v;
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

	@Override
	public void setCreated(final OffsetDateTime v) {
		this.created = v;
	}

	@Override
	public void setModified(final OffsetDateTime v) {
		this.modified = v;
	}

	public ExplosivGoodsProductEntity withId(final String v) {
		this.id = v;
		return this;
	}

	public ExplosivGoodsProductEntity withErrandId(final String v) {
		this.errandId = v;
		return this;
	}

	public ExplosivGoodsProductEntity withHazardClass(final String v) {
		this.hazardClass = v;
		return this;
	}

	public ExplosivGoodsProductEntity withProductName(final String v) {
		this.productName = v;
		return this;
	}

	public ExplosivGoodsProductEntity withQuantity(final BigDecimal v) {
		this.quantity = v;
		return this;
	}

	public ExplosivGoodsProductEntity withQuantityUnit(final String v) {
		this.quantityUnit = v;
		return this;
	}

	public ExplosivGoodsProductEntity withStorageType(final String v) {
		this.storageType = v;
		return this;
	}

	public ExplosivGoodsProductEntity withStorageLocation(final String v) {
		this.storageLocation = v;
		return this;
	}

	public ExplosivGoodsProductEntity withCreated(final OffsetDateTime v) {
		this.created = v;
		return this;
	}

	public ExplosivGoodsProductEntity withModified(final OffsetDateTime v) {
		this.modified = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final ExplosivGoodsProductEntity that = (ExplosivGoodsProductEntity) o;
		return Objects.equals(id, that.id) && Objects.equals(errandId, that.errandId)
			&& Objects.equals(hazardClass, that.hazardClass) && Objects.equals(productName, that.productName)
			&& Objects.equals(quantity, that.quantity) && Objects.equals(quantityUnit, that.quantityUnit)
			&& Objects.equals(storageType, that.storageType) && Objects.equals(storageLocation, that.storageLocation)
			&& Objects.equals(created, that.created) && Objects.equals(modified, that.modified);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, errandId, hazardClass, productName, quantity, quantityUnit, storageType, storageLocation,
			created, modified);
	}

	@Override
	public String toString() {
		return "ExplosivGoodsProductEntity{id='" + id + "', errandId='" + errandId + "', hazardClass='" + hazardClass
			+ "', productName='" + productName + "', quantity=" + quantity + " " + quantityUnit
			+ ", storageType='" + storageType + "', storageLocation='" + storageLocation
			+ "', created=" + created + ", modified=" + modified + '}';
	}
}
