package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.model.SotningsobjektEntity;

/**
 * Composes the formal egensotning beslutstext from the structured application data — the
 * per-objekt-tabell (sotningsobjekt) plus lagstöd (LSO 3:4), motivering, villkor och
 * överklagandehänvisning. Used as the decision's description so the auto-issued beslut
 * faktiskt listar de objekt det omfattar.
 *
 * Motiveringen kan redigeras av handläggaren ({@code details.motivering}); lämnas den tom
 * används den förifyllda standardmotiveringen. Texten byggs om vid beslutstillfället (i
 * {@code EgensotningDecisionListener}) så att en motivering som ändrats under manuell
 * granskning slår igenom i beslutsdokumentet.
 */
public final class EgensotningDecisionTextBuilder {

	private static final int MAX_DESCRIPTION_LENGTH = 4096;

	private static final String LAGSTOD = "Beslutet är fattat med stöd av 3 kap. 4 § andra stycket lagen (2003:778) om skydd mot olyckor (LSO).";
	private static final String MOTIVERING_PREFIX = "Motivering: ";
	private static final String DEFAULT_APPROVAL_MOTIVERING = "räddningstjänsten bedömer att rengöring (sotning) kan utföras på ett från brandskyddssynpunkt betryggande sätt och att sökanden har tillräcklig kunskap om anläggningens konstruktion och funktion.";
	private static final String DEFAULT_REJECTION_MOTIVERING = "räddningstjänsten bedömer efter manuell granskning att förutsättningarna för egensotning enligt 3 kap. 4 § LSO inte är uppfyllda.";
	private static final String VILLKOR = "Villkor: (1) medgivandet gäller i sex (6) år från beslutsdatum och måste därefter förnyas genom ny ansökan, och kan återkallas dessförinnan om brandskyddskontroll visar brister, (2) utförda sotningar ska dokumenteras i en särskild liggare som kan visas vid brandskyddskontroll, (3) medgivandet upphör automatiskt vid ägarbyte av fastigheten.";
	private static final String OVERKLAGANDE = "Detta beslut kan överklagas skriftligt till Länsstyrelsen i Västernorrlands län inom tre (3) veckor från delgivning; överklagandet skickas till Medelpads Räddningstjänstförbund.";

	private EgensotningDecisionTextBuilder() {}

	public static String buildApprovalDescription(final EgensotningDetailsEntity details, final List<SotningsobjektEntity> objekt) {
		final var sb = new StringBuilder("Ansökan om egensotning godkänd.");
		appendFastighet(sb, details);
		sb.append(" ").append(LAGSTOD);
		sb.append(" Sotningsobjekt som omfattas: ").append(formatObjekt(objekt)).append(".");
		sb.append(" ").append(MOTIVERING_PREFIX).append(resolveMotivering(details, DEFAULT_APPROVAL_MOTIVERING));
		sb.append(" ").append(VILLKOR);
		sb.append(" ").append(OVERKLAGANDE);

		return truncate(sb.toString());
	}

	public static String buildRejectionDescription(final EgensotningDetailsEntity details, final List<SotningsobjektEntity> objekt) {
		final var sb = new StringBuilder("Ansökan om egensotning avslås efter manuell granskning.");
		appendFastighet(sb, details);
		sb.append(" ").append(LAGSTOD);
		sb.append(" ").append(MOTIVERING_PREFIX).append(resolveMotivering(details, DEFAULT_REJECTION_MOTIVERING));
		sb.append(" ").append(OVERKLAGANDE);

		return truncate(sb.toString());
	}

	/**
	 * The handläggare-edited motivering when present, otherwise the supplied default. Editable from the
	 * details payload during manual review (single "Motivering"-rubrik, shared by godkännande/avslag).
	 */
	private static String resolveMotivering(final EgensotningDetailsEntity details, final String fallback) {
		return Optional.ofNullable(details)
			.map(EgensotningDetailsEntity::getMotivering)
			.filter(m -> !m.isBlank())
			.orElse(fallback);
	}

	private static void appendFastighet(final StringBuilder sb, final EgensotningDetailsEntity details) {
		Optional.ofNullable(details).map(EgensotningDetailsEntity::getFastighetsbeteckning)
			.filter(f -> !f.isBlank())
			.ifPresent(f -> sb.append(" Fastighet: ").append(f).append("."));
	}

	private static String truncate(final String text) {
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
