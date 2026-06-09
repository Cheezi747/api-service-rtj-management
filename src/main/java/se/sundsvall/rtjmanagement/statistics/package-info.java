/**
 * Statistics module — read-only aggregation for the handläggargränssnitt.
 *
 * Counts errands per status and per handläggare, plus an active handläggningstid derived from the
 * status-history (time in {@code AWAITING_*} statuses is treated as paused / with the applicant).
 * Type-agnostic: aggregates the whole {@code errand} table for a namespace, optionally scoped by
 * typeSlug, for the single cross-type admin list. Reads {@code core} (errand envelope) and
 * {@code statushistory} (transition timeline); writes nothing.
 */
@ApplicationModule(displayName = "Statistics")
package se.sundsvall.rtjmanagement.statistics;

import org.springframework.modulith.ApplicationModule;
