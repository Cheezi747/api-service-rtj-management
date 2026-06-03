/**
 * Remiss (samråd) module — outgoing remisser/samråd on an errand and their svar.
 *
 * <p>
 * Shared, type-agnostic and keyed by {@code errand_id} (like {@code permit} and {@code decisions}): an
 * LBE-handläggare skickar en remiss till en annan instans (14 § FBE — t.ex. miljökontor) eller begär
 * polisens yttrande för explosiv vara, och registrerar svaret när det kommer in. Captures
 * {@code instans} (mottagande myndighet), {@code recipient}, {@code sentAt}/{@code dueAt}, fritextsvaret
 * {@code responseText} och en lifecycle {@code status} (SENT → RESPONDED).
 * </p>
 *
 * <p>
 * Accessed over REST (no cross-module Java dependency); depends only on the exposed {@code core} errand
 * layer, so it stays inside its module boundary.
 * </p>
 */
@ApplicationModule(displayName = "Remisser")
package se.sundsvall.rtjmanagement.remiss;

import org.springframework.modulith.ApplicationModule;
