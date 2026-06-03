package se.sundsvall.rtjmanagement.types.egensotning.details.integration.templating;

import generated.se.sundsvall.templating.RenderRequest;
import generated.se.sundsvall.templating.RenderResponse;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@ExtendWith(MockitoExtension.class)
class TemplatingIntegrationTest {

	private static final String MUNICIPALITY_ID = "2281";

	@Mock
	private TemplatingClient templatingClientMock;

	@InjectMocks
	private TemplatingIntegration integration;

	@Test
	void renderPdfDecodesBase64Output() {
		final var pdf = "a-pdf-document".getBytes();
		when(templatingClientMock.render(eq(MUNICIPALITY_ID), any(RenderRequest.class)))
			.thenReturn(new RenderResponse().output(Base64.getEncoder().encodeToString(pdf)));

		final var result = integration.renderPdf(MUNICIPALITY_ID, new RenderRequest());

		assertThat(result).isEqualTo(pdf);
	}

	@Test
	void renderPdfWhenResponseNullThrowsBadGateway() {
		when(templatingClientMock.render(eq(MUNICIPALITY_ID), any(RenderRequest.class))).thenReturn(null);

		assertThatThrownBy(() -> integration.renderPdf(MUNICIPALITY_ID, new RenderRequest()))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_GATEWAY);
	}

	@Test
	void renderPdfWhenOutputBlankThrowsBadGateway() {
		when(templatingClientMock.render(eq(MUNICIPALITY_ID), any(RenderRequest.class)))
			.thenReturn(new RenderResponse().output("  "));

		assertThatThrownBy(() -> integration.renderPdf(MUNICIPALITY_ID, new RenderRequest()))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_GATEWAY);
	}
}
