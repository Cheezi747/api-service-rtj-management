package se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.model.SotningsobjektEntity;

@CircuitBreaker(name = "sotningsobjektRepository")
public interface SotningsobjektRepository extends JpaRepository<SotningsobjektEntity, String> {

	List<SotningsobjektEntity> findByErrandIdOrderByTypAscFabrikatAsc(String errandId);

	Optional<SotningsobjektEntity> findByErrandIdAndId(String errandId, String id);

	boolean existsByErrandId(String errandId);

	long deleteByErrandId(String errandId);
}
