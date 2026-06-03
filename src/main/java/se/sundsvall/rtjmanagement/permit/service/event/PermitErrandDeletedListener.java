package se.sundsvall.rtjmanagement.permit.service.event;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import se.sundsvall.rtjmanagement.core.service.event.ErrandDeleted;
import se.sundsvall.rtjmanagement.permit.integration.db.PermitRepository;

/**
 * Removes every permit row tied to a deleted errand. Mirrors the decisions module's cleanup —
 * {@code @ApplicationModuleListener} runs asynchronously in a fresh transaction after the originating
 * delete commits, keeping the modules loosely coupled.
 */
@Component
class PermitErrandDeletedListener {

	private final PermitRepository repository;

	PermitErrandDeletedListener(final PermitRepository repository) {
		this.repository = repository;
	}

	@ApplicationModuleListener
	void on(final ErrandDeleted event) {
		repository.deleteByErrandId(event.errandId());
	}
}
