package se.sundsvall.rtjmanagement.types.egensotning.details.integration.citizen;

import generated.se.sundsvall.citizen.CitizenExtended;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.citizen.configuration.CitizenConfiguration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.rtjmanagement.types.egensotning.details.integration.citizen.configuration.CitizenConfiguration.CLIENT_ID;

@FeignClient(name = CLIENT_ID, url = "${integration.citizen.url}", configuration = CitizenConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface CitizenClient {

	/**
	 * Resolve a citizen's personId (GUID) from their personnummer.
	 *
	 * @param  municipalityId the id of the municipality
	 * @param  personNumber   the applicant's personnummer
	 * @return                the citizen's personId (GUID), or {@code null} on 204 No Content
	 */
	@GetMapping(path = "/{municipalityId}/{personNumber}/guid", produces = APPLICATION_JSON_VALUE)
	String getGuid(
		@PathVariable final String municipalityId,
		@PathVariable final String personNumber);

	/**
	 * Fetch a citizen (incl. addresses) by personId.
	 *
	 * @param  municipalityId the id of the municipality
	 * @param  personId       the citizen's personId (GUID)
	 * @return                the citizen, or {@code null} on 204 No Content
	 */
	@GetMapping(path = "/{municipalityId}/{personId}", produces = APPLICATION_JSON_VALUE)
	CitizenExtended getCitizen(
		@PathVariable final String municipalityId,
		@PathVariable final String personId);
}
