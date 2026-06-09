package se.sundsvall.rtjmanagement.types.explosivvara.application.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import se.sundsvall.dept44.common.validators.annotation.OneOf;
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.api.model.ExplosivGoodsProduct;

/**
 * Komplett ansökan om tillstånd för explosiv vara i ett anrop. JSON-delen i multipart-anropet
 * {@code POST …/explosiv-vara/applications}; bilagor skickas som separata {@code files}-delar.
 * Servern skapar errand + details + explosiva varor + APPLICANT/CONTACT_PERSON-stakeholders + en
 * stakeholder per person (med personens roll) + bilagor atomiskt och startar processen sist.
 *
 * <p>
 * Till skillnad från egensotning är sökanden en <b>juridisk person</b> (företag med
 * organisationsnummer) — LBE-tillstånd söks yrkesmässigt. Föreståndare, deltagare och personer med
 * betydande inflytande ska godkännas och anges i {@code persons}.
 * </p>
 */
@Schema(description = "Komplett ansökan om tillstånd för explosiv vara (JSON-delen i multipart-anropet).")
public class ExplosivVaraApplication {

	@Schema(description = "Ärendetitel (valfri; genereras annars)", examples = "Ansökan om tillstånd för explosiv vara — Storgatan 5")
	private String title;

	@Schema(description = "Fritext om ansökan", examples = "Sprängarbas som hanterar dynamit för bergsprängning.")
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

	@Schema(description = "Sökande företags namn", examples = "Sprängbolaget AB")
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

	@Schema(description = "Typ av hantering", examples = "STORAGE", allowableValues = {
		"USE", "STORAGE", "TRADE", "TRANSFER"
	})
	@OneOf(value = {
		"USE", "STORAGE", "TRADE", "TRANSFER"
	}, nullable = true)
	private String typAvHantering;

	@Schema(description = "Anläggningstyp — befintlig eller ny anläggning", examples = "EXISTING", allowableValues = {
		"EXISTING", "NEW"
	})
	@OneOf(value = {
		"EXISTING", "NEW"
	}, nullable = true)
	private String anlaggningTyp;

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

	@Schema(description = "Explosiva varor som omfattas (minst en)")
	@NotEmpty
	@Valid
	private List<ExplosivGoodsProduct> products;

	@Schema(description = "Personer som anmäls (föreståndare, deltagare, personer med betydande inflytande) (minst en)")
	@NotEmpty
	@Valid
	private List<ExplosivApplicantPerson> persons;

