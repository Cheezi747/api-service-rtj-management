package se.sundsvall.rtjmanagement.types.egensotning.application.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import se.sundsvall.rtjmanagement.attachments.service.AttachmentService;
import se.sundsvall.rtjmanagement.core.api.model.Errand;
import se.sundsvall.rtjmanagement.core.service.ErrandService;
import se.sundsvall.rtjmanagement.stakeholders.api.model.ContactChannel;
import se.sundsvall.rtjmanagement.stakeholders.api.model.Stakeholder;
import se.sundsvall.rtjmanagement.stakeholders.service.StakeholderService;
import se.sundsvall.rtjmanagement.types.egensotning.application.api.model.EgensotningApplication;
import se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig;
import se.sundsvall.rtjmanagement.types.egensotning.details.api.model.EgensotningDetails;
import se.sundsvall.rtjmanagement.types.egensotning.details.service.EgensotningDetailsService;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.service.SotningsobjektService;

import static java.util.Optional.ofNullable;
import static org.springframework.util.StringUtils.hasText;

/**
 * One-call submission of a complete egensotning application. Creates the errand and all its child
 * data (details, sotningsobjekt, APPLICANT-stakeholder, attachments) and starts the BPMN process
 * <b>last</b>, all in one transaction. This avoids the race where verification would run before the
 * application data is persisted — the whole application is in place before the process starts.
 */
@Service
@Transactional
public class EgensotningApplicationService {

	private static final String PROCESS_DEFINITION_NAME = "Hantera ansökan om egensotning";
	private static final String DEFAULT_TITLE = "Ansökan om egensotning";
	private static final String CHANNEL_EMAIL = "Email";
	private static final String CHANNEL_PHONE = "Phone";

	private final ErrandService errandService;
	private final EgensotningDetailsService detailsService;
	private final SotningsobjektService sotningsobjektService;
	private final StakeholderService stakeholderService;
	private final AttachmentService attachmentService;

	EgensotningApplicationService(final ErrandService errandService, final EgensotningDetailsService detailsService,
		final SotningsobjektService sotningsobjektService, final StakeholderService stakeholderService,
		final AttachmentService attachmentService) {
		this.errandService = errandService;
		this.detailsService = detailsService;
		this.sotningsobjektService = sotningsobjektService;
		this.stakeholderService = stakeholderService;
		this.attachmentService = attachmentService;
	}

	public String submit(final String municipalityId, final String namespace, final EgensotningApplication application,
		final MultipartFile brandskyddskontroll, final MultipartFile utbildningsintyg) {
		// 1. Errand (utan processDefinitionName → processen startar INTE här)
		final var errandId = errandService.createErrand(municipalityId, namespace, toErrand(application));

		// 2-5. Ansökningsdata
		detailsService.upsert(municipalityId, namespace, errandId, toDetails(application));
		ofNullable(application.getSotningsobjekt()).orElseGet(List::of)
			.forEach(objekt -> sotningsobjektService.create(municipalityId, namespace, errandId, objekt));
		stakeholderService.create(municipalityId, namespace, errandId, toApplicantStakeholder(application));
		attachmentService.createAttachment(municipalityId, namespace, errandId, brandskyddskontroll, EgensotningModuleConfig.CATEGORY_BRANDSKYDDSKONTROLL);
		attachmentService.createAttachment(municipalityId, namespace, errandId, utbildningsintyg, EgensotningModuleConfig.CATEGORY_UTBILDNINGSINTYG);

		// 6. Starta processen sist — nu finns all data → verify kör på komplett ansökan
		errandService.startProcess(municipalityId, namespace, errandId, PROCESS_DEFINITION_NAME, null);

		return errandId;
	}

	private static Errand toErrand(final EgensotningApplication application) {
		return Errand.create()
			.withTypeSlug(EgensotningModuleConfig.TYPE_SLUG)
			.withTitle(hasText(application.getTitle()) ? application.getTitle() : DEFAULT_TITLE)
			.withStatus(EgensotningModuleConfig.STATUS_REGISTERED)
			.withDescription(application.getDescription())
			.withPriority(application.getPriority())
			.withReporterUserId(application.getReporterUserId())
			.withAssignedUserId(application.getAssignedUserId())
			.withApplicantEmail(application.getApplicantEmail());
	}

	private static EgensotningDetails toDetails(final EgensotningApplication application) {
		return EgensotningDetails.create()
			.withPersonnummer(application.getPersonnummer())
			.withFastighetsbeteckning(application.getFastighetsbeteckning())
			.withPropertyAddress(application.getPropertyAddress())
			.withOwnsProperty(application.getOwnsProperty())
			.withOwnershipMotivation(application.getOwnershipMotivation())
			.withAppliesForOtherProperty(application.getAppliesForOtherProperty());
	}

	private static Stakeholder toApplicantStakeholder(final EgensotningApplication application) {
		final var contactChannels = new ArrayList<ContactChannel>();
		ofNullable(application.getApplicantEmail()).filter(value -> !value.isBlank())
			.ifPresent(email -> contactChannels.add(ContactChannel.create().withKey(CHANNEL_EMAIL).withValue(email)));
		ofNullable(application.getApplicantPhone()).filter(phone -> !phone.isBlank())
			.ifPresent(phone -> contactChannels.add(ContactChannel.create().withKey(CHANNEL_PHONE).withValue(phone)));

		return Stakeholder.create()
			.withRole(EgensotningModuleConfig.ROLE_APPLICANT)
			.withExternalId(application.getPersonnummer())
			.withExternalIdType("PERSON")
			.withFirstName(application.getApplicantFirstName())
			.withLastName(application.getApplicantLastName())
			.withAddress(application.getApplicantAddress())
			.withZipCode(application.getApplicantZipCode())
			.withCity(application.getApplicantCity())
			.withCountry(application.getApplicantCountry())
			.withContactChannels(contactChannels.isEmpty() ? null : contactChannels);
	}
}
