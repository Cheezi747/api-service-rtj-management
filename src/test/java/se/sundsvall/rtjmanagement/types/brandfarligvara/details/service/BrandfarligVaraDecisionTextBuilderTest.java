package se.sundsvall.rtjmanagement.types.brandfarligvara.details.service;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.types.brandfarligvara.details.integration.db.model.BrandfarligVaraDetailsEntity;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.integration.db.model.HazardousGoodsProductEntity;

import static org.assertj.core.api.Assertions.assertThat;

class BrandfarligVaraDecisionTextBuilderTest {

	private static BrandfarligVaraDetailsEntity details() {
		return BrandfarligVaraDetailsEntity.create().withFastighetsbeteckning("Sundsvall Stenstaden 1:23");
	}

	@Test
	void includesHanteringsplatsLagstodOmfattningVillkorAndOverklagande() {
		final var products = List.of(
			HazardousGoodsProductEntity.create().withCategory("LIQUID").withProductName("Bensin")
				.withQuantity(new BigDecimal("1500")).withQuantityUnit("L").withFlashPoint(new BigDecimal("-40")).withStorageType("CISTERN"),
			HazardousGoodsProductEntity.create().withCategory("GAS").withProductName("Gasol")
				.withQuantity(new BigDecimal("60")).withQuantityUnit("KG"));

		final var text = BrandfarligVaraDecisionTextBuilder.buildApprovalDescription(details(), products);

		assertThat(text)
			.contains("Sundsvall Stenstaden 1:23")
			.contains("16–19 §§")
			.contains("brandfarlig vätska").contains("Bensin").contains("mängd 1500 L").contains("flampunkt -40 °C")
			.contains("brandfarlig gas").contains("Gasol").contains("mängd 60 KG")
			.contains("fem (5) år")
			.contains("Länsstyrelsen i Västernorrlands län");
	}

	@Test
	void noProductsNotesNone() {
		final var text = BrandfarligVaraDecisionTextBuilder.buildApprovalDescription(details(), List.of());

		assertThat(text).contains("inga varor angivna").contains("Länsstyrelsen i Västernorrlands län");
	}

	@Test
	void nullDetailsAndProductsDoesNotFail() {
		final var text = BrandfarligVaraDecisionTextBuilder.buildApprovalDescription(null, null);

		assertThat(text).contains("Tillstånd till hantering av brandfarlig vara beviljas").contains("inga varor angivna");
	}
}
