package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.model.SotningsobjektEntity;

/**
 * Composes the formal egensotning beslutstext from the structured application data — the
 * per-objekt-tabell (sotningsobjekt) plus lagstöd (LSO 3:4), motivering, villkor och
 * överklagandehänvisning. Used as the APPROVAL decision's description so the auto-issued
 * beslut faktiskt listar de objekt det omfattar.
 */
public final class EgensotningDecisionTextBuilder {

	private static final int MAX_DESCRIPTION_LENGTH = 4096;

	private static final String LAGSTOD = "Beslutet är fattat med stöd av 3 kap. 4 § andra stycket lagen (2003:778) om skydd mot olyckor (LSO).";
	private static final String MOTIVERING = "Motivering: räddningstjänsten bedömer att rengöring (sotning) kan utföras på ett från brandskyddssynpunkt betryggande sätt och att sökanden har tillräcklig kunskap om anläggningens konstruktion och funktion.";
	private static final String VILLKOR = "Villkor: (1) medgivandet gäller i sex (6) år från beslutsdatum och måste därefter förnyas genom ny ansökan, och kan återkallas dessförinnan om brandskyddskontroll visar brister, (2) utförda sotningar ska dokumenteras i en särskild liggare som kan visas vid brandskyddskontroll, (3) medgivandet upphör automatiskt vid ägarbyte av fastigheten.";
	private static final String OVERKLAGANDE = "Detta beslut kan överklagas skriftligt till Länsstyrelsen i Västernorrlands län inom tre (3) veckor från delgivning; överklagandet skickas till Medelpads Räddningstjänstförbund.";

	private EgensotningDecisionTextBuilder() {}

	public static String buildApprovalDescription(final EgensotningDetailsEntity details, final List<SotningsobjektEntity> objekt) {
		final var sb = new StringBuilder("Ansökan om egensotning godkänd.");
		Optional.ofNullable(details).map(EgensotningDetailsEntity::getFastighetsbeteckning)
			.filter(f -> !f.isBlank())
			.ifPresent(f -> sb.append(" Fastighet: ").append(f).append("."));
		sb.append(" ").append(LAGSTOD);
		sb.append(" Sotningsobjekt som omfattas: ").append(formatObjekt(objekt)).append(".");
		sb.append(" ").append(MOTIVERING);
		sb.append(" ").append(VILLKOR);
		sb.append(" ").append(OVERKLAGANDE);

		final var text = sb.toString();
		return text.length() > MAX_DESCRIPTION_LENGTH ? text.substring(0, MAX_DESCRIPTION_LENGTH - 3) + "..." : text;
	}

	private static String formatObjekt(final List<SotningsobjektEntity> objekt) {
		if (objekt == null || objekt.isEmpty()) {
			return "inga objekt angivna";
		}
		final var joiner = new StringJoiner("; ");
		objekt.forEach(o -> joiner.add(formatOne(o)));
		return joiner.toString();
	}

	private static String formatOne(final SotningsobjektEntity o) {
		final var parts = new StringJoiner(", ");
		Optional.ofNullable(o.getTyp()).ifPresent(parts::add);
		Optional.ofNullable(o.getFabrikat()).ifPresent(f -> parts.add("fabrikat " + f));
		Optional.ofNullable(o.getTillverkningsar()).ifPresent(y -> parts.add("tillv. " + y));
		Optional.ofNullable(o.getBransleslag()).ifPresent(b -> parts.add("bränsle " + b));
		Optional.ofNullable(o.getBranslemangd()).ifPresent(m -> parts.add("mängd " + m));
		Optional.ofNullable(o.getSotningsintervallVeckor()).ifPresent(v -> parts.add("sotningsintervall " + v + " veckor"));
		return parts.toString();
	}
}
