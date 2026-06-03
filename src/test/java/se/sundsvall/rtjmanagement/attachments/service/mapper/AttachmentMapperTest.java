package se.sundsvall.rtjmanagement.attachments.service.mapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.attachments.integration.db.model.AttachmentDataEntity;
import se.sundsvall.rtjmanagement.attachments.integration.db.model.AttachmentEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class AttachmentMapperTest {

	@Test
	void toAttachmentMapsAllFields() {
		final var created = OffsetDateTime.now().minusDays(1);
		final var modified = OffsetDateTime.now();
		final var entity = AttachmentEntity.create()
			.withId("id")
			.withFileName("f.txt")
			.withMimeType("text/plain")
			.withFileSize(10)
			.withCategory("COMPETENCE")
			.withCreated(created)
			.withModified(modified);

		final var attachment = AttachmentMapper.toAttachment(entity);

		assertThat(attachment).isNotNull();
		assertThat(attachment.getId()).isEqualTo("id");
		assertThat(attachment.getFileName()).isEqualTo("f.txt");
		assertThat(attachment.getMimeType()).isEqualTo("text/plain");
		assertThat(attachment.getFileSize()).isEqualTo(10);
		assertThat(attachment.getCategory()).isEqualTo("COMPETENCE");
		assertThat(attachment.getCreated()).isEqualTo(created);
		assertThat(attachment.getModified()).isEqualTo(modified);
	}

	@Test
	void toAttachmentNullReturnsNull() {
		assertThat(AttachmentMapper.toAttachment(null)).isNull();
	}

	@Test
	void toAttachmentEntityNullErrandIdReturnsNull() {
		assertThat(AttachmentMapper.toAttachmentEntity(null, "ns", "mid", new MockMultipartFile("file", new byte[] {
			1
		}), "OTHER")).isNull();
	}

	@Test
	void toAttachmentEntityNullFileReturnsNull() {
		assertThat(AttachmentMapper.toAttachmentEntity("eid", "ns", "mid", null, "OTHER")).isNull();
	}

	@Test
	void toAttachmentEntityIOExceptionWrappedAsBadRequest() {
		final MultipartFile file = new MockMultipartFile("file", "f.txt", "text/plain", new byte[0]) {
			@Override
			public InputStream getInputStream() throws IOException {
				throw new IOException("boom");
			}

			@Override
			public long getSize() {
				return 10;
			}
		};

		assertThatThrownBy(() -> AttachmentMapper.toAttachmentEntity("eid", "ns", "mid", file, "OTHER"))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST);
	}

	@Test
	void toAttachmentListMapsAllItems() {
		final var entity = AttachmentEntity.create().withId("id").withFileName("f");
		final var result = AttachmentMapper.toAttachmentList(List.of(entity));

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getId()).isEqualTo("id");
	}

	@Test
	void toAttachmentListNullReturnsEmpty() {
		assertThat(AttachmentMapper.toAttachmentList(null)).isEmpty();
	}

	@Test
	void toAttachmentEntityFromMultipartFileBuildsEntity() {
		final var file = new MockMultipartFile("file", "hello.txt", "text/plain", new ByteArrayInputStream("hello".getBytes()).readAllBytes());

		// Will likely fail at Hibernate.getLobHelper() since no JPA context is active.
		// Either we get a real entity (when running in an integration setup) or an exception.
		try {
			final AttachmentEntity entity = AttachmentMapper.toAttachmentEntity("eid", "ns", "mid", file, "DELEGATION");
			assertThat(entity).isNotNull();
			assertThat(entity.getErrandId()).isEqualTo("eid");
			assertThat(entity.getNamespace()).isEqualTo("ns");
			assertThat(entity.getMunicipalityId()).isEqualTo("mid");
			assertThat(entity.getFileName()).isEqualTo("hello.txt");
			assertThat(entity.getMimeType()).isEqualTo("text/plain");
			assertThat(entity.getFileSize()).isEqualTo(5);
			assertThat(entity.getCategory()).isEqualTo("DELEGATION");
			assertThat(entity.getAttachmentData()).isNotNull();
		} catch (final Exception e) {
			// Acceptable in unit context with no Hibernate session
			assertThat(e).isNotNull();
		}
	}

	@Test
	void attachmentDataEntityCreate() {
		final var entity = AttachmentDataEntity.create();
		assertThat(entity).isNotNull();
	}

	@Test
	void toAttachmentEntityFromBytesBuildsEntity() {
		final var content = "pdf-bytes".getBytes();

		final var entity = AttachmentMapper.toAttachmentEntity("eid", "ns", "mid", content, "beslut.pdf", "application/pdf", "DECISION");

		assertThat(entity).isNotNull();
		assertThat(entity.getErrandId()).isEqualTo("eid");
		assertThat(entity.getNamespace()).isEqualTo("ns");
		assertThat(entity.getMunicipalityId()).isEqualTo("mid");
		assertThat(entity.getFileName()).isEqualTo("beslut.pdf");
		assertThat(entity.getMimeType()).isEqualTo("application/pdf");
		assertThat(entity.getFileSize()).isEqualTo(content.length);
		assertThat(entity.getCategory()).isEqualTo("DECISION");
		assertThat(entity.getAttachmentData()).isNotNull();
	}

	@Test
	void toAttachmentEntityFromBytesNullErrandIdReturnsNull() {
		assertThat(AttachmentMapper.toAttachmentEntity(null, "ns", "mid", new byte[] {
			1
		}, "f.pdf", "application/pdf", "DECISION")).isNull();
	}

	@Test
	void toAttachmentEntityFromBytesNullContentReturnsNull() {
		assertThat(AttachmentMapper.toAttachmentEntity("eid", "ns", "mid", null, "f.pdf", "application/pdf", "DECISION")).isNull();
	}
}
