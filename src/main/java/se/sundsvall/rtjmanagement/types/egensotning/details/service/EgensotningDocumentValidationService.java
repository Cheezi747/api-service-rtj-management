package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import generated.eneo.AskAssistant;
import java.io.IOException;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.attachments.integration.db.AttachmentRepository;
import se.sundsvall.rtjmanagement.attachments.integration.db.model.AttachmentEntity;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.stakeholders.service.StakeholderService;
import se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig;
import se.sundsvall.rtjmanagement.types.egensotning.details.api.model.DocumentValidationResult;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.EgensotningDetailsRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.eneo.ByteArrayMultipartFile;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.eneo.EneoIntegration;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.eneo.EneoProperties;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Validates the two egensotning attachments with the Eneo LLM platform. Each document type is sent
 * to its own purpose-built Eneo assistant — the brandskyddskontroll PDF to the brandskyddskontroll
 * assistant, the utbildningsintyg PDF to the egensotning assistant — and each assistant judges
 * whether the document is the right type, valid, and matches the applicant (name / personnummer /
 * fastighet). The overall verdict is the AND of both.
 *
 * The verdict gates auto-approval. A non-valid verdict, an unparseable answer, or an Eneo outage all
 * resolve to {@code valid=false} (non-blocking) so the BPMN diverts the errand to manual review —
 * the LLM never auto-rejects. The result is persisted on the details row for the handläggare/audit.
 */
@Service
@Transactional
public class EgensotningDocumentValidationService {

	private static final Logger LOG = LoggerFactory.getLogger(EgensotningDocumentValidationService.class);

	private static final JsonMapper JSON = JsonMapper.builder()
		.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
		.build();

	private static final String ERRAND_NOT_FOUND_MESSAGE = "No errand with id '%s' found in namespace '%s' for municipality id '%s'";
	private static final String WRONG_TYPE_MESSAGE = "Errand '%s' has typeSlug '%s'; egensotning document validation requires typeSlug '%s'";

	private static final String UNKNOWN = "(okänt)";
	private static final String DOC_BRANDSKYDDSKONTROLL = "brandskyddskontrollintyg";
	private static final String DOC_UTBILDNINGSINTYG = "utbildningsintyg";
	private static final String REASON_MISSING_DOCUMENTS = "Båda bilagorna (brandskyddskontroll och utbildningsintyg) måste finnas för dokumentvalidering.";
	private static final String REASON_ENEO_UNAVAILABLE = "Kunde inte validera dokumentet mot Eneo — skickas till manuell granskning.";
	private static final String REASON_UNPARSEABLE = "Eneo returnerade ett svar som inte kunde tolkas — skickas till manuell granskning.";

	private final ErrandRepository errandRepository;
	private final EgensotningDetailsRepository detailsRepository;
	private final AttachmentRepository attachmentRepository;
	private final StakeholderService stakeholderService;
	private final EneoIntegration eneoIntegration;
	private final EneoProperties eneoProperties;

	EgensotningDocumentValidationService(final ErrandRepository errandRepository, final EgensotningDetailsRepository detailsRepository,
		final AttachmentRepository attachmentRepository, final StakeholderService stakeholderService,
		final EneoIntegration eneoIntegration, final EneoProperties eneoProperties) {
		this.errandRepository = errandRepository;
		this.detailsRepository = detailsRepository;
		this.attachmentRepository = attachmentRepository;
		this.stakeholderService = stakeholderService;
		this.eneoIntegration = eneoIntegration;
		this.eneoProperties = eneoProperties;
	}

