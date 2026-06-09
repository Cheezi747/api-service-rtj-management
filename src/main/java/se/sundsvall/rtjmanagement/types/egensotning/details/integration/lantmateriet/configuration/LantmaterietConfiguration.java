package se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet.configuration;

import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

/**
 * Feign config for the Lantmäteriet registerbeteckning API. Self-contained OAuth2 client-credentials
 * (token-uri / client-id / client-secret + the {@code registerbeteckning_direkt_v5_read} scope come
 * from {@code integration.lantmateriet.*}), so it needs no entry in {@code spring.security.oauth2.client}.
 */
@Import(FeignConfiguration.class)
public class LantmaterietConfiguration {

	public static final String CLIENT_ID = "lantmateriet";
	static final String SCOPE_REGISTERBETECKNING_DIREKT_V5_READ = "registerbeteckning_direkt_v5_read";

	@Bean
	FeignBuilderCustomizer feignBuilderCustomizer(final LantmaterietProperties properties) {
		return FeignMultiCustomizer.create()
			.withErrorDecoder(new ProblemErrorDecoder(CLIENT_ID))
			.withRetryableOAuth2InterceptorForClientRegistration(ClientRegistration.withRegistrationId(CLIENT_ID)
				.tokenUri(properties.oauth2TokenUrl())
				.clientId(properties.oauth2ClientId())
				.clientSecret(properties.oauth2ClientSecret())
				.scope(SCOPE_REGISTERBETECKNING_DIREKT_V5_READ)
				.authorizationGrantType(new AuthorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS.getValue()))
				.build())
			.composeCustomizersToOne();
	}
}
