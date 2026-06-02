package se.sundsvall.rtjmanagement.types.brandfarligvara.details.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.rtjmanagement.types.brandfarligvara.details.integration.db.model.BrandfarligVaraDetailsEntity;

@CircuitBreaker(name = "brandfarligVaraDetailsRepository")
public interface BrandfarligVaraDetailsRepository extends JpaRepository<BrandfarligVaraDetailsEntity, Long> {

	Optional<BrandfarligVaraDetailsEntity> findByErrandId(String errandId);

	long deleteByErrandId(String errandId);
}
