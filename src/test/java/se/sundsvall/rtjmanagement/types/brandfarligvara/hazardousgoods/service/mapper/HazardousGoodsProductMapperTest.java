package se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.service.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.api.model.HazardousGoodsProduct;

import static org.assertj.core.api.Assertions.assertThat;

class HazardousGoodsProductMapperTest {

	private static HazardousGoodsProduct fullProduct() {
		return HazardousGoodsProduct.create()
			.withCategory("LIQUID")
			.withProductName("Aspen 4")
			.withQuantity(new BigDecimal("1500.000"))
			.withQuantityUnit("L")
			.withStorageType("CISTERN")
			.withStorageLocation("INDOOR")
			.withFlashPoint(new BigDecimal("23.00"));
	}

	@Test
	void toEntityMapsAllFields() {
		final var entity = HazardousGoodsProductMapper.toEntity(fullProduct(), "errand-1");

		assertThat(entity).isNotNull();
		assertThat(entity.getErrandId()).isEqualTo("errand-1");
		assertThat(entity.getCategory()).isEqualTo("LIQUID");
		assertThat(entity.getProductName()).isEqualTo("Aspen 4");
		assertThat(entity.getQuantity()).isEqualByComparingTo("1500.000");
		assertThat(entity.getQuantityUnit()).isEqualTo("L");
		assertThat(entity.getStorageType()).isEqualTo("CISTERN");
		assertThat(entity.getStorageLocation()).isEqualTo("INDOOR");
		assertThat(entity.getFlashPoint()).isEqualByComparingTo("23.00");
	}

	@Test
	void toEntityNullReturnsNull() {
		assertThat(HazardousGoodsProductMapper.toEntity(null, "errand-1")).isNull();
	}

	@Test
	void toProductMapsAllFields() {
		final var entity = HazardousGoodsProductMapper.toEntity(fullProduct(), "errand-1").withId("p1");

		final var dto = HazardousGoodsProductMapper.toProduct(entity);

		assertThat(dto).isNotNull();
		assertThat(dto.getId()).isEqualTo("p1");
		assertThat(dto.getCategory()).isEqualTo("LIQUID");
		assertThat(dto.getProductName()).isEqualTo("Aspen 4");
		assertThat(dto.getFlashPoint()).isEqualByComparingTo("23.00");
	}

	@Test
	void toProductNullReturnsNull() {
		assertThat(HazardousGoodsProductMapper.toProduct(null)).isNull();
	}

	@Test
	void toProductListMapsAllItems() {
		final var entity = HazardousGoodsProductMapper.toEntity(fullProduct(), "errand-1").withId("p1");
		final var result = HazardousGoodsProductMapper.toProductList(List.of(entity));

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getId()).isEqualTo("p1");
	}

	@Test
	void toProductListNullReturnsEmpty() {
		assertThat(HazardousGoodsProductMapper.toProductList(null)).isEmpty();
	}

	@Test
	void applyPatchOnlyAppliesNonNullFields() {
		final var target = HazardousGoodsProductMapper.toEntity(fullProduct(), "errand-1");
		final var patch = HazardousGoodsProduct.create()
			.withProductName("Bensin 95")
			.withQuantity(new BigDecimal("99.000"));

		HazardousGoodsProductMapper.applyPatch(target, patch);

		assertThat(target.getProductName()).isEqualTo("Bensin 95");
		assertThat(target.getQuantity()).isEqualByComparingTo("99.000");
		// Untouched fields keep their original values
		assertThat(target.getCategory()).isEqualTo("LIQUID");
		assertThat(target.getStorageType()).isEqualTo("CISTERN");
	}

	@Test
	void applyPatchNullTargetOrPatchReturnsTarget() {
		assertThat(HazardousGoodsProductMapper.applyPatch(null, fullProduct())).isNull();
		final var target = HazardousGoodsProductMapper.toEntity(fullProduct(), "errand-1");
		assertThat(HazardousGoodsProductMapper.applyPatch(target, null)).isSameAs(target);
	}
}
