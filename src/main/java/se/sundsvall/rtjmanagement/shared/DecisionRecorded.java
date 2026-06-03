package se.sundsvall.rtjmanagement.shared;

import java.time.OffsetDateTime;

/**
 * Cross-module event: published by the decisions module whenever a decision is recorded against
 * an errand. Type modules subscribe to react — e.g. the egensotning module renders the formal
 * beslut as a PDF and stores it on the errand.
 *
 * Lives in the {@code shared} module (like {@link NotificationRequest}) so both the producing
 * decisions module and any consuming type module can reference it without a direct module
 * dependency. Carries the full decision payload (incl. {@code description}) so consumers don't
 * need to call back into the decisions module.
 */
public record DecisionRecorded(
	String decisionId,
	String errandId,
	String typeSlug,
	String outcome,
	String description,
	String decidedBy,
	OffsetDateTime timestamp) {}
