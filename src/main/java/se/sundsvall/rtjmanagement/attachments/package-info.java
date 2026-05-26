/**
 * Attachments module — files attached to an errand. Linked to {@code errand.id}
 * by FK; the envelope no longer owns the JPA relation.
 */
@ApplicationModule(displayName = "Attachments")
package se.sundsvall.rtjmanagement.attachments;

import org.springframework.modulith.ApplicationModule;
