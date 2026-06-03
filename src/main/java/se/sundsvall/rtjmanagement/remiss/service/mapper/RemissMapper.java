package se.sundsvall.rtjmanagement.remiss.service.mapper;

import java.util.List;
import se.sundsvall.rtjmanagement.remiss.api.model.Remiss;
import se.sundsvall.rtjmanagement.remiss.integration.db.model.RemissEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class RemissMapper {

	private RemissMapper() {}

	public static Remiss toRemiss(final RemissEntity entity) {
		return ofNullable(entity)
			.map(e -> Remiss.create()
				.withId(e.getId())
				.withInstans(e.getInstans())
				.withRecipient(e.getRecipient())
				.withSentAt(e.getSentAt())
				.withDueAt(e.getDueAt())
				.withResponseText(e.getResponseText())
				.withStatus(e.getStatus())
				.withCreated(e.getCreated())
				.withModified(e.getModified()))
			.orElse(null);
	}

	public static RemissEntity toRemissEntity(final Remiss remiss, final String errandId) {
		return ofNullable(remiss)
			.map(source -> RemissEntity.create()
				.withErrandId(errandId)
				.withInstans(source.getInstans())
				.withRecipient(source.getRecipient())
				.withSentAt(source.getSentAt())
				.withDueAt(source.getDueAt())
				.withResponseText(source.getResponseText())
				.withStatus(source.getStatus()))
			.orElse(null);
	}

	public static List<Remiss> toRemissList(final List<RemissEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(RemissMapper::toRemiss)
			.toList();
	}
}
