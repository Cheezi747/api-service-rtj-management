package se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet.configuration.LantmaterietConfiguration.CLIENT_ID;

@ExtendWith(MockitoExtension.class)
class LantmaterietConfigurationTest {

	@Spy
	private FeignMultiCustomizer feignMultiCustomizerSpy;

	@Mock
	private FeignBuilderCustomizer feignBuilderCustomizerMock;

	@Test
	void feignBuilderCustomizer() {
		final var configuration = new LantmaterietConfiguration();
		final var properties = new LantmaterietProperties("https://api.lantmateriet.se/token", "client-id", "client-secret");

		doReturn(feignMultiCustomizerSpy).when(feignMultiCustomizerSpy).withErrorDecoder(any(ProblemErrorDecoder.class));
		doReturn(feignMultiCustomizerSpy).when(feignMultiCustomizerSpy).withRetryableOAuth2InterceptorForClientRegistration(any(ClientRegistration.class));
		when(feignMultiCustomizerSpy.composeCustomizersToOne()).thenReturn(feignBuilderCustomizerMock);

		try (MockedStatic<FeignMultiCustomizer> feignMultiCustomizerMock = Mockito.mockStatic(FeignMultiCustomizer.class)) {
			feignMultiCustomizerMock.when(FeignMultiCustomizer::create).thenReturn(feignMultiCustomizerSpy);

			final var customizer = configuration.feignBuilderCustomizer(properties);

			verify(feignMultiCustomizerSpy).withErrorDecoder(any(ProblemErrorDecoder.class));
			verify(feignMultiCustomizerSpy).withRetryableOAuth2InterceptorForClientRegistration(any(ClientRegistration.class));
			verify(feignMultiCustomizerSpy).composeCustomizersToOne();
			assertThat(customizer).isSameAs(feignBuilderCustomizerMock);
		}
	}

	@Test
	void clientIdConstant() {
		assertThat(CLIENT_ID).isEqualTo("lantmateriet");
	}
}
