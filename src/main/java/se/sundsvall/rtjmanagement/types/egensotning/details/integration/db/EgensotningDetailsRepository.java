package se.sundsvall.rtjmanagement.types.egensotning.details.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.time.LocalDate;
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

	/**
	 * Approved egensotning decisions expiring within the reminder window [{@code from}, {@code to}]
	 * that have not yet had a reminder sent — drives the expiry-reminder scheduler. A null
	 * {@code validUntil} (decision not time-limited) is naturally excluded by the BETWEEN bound.
	 */
	List<EgensotningDetailsEntity> findByValidUntilBetweenAndReminderSentAtIsNull(LocalDate from, LocalDate to);

	/**
	 * All egensotning details with an issued decision ({@code validFrom} set) — drives the
	 * address-change monitor (R6). The scheduler resolves each errand's status and re-checks
	 * folkbokföring, revoking only those still DECIDED whose applicant is no longer registered.
	 */
	List<EgensotningDetailsEntity> findByValidFromIsNotNull();
}
