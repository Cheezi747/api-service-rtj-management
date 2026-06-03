package se.sundsvall.rtjmanagement.types.egensotning.configuration;

import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.sundsvall.rtjmanagement.core.service.registry.ErrandTypeContribution;
import se.sundsvall.rtjmanagement.stakeholders.api.model.RoleDefinition;
import se.sundsvall.rtjmanagement.stakeholders.service.StakeholderRoleContribution;

/**
 * Registers the {@code EGENSOTNING} errand type with core + stakeholders. Statuses
 * and transitions match the {@code egensotning-process} BPMN states:
 *
 * <pre>
 *   REGISTERED ─┬─▶ DECIDED                      (auto: all checks green → approval)
 *               ├─▶ AWAITING_SUPPLEMENTATION     (bilaga saknas → begär komplettering)
 *               ├─▶ UNDER_MANUAL_REVIEW          (ej folkbokförd / återansökan kräver granskning)
 *               └─▶ REJECTED                     (defensive)
 *
 *   AWAITING_SUPPLEMENTATION ─┬─▶ DECIDED        (komplettering → omverifiering → approval)
 *                             ├─▶ UNDER_MANUAL_REVIEW (omverifiering kräver granskning)
 *                             └─▶ REJECTED
 *
 *   UNDER_MANUAL_REVIEW ─┬─▶ DECIDED             (handläggare godkänner)
 *                        └─▶ REJECTED            (handläggare avslår)
 * </pre>
 *
 * Stakeholder roles:
 * <ul>
 * <li>{@code APPLICANT} — the citizen who filed the ansökan (max 1).</li>
 * <li>{@code BSK} — brandskyddskontrollant assigned to manual review (max 1).</li>
 * </ul>
 */
@Configuration
public class EgensotningModuleConfig {

	public static final String TYPE_SLUG = "EGENSOTNING";

	public static final String STATUS_REGISTERED = "REGISTERED";
	public static final String STATUS_AWAITING_SUPPLEMENTATION = "AWAITING_SUPPLEMENTATION";
	public static final String STATUS_UNDER_MANUAL_REVIEW = "UNDER_MANUAL_REVIEW";
	public static final String STATUS_DECIDED = "DECIDED";
	public static final String STATUS_REJECTED = "REJECTED";

	public static final String ROLE_APPLICANT = "APPLICANT";
	public static final String ROLE_BSK = "BSK";

	// Bilaga-kategorier — en egensotning-ansökan kräver exakt dessa två dokument.
	public static final String CATEGORY_BRANDSKYDDSKONTROLL = "BRANDSKYDDSKONTROLL";
	public static final String CATEGORY_UTBILDNINGSINTYG = "UTBILDNINGSINTYG";

	// Kategori för det systemgenererade beslutet (PDF) som lagras på ärendet.
	public static final String CATEGORY_DECISION = "DECISION";

	@Bean
	ErrandTypeContribution egensotningType() {
		return ErrandTypeContribution.builder(TYPE_SLUG)
			.displayName("Ansökan om egen sotning")
			.allowedTransition(STATUS_REGISTERED, STATUS_DECIDED, STATUS_AWAITING_SUPPLEMENTATION, STATUS_UNDER_MANUAL_REVIEW, STATUS_REJECTED)
			.allowedTransition(STATUS_AWAITING_SUPPLEMENTATION, STATUS_DECIDED, STATUS_UNDER_MANUAL_REVIEW, STATUS_REJECTED)
			.allowedTransition(STATUS_UNDER_MANUAL_REVIEW, STATUS_DECIDED, STATUS_REJECTED)
			.build();
	}

	@Bean
	StakeholderRoleContribution egensotningRoles() {
		return new StakeholderRoleContribution(TYPE_SLUG, Set.of(
			new RoleDefinition(ROLE_APPLICANT, "Sökande", 1, true),
			new RoleDefinition(ROLE_BSK, "Brandskyddskontrollant", 1, false)));
	}
}
