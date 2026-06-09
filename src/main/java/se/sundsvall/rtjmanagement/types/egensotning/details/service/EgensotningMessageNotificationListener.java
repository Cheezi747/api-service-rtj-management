package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import generated.se.sundsvall.messaging.EmailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.shared.MessagePosted;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.messaging.MessagingClient;

import static org.springframework.util.StringUtils.hasText;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.TYPE_SLUG;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.citizenEmailSender;

/**
 * Skickar en <b>innehållslös</b> e-postnotis till den sökande när en handläggare postar ett OUTBOUND-
 * meddelande i ett egensotningsärende. Själva meddelandet lever bara i in-app-tråden (R1,
 * {@code conversation}-modulen) — mejlet talar enbart om ATT ett nytt meddelande finns och hänvisar
 * till Mina sidor, aldrig om innehållet. Detta gäller även kräver-komplettering: handläggarens
 * fritext postas som ett meddelande och aviseras härifrån, i stället för att mejlas av BPMN-flödet.
 *
 * INBOUND (sökande → handläggare) ignoreras — handläggaren ser nya svar i admin-vyns notiser, inte
 * via mejl. {@code @ApplicationModuleListener} körs asynkront i egen transaktion efter commit, med
 * eventet durabelt mellanlagrat i Spring Moduliths outbox — ett messaging-avbrott kan aldrig fälla
 * själva meddelandeposten, och notisen återförsöks efter en omstart. Fel fångas och loggas.
 */
@Component
class EgensotningMessageNotificationListener {

	private static final Logger LOG = LoggerFactory.getLogger(EgensotningMessageNotificationListener.class);

	private static final String DIRECTION_OUTBOUND = "OUTBOUND";
	private static final String EMAIL_SUBJECT = "Du har ett nytt meddelande om din ansökan om egensotning";
	private static final String EMAIL_MESSAGE = """
		Hej! Du har fått ett nytt meddelande från Räddningstjänsten Medelpad i ditt ärende om egensotning. \
		Logga in på Mina sidor för att läsa och svara. Hälsningar, Räddningstjänsten Medelpad.""";

	private final ErrandRepository errandRepository;
	private final MessagingClient messagingClient;

	EgensotningMessageNotificationListener(final ErrandRepository errandRepository, final MessagingClient messagingClient) {
		this.errandRepository = errandRepository;
		this.messagingClient = messagingClient;
	}

	@ApplicationModuleListener
	void on(final MessagePosted event) {
		// Endast handläggare → sökande; sökandens egna svar mejlar vi inte tillbaka.
		if (!DIRECTION_OUTBOUND.equals(event.direction())) {
			return;
		}
		try {
			final var errand = errandRepository.findById(event.errandId()).orElse(null);
			// Bara egensotningsärenden — andra ärendetyper avgör själva om/hur de aviserar.
			if (errand == null || !TYPE_SLUG.equals(errand.getTypeSlug())) {
				return;
			}
			final var email = errand.getApplicantEmail();
			if (!hasText(email)) {
				LOG.warn("Egensotningsärende {} saknar applicant email — hoppar över meddelandenotis", event.errandId());
				return;
			}

			messagingClient.sendEmail(errand.getMunicipalityId(), new EmailRequest()
				.emailAddress(email)
				.subject(EMAIL_SUBJECT)
				.message(EMAIL_MESSAGE)
				.sender(citizenEmailSender()));

			LOG.info("Skickade innehållslös meddelandenotis för egensotningsärende {} (meddelande {})", event.errandId(), event.messageId());
		} catch (final RuntimeException e) {
			LOG.error("Misslyckades skicka meddelandenotis för egensotningsärende {}", event.errandId(), e);
		}
	}
}
