package se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.service.mapper;

import java.util.List;
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.api.model.ExplosivGoodsProduct;
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.integration.db.model.ExplosivGoodsProductEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class ExplosivGoodsProductMapper {

	private ExplosivGoodsProductMapper() {}

	public static ExplosivGoodsProduct toProduct(final ExplosivGoodsProductEntity entity) {
		return ofNullable(entity)
			.map(e -> ExplosivGoodsProduct.create()
				.withId(e.getId())
				.withHazardClass(e.getHazardClass())
				.withProductName(e.getProductName())
				.withQuantity(e.getQuantity())
				.withQuantityUnit(e.getQuantityUnit())
				.withStorageType(e.getStorageType())
				.withStorageLocation(e.getStorageLocation())
				.withCreated(e.getCreated())
				.withModified(e.getModified()))
			.orElse(null);
	}

	public static ExplosivGoodsProductEntity toEntity(final ExplosivGoodsProduct product, final String errandId) {
		return ofNullable(product)
			.map(p -> ExplosivGoodsProductEntity.create()
				.withErrandId(errandId)
				.withHazardClass(p.getHazardClass())
				.withProductName(p.getProductName())
				.withQuantity(p.getQuantity())
				.withQuantityUnit(p.getQuantityUnit())
				.withStorageType(p.getStorageType())
				.withStorageLocation(p.getStorageLocation()))
			.orElse(null);
	}

	public static List<ExplosivGoodsProduct> toProductList(final List<ExplosivGoodsProductEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(ExplosivGoodsProductMapper::toProduct)
			.toList();
	}

	/**
	 * PATCH semantics: only non-null fields on {@code patch} are applied to {@code target}.
	 */
	public static ExplosivGoodsProductEntity applyPatch(final ExplosivGoodsProductEntity target, final ExplosivGoodsProduct patch) {
		if (target == null || patch == null) {
			return target;
		}
		ofNullable(patch.getHazardClass()).ifPresent(target::setHazardClass);
		ofNullable(patch.getProductName()).ifPresent(target::setProductName);
		ofNullable(patch.getQuantity()).ifPresent(target::setQuantity);
		ofNullable(patch.getQuantityUnit()).ifPresent(target::setQuantityUnit);
		ofNullable(patch.getStorageType()).ifPresent(target::setStorageType);
		ofNullable(patch.getStorageLocation()).ifPresent(target::setStorageLocation);
		return target;
	}
}
