package se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OAuth2 client-credentials properties for the Lantmäteriet registerbeteckning API. The API base URL
 * is read directly by the Feign client from {@code integration.lantmateriet.registerbeteckning.url}.
 */
@ConfigurationProperties("integration.lantmateriet")
public record LantmaterietProperties(String oauth2TokenUrl, String oauth2ClientId, String oauth2ClientSecret) {
}
