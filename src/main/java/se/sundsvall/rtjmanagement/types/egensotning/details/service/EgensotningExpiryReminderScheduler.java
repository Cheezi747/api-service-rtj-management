package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import generated.se.sundsvall.messaging.EmailRequest;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.EgensotningDetailsRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.messaging.MessagingClient;

/**
 * Daily job that emails property owners ahead of their egensotning decision's six-year expiry so they
 * can re-apply (förnya). Scans egensotning details whose {@code validUntil} falls within the configured
 * lead window and that have not yet been reminded, sends a short avisering via the messaging platform,
 * and stamps {@code reminderSentAt} so each decision is reminded at most once. Failures are handled
 * per-row so one bad recipient does not abort the batch.
 */
@Component
class EgensotningExpiryReminderScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(EgensotningExpiryReminderScheduler.class);

	private static final String EMAIL_SUBJECT = "Påminnelse: ditt beslut om egensotning behöver förnyas";
	private static final String EMAIL_MESSAGE_TEMPLATE = """
		Hej! Ditt beslut om egensotning för %s upphör att gälla %s. För att fortsätta sköta sotningen \
		själv behöver du ansöka om förnyelse i god tid innan dess. Logga in på Mina sidor för att skicka \
		in en ny ansökan. Hälsningar, Räddningstjänsten Medelpad.""";

	private final EgensotningDetailsRepository egensotningDetailsRepository;
	private final ErrandRepository errandRepository;
	private final MessagingClient messagingClient;
	private final long leadTimeDays;

	EgensotningExpiryReminderScheduler(final EgensotningDetailsRepository egensotningDetailsRepository, final ErrandRepository errandRepository,
		final MessagingClient messagingClient, @Value("${scheduler.egensotning-expiry-reminder.lead-time-days}") final long leadTimeDays) {
		this.egensotningDetailsRepository = egensotningDetailsRepository;
		this.errandRepository = errandRepository;
		this.messagingClient = messagingClient;
		this.leadTimeDays = leadTimeDays;
	}

	@Dept44Scheduled(cron = "${scheduler.egensotning-expiry-reminder.cron}",
		name = "${scheduler.egensotning-expiry-reminder.name}",
		lockAtMostFor = "${scheduler.egensotning-expiry-reminder.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${scheduler.egensotning-expiry-reminder.maximum-execution-time}")
	void sendExpiryReminders() {
		final var today = LocalDate.now(ZoneId.systemDefault());
		final var windowEnd = today.plusDays(leadTimeDays);
		final var dueForReminder = egensotningDetailsRepository.findByValidUntilBetweenAndReminderSentAtIsNull(today, windowEnd);
		LOG.info("Found {} egensotning decision(s) expiring on or before {} to remind", dueForReminder.size(), windowEnd);

		var sent = 0;
		for (final var details : dueForReminder) {
			if (remind(details)) {
				sent++;
			}
		}
		LOG.info("Sent {} egensotning expiry reminder(s)", sent);
	}

	private boolean remind(final EgensotningDetailsEntity details) {
		try {
			final var errand = errandRepository.findById(details.getErrandId()).orElse(null);
			if (errand == null) {
				LOG.warn("Egensotning details {} references missing errand {} — skipping reminder", details.getId(), details.getErrandId());
				return false;
			}
			final var email = errand.getApplicantEmail();
			if (!StringUtils.hasText(email)) {
				LOG.warn("Errand {} has no applicant email — skipping egensotning expiry reminder", details.getErrandId());
				return false;
			}
			messagingClient.sendEmail(errand.getMunicipalityId(), buildEmail(email, details));
			details.setReminderSentAt(OffsetDateTime.now(ZoneId.systemDefault()));
			egensotningDetailsRepository.save(details);
			LOG.info("Sent egensotning expiry reminder for errand {} (expires {})", details.getErrandId(), details.getValidUntil());
			return true;
		} catch (final RuntimeException e) {
			LOG.error("Failed to send egensotning expiry reminder for errand {}", details.getErrandId(), e);
			return false;
		}
	}

	private EmailRequest buildEmail(final String email, final EgensotningDetailsEntity details) {
		final var fastighet = Optional.ofNullable(details.getFastighetsbeteckning()).filter(StringUtils::hasText).orElse("din fastighet");
		return new EmailRequest()
			.emailAddress(email)
			.subject(EMAIL_SUBJECT)
			.message(EMAIL_MESSAGE_TEMPLATE.formatted(fastighet, details.getValidUntil()));
	}
}
