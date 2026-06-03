package se.sundsvall.rtjmanagement.types.egensotning.details.integration.templating;

import generated.se.sundsvall.templating.RenderRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.stakeholders.api.model.Stakeholder;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;

/**
 * Builds the {@link RenderRequest} for the {@code egensotning-mottagningsbevis} template. The static
 * letter scaffolding (brevhuvud, hänvisning till 9 § förvaltningslagen, brödtext) lives in the stored
 * template; this mapper only supplies the variable data — i första hand mottagaren (Motpart). Handläggarens
 * kontaktuppgifter i brevhuvudet kan inte slås upp från {@code assignedUserId} och hålls statiska i mallen.
 */
@Component
public class MottagningsbevisTemplatingMapper {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private final String templateIdentifier;

	MottagningsbevisTemplatingMapper(@Value("${integration.templating.mottagningsbevis-template-identifier}") final String templateIdentifier) {
		this.templateIdentifier = templateIdentifier;
	}

	public RenderRequest toRenderRequest(final ErrandEntity errand, final EgensotningDetailsEntity details, final Stakeholder applicant) {
		final Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put("applicantName", applicantName(applicant));
		parameters.put("applicantAddress", Optional.ofNullable(applicant).map(Stakeholder::getAddress).orElse(""));
		parameters.put("applicantCity", Optional.ofNullable(applicant).map(Stakeholder::getCity).orElse(""));
		parameters.put("personnummer", personnummer(details, applicant));
		parameters.put("fastighetsbeteckning", Optional.ofNullable(details).map(EgensotningDetailsEntity::getFastighetsbeteckning).orElse(""));
		parameters.put("propertyAddress", Optional.ofNullable(details).map(EgensotningDetailsEntity::getPropertyAddress).orElse(""));
		parameters.put("errandNumber", Optional.ofNullable(errand).map(ErrandEntity::getErrandNumber).orElse(""));
		parameters.put("handlaggare", Optional.ofNullable(errand).map(ErrandEntity::getAssignedUserId).orElse(""));
		parameters.put("date", LocalDate.now(ZoneId.systemDefault()).format(DATE_FORMATTER));

		return new RenderRequest()
			.identifier(templateIdentifier)
			.parameters(parameters);
	}

	private static String applicantName(final Stakeholder applicant) {
		return Optional.ofNullable(applicant)
			.map(stakeholder -> {
				final var fullName = (nullToEmpty(stakeholder.getFirstName()) + " " + nullToEmpty(stakeholder.getLastName())).trim();
				return StringUtils.hasText(fullName) ? fullName : nullToEmpty(stakeholder.getOrganizationName());
			})
			.orElse("");
	}

	private static String personnummer(final EgensotningDetailsEntity details, final Stakeholder applicant) {
		return Optional.ofNullable(details).map(EgensotningDetailsEntity::getPersonnummer).filter(StringUtils::hasText)
			.or(() -> Optional.ofNullable(applicant).map(Stakeholder::getExternalId))
			.orElse("");
	}

	private static String nullToEmpty(final String value) {
		return Optional.ofNullable(value).orElse("");
	}
}
