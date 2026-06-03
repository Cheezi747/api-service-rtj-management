package se.sundsvall.rtjmanagement.types.brandfarligvara.application.api.model;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.api.model.HazardousGoodsProduct;

import static org.assertj.core.api.Assertions.assertThat;

class BrandfarligVaraApplicationTest {

	@Test
	void gettersAndSetters() {
		final var products = List.of(HazardousGoodsProduct.create().withCategory("GAS").withProductName("Gasol"));
		final var foreståndare = List.of(ApplicantResponsiblePerson.create().withFirstName("Anna"));
		final var application = BrandfarligVaraApplication.create();

		application.setTitle("Ansökan");
		application.setDescription("Beskrivning");
		application.setPriority("MEDIUM");
		application.setReporterUserId("reporter");
		application.setAssignedUserId("assignee");
		application.setApplicantEmail("kontakt@foretaget.se");
		application.setOrganizationNumber("5560123456");
		application.setCompanyName("Restaurang Ankaret AB");
		application.setCompanyAddress("Storgatan 5");
		application.setCompanyZipCode("85230");
		application.setCompanyCity("Sundsvall");
		application.setContactPersonName("Anna Karlsson");
		application.setContactPersonEmail("anna@foretaget.se");
		application.setContactPersonPhone("+46701234567");
		application.setVerksamhetstyp("RESTAURANT");
		application.setProxy(true);
		application.setFastighetsbeteckning("Sundsvall Stenstaden 1:23");
		application.setHandlingLocationAddress("Storgatan 5");
		application.setHandlingLocationZipCode("85230");
		application.setHandlingLocationCity("Sundsvall");
		application.setProducts(products);
		application.setResponsiblePersons(foreståndare);

		assertThat(application.getTitle()).isEqualTo("Ansökan");
		assertThat(application.getDescription()).isEqualTo("Beskrivning");
		assertThat(application.getPriority()).isEqualTo("MEDIUM");
		assertThat(application.getReporterUserId()).isEqualTo("reporter");
		assertThat(application.getAssignedUserId()).isEqualTo("assignee");
		assertThat(application.getApplicantEmail()).isEqualTo("kontakt@foretaget.se");
		assertThat(application.getOrganizationNumber()).isEqualTo("5560123456");
		assertThat(application.getCompanyName()).isEqualTo("Restaurang Ankaret AB");
		assertThat(application.getCompanyAddress()).isEqualTo("Storgatan 5");
		assertThat(application.getCompanyZipCode()).isEqualTo("85230");
		assertThat(application.getCompanyCity()).isEqualTo("Sundsvall");
		assertThat(application.getContactPersonName()).isEqualTo("Anna Karlsson");
		assertThat(application.getContactPersonEmail()).isEqualTo("anna@foretaget.se");
		assertThat(application.getContactPersonPhone()).isEqualTo("+46701234567");
		assertThat(application.getVerksamhetstyp()).isEqualTo("RESTAURANT");
		assertThat(application.isProxy()).isTrue();
		assertThat(application.getFastighetsbeteckning()).isEqualTo("Sundsvall Stenstaden 1:23");
		assertThat(application.getHandlingLocationAddress()).isEqualTo("Storgatan 5");
		assertThat(application.getHandlingLocationZipCode()).isEqualTo("85230");
		assertThat(application.getHandlingLocationCity()).isEqualTo("Sundsvall");
		assertThat(application.getProducts()).isEqualTo(products);
		assertThat(application.getResponsiblePersons()).isEqualTo(foreståndare);
	}

	@Test
	void builderEqualsHashCodeToString() {
		final var products = List.of(HazardousGoodsProduct.create().withCategory("GAS").withProductName("Gasol"));
		final var one = BrandfarligVaraApplication.create()
			.withApplicantEmail("kontakt@foretaget.se")
			.withOrganizationNumber("5560123456")
			.withCompanyName("Restaurang Ankaret AB")
			.withFastighetsbeteckning("Sundsvall Stenstaden 1:23")
			.withProxy(true)
			.withProducts(products)
			.withResponsiblePersons(List.of(ApplicantResponsiblePerson.create().withFirstName("Anna")));
		final var two = BrandfarligVaraApplication.create()
			.withApplicantEmail("kontakt@foretaget.se")
			.withOrganizationNumber("5560123456")
			.withCompanyName("Restaurang Ankaret AB")
			.withFastighetsbeteckning("Sundsvall Stenstaden 1:23")
			.withProxy(true)
			.withProducts(products)
			.withResponsiblePersons(List.of(ApplicantResponsiblePerson.create().withFirstName("Anna")));

		assertThat(one).isEqualTo(two).hasSameHashCodeAs(two);
		assertThat(one).hasToString(two.toString());
		assertThat(one).isNotEqualTo(BrandfarligVaraApplication.create());
	}
}
