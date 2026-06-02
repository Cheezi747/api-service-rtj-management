package se.sundsvall.rtjmanagement.types.egensotning.details.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;

@CircuitBreaker(name = "egensotningDetailsRepository")
public interface EgensotningDetailsRepository extends JpaRepository<EgensotningDetailsEntity, Long> {

	Optional<EgensotningDetailsEntity> findByErrandId(String errandId);

	long deleteByErrandId(String errandId);

	/**
	 * All egensotning details for the same applicant on OTHER errands — drives the
	 * återansökan (re-application) check. Each row's {@code errandId} is then resolved
	 * to its errand status to classify the prior application.
	 */
	List<EgensotningDetailsEntity> findByPersonnummerAndErrandIdNot(String personnummer, String errandId);
}
