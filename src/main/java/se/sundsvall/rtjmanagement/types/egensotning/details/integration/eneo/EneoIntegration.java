package se.sundsvall.rtjmanagement.types.egensotning.details.integration.eneo;

import generated.eneo.AskAssistant;
import generated.eneo.AskResponse;
import generated.eneo.FilePublic;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.dept44.problem.Problem;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static se.sundsvall.dept44.util.LogUtils.sanitizeForLogging;

/**
 * Thin wrapper over the municipality-specific {@link EneoClient}s. Resolves the right client for a
 * municipality and translates any transport/Eneo failure into a {@code BAD_GATEWAY} problem so the
 * caller can treat Eneo as a non-blocking dependency.
 */
@Component
public class EneoIntegration {

	static final String CLIENT_ID = "eneo";

	private static final Logger LOG = LoggerFactory.getLogger(EneoIntegration.class);

	/** Keyed by municipality id, value the municipality-specific configured {@link EneoClient}. */
	private final Map<String, EneoClient> eneoClients;

	public EneoIntegration(final Map<String, EneoClient> eneoClients) {
		this.eneoClients = eneoClients;
	}

	private EneoClient getEneoClient(final String municipalityId) {
		return ofNullable(eneoClients.get(municipalityId))
			.orElseThrow(() -> Problem.valueOf(INTERNAL_SERVER_ERROR, "Eneo client not configured for municipality id: %s".formatted(municipalityId)));
	}

	public AskResponse askAssistant(final String municipalityId, final UUID assistantId, final AskAssistant request) {
		final var client = getEneoClient(municipalityId);
		try {
			LOG.debug("Asking Eneo assistant {} with {} file(s)", assistantId, ofNullable(request.getFiles()).map(List::size).orElse(0));
			return client.askAssistant(assistantId, request);
		} catch (final Exception e) {
			LOG.error("Error asking Eneo assistant {}", assistantId, e);
			throw Problem.valueOf(BAD_GATEWAY, "Error asking Eneo assistant %s".formatted(assistantId));
		}
	}

	public FilePublic uploadFile(final String municipalityId, final MultipartFile file) {
		final var client = getEneoClient(municipalityId);
		try {
			LOG.debug("Uploading file '{}' to Eneo", sanitizeForLogging(file.getOriginalFilename()));
			return client.uploadFile(file).getBody();
		} catch (final Exception e) {
			LOG.error("Error uploading file '{}' to Eneo", sanitizeForLogging(file.getOriginalFilename()), e);
			throw Problem.valueOf(BAD_GATEWAY, "Error uploading file to Eneo");
		}
	}

	public void deleteFile(final String municipalityId, final UUID fileId) {
		try {
			getEneoClient(municipalityId).deleteFile(fileId);
		} catch (final Exception e) {
			// Best-effort cleanup — log and swallow so a failed delete never fails the validation result.
			LOG.warn("Error deleting Eneo file {}", fileId, e);
		}
	}
}
