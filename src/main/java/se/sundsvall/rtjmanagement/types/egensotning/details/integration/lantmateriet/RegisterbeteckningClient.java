package se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet.configuration.LantmaterietConfiguration;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet.model.Registerbeteckningsreferens;

import static se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet.configuration.LantmaterietConfiguration.CLIENT_ID;

/**
 * Lantmäteriets registerbeteckning Direkt v5 — fritextsökning som resolverar en fastighetsbeteckning
 * till dess kanoniska form + registerenhet (objektidentitet).
 */
@FeignClient(name = CLIENT_ID, url = "${integration.lantmateriet.registerbeteckning.url}", configuration = LantmaterietConfiguration.class)
@CircuitBreaker(name = CLIENT_ID)
public interface RegisterbeteckningClient {

	@GetMapping(path = "/referens/fritext")
	List<Registerbeteckningsreferens> getRegisterbeteckningsreferenser(
		@RequestParam final String beteckning,
		@RequestParam final String status,
		@RequestParam final int maxHits);
}
