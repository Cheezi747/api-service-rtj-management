package se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet.model.Registerbeteckningsreferens;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LantmaterietIntegrationTest {

	@Mock
	private RegisterbeteckningClient clientMock;

	@InjectMocks
	private LantmaterietIntegration integration;

	@Test
	void findReferensReturnsTopMatch() {
		final var ref = new Registerbeteckningsreferens("id1", "enh1", "SUNDSVALL STENSTADEN 1:23");
		when(clientMock.getRegisterbeteckningsreferenser("Sundsvall Stenstaden 1:23", "gällande", 1)).thenReturn(List.of(ref));

		assertThat(integration.findReferens("Sundsvall Stenstaden 1:23")).contains(ref);
	}

	@Test
	void findReferensEmptyWhenNoMatch() {
		when(clientMock.getRegisterbeteckningsreferenser(any(), any(), anyInt())).thenReturn(List.of());

		assertThat(integration.findReferens("Okänd 1:1")).isEmpty();
	}

	@Test
	void findReferensEmptyWhenClientReturnsNull() {
		when(clientMock.getRegisterbeteckningsreferenser(any(), any(), anyInt())).thenReturn(null);

		assertThat(integration.findReferens("Sundsvall 1:1")).isEmpty();
	}

	@Test
	void findReferensEmptyOnClientError() {
		when(clientMock.getRegisterbeteckningsreferenser(any(), any(), anyInt())).thenThrow(new RuntimeException("boom"));

		assertThat(integration.findReferens("Sundsvall 1:1")).isEmpty();
	}

	@Test
	void findReferensEmptyForBlankInputWithoutCallingClient() {
		assertThat(integration.findReferens("   ")).isEmpty();
		assertThat(integration.findReferens(null)).isEmpty();
		verifyNoInteractions(clientMock);
	}
}
