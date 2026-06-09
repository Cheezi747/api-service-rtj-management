package se.sundsvall.rtjmanagement.types.egensotning.configuration;

import java.util.Set;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;

import static org.springframework.http.HttpStatus.CONFLICT;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_DECIDED;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_REJECTED;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_REVOKED;

/**
 * Spärrar medborgar-/API-ändringar av ett egensotningsärende när det nått ett slutligt tillstånd.
 *
 * Inskickat och pågående ärenden (REGISTERED, AWAITING_SUPPLEMENTATION, UNDER_MANUAL_REVIEW) får
 * ändras — t.ex. medborgaren kompletterar en bilaga, eller handläggaren redigerar motiveringen under
 * manuell granskning — men när beslut fattats (DECIDED/REJECTED) är ärendet låst (409 Conflict).
 *
 * Spärren ligger på de medborgar-/API-vända egensotning-tjänsterna. Systemets egna skrivningar
 * (verifieringssteget, giltighetstid och beslut-PDF) går direkt mot repositoryt och berörs inte —
 * därför kan beslutsdokumentet fortfarande lagras när status blir DECIDED.
 */
public final class EgensotningMutationGuard {

	private static final Set<String> LOCKED_STATUSES = Set.of(STATUS_DECIDED, STATUS_REJECTED, STATUS_REVOKED);
	private static final String LOCKED_MESSAGE = "Errand '%s' is in status '%s' and can no longer be modified (the decision is final)";

	private EgensotningMutationGuard() {}

	/**
	 * Throws {@code 409 Conflict} when the errand is in a final state. No-op for null or non-terminal errands.
	 */
	public static void assertMutable(final ErrandEntity errand) {
		if (errand != null && errand.getStatus() != null && LOCKED_STATUSES.contains(errand.getStatus())) {
			throw Problem.valueOf(CONFLICT, LOCKED_MESSAGE.formatted(errand.getId(), errand.getStatus()));
		}
	}
}
