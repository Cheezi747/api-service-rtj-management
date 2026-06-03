package se.sundsvall.rtjmanagement.types.egensotning.details.integration.templating;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.shared.DecisionRecorded;
import se.sundsvall.rtjmanagement.stakeholders.api.model.Stakeholder;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.model.SotningsobjektEntity;

import static org.assertj.core.api.Assertions.assertThat;

class TemplatingMapperTest {

	private final TemplatingMapper mapper = new TemplatingMapper();

	TemplatingMapperTest() {
		ReflectionTestUtils.setField(mapper, "templateIdentifier", "egensotning-beslut");
	}

	@Test
	void toRenderRequestMapsApprovalFields() {
		final var timestamp = OffsetDateTime.parse("2026-06-03T10:15:30+02:00");
		final var event = new DecisionRecorded("dec-1", "err-1", "EGENSOTNING", "APPROVED", "Ansökan godkänd.", "operaton", timestamp);
		final var errand = ErrandEntity.create().withErrandNumber("RTJ-2026-001").withApplicantEmail("sokande@example.com");
		final var details = EgensotningDetailsEntity.create().withPersonnummer("199001011234")
			.withFastighetsbeteckning("Sundsvall Stenstaden 1:23").withPropertyAddress("Storgatan 5");
		final var objekt = SotningsobjektEntity.create().withTyp("Panna").withFabrikat("CTC").withTillverkningsar(2018)
			.withBransleslag("Ved").withBranslemangd("3 m3").withSotningsintervallVeckor(52);
		final var applicant = Stakeholder.create().withFirstName("Test").withLastName("Testsson").withExternalId("199001011234");

		final var request = mapper.toRenderRequest(event, errand, details, List.of(objekt), applicant);

		assertThat(request.getIdentifier()).isEqualTo("egensotning-beslut");
		final var parameters = request.getParameters();
		assertThat(parameters)
			.containsEntry("approved", true)
			.containsEntry("outcome", "APPROVED")
			.containsEntry("decisionId", "dec-1")
			.containsEntry("decisionDate", "2026-06-03")
			.containsEntry("decidedBy", "operaton")
			.containsEntry("decisionText", "Ansökan godkänd.")
			.containsEntry("errandNumber", "RTJ-2026-001")
			.containsEntry("applicantEmail", "sokande@example.com")
			.containsEntry("applicantName", "Test Testsson")
			.containsEntry("personnummer", "199001011234")
			.containsEntry("fastighetsbeteckning", "Sundsvall Stenstaden 1:23")
			.containsEntry("propertyAddress", "Storgatan 5");

		@SuppressWarnings("unchecked")
		final var objektList = (List<Map<String, Object>>) parameters.get("sotningsobjekt");
		assertThat(objektList).singleElement().satisfies(row -> assertThat(row)
			.containsEntry("typ", "Panna")
			.containsEntry("fabrikat", "CTC")
			.containsEntry("tillverkningsar", "2018")
			.containsEntry("bransleslag", "Ved")
			.containsEntry("branslemangd", "3 m3")
			.containsEntry("sotningsintervallVeckor", "52"));
	}

	@Test
	void toRenderRequestRejectionWithoutApplicantOrObjekt() {
		final var event = new DecisionRecorded("dec-2", "err-2", "EGENSOTNING", "REJECTED", null, "bsk01", null);

		final var request = mapper.toRenderRequest(event, ErrandEntity.create(), null, List.of(), null);

		final var parameters = request.getParameters();
		assertThat(parameters)
			.containsEntry("approved", false)
			.containsEntry("outcome", "REJECTED")
			.containsEntry("decisionDate", "")
			.containsEntry("decisionText", "")
			.containsEntry("applicantName", "")
			.containsEntry("personnummer", "")
			.containsEntry("fastighetsbeteckning", "")
			.containsEntry("errandNumber", "");
		assertThat((List<?>) parameters.get("sotningsobjekt")).isEmpty();
	}

	@Test
	void toRenderRequestFallsBackToOrganizationNameAndExternalId() {
		final var event = new DecisionRecorded("dec-3", "err-3", "EGENSOTNING", "APPROVED", "x", "operaton", null);
		final var applicant = Stakeholder.create().withOrganizationName("Acme AB").withExternalId("5560001234");

		final var request = mapper.toRenderRequest(event, ErrandEntity.create(), null, null, applicant);

		assertThat(request.getParameters())
			.containsEntry("applicantName", "Acme AB")
			.containsEntry("personnummer", "5560001234");
	}
}
