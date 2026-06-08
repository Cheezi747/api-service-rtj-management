package se.sundsvall.rtjmanagement.types.egensotning.details.integration.eneo;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ByteArrayMultipartFileTest {

	@Test
	void exposesContentAndMetadata() throws IOException {
		final var content = "pdf-bytes".getBytes();
		final var file = new ByteArrayMultipartFile("upload_file", "bilaga.pdf", "application/pdf", content);

		assertThat(file.getName()).isEqualTo("upload_file");
		assertThat(file.getOriginalFilename()).isEqualTo("bilaga.pdf");
		assertThat(file.getContentType()).isEqualTo("application/pdf");
		assertThat(file.isEmpty()).isFalse();
		assertThat(file.getSize()).isEqualTo(content.length);
		assertThat(file.getBytes()).isEqualTo(content);
		assertThat(file.getInputStream().readAllBytes()).isEqualTo(content);
	}

	@Test
	void nullContentIsEmpty() {
		final var file = new ByteArrayMultipartFile("upload_file", "empty.pdf", "application/pdf", null);

		assertThat(file.isEmpty()).isTrue();
		assertThat(file.getSize()).isZero();
	}

	@Test
	void transferToWritesContent() throws IOException {
		final var content = "pdf-bytes".getBytes();
		final var file = new ByteArrayMultipartFile("upload_file", "bilaga.pdf", "application/pdf", content);
		final var dest = Files.createTempFile("eneo-test", ".pdf");

		file.transferTo(dest);

		assertThat(Files.readAllBytes(dest)).isEqualTo(content);
		Files.deleteIfExists(dest);
	}
}
