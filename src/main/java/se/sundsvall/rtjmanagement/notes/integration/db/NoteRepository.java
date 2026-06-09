package se.sundsvall.rtjmanagement.notes.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.rtjmanagement.notes.integration.db.model.NoteEntity;

@CircuitBreaker(name = "noteRepository")
public interface NoteRepository extends JpaRepository<NoteEntity, String> {

	List<NoteEntity> findByErrandIdOrderByCreatedDesc(String errandId);
}
