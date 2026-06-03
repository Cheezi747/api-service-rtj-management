package se.sundsvall.rtjmanagement.attachments.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.rtjmanagement.attachments.api.model.Attachment;
import se.sundsvall.rtjmanagement.attachments.integration.db.AttachmentRepository;
import se.sundsvall.rtjmanagement.attachments.integration.db.model.AttachmentEntity;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.rtjmanagement.attachments.service.mapper.AttachmentMapper.toAttachment;
import static se.sundsvall.rtjmanagement.attachments.service.mapper.AttachmentMapper.toAttachmentEntity;
import static se.sundsvall.rtjmanagement.attachments.service.mapper.AttachmentMapper.toAttachmentList;

@Service
@Transactional
public class AttachmentService {

	private static final String ERRAND_NOT_FOUND_MESSAGE = "No errand with id '%s' found in namespace '%s' for municipality id '%s'";
	private static final String ATTACHMENT_NOT_FOUND_MESSAGE = "No attachment with id '%s' found on errand '%s' in namespace '%s' for municipality id '%s'";
	private static final String STREAM_ERROR_MESSAGE = "%s occurred when copying file with attachment id '%s' to response: %s";

	private final ErrandRepository errandRepository;
	private final AttachmentRepository attachmentRepository;

	AttachmentService(final ErrandRepository errandRepository, final AttachmentRepository attachmentRepository) {
		this.errandRepository = errandRepository;
		this.attachmentRepository = attachmentRepository;
	}

	public String createAttachment(final String municipalityId, final String namespace, final String errandId, final MultipartFile file, final String category) {
		ensureErrandExists(municipalityId, namespace, errandId);
		final var saved = attachmentRepository.save(toAttachmentEntity(errandId, namespace, municipalityId, file, category));
		return saved.getId();
	}

	/**
	 * Stores a system-generated file (e.g. a rendered decision PDF) as an attachment on the errand.
	 */
	public String createAttachment(final String municipalityId, final String namespace, final String errandId,
		final byte[] content, final String fileName, final String mimeType, final String category) {
		ensureErrandExists(municipalityId, namespace, errandId);
		final var saved = attachmentRepository.save(toAttachmentEntity(errandId, namespace, municipalityId, content, fileName, mimeType, category));
		return saved.getId();
	}

	@Transactional(readOnly = true)
	public List<Attachment> readAttachments(final String municipalityId, final String namespace, final String errandId) {
		ensureErrandExists(municipalityId, namespace, errandId);
		return toAttachmentList(attachmentRepository.findByErrandId(errandId));
	}

	@Transactional(readOnly = true)
	public Attachment readAttachment(final String municipalityId, final String namespace, final String errandId, final String attachmentId) {
		return toAttachment(findAttachment(municipalityId, namespace, errandId, attachmentId));
	}

	public void streamAttachmentFile(final String municipalityId, final String namespace, final String errandId, final String attachmentId, final HttpServletResponse response) {
		final var attachment = findAttachment(municipalityId, namespace, errandId, attachmentId);
		try {
			response.addHeader(CONTENT_TYPE, attachment.getMimeType());
			response.addHeader(CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"");
			ofNullable(attachment.getFileSize()).ifPresent(response::setContentLength);
			StreamUtils.copy(attachment.getAttachmentData().getFile().getBinaryStream(), response.getOutputStream());
		} catch (final IOException | SQLException exception) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, STREAM_ERROR_MESSAGE.formatted(exception.getClass().getSimpleName(), attachment.getId(), exception.getMessage()));
		}
	}

	public void deleteAttachment(final String municipalityId, final String namespace, final String errandId, final String attachmentId) {
		final var entity = findAttachment(municipalityId, namespace, errandId, attachmentId);
		attachmentRepository.delete(entity);
	}

	private void ensureErrandExists(final String municipalityId, final String namespace, final String errandId) {
		errandRepository.findByIdAndNamespaceAndMunicipalityId(errandId, namespace, municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERRAND_NOT_FOUND_MESSAGE.formatted(errandId, namespace, municipalityId)));
	}

	private AttachmentEntity findAttachment(final String municipalityId, final String namespace, final String errandId, final String attachmentId) {
		return attachmentRepository.findByNamespaceAndMunicipalityIdAndErrandIdAndId(namespace, municipalityId, errandId, attachmentId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ATTACHMENT_NOT_FOUND_MESSAGE.formatted(attachmentId, errandId, namespace, municipalityId)));
	}
}
