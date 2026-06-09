package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import generated.se.sundsvall.messaging.EmailAttachment;
import generated.se.sundsvall.messaging.EmailRequest;
import java.util.Base64;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.service.event.ErrandAssigned;
import se.sundsvall.rtjmanagement.stakeholders.service.StakeholderService;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.EgensotningDetailsRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.messaging.MessagingClient;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.templating.MottagningsbevisTemplatingMapper;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.templating.TemplatingIntegration;

import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.util.StringUtils.hasText;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.ROLE_APPLICANT;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_UNDER_MANUAL_REVIEW;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.TYPE_SLUG;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.citizenEmailSender;

/**
 * Skickar ett mottagningsbevis till den sökande när en handläggare (BSK) först tilldelas ett
 * egensotningsärende som ligger under manuell granskning — dvs. när handläggningen påbörjas. Beviset
 * renderas som PDF via templating-tjänsten (mall {@code egensotning-mottagningsbevis}) och bifogas i
 * mejlet via messaging-plattformen.
 *
 * {@code @ApplicationModuleListener} körs asynkront i en egen transaktion efter att tilldelningen
 * committats, med eventet durabelt mellanlagrat i Spring Moduliths outbox — så ett templating-/messaging-
 * avbrott kan aldrig fälla själva tilldelningen, och arbetet återförsöks efter en omstart. Fel fångas och
 * loggas i stället för att kastas vidare.
 */
@Component
class EgensotningMottagningsbevisListener {

	private static final Logger LOG = LoggerFactory.getLogger(EgensotningMottagningsbevisListener.class);

	private static final String EMAIL_SUBJECT = "Mottagningsbevis – ansökan om egensotning";
	private static final String EMAIL_MESSAGE = """
		Hej! Din ansökan om egensotning har tagits emot och handläggningen har påbörjats. Bifogat finns \
		ditt mottagningsbevis. Du kan följa ditt ärende på Mina sidor. Hälsningar, Räddningstjänsten Medelpad.""";
	private static final String ATTACHMENT_NAME = "Mottagningsbevis egensotning.pdf";

	private final ErrandRepository errandRepository;
	private final EgensotningDetailsRepository egensotningDetailsRepository;
	private final StakeholderService stakeholderService;
	private final MottagningsbevisTemplatingMapper mottagningsbevisTemplatingMapper;
	private final TemplatingIntegration templatingIntegration;
	private final MessagingClient messagingClient;

	EgensotningMottagningsbevisListener(final ErrandRepository errandRepository, final EgensotningDetailsRepository egensotningDetailsRepository,
		final StakeholderService stakeholderService, final MottagningsbevisTemplatingMapper mottagningsbevisTemplatingMapper,
		final TemplatingIntegration templatingIntegration, final MessagingClient messagingClient) {
		this.errandRepository = errandRepository;
		this.egensotningDetailsRepository = egensotningDetailsRepository;
		this.stakeholderService = stakeholderService;
		this.mottagningsbevisTemplatingMapper = mottagningsbevisTemplatingMapper;
		this.templatingIntegration = templatingIntegration;
		this.messagingClient = messagingClient;
	}

	@ApplicationModuleListener
	void on(final ErrandAssigned event) {
		// Endast egensotning, och endast vid FÖRSTA tilldelningen (tomt previousAssignee) — en
		// omtilldelning ska inte skicka ett nytt mottagningsbevis.
		if (!TYPE_SLUG.equals(event.typeSlug()) || hasText(event.previousAssignee())) {
			return;
		}
		try {
			final var errand = errandRepository.findById(event.errandId()).orElse(null);
			// Beviset skickas bara när ärendet faktiskt fallit ut för manuell hantering.
			if (errand == null || !STATUS_UNDER_MANUAL_REVIEW.equals(errand.getStatus())) {
				return;
			}
			final var email = errand.getApplicantEmail();
			if (!hasText(email)) {
				LOG.warn("Egensotningsärende {} saknar applicant email — hoppar över mottagningsbevis", event.errandId());
				return;
			}

			final var details = egensotningDetailsRepository.findByErrandId(event.errandId()).orElse(null);
			final var applicant = stakeholderService.readAll(errand.getMunicipalityId(), errand.getNamespace(), event.errandId()).stream()
				.filter(stakeholder -> ROLE_APPLICANT.equals(stakeholder.getRole()))
				.findFirst()
				.orElse(null);

			final var pdf = templatingIntegration.renderPdf(errand.getMunicipalityId(),
				mottagningsbevisTemplatingMapper.toRenderRequest(errand, details, applicant));

			final var attachment = new EmailAttachment()
				.name(ATTACHMENT_NAME)
				.contentType(APPLICATION_PDF_VALUE)
				.content(Base64.getEncoder().encodeToString(pdf));

			messagingClient.sendEmail(errand.getMunicipalityId(), new EmailRequest()
				.emailAddress(email)
				.subject(EMAIL_SUBJECT)
				.message(EMAIL_MESSAGE)
				.attachments(List.of(attachment))
				.sender(citizenEmailSender()));

			LOG.info("Skickade mottagningsbevis för egensotningsärende {} (tilldelat {})", event.errandId(), event.newAssignee());
		} catch (final RuntimeException e) {
			LOG.error("Misslyckades skicka mottagningsbevis för egensotningsärende {}", event.errandId(), e);
		}
	}
}
