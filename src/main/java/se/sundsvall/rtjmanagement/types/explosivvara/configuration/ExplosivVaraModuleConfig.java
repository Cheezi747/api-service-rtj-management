package se.sundsvall.rtjmanagement.types.explosivvara.configuration;

import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.sundsvall.rtjmanagement.core.service.registry.ErrandTypeContribution;
import se.sundsvall.rtjmanagement.stakeholders.api.model.RoleDefinition;
import se.sundsvall.rtjmanagement.stakeholders.service.StakeholderRoleContribution;

/**
 * Registers the {@code EXPLOSIV_VARA} errand type. Statuses and transitions match the
 * {@code explosiv-vara-process} BPMN states:
 *
 * <pre>
 *   REGISTERED ─┬─▶ AWAITING_POLICE_STATEMENT   (begär yttrande 14§ FBE)
 *               ├─▶ AWAITING_SUPPLEMENTATION     (handläggare begär komplettering)
 *               ├─▶ INSPECTION_SCHEDULED         (tillsynsbesök behövs innan beslut)
 *               ├─▶ DECIDED                      (granskning OK → tillstånd)
 *               └─▶ REJECTED                     (direkt avslag — t.ex. ofullständig ansökan)
 *
 *   AWAITING_POLICE_STATEMENT ─┬─▶ AWAITING_SUPPLEMENTATION (komplettering behövs efter yttrande)
 *                              ├─▶ INSPECTION_SCHEDULED      (tillsyn behövs)
 *                              ├─▶ DECIDED                   (yttrande OK → tillstånd)
 *                              └─▶ REJECTED                  (avslag efter yttrande)
 *
 *   AWAITING_SUPPLEMENTATION ─┬─▶ INSPECTION_SCHEDULED (komplettering räcker inte, tillsyn behövs)
 *                             ├─▶ DECIDED              (komplettering räcker)
 *                             └─▶ REJECTED             (utebliven komplettering)
 *
 *   INSPECTION_SCHEDULED ─┬─▶ DECIDED        (tillsyn godkänd → tillstånd)
 *                         └─▶ REJECTED       (tillsyn underkänd → avslag)
 *
 *   DECIDED ──▶ REVOKED   (återkallande enligt 20 § LBE)
 * </pre>
 *
 * Stakeholder roles (alla type-scoped):
 * <ul>
 * <li>{@code APPLICANT} — företaget som söker tillstånd (max 1, required).</li>
 * <li>{@code CONTACT_PERSON} — kontaktperson hos sökande (max 1, optional).</li>
 * <li>{@code INVOICEE} — separat faktureringspart (max 1, optional).</li>
 * <li>{@code RESPONSIBLE_PERSON} — föreståndare som ska godkännas (min 1, flera tillåtna).</li>
 * <li>{@code PARTICIPANT} — deltagare som ska godkännas (flera tillåtna, optional).</li>
 * <li>{@code SIGNIFICANT_INFLUENCE} — person med betydande inflytande (flera tillåtna, optional).</li>
 * <li>{@code INSPECTION_OFFICER} — RTJ-handläggare som genomför tillsyn (max 1, optional).</li>
 * </ul>
 *
 * Typespecifika datafält (utöver kärn-Errand) lagras i två separata tabeller:
 * <ul>
 * <li>{@code explosiv_vara_details} — 1:1 (typ av hantering, ombud-flagga, hanteringsplats).</li>
 * <li>{@code explosiv_goods_product} — N rader per errand (produktinventering per riskklass).</li>
 * </ul>
 */
@Configuration
public class ExplosivVaraModuleConfig {

	public static final String TYPE_SLUG = "EXPLOSIV_VARA";

	public static final String STATUS_REGISTERED = "REGISTERED";
	public static final String STATUS_AWAITING_POLICE_STATEMENT = "AWAITING_POLICE_STATEMENT";
	public static final String STATUS_AWAITING_SUPPLEMENTATION = "AWAITING_SUPPLEMENTATION";
	public static final String STATUS_INSPECTION_SCHEDULED = "INSPECTION_SCHEDULED";
	public static final String STATUS_DECIDED = "DECIDED";
	public static final String STATUS_REJECTED = "REJECTED";
	public static final String STATUS_REVOKED = "REVOKED";

	public static final String ROLE_APPLICANT = "APPLICANT";
	public static final String ROLE_CONTACT_PERSON = "CONTACT_PERSON";
	public static final String ROLE_INVOICEE = "INVOICEE";
	public static final String ROLE_RESPONSIBLE_PERSON = "RESPONSIBLE_PERSON";
	public static final String ROLE_PARTICIPANT = "PARTICIPANT";
	public static final String ROLE_SIGNIFICANT_INFLUENCE = "SIGNIFICANT_INFLUENCE";
	public static final String ROLE_INSPECTION_OFFICER = "INSPECTION_OFFICER";

	@Bean
	ErrandTypeContribution explosivVaraType() {
		return ErrandTypeContribution.builder(TYPE_SLUG)
			.displayName("Ansökan om tillstånd för explosiv vara")
			.allowedTransition(STATUS_REGISTERED, STATUS_AWAITING_POLICE_STATEMENT, STATUS_AWAITING_SUPPLEMENTATION, STATUS_INSPECTION_SCHEDULED, STATUS_DECIDED, STATUS_REJECTED)
			.allowedTransition(STATUS_AWAITING_POLICE_STATEMENT, STATUS_AWAITING_SUPPLEMENTATION, STATUS_INSPECTION_SCHEDULED, STATUS_DECIDED, STATUS_REJECTED)
			.allowedTransition(STATUS_AWAITING_SUPPLEMENTATION, STATUS_INSPECTION_SCHEDULED, STATUS_DECIDED, STATUS_REJECTED)
			.allowedTransition(STATUS_INSPECTION_SCHEDULED, STATUS_DECIDED, STATUS_REJECTED)
			.allowedTransition(STATUS_DECIDED, STATUS_REVOKED)
			.build();
	}

	@Bean
	StakeholderRoleContribution explosivVaraRoles() {
		return new StakeholderRoleContribution(TYPE_SLUG, Set.of(
			new RoleDefinition(ROLE_APPLICANT, "Sökande (företag)", 1, true),
			new RoleDefinition(ROLE_CONTACT_PERSON, "Kontaktperson", 1, false),
			new RoleDefinition(ROLE_INVOICEE, "Faktureringspart", 1, false),
			new RoleDefinition(ROLE_RESPONSIBLE_PERSON, "Föreståndare", 0, true), // 0 = obegränsat antal
			new RoleDefinition(ROLE_PARTICIPANT, "Deltagare", 0, false),
			new RoleDefinition(ROLE_SIGNIFICANT_INFLUENCE, "Person med betydande inflytande", 0, false),
			new RoleDefinition(ROLE_INSPECTION_OFFICER, "Tillsynsförrättare", 1, false)));
	}
}
