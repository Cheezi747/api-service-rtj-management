package se.sundsvall.rtjmanagement.core.service.event;

import java.time.OffsetDateTime;

public record ErrandCreated(
	String errandId,
	String typeSlug,
	String municipalityId,
	String namespace,
	String reporterUserId,
	String assignedUserId,
	OffsetDateTime timestamp)
	implements
	ErrandEvent {}
