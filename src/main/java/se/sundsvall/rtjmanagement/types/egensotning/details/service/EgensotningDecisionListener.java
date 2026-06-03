package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import se.sundsvall.rtjmanagement.attachments.service.AttachmentService;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.shared.DecisionRecorded;
import se.sundsvall.rtjmanagement.stakeholders.service.StakeholderService;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.EgensotningDetailsRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.templating.TemplatingIntegration;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.templating.TemplatingMapper;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.SotningsobjektRepository;

import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.CATEGORY_DECISION;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.ROLE_APPLICANT;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.TYPE_SLUG;

/**
 * Renders the formal egensotning beslut as a PDF (via the templating service) and stores it as a
 * DECISION attachment on the errand whenever a decision is recorded.
 *
 * {@code @ApplicationModuleListener} runs asynchronously in a fresh transaction after the decision
 * commits, with the event durably staged in Spring Modulith's outbox in between — so a templating
 * outage can never fail the decision itself, and the PDF work survives a restart and is retried.
 * Failures are caught and logged rather than rethrown.
 */
@Component
class EgensotningDecisionListener {

	private static final Logger LOG = LoggerFactory.getLogger(EgensotningDecisionListener.class);
	private static final String OUTCOME_APPROVED = "APPROVED";

	private final ErrandRepository errandRepository;
	private final EgensotningDetailsRepository egensotningDetailsRepository;
	private final SotningsobjektRepository sotningsobjektRepository;
	private final StakeholderService stakeholderService;
	private final TemplatingMapper templatingMapper;
	private final TemplatingIntegration templatingIntegration;
	private final AttachmentService attachmentService;

	EgensotningDecisionListener(final ErrandRepository errandRepository, final EgensotningDetailsRepository egensotningDetailsRepository,
		final SotningsobjektRepository sotningsobjektRepository, final StakeholderService stakeholderService,
		final TemplatingMapper templatingMapper, final TemplatingIntegration templatingIntegration, final AttachmentService attachmentService) {
		this.errandRepository = errandRepository;
		this.egensotningDetailsRepository = egensotningDetailsRepository;
		this.sotningsobjektRepository = sotningsobjektRepository;
		this.stakeholderService = stakeholderService;
		this.templatingMapper = templatingMapper;
		this.templatingIntegration = templatingIntegration;
		this.attachmentService = attachmentService;
	}

	@ApplicationModuleListener
	void on(final DecisionRecorded event) {
		if (!TYPE_SLUG.equals(event.typeSlug())) {
			return;
		}
		try {
			final var errand = errandRepository.findById(event.errandId()).orElse(null);
			if (errand == null) {
				LOG.warn("Egensotning decision {} references missing errand {} — no decision PDF stored", event.decisionId(), event.errandId());
				return;
			}

			final var details = egensotningDetailsRepository.findByErrandId(event.errandId()).orElse(null);
			// Tidsbegränsa godkända beslut (sex år) innan ev. tidig retur på befintlig PDF — giltigheten
			// ska sättas oavsett vem som producerat beslutsdokumentet.
			setValidityIfApproved(event, details);

			// Idempotens: skapa inte ett nytt beslutsdokument om ärendet redan har ett (t.ex. en
			// handläggare har under manuell hantering laddat upp eller justerat ett eget). Den
			// befintliga PDF:en behålls och används för utskicket.
			if (attachmentService.hasAttachmentOfCategory(event.errandId(), CATEGORY_DECISION)) {
				LOG.info("Errand {} already has a decision PDF — keeping the existing one, skipping generation", event.errandId());
				return;
			}

			final var sotningsobjekt = sotningsobjektRepository.findByErrandIdOrderByTypAscFabrikatAsc(event.errandId());
			final var applicant = stakeholderService.readAll(errand.getMunicipalityId(), errand.getNamespace(), event.errandId()).stream()
				.filter(stakeholder -> ROLE_APPLICANT.equals(stakeholder.getRole()))
				.findFirst()
				.orElse(null);

			final var request = templatingMapper.toRenderRequest(event, errand, details, sotningsobjekt, applicant);
			final var pdf = templatingIntegration.renderPdf(errand.getMunicipalityId(), request);
			final var fileName = "beslut-egensotning-%s.pdf".formatted(
				Optional.ofNullable(errand.getErrandNumber()).filter(StringUtils::hasText).orElse(event.decisionId()));

			attachmentService.createAttachment(errand.getMunicipalityId(), errand.getNamespace(), event.errandId(), pdf, fileName, APPLICATION_PDF_VALUE, CATEGORY_DECISION);
			LOG.info("Stored egensotning decision PDF '{}' on errand {}", fileName, event.errandId());
		} catch (final RuntimeException e) {
			LOG.error("Failed to render/store egensotning decision PDF for errand {} (decision {})", event.errandId(), event.decisionId(), e);
		}
	}

	/**
	 * Time-limits an approved egensotning decision to six years (from beslutsdatum, framflyttat till
	 * nästa fasta datum). Runs only for APPROVAL decisions and at most once per details row (idempotent
	 * on {@code validUntil}); REJECTION-beslut får ingen giltighetstid.
	 */
	private void setValidityIfApproved(final DecisionRecorded event, final EgensotningDetailsEntity details) {
		if (details == null || !OUTCOME_APPROVED.equals(event.outcome()) || details.getValidUntil() != null) {
			return;
		}
		final var validFrom = LocalDate.now(ZoneId.systemDefault());
		details.setValidFrom(validFrom);
		details.setValidUntil(EgensotningValidityCalculator.computeValidUntil(validFrom));
		details.setReminderSentAt(null);
		egensotningDetailsRepository.save(details);
		LOG.info("Egensotning decision {} on errand {} time-limited: valid {} – {}", event.decisionId(), event.errandId(), validFrom, details.getValidUntil());
	}
}