	public DocumentValidationResult validateDocuments(final String municipalityId, final String namespace, final String errandId) {
		assertEgensotningErrand(municipalityId, namespace, errandId);

		final var details = detailsRepository.findByErrandId(errandId).orElse(null);

		final var brandskyddskontroll = findAttachment(errandId, EgensotningModuleConfig.CATEGORY_BRANDSKYDDSKONTROLL);
		final var utbildningsintyg = findAttachment(errandId, EgensotningModuleConfig.CATEGORY_UTBILDNINGSINTYG);
		if (brandskyddskontroll.isEmpty() || utbildningsintyg.isEmpty()) {
			LOG.info("Egensotning errand {} is missing one or both required bilagor; document validation routes to manual review", errandId);
			return persist(details, DocumentValidationResult.create().withValid(false).withReason(REASON_MISSING_DOCUMENTS));
		}

		final var applicantContext = buildApplicantContext(municipalityId, namespace, errandId, details);

		// brandskyddskontroll PDF → brandskyddskontroll assistant; utbildningsintyg PDF → egensotning assistant.
		final var brandResult = validateOne(eneoProperties.assistants().brandskyddskontroll(), DOC_BRANDSKYDDSKONTROLL, applicantContext, brandskyddskontroll.get());
		final var utbResult = validateOne(eneoProperties.assistants().egensotning(), DOC_UTBILDNINGSINTYG, applicantContext, utbildningsintyg.get());

		return persist(details, combine(brandResult, utbResult));
	}

	private DocumentValidationResult validateOne(final UUID assistantId, final String documentLabel,
		final String applicantContext, final AttachmentEntity attachment) {
		UUID uploadedFileId = null;
		try {
			final var file = new ByteArrayMultipartFile("upload_file", attachment.getFileName(), attachment.getMimeType(), readBytes(attachment));
			uploadedFileId = eneoIntegration.uploadFile(file).getId();

			final var request = new AskAssistant().question(buildPrompt(documentLabel, applicantContext)).files(List.of(uploadedFileId));
			final var answer = eneoIntegration.askAssistant(assistantId, request).getAnswer();

			return parseVerdict(answer);
		} catch (final ThrowableProblem e) {
			// Eneo unreachable / upstream error — non-blocking, divert to manual review.
			// The upstream cause is carried into the reason (handläggare-facing audit) for diagnosis.
			LOG.warn("Eneo validation of {} failed: {}", documentLabel, e.getMessage());
			return DocumentValidationResult.create().withValid(false).withReason(REASON_ENEO_UNAVAILABLE + " [" + e.getMessage() + "]");
		} finally {
			Optional.ofNullable(uploadedFileId).ifPresent(eneoIntegration::deleteFile);
		}
	}

	/**
	 * Combines the two per-document verdicts: valid only if both are valid; each boolean flag ANDed;
	 * reasons concatenated so the handläggare sees both motivations.
	 */
	private static DocumentValidationResult combine(final DocumentValidationResult brand, final DocumentValidationResult utbildning) {
		return DocumentValidationResult.create()
			.withValid(Boolean.TRUE.equals(brand.getValid()) && Boolean.TRUE.equals(utbildning.getValid()))
			.withDocumentTypeOk(Boolean.TRUE.equals(brand.getDocumentTypeOk()) && Boolean.TRUE.equals(utbildning.getDocumentTypeOk()))
			.withIdentityMatch(Boolean.TRUE.equals(brand.getIdentityMatch()) && Boolean.TRUE.equals(utbildning.getIdentityMatch()))
			.withReason("Brandskyddskontroll: %s | Utbildningsintyg: %s".formatted(
				Optional.ofNullable(brand.getReason()).orElse("-"),
				Optional.ofNullable(utbildning.getReason()).orElse("-")));
	}

	/**
	 * Parses the LLM answer into a verdict. The assistant is instructed to reply with a single JSON
	 * object; we defensively extract the {@code { ... }} span (in case it is wrapped in prose or code
	 * fences) and fall back to a non-valid verdict if it cannot be parsed.
	 */
	private DocumentValidationResult parseVerdict(final String answer) {
		final var json = extractJsonObject(answer);
		if (json == null) {
			LOG.warn("Eneo answer contained no JSON object; routing to manual review");
			return DocumentValidationResult.create().withValid(false).withReason(REASON_UNPARSEABLE);
		}
		try {
			final var verdict = JSON.readValue(json, Verdict.class);
			return DocumentValidationResult.create()
				.withValid(Boolean.TRUE.equals(verdict.valid()))
				.withDocumentTypeOk(verdict.documentTypeOk())
				.withIdentityMatch(verdict.identityMatch())
				.withReason(verdict.reason());
		} catch (final Exception e) {
			LOG.warn("Failed to parse Eneo verdict JSON; routing to manual review", e);
			return DocumentValidationResult.create().withValid(false).withReason(REASON_UNPARSEABLE);
		}
	}