	public static ExplosivVaraApplication create() {
		return new ExplosivVaraApplication();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public ExplosivVaraApplication withTitle(final String title) {
		this.title = title;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public ExplosivVaraApplication withDescription(final String description) {
		this.description = description;
		return this;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(final String priority) {
		this.priority = priority;
	}

	public ExplosivVaraApplication withPriority(final String priority) {
		this.priority = priority;
		return this;
	}

	public String getReporterUserId() {
		return reporterUserId;
	}

	public void setReporterUserId(final String reporterUserId) {
		this.reporterUserId = reporterUserId;
	}

	public ExplosivVaraApplication withReporterUserId(final String reporterUserId) {
		this.reporterUserId = reporterUserId;
		return this;
	}

	public String getAssignedUserId() {
		return assignedUserId;
	}

	public void setAssignedUserId(final String assignedUserId) {
		this.assignedUserId = assignedUserId;
	}

	public ExplosivVaraApplication withAssignedUserId(final String assignedUserId) {
		this.assignedUserId = assignedUserId;
		return this;
	}

	public String getApplicantEmail() {
		return applicantEmail;
	}

	public void setApplicantEmail(final String applicantEmail) {
		this.applicantEmail = applicantEmail;
	}

	public ExplosivVaraApplication withApplicantEmail(final String applicantEmail) {
		this.applicantEmail = applicantEmail;
		return this;
	}

	public String getOrganizationNumber() {
		return organizationNumber;
	}

	public void setOrganizationNumber(final String organizationNumber) {
		this.organizationNumber = organizationNumber;
	}

	public ExplosivVaraApplication withOrganizationNumber(final String organizationNumber) {
		this.organizationNumber = organizationNumber;
		return this;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(final String companyName) {
		this.companyName = companyName;
	}

	public ExplosivVaraApplication withCompanyName(final String companyName) {
		this.companyName = companyName;
		return this;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(final String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public ExplosivVaraApplication withCompanyAddress(final String companyAddress) {
		this.companyAddress = companyAddress;
		return this;
	}

	public String getCompanyZipCode() {
		return companyZipCode;
	}

	public void setCompanyZipCode(final String companyZipCode) {
		this.companyZipCode = companyZipCode;
	}

	public ExplosivVaraApplication withCompanyZipCode(final String companyZipCode) {
		this.companyZipCode = companyZipCode;
		return this;
	}

	public String getCompanyCity() {
		return companyCity;
	}

	public void setCompanyCity(final String companyCity) {
		this.companyCity = companyCity;
	}

	public ExplosivVaraApplication withCompanyCity(final String companyCity) {
		this.companyCity = companyCity;
		return this;
	}

	public String getContactPersonName() {
		return contactPersonName;
	}

	public void setContactPersonName(final String contactPersonName) {
		this.contactPersonName = contactPersonName;
	}

	public ExplosivVaraApplication withContactPersonName(final String contactPersonName) {
		this.contactPersonName = contactPersonName;
		return this;
	}

	public String getContactPersonEmail() {
		return contactPersonEmail;
	}

	public void setContactPersonEmail(final String contactPersonEmail) {
		this.contactPersonEmail = contactPersonEmail;
	}

	public ExplosivVaraApplication withContactPersonEmail(final String contactPersonEmail) {
		this.contactPersonEmail = contactPersonEmail;
		return this;
	}

	public String getContactPersonPhone() {
		return contactPersonPhone;
	}

	public void setContactPersonPhone(final String contactPersonPhone) {
		this.contactPersonPhone = contactPersonPhone;
	}

	public ExplosivVaraApplication withContactPersonPhone(final String contactPersonPhone) {
		this.contactPersonPhone = contactPersonPhone;
		return this;
	}

	public String getTypAvHantering() {
		return typAvHantering;
	}

	public void setTypAvHantering(final String typAvHantering) {
		this.typAvHantering = typAvHantering;
	}

	public ExplosivVaraApplication withTypAvHantering(final String typAvHantering) {
		this.typAvHantering = typAvHantering;
		return this;
	}

	public String getAnlaggningTyp() {
		return anlaggningTyp;
	}

	public void setAnlaggningTyp(final String anlaggningTyp) {
		this.anlaggningTyp = anlaggningTyp;
	}

	public ExplosivVaraApplication withAnlaggningTyp(final String anlaggningTyp) {
		this.anlaggningTyp = anlaggningTyp;
		return this;
	}

	public boolean isProxy() {
		return isProxy;
	}

	public void setProxy(final boolean isProxy) {
		this.isProxy = isProxy;
	}

	public ExplosivVaraApplication withProxy(final boolean isProxy) {
		this.isProxy = isProxy;
		return this;
	}

	public String getFastighetsbeteckning() {
		return fastighetsbeteckning;
	}

	public void setFastighetsbeteckning(final String fastighetsbeteckning) {
		this.fastighetsbeteckning = fastighetsbeteckning;
	}

	public ExplosivVaraApplication withFastighetsbeteckning(final String fastighetsbeteckning) {
		this.fastighetsbeteckning = fastighetsbeteckning;
		return this;
	}

	public String getHandlingLocationAddress() {
		return handlingLocationAddress;
	}

	public void setHandlingLocationAddress(final String handlingLocationAddress) {
		this.handlingLocationAddress = handlingLocationAddress;
	}

	public ExplosivVaraApplication withHandlingLocationAddress(final String handlingLocationAddress) {
		this.handlingLocationAddress = handlingLocationAddress;
		return this;
	}

	public String getHandlingLocationZipCode() {
		return handlingLocationZipCode;
	}

	public void setHandlingLocationZipCode(final String handlingLocationZipCode) {
		this.handlingLocationZipCode = handlingLocationZipCode;
	}

	public ExplosivVaraApplication withHandlingLocationZipCode(final String handlingLocationZipCode) {
		this.handlingLocationZipCode = handlingLocationZipCode;
		return this;
	}

	public String getHandlingLocationCity() {
		return handlingLocationCity;
	}

	public void setHandlingLocationCity(final String handlingLocationCity) {
		this.handlingLocationCity = handlingLocationCity;
	}

	public ExplosivVaraApplication withHandlingLocationCity(final String handlingLocationCity) {
		this.handlingLocationCity = handlingLocationCity;
		return this;
	}

	public List<ExplosivGoodsProduct> getProducts() {
		return products;
	}

	public void setProducts(final List<ExplosivGoodsProduct> products) {
		this.products = products;
	}

	public ExplosivVaraApplication withProducts(final List<ExplosivGoodsProduct> products) {
		this.products = products;
		return this;
	}

	public List<ExplosivApplicantPerson> getPersons() {
		return persons;
	}

	public void setPersons(final List<ExplosivApplicantPerson> persons) {
		this.persons = persons;
	}

	public ExplosivVaraApplication withPersons(final List<ExplosivApplicantPerson> persons) {
		this.persons = persons;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final ExplosivVaraApplication that = (ExplosivVaraApplication) o;
		return isProxy == that.isProxy && Objects.equals(title, that.title) && Objects.equals(description, that.description)
			&& Objects.equals(priority, that.priority) && Objects.equals(reporterUserId, that.reporterUserId)
			&& Objects.equals(assignedUserId, that.assignedUserId) && Objects.equals(applicantEmail, that.applicantEmail)
			&& Objects.equals(organizationNumber, that.organizationNumber) && Objects.equals(companyName, that.companyName)
			&& Objects.equals(companyAddress, that.companyAddress) && Objects.equals(companyZipCode, that.companyZipCode)
			&& Objects.equals(companyCity, that.companyCity) && Objects.equals(contactPersonName, that.contactPersonName)
			&& Objects.equals(contactPersonEmail, that.contactPersonEmail) && Objects.equals(contactPersonPhone, that.contactPersonPhone)
			&& Objects.equals(typAvHantering, that.typAvHantering) && Objects.equals(anlaggningTyp, that.anlaggningTyp)
			&& Objects.equals(fastighetsbeteckning, that.fastighetsbeteckning)
			&& Objects.equals(handlingLocationAddress, that.handlingLocationAddress) && Objects.equals(handlingLocationZipCode, that.handlingLocationZipCode)
			&& Objects.equals(handlingLocationCity, that.handlingLocationCity) && Objects.equals(products, that.products)
			&& Objects.equals(persons, that.persons);
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, description, priority, reporterUserId, assignedUserId, applicantEmail, organizationNumber,
			companyName, companyAddress, companyZipCode, companyCity, contactPersonName, contactPersonEmail, contactPersonPhone,
			typAvHantering, anlaggningTyp, isProxy, fastighetsbeteckning, handlingLocationAddress, handlingLocationZipCode, handlingLocationCity,
			products, persons);
	}

	@Override
	public String toString() {
		return "ExplosivVaraApplication{title='" + title + "', applicantEmail='" + applicantEmail + "', organizationNumber='"
			+ organizationNumber + "', companyName='" + companyName + "', typAvHantering='" + typAvHantering + "', isProxy=" + isProxy
			+ ", fastighetsbeteckning='" + fastighetsbeteckning + "', products=" + products + ", persons=" + persons + "}";
	}
}
