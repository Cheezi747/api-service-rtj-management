package se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.service.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.api.model.ExplosivGoodsProduct;

import static org.assertj.core.api.Assertions.assertThat;

class ExplosivGoodsProductMapperTest {

	private static ExplosivGoodsProduct fullProduct() {
		return ExplosivGoodsProduct.create()
			.withHazardClass("1.1")
			.withProductName("Dynamit")
			.withQuantity(new BigDecimal("250.000"))
			.withQuantityUnit("KG")
			.withStorageType("MAGAZINE")
			.withStorageLocation("UNDERGROUND");
	}

	@Test
	void toEntityMapsAllFields() {
		final var entity = ExplosivGoodsProductMapper.toEntity(fullProduct(), "errand-1");

		assertThat(entity).isNotNull();
		assertThat(entity.getErrandId()).isEqualTo("errand-1");
		assertThat(entity.getHazardClass()).isEqualTo("1.1");
		assertThat(entity.getProductName()).isEqualTo("Dynamit");
		assertThat(entity.getQuantity()).isEqualByComparingTo("250.000");
		assertThat(entity.getQuantityUnit()).isEqualTo("KG");
		assertThat(entity.getStorageType()).isEqualTo("MAGAZINE");
		assertThat(entity.getStorageLocation()).isEqualTo("UNDERGROUND");
	}

	@Test
	void toEntityNullReturnsNull() {
		assertThat(ExplosivGoodsProductMapper.toEntity(null, "errand-1")).isNull();
	}

	@Test
	void toProductMapsAllFields() {
		final var entity = ExplosivGoodsProductMapper.toEntity(fullProduct(), "errand-1").withId("p1");

		final var dto = ExplosivGoodsProductMapper.toProduct(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo("p1");
		assertThat(dto.getHazardClass()).isEqualTo("1.1");
		assertThat(dto.getProductName()).isEqualTo("Dynamit");
		assertThat(dto.getStorageLocation()).isEqualTo("UNDERGROUND");
	}

	@Test
	void toProductNullReturnsNull() {
		assertThat(ExplosivGoodsProductMapper.toProduct(null)).isNull();
	}

	@Test
	void toProductListMapsAllItems() {
		final var entity = ExplosivGoodsProductMapper.toEntity(fullProduct(), "errand-1").withId("p1");
		final var result = ExplosivGoodsProductMapper.toProductList(List.of(entity));

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getId()).isEqualTo("p1");
	}

	@Test
	void toProductListNullReturnsEmpty() {
		assertThat(ExplosivGoodsProductMapper.toProductList(null)).isEmpty();
	}

	@Test
	void applyPatchOnlyAppliesNonNullFields() {
		final var target = ExplosivGoodsProductMapper.toEntity(fullProduct(), "errand-1");
		final var patch = ExplosivGoodsProduct.create()
			.withProductName("Sprängdeg")
			.withQuantity(new BigDecimal("99.000"));

		ExplosivGoodsProductMapper.applyPatch(target, patch);

		assertThat(target.getProductName()).isEqualTo("Sprängdeg");
		assertThat(target.getQuantity()).isEqualByComparingTo("99.000");
		// Untouched fields keep their original values
		assertThat(target.getHazardClass()).isEqualTo("1.1");
		assertThat(target.getStorageType()).isEqualTo("MAGAZINE");
	}

	@Test
	void applyPatchNullTargetOrPatchReturnsTarget() {
		assertThat(ExplosivGoodsProductMapper.applyPatch(null, fullProduct())).isNull();
		final var target = ExplosivGoodsProductMapper.toEntity(fullProduct(), "errand-1");
		assertThat(ExplosivGoodsProductMapper.applyPatch(target, null)).isSameAs(target);
	}
}
