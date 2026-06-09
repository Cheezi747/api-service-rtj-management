package se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet.model.Registerbeteckningsreferens;

import static se.sundsvall.dept44.util.LogUtils.sanitizeForLogging;

/**
 * Thin wrapper over {@link RegisterbeteckningClient}. Resolves a fastighetsbeteckning to its top
 * gällande (current) registerbeteckning. Lantmäteriet is treated as a <b>non-blocking</b> dependency:
 * any lookup failure (or no match) yields an empty result so the caller can fall back to a local check
 * rather than blocking a citizen's submission on an upstream outage.
 */
@Component
public class LantmaterietIntegration {

	static final String STATUS_GALLANDE = "gällande";
	private static final int MAX_HITS = 1;

	private static final Logger LOG = LoggerFactory.getLogger(LantmaterietIntegration.class);

	private final RegisterbeteckningClient client;

	public LantmaterietIntegration(final RegisterbeteckningClient client) {
		this.client = client;
	}

	public Optional<Registerbeteckningsreferens> findReferens(final String beteckning) {
		if (!StringUtils.hasText(beteckning)) {
			return Optional.empty();
		}
		try {
			return Optional.ofNullable(client.getRegisterbeteckningsreferenser(beteckning, STATUS_GALLANDE, MAX_HITS))
				.orElseGet(List::of).stream()
				.findFirst();
		} catch (final RuntimeException e) {
			LOG.info("Lantmäteriet registerbeteckning lookup failed for '{}': {}", sanitizeForLogging(beteckning), e.getMessage());
			return Optional.empty();
		}
	}
}
