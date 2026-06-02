package se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.service.mapper;

import java.util.List;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.api.model.HazardousGoodsProduct;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.integration.db.model.HazardousGoodsProductEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class HazardousGoodsProductMapper {

	private HazardousGoodsProductMapper() {}

	public static HazardousGoodsProduct toProduct(final HazardousGoodsProductEntity entity) {
		return ofNullable(entity)
			.map(e -> HazardousGoodsProduct.create()
				.withId(e.getId())
				.withCategory(e.getCategory())
				.withProductName(e.getProductName())
				.withQuantity(e.getQuantity())
				.withQuantityUnit(e.getQuantityUnit())
				.withStorageType(e.getStorageType())
				.withStorageLocation(e.getStorageLocation())
				.withFlashPoint(e.getFlashPoint())
				.withCreated(e.getCreated())
				.withModified(e.getModified()))
			.orElse(null);
	}

	public static HazardousGoodsProductEntity toEntity(final HazardousGoodsProduct product, final String errandId) {
		return ofNullable(product)
			.map(p -> HazardousGoodsProductEntity.create()
				.withErrandId(errandId)
				.withCategory(p.getCategory())
				.withProductName(p.getProductName())
				.withQuantity(p.getQuantity())
				.withQuantityUnit(p.getQuantityUnit())
				.withStorageType(p.getStorageType())
				.withStorageLocation(p.getStorageLocation())
				.withFlashPoint(p.getFlashPoint()))
			.orElse(null);
	}

	public static List<HazardousGoodsProduct> toProductList(final List<HazardousGoodsProductEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(HazardousGoodsProductMapper::toProduct)
			.toList();
	}

	/**
	 * PATCH semantics: only non-null fields on {@code patch} are applied to {@code target}.
	 * {@code flashPoint} is always overwritten because BigDecimal {@code null} is the explicit "clear" signal too.
	 */
	public static HazardousGoodsProductEntity applyPatch(final HazardousGoodsProductEntity target, final HazardousGoodsProduct patch) {
		if (target == null || patch == null) {
			return target;
		}
		ofNullable(patch.getCategory()).ifPresent(target::setCategory);
		ofNullable(patch.getProductName()).ifPresent(target::setProductName);
		ofNullable(patch.getQuantity()).ifPresent(target::setQuantity);
		ofNullable(patch.getQuantityUnit()).ifPresent(target::setQuantityUnit);
		ofNullable(patch.getStorageType()).ifPresent(target::setStorageType);
		ofNullable(patch.getStorageLocation()).ifPresent(target::setStorageLocation);
		ofNullable(patch.getFlashPoint()).ifPresent(target::setFlashPoint);
		return target;
	}
}
