package se.sundsvall.rtjmanagement.types.brandfarligvara.application.service;

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
import se.sundsvall.rtjmanagement.types.brandfarligvara.application.api.model.ApplicantResponsiblePerson;
import se.sundsvall.rtjmanagement.types.brandfarligvara.application.api.model.BrandfarligVaraApplication;
import se.sundsvall.rtjmanagement.types.brandfarligvara.configuration.BrandfarligVaraModuleConfig;
import se.sundsvall.rtjmanagement.types.brandfarligvara.details.api.model.BrandfarligVaraDetails;
import se.sundsvall.rtjmanagement.types.brandfarligvara.details.service.BrandfarligVaraDetailsService;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.service.HazardousGoodsService;

import static java.util.Optional.ofNullable;
import static org.springframework.util.StringUtils.hasText;

/**
 * One-call submission of a complete brandfarlig-vara application. Creates the errand and all its
 * child data (details, brandfarliga varor, APPLICANT/CONTACT_PERSON/RESPONSIBLE_PERSON-stakeholders,
 * attachments) and starts the BPMN process <b>last</b>, all in one transaction. This avoids the race
 * where verification would run before the application data is persisted.
 *
 * <p>
 * Mirrors {@code EgensotningApplicationService}; the difference is that the applicant is a juridisk
 * person (company with organisationsnummer) rather than a private person, and there is no
 * auto-approval — the process always routes to manual handläggning.
 * </p>
 */
@Service
@Transactional
public class BrandfarligVaraApplicationService {

	private static final String PROCESS_DEFINITION_NAME = "Hantera ansökan om tillstånd för brandfarlig vara";
	private static final String DEFAULT_TITLE = "Ansökan om tillstånd för brandfarlig vara";
	private static final String CHANNEL_EMAIL = "Email";
	private static final String CHANNEL_PHONE = "Phone";
	private static final String EXTERNAL_ID_TYPE_ORGANIZATION = "ORGANIZATION";
	private static final String EXTERNAL_ID_TYPE_PERSON = "PERSON";
	private static final String ATTACHMENT_CATEGORY_OTHER = "OTHER";

	private final ErrandService errandService;
	private final BrandfarligVaraDetailsService detailsService;
	private final HazardousGoodsService hazardousGoodsService;
	private final StakeholderService stakeholderService;
	private final AttachmentService attachmentService;

	BrandfarligVaraApplicationService(final ErrandService errandService, final BrandfarligVaraDetailsService detailsService,
		final HazardousGoodsService hazardousGoodsService, final StakeholderService stakeholderService,
		final AttachmentService attachmentService) {
		this.errandService = errandService;
		this.detailsService = detailsService;
		this.hazardousGoodsService = hazardousGoodsService;
		this.stakeholderService = stakeholderService;
		this.attachmentService = attachmentService;
	}

	public String submit(final String municipalityId, final String namespace, final BrandfarligVaraApplication application, final List<MultipartFile> files) {
		// 1. Errand (utan processDefinitionName → processen startar INTE här)
		final var errandId = errandService.createErrand(municipalityId, namespace, toErrand(application));

		// 2. Typespecifika data (1:1)
		detailsService.upsert(municipalityId, namespace, errandId, toDetails(application));

		// 3. Brandfarliga varor (N)
		ofNullable(application.getProducts()).orElseGet(List::of)
			.forEach(product -> hazardousGoodsService.create(municipalityId, namespace, errandId, product));

		// 4. Stakeholders: APPLICANT (företag) + ev. CONTACT_PERSON + föreståndare (RESPONSIBLE_PERSON)
		stakeholderService.create(municipalityId, namespace, errandId, toApplicantStakeholder(application));
		ofNullable(toContactPersonStakeholder(application))
			.ifPresent(stakeholder -> stakeholderService.create(municipalityId, namespace, errandId, stakeholder));
		ofNullable(application.getResponsiblePersons()).orElseGet(List::of)
			.forEach(person -> stakeholderService.create(municipalityId, namespace, errandId, toResponsiblePersonStakeholder(person)));

		// 5. Bilagor
		ofNullable(files).orElseGet(List::of)
			.forEach(file -> attachmentService.createAttachment(municipalityId, namespace, errandId, file, ATTACHMENT_CATEGORY_OTHER));

		// 6. Starta processen sist — nu finns all data → verify kör på komplett ansökan
		errandService.startProcess(municipalityId, namespace, errandId, PROCESS_DEFINITION_NAME, null);

		return errandId;
	}

