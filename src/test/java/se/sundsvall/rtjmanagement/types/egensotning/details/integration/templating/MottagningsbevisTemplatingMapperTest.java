package se.sundsvall.rtjmanagement.types.egensotning.details.integration.templating;

import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.stakeholders.api.model.Stakeholder;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;

import static org.assertj.core.api.Assertions.assertThat;

class MottagningsbevisTemplatingMapperTest {

	private final MottagningsbevisTemplatingMapper mapper = new MottagningsbevisTemplatingMapper("egensotning-mottagningsbevis");

	@Test
	void toRenderRequestMapsApplicantAndErrandFields() {
		final var errand = ErrandEntity.create().withErrandNumber("RTJ-2026-001").withAssignedUserId("bsk01");
		final var details = EgensotningDetailsEntity.create().withPersonnummer("199001011234")
			.withFastighetsbeteckning("Sundsvall Stenstaden 1:23").withPropertyAddress("Storgatan 5");
		final var applicant = Stakeholder.create().withFirstName("Test").withLastName("Testsson")
			.withAddress("Storgatan 5").withCity("Sundsvall").withExternalId("199001011234");

		final var request = mapper.toRenderRequest(errand, details, applicant);

		assertThat(request.getIdentifier()).isEqualTo("egensotning-mottagningsbevis");
		assertThat(request.getParameters())
			.containsEntry("applicantName", "Test Testsson")
			.containsEntry("applicantAddress", "Storgatan 5")
			.containsEntry("applicantCity", "Sundsvall")
			.containsEntry("personnummer", "199001011234")
			.containsEntry("fastighetsbeteckning", "Sundsvall Stenstaden 1:23")
			.containsEntry("propertyAddress", "Storgatan 5")
			.containsEntry("errandNumber", "RTJ-2026-001")
			.containsEntry("handlaggare", "bsk01")
			.containsKey("date");
	}

	@Test
	void toRenderRequestIsNullSafeForMissingDetailsAndApplicant() {
		final var request = mapper.toRenderRequest(ErrandEntity.create(), null, null);

		assertThat(request.getIdentifier()).isEqualTo("egensotning-mottagningsbevis");
		assertThat(request.getParameters())
			.containsEntry("applicantName", "")
			.containsEntry("applicantAddress", "")
			.containsEntry("applicantCity", "")
			.containsEntry("personnummer", "")
			.containsEntry("fastighetsbeteckning", "")
			.containsEntry("propertyAddress", "")
			.containsEntry("errandNumber", "")
			.containsEntry("handlaggare", "");
	}

	@Test
	void toRenderRequestFallsBackToOrganizationNameAndExternalId() {
		final var applicant = Stakeholder.create().withOrganizationName("Acme AB").withExternalId("5560001234");

		final var request = mapper.toRenderRequest(ErrandEntity.create(), null, applicant);

		assertThat(request.getParameters())
			.containsEntry("applicantName", "Acme AB")
			.containsEntry("personnummer", "5560001234");
	}
}
