/**
 * Conversation module — structured messages between handläggare and sökande on an errand.
 *
 * Universal across all errand types: a message is just {@code (errandId, direction, body, author,
 * created)}. OUTBOUND = handläggare → sökande (e.g. a kräver-komplettering-meddelande shown in the
 * citizen's ärende), INBOUND = sökande → handläggare (in-app reply). Persists the thread and
 * publishes a {@code MessagePosted} event (in the {@code shared} module); type modules subscribe to
 * notify. The egensotning module sends the applicant a <b>content-free</b> e-mail notification — a
 * new-message notice pointing to Mina sidor — on OUTBOUND, so the message body itself never leaves
 * this in-app thread, and the BPMN flow no longer e-mails the handläggare's free-text.
 */
@ApplicationModule(displayName = "Conversation")
package se.sundsvall.rtjmanagement.conversation;

import org.springframework.modulith.ApplicationModule;
