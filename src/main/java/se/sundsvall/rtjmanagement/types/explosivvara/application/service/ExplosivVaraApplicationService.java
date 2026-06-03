package se.sundsvall.rtjmanagement.types.explosivvara.application.service;

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
import se.sundsvall.rtjmanagement.types.explosivvara.application.api.model.ExplosivApplicantPerson;
import se.sundsvall.rtjmanagement.types.explosivvara.application.api.model.ExplosivVaraApplication;
import se.sundsvall.rtjmanagement.types.explosivvara.configuration.ExplosivVaraModuleConfig;
import se.sundsvall.rtjmanagement.types.explosivvara.details.api.model.ExplosivVaraDetails;
import se.sundsvall.rtjmanagement.types.explosivvara.details.service.ExplosivVaraDetailsService;
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.service.ExplosivGoodsService;

import static java.util.Optional.ofNullable;
import static org.springframework.util.StringUtils.hasText;

/**
 * One-call submission of a complete explosiv-vara application. Creates the errand and all its
 * child data (details, explosiva varor, APPLICANT/CONTACT_PERSON-stakeholders, one stakeholder per
 * person in {@code persons} carrying that person's role, attachments) and starts the BPMN process
 * <b>last</b>, all in one transaction. This avoids the race where verification would run before the
 * application data is persisted.
 *
 * <p>
 * Mirrors {@code BrandfarligVaraApplicationService}; the difference is that explosiv vara carries
 * godkända personer (föreståndare/deltagare/person med betydande inflytande) — each person maps to a
 * stakeholder whose role comes from the person itself.
 * </p>
 */
@Service
@Transactional
public class ExplosivVaraApplicationService {

	private static final String PROCESS_DEFINITION_NAME = "Hantera ansökan om tillstånd för explosiv vara";
	private static final String DEFAULT_TITLE = "Ansökan om tillstånd för explosiv vara";
	private static final String CHANNEL_EMAIL = "Email";
	private static final String CHANNEL_PHONE = "Phone";
	private static final String EXTERNAL_ID_TYPE_ORGANIZATION = "ORGANIZATION";
	private static final String EXTERNAL_ID_TYPE_PERSON = "PERSON";
	private static final String ATTACHMENT_CATEGORY_OTHER = "OTHER";

	private final ErrandService errandService;
	private final ExplosivVaraDetailsService detailsService;
	private final ExplosivGoodsService explosivGoodsService;
	private final StakeholderService stakeholderService;
	private final AttachmentService attachmentService;

	ExplosivVaraApplicationService(final ErrandService errandService, final ExplosivVaraDetailsService detailsService,
		final ExplosivGoodsService explosivGoodsService, final StakeholderService stakeholderService,
		final AttachmentService attachmentService) {
		this.errandService = errandService;
		this.detailsService = detailsService;
		this.explosivGoodsService = explosivGoodsService;
		this.stakeholderService = stakeholderService;
		this.attachmentService = attachmentService;
	}

	public String submit(final String municipalityId, final String namespace, final ExplosivVaraApplication application, final List<MultipartFile> files) {
		// 1. Errand (utan processDefinitionName → processen startar INTE här)
		final var errandId = errandService.createErrand(municipalityId, namespace, toErrand(application));

		// 2. Typespecifika data (1:1)
		detailsService.upsert(municipalityId, namespace, errandId, toDetails(application));

		// 3. Explosiva varor (N)
		ofNullable(application.getProducts()).orElseGet(List::of)
			.forEach(product -> explosivGoodsService.create(municipalityId, namespace, errandId, product));

		// 4. Stakeholders: APPLICANT (företag) + ev. CONTACT_PERSON + en stakeholder per person (med personens roll)
		stakeholderService.create(municipalityId, namespace, errandId, toApplicantStakeholder(application));
		ofNullable(toContactPersonStakeholder(application))
			.ifPresent(stakeholder -> stakeholderService.create(municipalityId, namespace, errandId, stakeholder));
		ofNullable(application.getPersons()).orElseGet(List::of)
			.forEach(person -> stakeholderService.create(municipalityId, namespace, errandId, toPersonStakeholder(person)));

		// 5. Bilagor
		ofNullable(files).orElseGet(List::of)
			.forEach(file -> attachmentService.createAttachment(municipalityId, namespace, errandId, file, ATTACHMENT_CATEGORY_OTHER));

		// 6. Starta processen sist — nu finns all data → verify kör på komplett ansökan
		errandService.startProcess(municipalityId, namespace, errandId, PROCESS_DEFINITION_NAME, null);

		return errandId;
	}

	private static Errand toErrand(final ExplosivVaraApplication application) {
		return Errand.create()
			.withTypeSlug(ExplosivVaraModuleConfig.TYPE_SLUG)
			.withTitle(hasText(application.getTitle()) ? application.getTitle() : DEFAULT_TITLE)
			.withStatus(ExplosivVaraModuleConfig.STATUS_REGISTERED)
			.withDescription(application.getDescription())
			.withPriority(application.getPriority())
			.withReporterUserId(application.getReporterUserId())
			.withAssignedUserId(application.getAssignedUserId())
			.withApplicantEmail(application.getApplicantEmail());
	}

	private static ExplosivVaraDetails toDetails(final ExplosivVaraApplication application) {
		return ExplosivVaraDetails.create()
			.withTypAvHantering(application.getTypAvHantering())
			.withProxy(application.isProxy())
			.withFastighetsbeteckning(application.getFastighetsbeteckning())
			.withHandlingLocationAddress(application.getHandlingLocationAddress())
			.withHandlingLocationZipCode(application.getHandlingLocationZipCode())
			.withHandlingLocationCity(application.getHandlingLocationCity());
	}

	private static Stakeholder toApplicantStakeholder(final ExplosivVaraApplication application) {
		final var contactChannels = new ArrayList<ContactChannel>();
		addChannel(contactChannels, CHANNEL_EMAIL, application.getApplicantEmail());

		return Stakeholder.create()
			.withRole(ExplosivVaraModuleConfig.ROLE_APPLICANT)
			.withExternalId(application.getOrganizationNumber())
			.withExternalIdType(EXTERNAL_ID_TYPE_ORGANIZATION)
			.withOrganizationName(application.getCompanyName())
			.withAddress(application.getCompanyAddress())
			.withZipCode(application.getCompanyZipCode())
			.withCity(application.getCompanyCity())
			.withContactChannels(contactChannels.isEmpty() ? null : contactChannels);
	}

	private static Stakeholder toContactPersonStakeholder(final ExplosivVaraApplication application) {
		if (!hasText(application.getContactPersonName()) && !hasText(application.getContactPersonEmail()) && !hasText(application.getContactPersonPhone())) {
			return null;
		}
		final var contactChannels = new ArrayList<ContactChannel>();
		addChannel(contactChannels, CHANNEL_EMAIL, application.getContactPersonEmail());
		addChannel(contactChannels, CHANNEL_PHONE, application.getContactPersonPhone());

		return Stakeholder.create()
			.withRole(ExplosivVaraModuleConfig.ROLE_CONTACT_PERSON)
			.withFirstName(application.getContactPersonName())
			.withContactChannels(contactChannels.isEmpty() ? null : contactChannels);
	}

	private static Stakeholder toPersonStakeholder(final ExplosivApplicantPerson person) {
		final var contactChannels = new ArrayList<ContactChannel>();
		addChannel(contactChannels, CHANNEL_EMAIL, person.getEmail());
		addChannel(contactChannels, CHANNEL_PHONE, person.getPhone());

		return Stakeholder.create()
			.withRole(person.getRole())
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
