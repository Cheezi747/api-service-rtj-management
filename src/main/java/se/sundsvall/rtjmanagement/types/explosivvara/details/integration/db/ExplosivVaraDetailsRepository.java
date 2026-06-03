package se.sundsvall.rtjmanagement.types.explosivvara.details.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.rtjmanagement.types.explosivvara.details.integration.db.model.ExplosivVaraDetailsEntity;

@CircuitBreaker(name = "explosivVaraDetailsRepository")
public interface ExplosivVaraDetailsRepository extends JpaRepository<ExplosivVaraDetailsEntity, Long> {

	Optional<ExplosivVaraDetailsEntity> findByErrandId(String errandId);

	long deleteByErrandId(String errandId);
}
