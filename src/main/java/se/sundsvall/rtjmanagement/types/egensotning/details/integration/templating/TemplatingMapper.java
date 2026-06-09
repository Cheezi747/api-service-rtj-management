package se.sundsvall.rtjmanagement.types.egensotning.details.integration.templating;

import generated.se.sundsvall.templating.RenderRequest;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.shared.DecisionRecorded;
import se.sundsvall.rtjmanagement.stakeholders.api.model.Stakeholder;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.model.SotningsobjektEntity;

/**
 * Builds the {@link RenderRequest} for the {@code egensotning-beslut} template from the recorded
 * decision plus the errand's egensotning data. The legal scaffolding (lagstöd, villkor,
 * överklagandehänvisning) lives in the stored template; this mapper only supplies the variable
 * data the template fills in.
 */
@Component
public class TemplatingMapper {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final String OUTCOME_APPROVED = "APPROVED";

	@Value("${integration.templating.template-identifier}")
	private String templateIdentifier;

	public RenderRequest toRenderRequest(
		final DecisionRecorded event,
		final ErrandEntity errand,
		final EgensotningDetailsEntity details,
		final List<SotningsobjektEntity> sotningsobjekt,
		final Stakeholder applicant,
		final String decisionText) {

		final Map<String, Object> parameters = new LinkedHashMap<>();
		parameters.put("approved", OUTCOME_APPROVED.equals(event.outcome()));
		parameters.put("outcome", nullToEmpty(event.outcome()));
		parameters.put("decisionId", nullToEmpty(event.decisionId()));
		parameters.put("decisionDate", Optional.ofNullable(event.timestamp()).map(DATE_FORMATTER::format).orElse(""));
		parameters.put("decidedBy", nullToEmpty(event.decidedBy()));
		parameters.put("decisionText", nullToEmpty(decisionText));
		parameters.put("errandNumber", Optional.ofNullable(errand).map(ErrandEntity::getErrandNumber).orElse(""));
		parameters.put("applicantEmail", Optional.ofNullable(errand).map(ErrandEntity::getApplicantEmail).orElse(""));
		parameters.put("applicantName", applicantName(applicant));
		parameters.put("personnummer", personnummer(details, applicant));
		parameters.put("fastighetsbeteckning", Optional.ofNullable(details).map(EgensotningDetailsEntity::getFastighetsbeteckning).orElse(""));
		parameters.put("propertyAddress", Optional.ofNullable(details).map(EgensotningDetailsEntity::getPropertyAddress).orElse(""));
		parameters.put("sotningsobjekt", toSotningsobjektParameters(sotningsobjekt));

		return new RenderRequest()
			.identifier(templateIdentifier)
			.parameters(parameters);
	}

	private static List<Map<String, Object>> toSotningsobjektParameters(final List<SotningsobjektEntity> sotningsobjekt) {
		return Optional.ofNullable(sotningsobjekt).orElseGet(List::of).stream()
			.map(TemplatingMapper::toSotningsobjektParameter)
			.toList();
	}

	private static Map<String, Object> toSotningsobjektParameter(final SotningsobjektEntity objekt) {
		final Map<String, Object> map = new LinkedHashMap<>();
		map.put("typ", nullToEmpty(objekt.getTyp()));
		map.put("fabrikat", nullToEmpty(objekt.getFabrikat()));
		map.put("tillverkningsar", Optional.ofNullable(objekt.getTillverkningsar()).map(String::valueOf).orElse(""));
		map.put("bransleslag", nullToEmpty(objekt.getBransleslag()));
		map.put("branslemangd", nullToEmpty(objekt.getBranslemangd()));
		map.put("sotningsintervallVeckor", Optional.ofNullable(objekt.getSotningsintervallVeckor()).map(String::valueOf).orElse(""));
		return map;
	}

	private static String applicantName(final Stakeholder applicant) {
		return Optional.ofNullable(applicant)
			.map(stakeholder -> {
				final var fullName = (nullToEmpty(stakeholder.getFirstName()) + " " + nullToEmpty(stakeholder.getLastName())).trim();
				if (StringUtils.hasText(fullName)) {
					return fullName;
				}
				return nullToEmpty(stakeholder.getOrganizationName());
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
