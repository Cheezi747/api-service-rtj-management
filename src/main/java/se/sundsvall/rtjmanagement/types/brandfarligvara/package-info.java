/**
 * Type module for BRANDFARLIG_VARA-errands (ansökan om tillstånd för brandfarlig vara enligt LBE).
 *
 * <p>
 * Följer samma utbyggnadsmönster som {@code types.egensotning}: en {@code @Configuration}-klass
 * registrerar {@link se.sundsvall.rtjmanagement.core.service.registry.ErrandTypeContribution} och
 * {@link se.sundsvall.rtjmanagement.stakeholders.service.StakeholderRoleContribution} som Spring-beans.
 * </p>
 *
 * <p>
 * Skillnad mot egensotning: brandfarlig-vara har typespecifika datafält som inte ryms i kärn-Errand,
 * därför finns två sub-paket med egna entities, services och resources:
 * </p>
 * <ul>
 * <li>{@code details} — 1:1 med Errand. Scalarfält (verksamhetstyp, ombud, hanteringsplats).</li>
 * <li>{@code hazardousgoods} — N per Errand. Produktinventering grupperad per LBE-kategori
 * (GAS / LIQUID / AEROSOL / REACTIVE).</li>
 * </ul>
 */
package se.sundsvall.rtjmanagement.types.brandfarligvara;
