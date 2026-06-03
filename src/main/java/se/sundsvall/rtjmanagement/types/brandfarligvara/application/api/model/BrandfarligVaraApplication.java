package se.sundsvall.rtjmanagement.types.brandfarligvara.application.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.common.validators.annotation.OneOf;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.api.model.HazardousGoodsProduct;

/**
 * Komplett ansökan om tillstånd för brandfarlig vara i ett anrop. JSON-delen i multipart-anropet
 * {@code POST …/brandfarlig-vara/applications}; bilagor skickas som separata {@code files}-delar.
 * Servern skapar errand + details + brandfarliga varor + APPLICANT/CONTACT_PERSON/RESPONSIBLE_PERSON-
 * stakeholders + bilagor atomiskt och startar processen sist.
 *
 * <p>
 * Till skillnad från egensotning är sökanden en <b>juridisk person</b> (företag med
 * organisationsnummer) — LBE-tillstånd söks yrkesmässigt. Fakturering/ombud och PBI (personer med
 * betydande inflytande) hanteras i v2.
 * </p>
 */
@Schema(description = "Komplett ansökan om tillstånd för brandfarlig vara (JSON-delen i multipart-anropet).")
public class BrandfarligVaraApplication {

	@Schema(description = "Ärendetitel (valfri; genereras annars)", examples = "Ansökan om tillstånd för brandfarlig vara — Storgatan 5")
	private String title;

	@Schema(description = "Fritext om ansökan", examples = "Restaurang som hanterar gasol för spis.")
	private String description;

	@Schema(description = "Prioritet", examples = "MEDIUM")
	private String priority;

	@Schema(description = "Användar-id för anmälaren", examples = "medborgare-202304011234")
	private String reporterUserId;

	@Schema(description = "Användar-id för handläggaren", examples = "handlaggare-anders-svensson")
	private String assignedUserId;

	@Schema(description = "Sökandes e-post (för beslut/kompletteringsmail)", examples = "kontakt@foretaget.se")
	@NotBlank
	@Email
	private String applicantEmail;

	@Schema(description = "Sökande företags organisationsnummer", examples = "5560123456")
	@NotBlank
	@Size(max = 16)
	private String organizationNumber;

	@Schema(description = "Sökande företags namn", examples = "Restaurang Ankaret AB")
	@NotBlank
	@Size(max = 255)
	private String companyName;

	@Schema(description = "Sökande företags postadress", examples = "Storgatan 5")
	private String companyAddress;

	@Schema(description = "Sökande företags postnummer", examples = "85230")
	private String companyZipCode;

	@Schema(description = "Sökande företags ort", examples = "Sundsvall")
	private String companyCity;

	@Schema(description = "Kontaktpersonens namn", examples = "Anna Karlsson")
	private String contactPersonName;

	@Schema(description = "Kontaktpersonens e-post", examples = "anna.karlsson@foretaget.se")
	private String contactPersonEmail;

	@Schema(description = "Kontaktpersonens telefon", examples = "+46701234567")
	private String contactPersonPhone;

	@Schema(description = "Verksamhetstyp", examples = "RESTAURANT", allowableValues = {
		"RESTAURANT", "FUEL_STATION", "RETAIL", "OTHER"
	})
	@OneOf(value = {
		"RESTAURANT", "FUEL_STATION", "RETAIL", "OTHER"
	}, nullable = true)
	private String verksamhetstyp;

	@Schema(description = "True om sökanden agerar som ombud. När true förväntas en bilaga med category=DELEGATION (fullmakt).", examples = "false", defaultValue = "false")
	private boolean isProxy;

	@Schema(description = "Fastighetsbeteckning för hanteringsplatsen", examples = "Sundsvall Stenstaden 1:23")
	@NotBlank
	@Size(max = 255)
	private String fastighetsbeteckning;

	@Schema(description = "Adress till hanteringsplats", examples = "Storgatan 5")
	@Size(max = 255)
	private String handlingLocationAddress;

	@Schema(description = "Postnummer för hanteringsplats", examples = "85230")
	@Size(max = 16)
	private String handlingLocationZipCode;

	@Schema(description = "Ort för hanteringsplats", examples = "Sundsvall")
	@Size(max = 255)
	private String handlingLocationCity;

