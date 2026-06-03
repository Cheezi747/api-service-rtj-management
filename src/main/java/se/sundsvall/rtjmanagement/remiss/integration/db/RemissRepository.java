package se.sundsvall.rtjmanagement.remiss.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.rtjmanagement.remiss.integration.db.model.RemissEntity;

@CircuitBreaker(name = "remissRepository")
public interface RemissRepository extends JpaRepository<RemissEntity, String> {

	List<RemissEntity> findByErrandIdOrderByCreatedDesc(String errandId);

	Optional<RemissEntity> findByErrandIdAndId(String errandId, String id);

	long deleteByErrandId(String errandId);
}
