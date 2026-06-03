package se.sundsvall.rtjmanagement.types.explosivvara.details.service;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import se.sundsvall.rtjmanagement.types.explosivvara.details.integration.db.model.ExplosivVaraDetailsEntity;
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.integration.db.model.ExplosivGoodsProductEntity;

/**
 * Composes the formal tillståndsbeslut text for an explosiv-vara permit from the structured
 * application data — the hanteringsplats, the omfattning (the explosive-goods inventory), lagstöd
 * (16 och 19 §§ LBE), tillståndstid, godkända personer/NATEV, villkor och överklagandehänvisning.
 *
 * <p>
 * Used as the APPROVAL decision's description so the issued tillståndsbeslut actually lists the
 * varor och mängder it covers. Mirrors {@code BrandfarligVaraDecisionTextBuilder}. The generated text
 * is a draft the handläggare reviews — company name / tillståndshavare (a stakeholder) is not woven
 * in here in v1; it is added when the handläggare finalises the beslut.
 * </p>
 */
public final class ExplosivVaraDecisionTextBuilder {

	private static final int MAX_DESCRIPTION_LENGTH = 4096;

	private static final String LAGSTOD = "Beslutet är fattat med stöd av 16 och 19 §§ lagen (2010:1011) om brandfarliga och explosiva varor (LBE).";
	private static final String TILLSTANDSTID = "Tillståndet gäller i högst tre (3) år (19 b § LBE), dock längst till nästkommande fasta datum (1 mars, 1 juni, 1 september eller 1 december).";
	private static final String GODKANNANDE = "Godkända föreståndare och deltagare anges i bilaga till beslutet.";
	private static final String NATEV = "Uppgifter om tillstånd och godkända personer registreras i NATEV (Nationellt tillståndsregister explosiv vara).";
	private static final String VILLKOR = "Villkor: hanteringen ska ske i enlighet med de uppgifter och handlingar som ligger till grund för tillståndet; väsentliga förändringar av hanteringen eller hanterade mängder kräver nytt tillstånd.";
	private static final String OVERKLAGANDE = "Detta beslut kan överklagas skriftligt till Länsstyrelsen i Västernorrlands län inom tre (3) veckor från delgivning; överklagandet skickas till Medelpads Räddningstjänstförbund.";

	private ExplosivVaraDecisionTextBuilder() {}

	public static String buildApprovalDescription(final ExplosivVaraDetailsEntity details, final List<ExplosivGoodsProductEntity> products) {
		final var sb = new StringBuilder("Tillstånd till hantering av explosiv vara beviljas.");
		Optional.ofNullable(details).map(ExplosivVaraDetailsEntity::getFastighetsbeteckning)
			.filter(f -> !f.isBlank())
			.ifPresent(f -> sb.append(" Hanteringsplats: ").append(f).append("."));
		sb.append(" ").append(LAGSTOD);
		sb.append(" Tillståndet omfattar följande explosiva varor: ").append(formatProducts(products)).append(".");
		sb.append(" ").append(TILLSTANDSTID);
		sb.append(" ").append(GODKANNANDE);
		sb.append(" ").append(NATEV);
		sb.append(" ").append(VILLKOR);
		sb.append(" ").append(OVERKLAGANDE);

		final var text = sb.toString();
		return text.length() > MAX_DESCRIPTION_LENGTH ? text.substring(0, MAX_DESCRIPTION_LENGTH - 3) + "..." : text;
	}

	private static String formatProducts(final List<ExplosivGoodsProductEntity> products) {
		if (products == null || products.isEmpty()) {
			return "inga varor angivna";
		}
		final var joiner = new StringJoiner("; ");
		products.forEach(p -> joiner.add(formatOne(p)));
		return joiner.toString();
	}

	private static String formatOne(final ExplosivGoodsProductEntity p) {
		final var parts = new StringJoiner(", ");
		Optional.ofNullable(hazardClassLabel(p.getHazardClass())).ifPresent(parts::add);
		Optional.ofNullable(p.getProductName()).ifPresent(parts::add);
		Optional.ofNullable(p.getQuantity()).ifPresent(q -> parts.add("mängd " + q.toPlainString()
			+ Optional.ofNullable(p.getQuantityUnit()).map(u -> " " + u).orElse("")));
		Optional.ofNullable(p.getStorageType()).ifPresent(s -> parts.add("förvaring " + s));
		Optional.ofNullable(p.getStorageLocation()).ifPresent(parts::add);
		return parts.toString();
	}

	private static String hazardClassLabel(final String hazardClass) {
		if (hazardClass == null) {
			return null;
		}
		return "riskgrupp " + hazardClass;
	}
}
