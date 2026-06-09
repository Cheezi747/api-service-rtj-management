package se.sundsvall.rtjmanagement.types.brandfarligvara.details.service;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import se.sundsvall.rtjmanagement.types.brandfarligvara.details.integration.db.model.BrandfarligVaraDetailsEntity;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.integration.db.model.HazardousGoodsProductEntity;

/**
 * Composes the formal tillståndsbeslut text for a brandfarlig-vara permit from the structured
 * application data — the hanteringsplats, the omfattning (the hazardous-goods inventory), lagstöd
 * (16–19 §§ LBE), tillståndstid, villkor och överklagandehänvisning.
 *
 * <p>
 * Used as the APPROVAL decision's description so the issued tillståndsbeslut actually lists the
 * varor och mängder it covers. Mirrors {@code EgensotningDecisionTextBuilder}. The generated text
 * is a draft the handläggare reviews — company name / tillståndshavare (a stakeholder) is not woven
 * in here in v1; it is added when the handläggare finalises the beslut.
 * </p>
 */
public final class BrandfarligVaraDecisionTextBuilder {

	private static final int MAX_DESCRIPTION_LENGTH = 4096;

	private static final String LAGSTOD = "Beslutet är fattat med stöd av 16–19 §§ lagen (2010:1011) om brandfarliga och explosiva varor (LBE).";
	private static final String TILLSTANDSTID = "Tillståndet gäller i fem (5) år från beslutsdatum, dock längst till nästkommande fasta datum (1 mars, 1 juni, 1 september eller 1 december).";
	private static final String VILLKOR = "Villkor: hanteringen ska ske i enlighet med de uppgifter och handlingar som ligger till grund för tillståndet; väsentliga förändringar av hanteringen eller hanterade mängder kräver nytt tillstånd.";
	private static final String OVERKLAGANDE = "Detta beslut kan överklagas skriftligt till Länsstyrelsen i Västernorrlands län inom tre (3) veckor från delgivning; överklagandet skickas till Medelpads Räddningstjänstförbund.";

	private BrandfarligVaraDecisionTextBuilder() {}

	public static String buildApprovalDescription(final BrandfarligVaraDetailsEntity details, final List<HazardousGoodsProductEntity> products) {
		final var sb = new StringBuilder("Tillstånd till hantering av brandfarlig vara beviljas.");
		Optional.ofNullable(details).map(BrandfarligVaraDetailsEntity::getFastighetsbeteckning)
			.filter(f -> !f.isBlank())
			.ifPresent(f -> sb.append(" Hanteringsplats: ").append(f).append("."));
		sb.append(" ").append(LAGSTOD);
		sb.append(" Tillståndet omfattar följande brandfarliga varor: ").append(formatProducts(products)).append(".");
		sb.append(" ").append(TILLSTANDSTID);
		sb.append(" ").append(VILLKOR);
		sb.append(" ").append(OVERKLAGANDE);

		final var text = sb.toString();
		if (text.length() > MAX_DESCRIPTION_LENGTH) {
			return text.substring(0, MAX_DESCRIPTION_LENGTH - 3) + "...";
		}
		return text;
	}

	private static String formatProducts(final List<HazardousGoodsProductEntity> products) {
		if (products == null || products.isEmpty()) {
			return "inga varor angivna";
		}
		final var joiner = new StringJoiner("; ");
		products.forEach(p -> joiner.add(formatOne(p)));
		return joiner.toString();
	}

	private static String formatOne(final HazardousGoodsProductEntity p) {
		final var parts = new StringJoiner(", ");
		Optional.ofNullable(categoryLabel(p.getCategory())).ifPresent(parts::add);
		Optional.ofNullable(p.getProductName()).ifPresent(parts::add);
		Optional.ofNullable(p.getQuantity()).ifPresent(q -> parts.add("mängd " + q.toPlainString()
			+ Optional.ofNullable(p.getQuantityUnit()).map(u -> " " + u).orElse("")));
		Optional.ofNullable(p.getFlashPoint()).ifPresent(fp -> parts.add("flampunkt " + fp.toPlainString() + " °C"));
		Optional.ofNullable(p.getStorageType()).ifPresent(s -> parts.add("förvaring " + s));
		Optional.ofNullable(p.getStorageLocation()).ifPresent(parts::add);
		return parts.toString();
	}

	private static String categoryLabel(final String category) {
		if (category == null) {
			return null;
		}
		return switch (category) {
			case "GAS" -> "brandfarlig gas";
			case "LIQUID" -> "brandfarlig vätska";
			case "AEROSOL" -> "aerosol";
			case "REACTIVE" -> "brandreaktiv vara";
			default -> category;
		};
	}
}
