package se.sundsvall.rtjmanagement.types.egensotning.details.integration.eneo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration for the Eneo (Sundsvall LLM platform) integration used to validate egensotning
 * attachments. Each document type is validated by its own purpose-built Eneo assistant
 * (brandskyddskontroll resp. egensotning); both live in the same Eneo space. OAuth2 is built
 * programmatically in {@link EneoConfiguration}, and each municipality gets its own base url +
 * api-key.
 */
@Validated
@ConfigurationProperties(prefix = "integration.eneo")
public record EneoProperties(

	@Valid @NotNull Oauth2 oauth2,

	@Valid @NotNull Assistants assistants,

	UUID spaceId,

	@DefaultValue("5") int connectTimeoutInSeconds,

	@DefaultValue("30") int readTimeoutInSeconds,

	Map<String, MunicipalityConfig> municipalities) {

	public record Oauth2(
		@NotBlank String tokenUrl,
		@NotBlank String clientId,
		@NotBlank String clientSecret,
		@DefaultValue("client_credentials") String authorizationGrantType) {
	}

	/** Eneo assistant ids, one per validated document type. */
	public record Assistants(
		@NotNull UUID brandskyddskontroll,
		@NotNull UUID egensotning) {
	}

	public record MunicipalityConfig(String url, String apiKey) {
	}
}
