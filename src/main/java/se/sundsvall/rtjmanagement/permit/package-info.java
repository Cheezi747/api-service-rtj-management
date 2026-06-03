/**
 * Permit (tillstånd) module — the issued LBE permit with a validity period and conditions.
 *
 * <p>
 * Shared, type-agnostic and keyed by {@code errand_id} (like {@code decisions}): a brandfarlig-vara
 * or explosiv-vara errand issues a permit when the tillståndsbeslut is taken. Captures what the flat
 * {@code Decision} cannot — {@code validFrom}/{@code validUntil} (giltighetstid), {@code conditions}
 * (villkor) and a lifecycle {@code status} (ACTIVE → REVOKED for återkallande enligt 20 § LBE).
 * </p>
 *
 * <p>
 * The validity period encodes MRF:s rule (LBE-processbeskrivningen §3.9.1): 5 år för brandfarlig vara,
 * högst 3 år för explosiv vara (19 b § LBE), förlängt till nästkommande fasta datum (1 mars, 1 juni,
 * 1 september eller 1 december) — see {@link se.sundsvall.rtjmanagement.permit.service.PermitValidityCalculator}.
 * </p>
 *
 * <p>
 * Accessed over REST (no cross-module Java dependency); depends only on the exposed {@code core} errand
 * layer, so it stays inside its module boundary.
 * </p>
 */
@ApplicationModule(displayName = "Permits")
package se.sundsvall.rtjmanagement.permit;

import org.springframework.modulith.ApplicationModule;
