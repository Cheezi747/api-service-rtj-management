package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import generated.se.sundsvall.messaging.EmailRequest;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.rtjmanagement.core.api.model.PatchErrand;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.core.service.ErrandService;
import se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.EgensotningDetailsRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.messaging.MessagingClient;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_DECIDED;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_REVOKED;

/**
 * Återkallar ett godkänt egensotningsmedgivande (R6). Sätter ärendets status till {@code REVOKED}
 * (via core, så övergången hamnar i statushistoriken), nollställer {@code validUntil} så att inga
 * fler förnyelsepåminnelser skickas, stämplar {@code revokedAt}/{@code revocationReason} för
 * handläggar-UI/revision och aviserar sökanden via mejl. Kan bara köras på ett {@code DECIDED}
 * egensotningsärende.
 *
 * Triggas av {@code EgensotningAddressMonitorScheduler} (automatiskt vid adressändring) eller manuellt
 * av en handläggare via revoke-endpointen (t.ex. vid underkänd brandskyddskontroll).
 */
@Service
@Transactional
public class EgensotningRevocationService {

	private static final Logger LOG = LoggerFactory.getLogger(EgensotningRevocationService.class);

	private static final String EMAIL_SUBJECT = "Ditt beslut om egensotning har återkallats";
	private static final String EMAIL_MESSAGE_TEMPLATE = """
		Hej! Ditt beslut om egensotning för %s har återkallats. Du behöver fortsättningsvis anlita ordinarie \
		sotning tills ett nytt medgivande har beviljats. Vid frågor, kontakta Räddningstjänsten Medelpad.""";

	private static final String ERRAND_NOT_FOUND_MESSAGE = "No errand with id '%s' found in namespace '%s' for municipality id '%s'";
	private static final String WRONG_TYPE_MESSAGE = "Errand '%s' has typeSlug '%s'; egensotning revocation requires typeSlug '%s'";
	private static final String NOT_DECIDED_MESSAGE = "Errand '%s' is in status '%s'; only a DECIDED egensotning decision can be revoked";

	private final ErrandRepository errandRepository;
	private final EgensotningDetailsRepository detailsRepository;
	private final ErrandService errandService;
	private final MessagingClient messagingClient;

	EgensotningRevocationService(final ErrandRepository errandRepository, final EgensotningDetailsRepository detailsRepository,
		final ErrandService errandService, final MessagingClient messagingClient) {
		this.errandRepository = errandRepository;
		this.detailsRepository = detailsRepository;
		this.errandService = errandService;
		this.messagingClient = messagingClient;
	}

	public void revoke(final String municipalityId, final String namespace, final String errandId, final String reason) {
		final var errand = findDecidedEgensotning(municipalityId, namespace, errandId);

		final var patch = PatchErrand.create();
		patch.setStatus(STATUS_REVOKED);
		errandService.updateErrand(municipalityId, namespace, errandId, patch);

		detailsRepository.findByErrandId(errandId).ifPresent(details -> {
			details.setValidUntil(null);
			details.setRevokedAt(OffsetDateTime.now(ZoneId.systemDefault()));
			details.setRevocationReason(reason);
			detailsRepository.save(details);
			sendRevocationEmail(errand, details);
		});

		LOG.info("Revoked egensotning decision on errand {} (reason: {})", errandId, reason);
	}

	private ErrandEntity findDecidedEgensotning(final String municipalityId, final String namespace, final String errandId) {
		final var errand = errandRepository.findByIdAndNamespaceAndMunicipalityId(errandId, namespace, municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERRAND_NOT_FOUND_MESSAGE.formatted(errandId, namespace, municipalityId)));
		if (!EgensotningModuleConfig.TYPE_SLUG.equals(errand.getTypeSlug())) {
			throw Problem.valueOf(BAD_REQUEST, WRONG_TYPE_MESSAGE.formatted(errandId, errand.getTypeSlug(), EgensotningModuleConfig.TYPE_SLUG));
		}
		if (!STATUS_DECIDED.equals(errand.getStatus())) {
			throw Problem.valueOf(CONFLICT, NOT_DECIDED_MESSAGE.formatted(errandId, errand.getStatus()));
		}
		return errand;
	}

	private void sendRevocationEmail(final ErrandEntity errand, final EgensotningDetailsEntity details) {
		final var email = errand.getApplicantEmail();
		if (!StringUtils.hasText(email)) {
			LOG.warn("Errand {} has no applicant email — skipping revocation notice", errand.getId());
			return;
		}
		final var fastighet = Optional.ofNullable(details.getFastighetsbeteckning()).filter(StringUtils::hasText).orElse("din fastighet");
		try {
			messagingClient.sendEmail(errand.getMunicipalityId(), new EmailRequest()
				.emailAddress(email)
				.subject(EMAIL_SUBJECT)
				.message(EMAIL_MESSAGE_TEMPLATE.formatted(fastighet))
				.sender(EgensotningModuleConfig.citizenEmailSender()));
		} catch (final RuntimeException e) {
			// The decision is already revoked; a failed e-mail must not roll that back.
			LOG.error("Failed to send egensotning revocation notice for errand {}", errand.getId(), e);
		}
	}
}
