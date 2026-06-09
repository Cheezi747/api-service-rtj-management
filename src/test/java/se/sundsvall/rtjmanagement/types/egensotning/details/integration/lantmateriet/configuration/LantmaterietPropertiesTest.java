package se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.rtjmanagement.Application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class LantmaterietPropertiesTest {

	@Autowired
	private LantmaterietProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.oauth2TokenUrl()).isEqualTo("http://token.url");
		assertThat(properties.oauth2ClientId()).isEqualTo("the-client-id");
		assertThat(properties.oauth2ClientSecret()).isEqualTo("the-client-secret");
	}
}
