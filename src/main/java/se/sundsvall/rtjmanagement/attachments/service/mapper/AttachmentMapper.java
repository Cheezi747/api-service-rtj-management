package se.sundsvall.rtjmanagement.attachments.service.mapper;

import java.io.IOException;
import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.rtjmanagement.attachments.api.model.Attachment;
import se.sundsvall.rtjmanagement.attachments.integration.db.model.AttachmentDataEntity;
import se.sundsvall.rtjmanagement.attachments.integration.db.model.AttachmentEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public final class AttachmentMapper {

	private AttachmentMapper() {}

	public static Attachment toAttachment(final AttachmentEntity entity) {
		return ofNullable(entity)
			.map(e -> Attachment.create()
				.withId(e.getId())
				.withFileName(e.getFileName())
				.withMimeType(e.getMimeType())
				.withFileSize(e.getFileSize())
				.withCategory(e.getCategory())
				.withCreated(e.getCreated())
				.withModified(e.getModified()))
			.orElse(null);
	}

	public static AttachmentEntity toAttachmentEntity(final String errandId, final String namespace,
		final String municipalityId, final MultipartFile file, final String category) {

		if (errandId == null || file == null) {
			return null;
		}
		try {
			return AttachmentEntity.create()
				.withErrandId(errandId)
				.withNamespace(namespace)
				.withMunicipalityId(municipalityId)
				.withFileName(file.getOriginalFilename())
				.withMimeType(file.getContentType())
				.withFileSize(Math.toIntExact(file.getSize()))
				.withCategory(category)
				.withAttachmentData(AttachmentDataEntity.create()
					.withFile(Hibernate.getLobHelper().createBlob(file.getInputStream(), file.getSize())));
		} catch (final IOException ioException) {
			throw Problem.valueOf(BAD_REQUEST, "Could not read input stream: %s".formatted(ioException.getMessage()));
		}
	}

	/**
	 * Builds an attachment entity from in-memory bytes (e.g. a system-generated PDF) rather than an
	 * uploaded {@link MultipartFile}. Used when the service itself produces the file content.
	 */
	public static AttachmentEntity toAttachmentEntity(final String errandId, final String namespace,
		final String municipalityId, final byte[] content, final String fileName, final String mimeType, final String category) {

		if (errandId == null || content == null) {
			return null;
		}
		return AttachmentEntity.create()
			.withErrandId(errandId)
			.withNamespace(namespace)
			.withMunicipalityId(municipalityId)
			.withFileName(fileName)
			.withMimeType(mimeType)
			.withFileSize(content.length)
			.withCategory(category)
			.withAttachmentData(AttachmentDataEntity.create()
				.withFile(Hibernate.getLobHelper().createBlob(content)));
	}

	public static List<Attachment> toAttachmentList(final List<AttachmentEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(AttachmentMapper::toAttachment)
			.toList();
	}
}
