/**
 * Type module for EXPLOSIV_VARA-errands (ansökan om tillstånd för explosiv vara enligt LBE).
 *
 * <p>
 * Följer samma utbyggnadsmönster som {@code types.egensotning}: en {@code @Configuration}-klass
 * registrerar {@link se.sundsvall.rtjmanagement.core.service.registry.ErrandTypeContribution} och
 * {@link se.sundsvall.rtjmanagement.stakeholders.service.StakeholderRoleContribution} som Spring-beans.
 * </p>
 *
 * <p>
 * Skillnad mot egensotning: explosiv-vara har typespecifika datafält som inte ryms i kärn-Errand,
 * därför finns två sub-paket med egna entities, services och resources:
 * </p>
 * <ul>
 * <li>{@code details} — 1:1 med Errand. Scalarfält (typ av hantering, ombud, hanteringsplats).</li>
 * <li>{@code explosivgoods} — N per Errand. Produktinventering grupperad per MSBFS-riskklass
 * (1.1 / 1.2 / 1.3 / 1.4 / 1.5 / 1.6).</li>
 * </ul>
 */
package se.sundsvall.rtjmanagement.types.explosivvara;
