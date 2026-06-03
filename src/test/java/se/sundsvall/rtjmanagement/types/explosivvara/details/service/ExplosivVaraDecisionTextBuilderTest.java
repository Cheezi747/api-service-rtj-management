package se.sundsvall.rtjmanagement.types.explosivvara.details.service;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.types.explosivvara.details.integration.db.model.ExplosivVaraDetailsEntity;
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.integration.db.model.ExplosivGoodsProductEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ExplosivVaraDecisionTextBuilderTest {

	private static ExplosivVaraDetailsEntity details() {
		return ExplosivVaraDetailsEntity.create().withFastighetsbeteckning("Sundsvall Stenstaden 1:23");
	}

	@Test
	void includesHanteringsplatsLagstodOmfattningVillkorAndOverklagande() {
		final var products = List.of(
			ExplosivGoodsProductEntity.create().withHazardClass("1.1").withProductName("Dynamit")
				.withQuantity(new BigDecimal("250")).withQuantityUnit("KG").withStorageType("MAGAZINE").withStorageLocation("UNDERGROUND"),
			ExplosivGoodsProductEntity.create().withHazardClass("1.4").withProductName("Sprängkapslar")
				.withQuantity(new BigDecimal("500")).withQuantityUnit("STK"));

		final var text = ExplosivVaraDecisionTextBuilder.buildApprovalDescription(details(), products);

		assertThat(text)
			.contains("Sundsvall Stenstaden 1:23")
			.contains("16 och 19 §§")
			.contains("riskgrupp 1.1").contains("Dynamit").contains("mängd 250 KG").contains("förvaring MAGAZINE").contains("UNDERGROUND")
			.contains("riskgrupp 1.4").contains("Sprängkapslar").contains("mängd 500 STK")
			.contains("tre (3) år")
			.contains("NATEV")
			.contains("Länsstyrelsen i Västernorrlands län");
	}

	@Test
	void noProductsNotesNone() {
		final var text = ExplosivVaraDecisionTextBuilder.buildApprovalDescription(details(), List.of());

		assertThat(text).contains("inga varor angivna").contains("Länsstyrelsen i Västernorrlands län");
	}

	@Test
	void nullDetailsAndProductsDoesNotFail() {
		final var text = ExplosivVaraDecisionTextBuilder.buildApprovalDescription(null, null);

		assertThat(text).contains("Tillstånd till hantering av explosiv vara beviljas").contains("inga varor angivna");
	}
}
