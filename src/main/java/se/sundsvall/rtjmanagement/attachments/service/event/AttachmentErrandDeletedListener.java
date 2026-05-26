package se.sundsvall.rtjmanagement.attachments.service.event;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import se.sundsvall.rtjmanagement.attachments.integration.db.AttachmentRepository;
import se.sundsvall.rtjmanagement.core.service.event.ErrandDeleted;

/**
 * Removes every attachment row tied to a deleted errand. {@code @ApplicationModuleListener}
 * runs asynchronously in a fresh transaction after the originating delete commits, with the
 * event durably staged in Spring Modulith's outbox in between — replaces the legacy DB-level
 * cascade and keeps the modules loosely coupled.
 */
@Component
class AttachmentErrandDeletedListener {

	private final AttachmentRepository repository;

	AttachmentErrandDeletedListener(final AttachmentRepository repository) {
		this.repository = repository;
	}

	@ApplicationModuleListener
	void on(final ErrandDeleted event) {
		repository.deleteByErrandId(event.errandId());
	}
}