	private static Errand toErrand(final BrandfarligVaraApplication application) {
		return Errand.create()
			.withTypeSlug(BrandfarligVaraModuleConfig.TYPE_SLUG)
			.withTitle(hasText(application.getTitle()) ? application.getTitle() : DEFAULT_TITLE)
			.withStatus(BrandfarligVaraModuleConfig.STATUS_REGISTERED)
			.withDescription(application.getDescription())
			.withPriority(application.getPriority())
			.withReporterUserId(application.getReporterUserId())
			.withAssignedUserId(application.getAssignedUserId())
			.withApplicantEmail(application.getApplicantEmail());
	}

	private static BrandfarligVaraDetails toDetails(final BrandfarligVaraApplication application) {
		return BrandfarligVaraDetails.create()
			.withVerksamhetstyp(application.getVerksamhetstyp())
			.withProxy(application.isProxy())
			.withFastighetsbeteckning(application.getFastighetsbeteckning())
			.withHandlingLocationAddress(application.getHandlingLocationAddress())
			.withHandlingLocationZipCode(application.getHandlingLocationZipCode())
			.withHandlingLocationCity(application.getHandlingLocationCity());
	}

	private static Stakeholder toApplicantStakeholder(final BrandfarligVaraApplication application) {
		final var contactChannels = new ArrayList<ContactChannel>();
		addChannel(contactChannels, CHANNEL_EMAIL, application.getApplicantEmail());

		return Stakeholder.create()
			.withRole(BrandfarligVaraModuleConfig.ROLE_APPLICANT)
			.withExternalId(application.getOrganizationNumber())
			.withExternalIdType(EXTERNAL_ID_TYPE_ORGANIZATION)
			.withOrganizationName(application.getCompanyName())
			.withAddress(application.getCompanyAddress())
			.withZipCode(application.getCompanyZipCode())
			.withCity(application.getCompanyCity())
			.withContactChannels(contactChannels.isEmpty() ? null : contactChannels);
	}

	private static Stakeholder toContactPersonStakeholder(final BrandfarligVaraApplication application) {
		if (!hasText(application.getContactPersonName()) && !hasText(application.getContactPersonEmail()) && !hasText(application.getContactPersonPhone())) {
			return null;
		}
		final var contactChannels = new ArrayList<ContactChannel>();
		addChannel(contactChannels, CHANNEL_EMAIL, application.getContactPersonEmail());
		addChannel(contactChannels, CHANNEL_PHONE, application.getContactPersonPhone());

		return Stakeholder.create()
			.withRole(BrandfarligVaraModuleConfig.ROLE_CONTACT_PERSON)
			.withFirstName(application.getContactPersonName())
			.withContactChannels(contactChannels.isEmpty() ? null : contactChannels);
	}

	private static Stakeholder toResponsiblePersonStakeholder(final ApplicantResponsiblePerson person) {
		final var contactChannels = new ArrayList<ContactChannel>();
		addChannel(contactChannels, CHANNEL_EMAIL, person.getEmail());
		addChannel(contactChannels, CHANNEL_PHONE, person.getPhone());

		return Stakeholder.create()
			.withRole(BrandfarligVaraModuleConfig.ROLE_RESPONSIBLE_PERSON)
			.withExternalId(person.getPersonnummer())
			.withExternalIdType(EXTERNAL_ID_TYPE_PERSON)
			.withFirstName(person.getFirstName())
			.withLastName(person.getLastName())
			.withContactChannels(contactChannels.isEmpty() ? null : contactChannels);
	}

	private static void addChannel(final List<ContactChannel> channels, final String key, final String value) {
		ofNullable(value).filter(v -> !v.isBlank())
			.ifPresent(v -> channels.add(ContactChannel.create().withKey(key).withValue(v)));
	}
}