	private static String extractJsonObject(final String answer) {
		if (answer == null) {
			return null;
		}
		final var start = answer.indexOf('{');
		final var end = answer.lastIndexOf('}');
		return (start >= 0 && end > start) ? answer.substring(start, end + 1) : null;
	}

	private String buildApplicantContext(final String municipalityId, final String namespace, final String errandId, final EgensotningDetailsEntity details) {
		final var applicantName = stakeholderService.readAll(municipalityId, namespace, errandId).stream()
			.filter(stakeholder -> EgensotningModuleConfig.ROLE_APPLICANT.equals(stakeholder.getRole()))
			.findFirst()
			.map(stakeholder -> (Optional.ofNullable(stakeholder.getFirstName()).orElse("") + " " + Optional.ofNullable(stakeholder.getLastName()).orElse("")).trim())
			.filter(name -> !name.isBlank())
			.orElse(UNKNOWN);

		return """
			- Namn: %s
			- Personnummer: %s
			- Fastighet: %s
			- Adress: %s""".formatted(
			applicantName,
			Optional.ofNullable(details).map(EgensotningDetailsEntity::getPersonnummer).orElse(UNKNOWN),
			Optional.ofNullable(details).map(EgensotningDetailsEntity::getFastighetsbeteckning).orElse(UNKNOWN),
			Optional.ofNullable(details).map(EgensotningDetailsEntity::getPropertyAddress).orElse(UNKNOWN));
	}

	private String buildPrompt(final String documentLabel, final String applicantContext) {
		return """
			Validera det bifogade dokumentet i en ansökan om egensotning. Förväntad dokumenttyp: %s.

			Kontrollera att:
			1. Dokumentet är av rätt typ (%s).
			2. Dokumentet är giltigt och fullständigt.
			3. Namn, personnummer och fastighet/adress i dokumentet stämmer med sökanden.

			Sökandens uppgifter:
			%s

			Svara ENDAST med ett JSON-objekt på formen:
			{"valid": boolean, "documentTypeOk": boolean, "identityMatch": boolean, "reason": "kort motivering på svenska"}
			Sätt "valid" till true endast om alla tre kontrollerna är uppfyllda.""".formatted(documentLabel, documentLabel, applicantContext);
	}

	private Optional<AttachmentEntity> findAttachment(final String errandId, final String category) {
		return attachmentRepository.findByErrandId(errandId).stream()
			.filter(attachment -> category.equals(attachment.getCategory()))
			.findFirst();
	}

	private static byte[] readBytes(final AttachmentEntity attachment) {
		try (final var stream = attachment.getAttachmentData().getFile().getBinaryStream()) {
			return stream.readAllBytes();
		} catch (final SQLException | IOException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "Could not read attachment '%s' content".formatted(attachment.getId()));
		}
	}

	private DocumentValidationResult persist(final EgensotningDetailsEntity details, final DocumentValidationResult result) {
		Optional.ofNullable(details).ifPresent(entity -> {
			entity.setDocumentsValid(result.getValid());
			entity.setDocumentValidationDetail(result.getReason());
			entity.setDocumentValidatedAt(OffsetDateTime.now(ZoneId.systemDefault()));
			detailsRepository.save(entity);
		});
		return result;
	}

	private void assertEgensotningErrand(final String municipalityId, final String namespace, final String errandId) {
		final var errand = errandRepository.findByIdAndNamespaceAndMunicipalityId(errandId, namespace, municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERRAND_NOT_FOUND_MESSAGE.formatted(errandId, namespace, municipalityId)));
		if (!EgensotningModuleConfig.TYPE_SLUG.equals(errand.getTypeSlug())) {
			throw Problem.valueOf(BAD_REQUEST, WRONG_TYPE_MESSAGE.formatted(errandId, errand.getTypeSlug(), EgensotningModuleConfig.TYPE_SLUG));
		}
	}

	private record Verdict(Boolean valid, Boolean documentTypeOk, Boolean identityMatch, String reason) {
	}
}
