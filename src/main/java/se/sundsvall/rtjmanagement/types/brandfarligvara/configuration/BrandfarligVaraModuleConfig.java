package se.sundsvall.rtjmanagement.types.brandfarligvara.configuration;

import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.sundsvall.rtjmanagement.core.service.registry.ErrandTypeContribution;
import se.sundsvall.rtjmanagement.stakeholders.api.model.RoleDefinition;
import se.sundsvall.rtjmanagement.stakeholders.service.StakeholderRoleContribution;

/**
 * Registers the {@code BRANDFARLIG_VARA} errand type. Statuses and transitions match the
 * {@code brandfarlig-vara-process} BPMN states:
 *
 * <pre>
 *   REGISTERED ─┬─▶ DECIDED                      (happy path: granskning OK → tillstånd)
 *               ├─▶ AWAITING_SUPPLEMENTATION     (handläggare begär komplettering)
 *               ├─▶ INSPECTION_SCHEDULED         (tillsynsbesök behövs innan beslut)
 *               └─▶ REJECTED                     (direkt avslag — t.ex. ofullständig ansökan)
 *
 *   AWAITING_SUPPLEMENTATION ─┬─▶ DECIDED              (komplettering räcker)
 *                             ├─▶ INSPECTION_SCHEDULED (komplettering räcker inte, tillsyn behövs)
 *                             └─▶ REJECTED             (utebliven komplettering)
 *
 *   INSPECTION_SCHEDULED ─┬─▶ DECIDED        (tillsyn godkänd → tillstånd)
 *                         └─▶ REJECTED       (tillsyn underkänd → avslag)
 * </pre>
 *
 * Stakeholder roles (alla type-scoped):
 * <ul>
 * <li>{@code APPLICANT} — företaget som söker tillstånd (max 1, required).</li>
 * <li>{@code CONTACT_PERSON} — kontaktperson hos sökande (max 1, optional).</li>
 * <li>{@code INVOICEE} — separat faktureringspart (max 1, optional).</li>
 * <li>{@code RESPONSIBLE_PERSON} — föreståndare för verksamheten (min 1, flera tillåtna).</li>
 * <li>{@code INSPECTION_OFFICER} — RTJ-handläggare som genomför tillsyn (max 1, optional).</li>
 * </ul>
 *
 * Typespecifika datafält (utöver kärn-Errand) lagras i två separata tabeller:
 * <ul>
 * <li>{@code brandfarlig_vara_details} — 1:1 (verksamhetstyp, ombud-flagga, hanteringsplats).</li>
 * <li>{@code hazardous_goods_product} — N rader per errand (produktinventering per kategori).</li>
 * </ul>
 */
@Configuration
public class BrandfarligVaraModuleConfig {

	public static final String TYPE_SLUG = "BRANDFARLIG_VARA";

	public static final String STATUS_REGISTERED = "REGISTERED";
	public static final String STATUS_AWAITING_SUPPLEMENTATION = "AWAITING_SUPPLEMENTATION";
	public static final String STATUS_INSPECTION_SCHEDULED = "INSPECTION_SCHEDULED";
	public static final String STATUS_DECIDED = "DECIDED";
	public static final String STATUS_REJECTED = "REJECTED";

	public static final String ROLE_APPLICANT = "APPLICANT";
	public static final String ROLE_CONTACT_PERSON = "CONTACT_PERSON";
	public static final String ROLE_INVOICEE = "INVOICEE";
	public static final String ROLE_RESPONSIBLE_PERSON = "RESPONSIBLE_PERSON";
	public static final String ROLE_INSPECTION_OFFICER = "INSPECTION_OFFICER";

	@Bean
	ErrandTypeContribution brandfarligVaraType() {
		return ErrandTypeContribution.builder(TYPE_SLUG)
			.displayName("Ansökan om tillstånd för brandfarlig vara")
			.allowedTransition(STATUS_REGISTERED, STATUS_DECIDED, STATUS_AWAITING_SUPPLEMENTATION, STATUS_INSPECTION_SCHEDULED, STATUS_REJECTED)
			.allowedTransition(STATUS_AWAITING_SUPPLEMENTATION, STATUS_DECIDED, STATUS_INSPECTION_SCHEDULED, STATUS_REJECTED)
			.allowedTransition(STATUS_INSPECTION_SCHEDULED, STATUS_DECIDED, STATUS_REJECTED)
			.build();
	}

	@Bean
	StakeholderRoleContribution brandfarligVaraRoles() {
		return new StakeholderRoleContribution(TYPE_SLUG, Set.of(
			new RoleDefinition(ROLE_APPLICANT, "Sökande (företag)", 1, true),
			new RoleDefinition(ROLE_CONTACT_PERSON, "Kontaktperson", 1, false),
			new RoleDefinition(ROLE_INVOICEE, "Faktureringspart", 1, false),
			new RoleDefinition(ROLE_RESPONSIBLE_PERSON, "Föreståndare", 0, true), // 0 = obegränsat antal
			new RoleDefinition(ROLE_INSPECTION_OFFICER, "Tillsynsförrättare", 1, false)));
	}
}
