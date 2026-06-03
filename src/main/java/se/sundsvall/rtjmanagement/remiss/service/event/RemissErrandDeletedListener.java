package se.sundsvall.rtjmanagement.remiss.service.event;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import se.sundsvall.rtjmanagement.core.service.event.ErrandDeleted;
import se.sundsvall.rtjmanagement.remiss.integration.db.RemissRepository;

/**
 * Removes every remiss row tied to a deleted errand. Mirrors the permit module's cleanup —
 * {@code @ApplicationModuleListener} runs asynchronously in a fresh transaction after the originating
 * delete commits, keeping the modules loosely coupled.
 */
@Component
class RemissErrandDeletedListener {

	private final RemissRepository repository;

	RemissErrandDeletedListener(final RemissRepository repository) {
		this.repository = repository;
	}

	@ApplicationModuleListener
	void on(final ErrandDeleted event) {
		repository.deleteByErrandId(event.errandId());
	}
}
