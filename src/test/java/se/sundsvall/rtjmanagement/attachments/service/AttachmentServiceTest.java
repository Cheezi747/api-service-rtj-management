package se.sundsvall.rtjmanagement.attachments.service;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.sql.rowset.serial.SerialBlob;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.attachments.integration.db.AttachmentRepository;
import se.sundsvall.rtjmanagement.attachments.integration.db.model.AttachmentDataEntity;
import se.sundsvall.rtjmanagement.attachments.integration.db.model.AttachmentEntity;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "MY_NAMESPACE";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String ATTACHMENT_ID = "dddddddd-dddd-dddd-dddd-dddddddddddd";

	@Mock
	private ErrandRepository errandRepositoryMock;

	@Mock
	private AttachmentRepository attachmentRepositoryMock;

	@InjectMocks
	private AttachmentService service;

	@Test
	void streamAttachmentFileWrapsSqlExceptionAsProblem() throws SQLException {
		// Force the IOException | SQLException catch branch via a blob whose stream throws SQLException.
		final var blob = mock(Blob.class);
		when(blob.getBinaryStream()).thenThrow(new SQLException("blob boom"));
		final var data = AttachmentDataEntity.create().withFile(blob);
		final var attachment = AttachmentEntity.create()
			.withId(ATTACHMENT_ID).withErrandId(ERRAND_ID)
			.withFileName("f.txt").withMimeType("text/plain").withFileSize(10)
			.withAttachmentData(data);
		final var response = mock(HttpServletResponse.class);

		when(attachmentRepositoryMock.findByNamespaceAndMunicipalityIdAndErrandIdAndId(NAMESPACE, MUNICIPALITY_ID, ERRAND_ID, ATTACHMENT_ID))
			.thenReturn(Optional.of(attachment));

		assertThatThrownBy(() -> service.streamAttachmentFile(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, ATTACHMENT_ID, response))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", INTERNAL_SERVER_ERROR);
	}

	@Test
	void readAttachmentsReturnsMappedList() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(mock(ErrandEntity.class)));
		when(attachmentRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(List.of(
			AttachmentEntity.create().withId(ATTACHMENT_ID).withErrandId(ERRAND_ID).withFileName("a.pdf").withMimeType("application/pdf").withFileSize(5)));

		final var result = service.readAttachments(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result).hasSize(1);
	}

	@Test
	void readAttachmentsThrowsWhenErrandMissing() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.readAttachments(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void readAttachmentReturnsMapped() {
		when(attachmentRepositoryMock.findByNamespaceAndMunicipalityIdAndErrandIdAndId(NAMESPACE, MUNICIPALITY_ID, ERRAND_ID, ATTACHMENT_ID))
			.thenReturn(Optional.of(AttachmentEntity.create().withId(ATTACHMENT_ID).withErrandId(ERRAND_ID).withFileName("a.pdf").withMimeType("application/pdf").withFileSize(5)));

		final var result = service.readAttachment(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, ATTACHMENT_ID);

		assertThat(result.getId()).isEqualTo(ATTACHMENT_ID);
	}

	@Test
	void readAttachmentThrowsWhenMissing() {
		when(attachmentRepositoryMock.findByNamespaceAndMunicipalityIdAndErrandIdAndId(NAMESPACE, MUNICIPALITY_ID, ERRAND_ID, ATTACHMENT_ID))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.readAttachment(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, ATTACHMENT_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void deleteAttachmentRemovesEntity() {
		final var entity = AttachmentEntity.create().withId(ATTACHMENT_ID).withErrandId(ERRAND_ID);
		when(attachmentRepositoryMock.findByNamespaceAndMunicipalityIdAndErrandIdAndId(NAMESPACE, MUNICIPALITY_ID, ERRAND_ID, ATTACHMENT_ID))
			.thenReturn(Optional.of(entity));

		service.deleteAttachment(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, ATTACHMENT_ID);

		verify(attachmentRepositoryMock).delete(entity);
	}

	@Test
	void createAttachmentSavesAndReturnsId() throws Exception {
		final var file = mock(MultipartFile.class);
		when(file.getOriginalFilename()).thenReturn("cert.pdf");
		when(file.getContentType()).thenReturn("application/pdf");
		when(file.getSize()).thenReturn(4L);
		when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(mock(ErrandEntity.class)));
		when(attachmentRepositoryMock.save(any())).thenReturn(AttachmentEntity.create().withId(ATTACHMENT_ID));

		final var id = service.createAttachment(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, file);

		assertThat(id).isEqualTo(ATTACHMENT_ID);
	}

	@Test
	void streamAttachmentFileCopiesToResponse() throws Exception {
		final var data = AttachmentDataEntity.create().withFile(new SerialBlob("file-bytes".getBytes()));
		final var attachment = AttachmentEntity.create()
			.withId(ATTACHMENT_ID).withErrandId(ERRAND_ID)
			.withFileName("f.txt").withMimeType("text/plain").withFileSize(10)
			.withAttachmentData(data);
		final var response = mock(HttpServletResponse.class);
		when(response.getOutputStream()).thenReturn(mock(ServletOutputStream.class));
		when(attachmentRepositoryMock.findByNamespaceAndMunicipalityIdAndErrandIdAndId(NAMESPACE, MUNICIPALITY_ID, ERRAND_ID, ATTACHMENT_ID))
			.thenReturn(Optional.of(attachment));

		service.streamAttachmentFile(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, ATTACHMENT_ID, response);

		verify(response).addHeader("Content-Type", "text/plain");
		verify(response).setContentLength(10);
	}
}
