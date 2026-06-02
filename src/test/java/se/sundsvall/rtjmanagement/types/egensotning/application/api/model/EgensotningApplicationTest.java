package se.sundsvall.rtjmanagement.types.egensotning.application.api.model;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.api.model.Sotningsobjekt;

import static org.assertj.core.api.Assertions.assertThat;

class EgensotningApplicationTest {

	@Test
	void gettersAndSetters() {
		final var objekt = List.of(Sotningsobjekt.create().withTyp("Värmepanna"));
		final var application = EgensotningApplication.create();

		application.setTitle("Ansökan");
		application.setDescription("Beskrivning");
		application.setPriority("MEDIUM");
		application.setReporterUserId("reporter");
		application.setAssignedUserId("assignee");
		application.setApplicantEmail("anna@example.se");
		application.setPersonnummer("198507231234");
		application.setFastighetsbeteckning("Sundsvall Stenstaden 1:23");
		application.setPropertyAddress("Storgatan 5");
		application.setApplicantFirstName("Anna");
		application.setApplicantLastName("Karlsson");
		application.setApplicantAddress("Storgatan 5");
		application.setApplicantZipCode("85230");
		application.setApplicantCity("Sundsvall");
		application.setApplicantCountry("SE");
		application.setApplicantPhone("+46701234567");
		application.setSotningsobjekt(objekt);

		assertThat(application.getTitle()).isEqualTo("Ansökan");
		assertThat(application.getDescription()).isEqualTo("Beskrivning");
		assertThat(application.getPriority()).isEqualTo("MEDIUM");
		assertThat(application.getReporterUserId()).isEqualTo("reporter");
		assertThat(application.getAssignedUserId()).isEqualTo("assignee");
		assertThat(application.getApplicantEmail()).isEqualTo("anna@example.se");
		assertThat(application.getPersonnummer()).isEqualTo("198507231234");
		assertThat(application.getFastighetsbeteckning()).isEqualTo("Sundsvall Stenstaden 1:23");
		assertThat(application.getPropertyAddress()).isEqualTo("Storgatan 5");
		assertThat(application.getApplicantFirstName()).isEqualTo("Anna");
		assertThat(application.getApplicantLastName()).isEqualTo("Karlsson");
		assertThat(application.getApplicantAddress()).isEqualTo("Storgatan 5");
		assertThat(application.getApplicantZipCode()).isEqualTo("85230");
		assertThat(application.getApplicantCity()).isEqualTo("Sundsvall");
		assertThat(application.getApplicantCountry()).isEqualTo("SE");
		assertThat(application.getApplicantPhone()).isEqualTo("+46701234567");
		assertThat(application.getSotningsobjekt()).isEqualTo(objekt);
	}
}
