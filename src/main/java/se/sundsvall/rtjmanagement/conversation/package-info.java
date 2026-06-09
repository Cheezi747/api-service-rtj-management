/**
 * Conversation module — structured messages between handläggare and sökande on an errand.
 *
 * Universal across all errand types: a message is just {@code (errandId, direction, body, author,
 * created)}. OUTBOUND = handläggare → sökande (e.g. a "kräver komplettering"-meddelande shown in the
 * citizen's ärende), INBOUND = sökande → handläggare (in-app reply). Pure persistence + read API; the
 * actual e-mail notification for an outbound supplement request is driven by the BPMN
 * {@code request-supplement} flow (operaton {@code send-email}), not by this module.
 */
@ApplicationModule(displayName = "Conversation")
package se.sundsvall.rtjmanagement.conversation;

import org.springframework.modulith.ApplicationModule;
