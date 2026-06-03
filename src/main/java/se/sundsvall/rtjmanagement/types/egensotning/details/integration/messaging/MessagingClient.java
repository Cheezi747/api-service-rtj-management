package se.sundsvall.rtjmanagement.types.egensotning.details.integration.messaging;

import generated.se.sundsvall.messaging.EmailRequest;
import generated.se.sundsvall.messaging.MessageResult;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.messaging.configuration.MessagingConfiguration;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.sundsvall.rtjmanagement.types.egensotning.details.integration.messaging.configuration.MessagingConfiguration.CLIENT_ID;

/**
 * Sends citizen-facing email through the Sundsvall messaging platform. Used by the egensotning
 * expiry-reminder scheduler to notify property owners before their decision lapses.
 */
@FeignClient(name = CLIENT_ID, url = "${integration.messaging.url}", configuration = MessagingConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface MessagingClient {

	/**
	 * Sends a plain-text email.
	 *
	 * @param  municipalityId the id of the municipality
	 * @param  request        the email payload (recipient, subject, message)
	 * @return                the messaging platform's result (message + delivery ids)
	 */
	@PostMapping(path = "/{municipalityId}/email", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	MessageResult sendEmail(
		@PathVariable(name = "municipalityId") final String municipalityId,
		@RequestBody final EmailRequest request);
}
