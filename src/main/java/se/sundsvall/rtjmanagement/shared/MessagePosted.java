package se.sundsvall.rtjmanagement.shared;

import java.time.OffsetDateTime;

/**
 * Cross-module event: published by the conversation module whenever a message is posted on an
 * errand. Type modules subscribe to react — e.g. the egensotning module sends the applicant a
 * content-free e-mail notification — a new-message notice pointing to Mina sidor — when an OUTBOUND
 * message (handläggare → sökande) is posted. The message body itself never leaves the in-app
 * thread; the e-mail only signals that a new message exists.
 *
 * Lives in the {@code shared} module (like {@link DecisionRecorded}) so both the producing
 * conversation module and any consuming type module can reference it without a direct module
 * dependency. {@code direction} is INBOUND (sökande → handläggare) or OUTBOUND (handläggare →
 * sökande); consumers typically act only on OUTBOUND.
 */
public record MessagePosted(
	String messageId,
	String errandId,
	String direction,
	String author,
	OffsetDateTime timestamp) {}