	@Schema(description = "Brandfarliga varor som omfattas (minst en)")
	@NotEmpty
	@Valid
	private List<HazardousGoodsProduct> products;

	@Schema(description = "Föreståndare (minst en)")
	@NotEmpty
	@Valid
	private List<ApplicantResponsiblePerson> responsiblePersons;

	public static BrandfarligVaraApplication create() {
		return new BrandfarligVaraApplication();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String v) {
		this.title = v;
	}

	public BrandfarligVaraApplication withTitle(final String v) {
		this.title = v;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String v) {
		this.description = v;
	}

	public BrandfarligVaraApplication withDescription(final String v) {
		this.description = v;
		return this;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(final String v) {
		this.priority = v;
	}

	public BrandfarligVaraApplication withPriority(final String v) {
		this.priority = v;
		return this;
	}

	public String getReporterUserId() {
		return reporterUserId;
	}

	public void setReporterUserId(final String v) {
		this.reporterUserId = v;
	}

	public BrandfarligVaraApplication withReporterUserId(final String v) {
		this.reporterUserId = v;
		return this;
	}

	public String getAssignedUserId() {
		return assignedUserId;
	}

	public void setAssignedUserId(final String v) {
		this.assignedUserId = v;
	}

	public BrandfarligVaraApplication withAssignedUserId(final String v) {
		this.assignedUserId = v;
		return this;
	}

	public String getApplicantEmail() {
		return applicantEmail;
	}

	public void setApplicantEmail(final String v) {
		this.applicantEmail = v;
	}

	public BrandfarligVaraApplication withApplicantEmail(final String v) {
		this.applicantEmail = v;
		return this;
	}

	public String getOrganizationNumber() {
		return organizationNumber;
	}

	public void setOrganizationNumber(final String v) {
		this.organizationNumber = v;
	}

	public BrandfarligVaraApplication withOrganizationNumber(final String v) {
		this.organizationNumber = v;
		return this;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(final String v) {
		this.companyName = v;
	}

	public BrandfarligVaraApplication withCompanyName(final String v) {
		this.companyName = v;
		return this;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(final String v) {
		this.companyAddress = v;
	}

	public BrandfarligVaraApplication withCompanyAddress(final String v) {
		this.companyAddress = v;
		return this;
	}

	public String getCompanyZipCode() {
		return companyZipCode;
	}

	public void setCompanyZipCode(final String v) {
		this.companyZipCode = v;
	}

	public BrandfarligVaraApplication withCompanyZipCode(final String v) {
		this.companyZipCode = v;
		return this;
	}

	public String getCompanyCity() {
		return companyCity;
	}

	public void setCompanyCity(final String v) {
		this.companyCity = v;
	}

	public BrandfarligVaraApplication withCompanyCity(final String v) {
		this.companyCity = v;
		return this;
	}

	public String getContactPersonName() {
		return contactPersonName;
	}

	public void setContactPersonName(final String v) {
		this.contactPersonName = v;
	}

	public BrandfarligVaraApplication withContactPersonName(final String v) {
		this.contactPersonName = v;
		return this;
	}

	public String getContactPersonEmail() {
		return contactPersonEmail;
	}

	public void setContactPersonEmail(final String v) {
		this.contactPersonEmail = v;
	}

	public BrandfarligVaraApplication withContactPersonEmail(final String v) {
		this.contactPersonEmail = v;
		return this;
	}

	public String getContactPersonPhone() {
		return contactPersonPhone;
	}

	public void setContactPersonPhone(final String v) {
		this.contactPersonPhone = v;
	}

	public BrandfarligVaraApplication withContactPersonPhone(final String v) {
		this.contactPersonPhone = v;
		return this;
	}

	public String getVerksamhetstyp() {
		return verksamhetstyp;
	}

	public void setVerksamhetstyp(final String v) {
		this.verksamhetstyp = v;
	}

	public BrandfarligVaraApplication withVerksamhetstyp(final String v) {
		this.verksamhetstyp = v;
		return this;
	}

	public boolean isProxy() {
		return isProxy;
	}

	public void setProxy(final boolean v) {
		this.isProxy = v;
	}

	public BrandfarligVaraApplication withProxy(final boolean v) {
		this.isProxy = v;
		return this;
	}

	public String getFastighetsbeteckning() {
		return fastighetsbeteckning;
	}

	public void setFastighetsbeteckning(final String v) {
		this.fastighetsbeteckning = v;
	}

	public BrandfarligVaraApplication withFastighetsbeteckning(final String v) {
		this.fastighetsbeteckning = v;
		return this;
	}

	public String getHandlingLocationAddress() {
		return handlingLocationAddress;
	}

	public void setHandlingLocationAddress(final String v) {
		this.handlingLocationAddress = v;
	}

	public BrandfarligVaraApplication withHandlingLocationAddress(final String v) {
		this.handlingLocationAddress = v;
		return this;
	}

	public String getHandlingLocationZipCode() {
		return handlingLocationZipCode;
	}

	public void setHandlingLocationZipCode(final String v) {
		this.handlingLocationZipCode = v;
	}

	public BrandfarligVaraApplication withHandlingLocationZipCode(final String v) {
		this.handlingLocationZipCode = v;
		return this;
	}

	public String getHandlingLocationCity() {
		return handlingLocationCity;
	}

	public void setHandlingLocationCity(final String v) {
		this.handlingLocationCity = v;
	}

	public BrandfarligVaraApplication withHandlingLocationCity(final String v) {
		this.handlingLocationCity = v;
		return this;
	}

	public List<HazardousGoodsProduct> getProducts() {
		return products;
	}

	public void setProducts(final List<HazardousGoodsProduct> v) {
		this.products = v;
	}

	public BrandfarligVaraApplication withProducts(final List<HazardousGoodsProduct> v) {
		this.products = v;
		return this;
	}

	public List<ApplicantResponsiblePerson> getResponsiblePersons() {
		return responsiblePersons;
	}

	public void setResponsiblePersons(final List<ApplicantResponsiblePerson> v) {
		this.responsiblePersons = v;
	}

	public BrandfarligVaraApplication withResponsiblePersons(final List<ApplicantResponsiblePerson> v) {
		this.responsiblePersons = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final BrandfarligVaraApplication that = (BrandfarligVaraApplication) o;
		return isProxy == that.isProxy && Objects.equals(title, that.title) && Objects.equals(description, that.description)
			&& Objects.equals(priority, that.priority) && Objects.equals(reporterUserId, that.reporterUserId)
			&& Objects.equals(assignedUserId, that.assignedUserId) && Objects.equals(applicantEmail, that.applicantEmail)
			&& Objects.equals(organizationNumber, that.organizationNumber) && Objects.equals(companyName, that.companyName)
			&& Objects.equals(companyAddress, that.companyAddress) && Objects.equals(companyZipCode, that.companyZipCode)
			&& Objects.equals(companyCity, that.companyCity) && Objects.equals(contactPersonName, that.contactPersonName)
			&& Objects.equals(contactPersonEmail, that.contactPersonEmail) && Objects.equals(contactPersonPhone, that.contactPersonPhone)
			&& Objects.equals(verksamhetstyp, that.verksamhetstyp) && Objects.equals(fastighetsbeteckning, that.fastighetsbeteckning)
			&& Objects.equals(handlingLocationAddress, that.handlingLocationAddress) && Objects.equals(handlingLocationZipCode, that.handlingLocationZipCode)
			&& Objects.equals(handlingLocationCity, that.handlingLocationCity) && Objects.equals(products, that.products)
			&& Objects.equals(responsiblePersons, that.responsiblePersons);
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, description, priority, reporterUserId, assignedUserId, applicantEmail, organizationNumber,
			companyName, companyAddress, companyZipCode, companyCity, contactPersonName, contactPersonEmail, contactPersonPhone,
			verksamhetstyp, isProxy, fastighetsbeteckning, handlingLocationAddress, handlingLocationZipCode, handlingLocationCity,
			products, responsiblePersons);
	}

	@Override
	public String toString() {
		return "BrandfarligVaraApplication{title='" + title + "', applicantEmail='" + applicantEmail + "', organizationNumber='"
			+ organizationNumber + "', companyName='" + companyName + "', verksamhetstyp='" + verksamhetstyp + "', isProxy=" + isProxy
			+ ", fastighetsbeteckning='" + fastighetsbeteckning + "', products=" + products + ", responsiblePersons=" + responsiblePersons + "}";
	}
}
