package se.sundsvall.rtjmanagement.types.egensotning.details.integration.eneo;

import generated.eneo.AskAssistant;
import generated.eneo.AskResponse;
import generated.eneo.FilePublic;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@ExtendWith(MockitoExtension.class)
class EneoIntegrationTest {

	private static final UUID ASSISTANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
	private static final UUID FILE_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

	@Mock
	private EneoClient eneoClientMock;

	private EneoIntegration integration() {
		return new EneoIntegration(eneoClientMock);
	}

	private static ByteArrayMultipartFile file() {
		return new ByteArrayMultipartFile("upload_file", "bilaga.pdf", "application/pdf", "pdf".getBytes());
	}

	@Test
	void askAssistantReturnsResponse() {
		final var response = new AskResponse().answer("ok");
		when(eneoClientMock.askAssistant(ASSISTANT_ID, new AskAssistant().question("q"))).thenReturn(response);

		assertThat(integration().askAssistant(ASSISTANT_ID, new AskAssistant().question("q"))).isSameAs(response);
	}

	@Test
	void askAssistantWrapsErrorAsBadGateway() {
		when(eneoClientMock.askAssistant(any(), any())).thenThrow(new RuntimeException("boom"));

		assertThatThrownBy(() -> integration().askAssistant(ASSISTANT_ID, new AskAssistant().question("q")))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_GATEWAY);
	}

	@Test
	void uploadFileReturnsBody() {
		final var filePublic = new FilePublic().id(FILE_ID);
		when(eneoClientMock.uploadFile(any())).thenReturn(ResponseEntity.ok(filePublic));

		assertThat(integration().uploadFile(file()).getId()).isEqualTo(FILE_ID);
	}

	@Test
	void uploadFileWrapsErrorAsBadGateway() {
		when(eneoClientMock.uploadFile(any())).thenThrow(new RuntimeException("boom"));

		assertThatThrownBy(() -> integration().uploadFile(file()))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_GATEWAY);
	}

	@Test
	void deleteFileDelegates() {
		when(eneoClientMock.deleteFile(FILE_ID)).thenReturn(ResponseEntity.noContent().build());

		integration().deleteFile(FILE_ID);

		verify(eneoClientMock).deleteFile(FILE_ID);
	}

	@Test
	void deleteFileSwallowsError() {
		when(eneoClientMock.deleteFile(FILE_ID)).thenThrow(new RuntimeException("boom"));

		// Best-effort cleanup must not throw.
		integration().deleteFile(FILE_ID);
	}
}
