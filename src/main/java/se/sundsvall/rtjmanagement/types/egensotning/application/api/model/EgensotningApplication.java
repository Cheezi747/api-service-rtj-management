package se.sundsvall.rtjmanagement.types.egensotning.application.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.api.model.Sotningsobjekt;

/**
 * Komplett egensotning-ansökan i ett anrop. JSON-delen i multipart-anropet
 * {@code POST …/egensotning/applications}; bilagor skickas som separata {@code files}-delar.
 * Servern skapar errand + details + sotningsobjekt + APPLICANT-stakeholder + bilagor atomiskt
 * och startar processen sist.
 */
@Schema(description = "Komplett egensotning-ansökan (JSON-delen i multipart-anropet).")
public class EgensotningApplication {

	@Schema(description = "Ärendetitel (valfri; genereras annars)", examples = "Ansökan om egensotning — Storgatan 5")
	private String title;

	@Schema(description = "Fritext om ansökan", examples = "Sökande vill själv sota eldstad i villa byggd 1972.")
	private String description;

	@Schema(description = "Prioritet", examples = "MEDIUM")
	private String priority;

	@Schema(description = "Användar-id för anmälaren", examples = "medborgare-202304011234")
	private String reporterUserId;

	@Schema(description = "Användar-id för handläggaren", examples = "bsk-anders-svensson")
	private String assignedUserId;

	@Schema(description = "Sökandes e-post (för beslut/kompletteringsmail)", examples = "anna.karlsson@example.se")
	@NotBlank
	@Email
	private String applicantEmail;

	@Schema(description = "Sökandes personnummer (folkbokförings- och återansökan-kontroll)", examples = "198507231234")
	@NotBlank
	@Size(max = 16)
	private String personnummer;

	@Schema(description = "Fastighetsbeteckning ansökan gäller", examples = "Sundsvall Stenstaden 1:23")
	@NotBlank
	@Size(max = 255)
	private String fastighetsbeteckning;

	@Schema(description = "Adress till fastigheten", examples = "Storgatan 5")
	private String propertyAddress;

	@Schema(description = "Sökandes förnamn", examples = "Anna")
	private String applicantFirstName;

	@Schema(description = "Sökandes efternamn", examples = "Karlsson")
	private String applicantLastName;

	@Schema(description = "Sökandes adress", examples = "Storgatan 5")
	private String applicantAddress;

	@Schema(description = "Sökandes postnummer", examples = "85230")
	private String applicantZipCode;

	@Schema(description = "Sökandes ort", examples = "Sundsvall")
	private String applicantCity;

	@Schema(description = "Sökandes land", examples = "SE")
	private String applicantCountry;

	@Schema(description = "Sökandes telefonnummer", examples = "+46701234567")
	private String applicantPhone;

	@Schema(description = "Sotningsobjekt som omfattas (minst ett)")
	@NotEmpty
	@Valid
	private List<Sotningsobjekt> sotningsobjekt;

	public static EgensotningApplication create() {
		return new EgensotningApplication();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(final String priority) {
		this.priority = priority;
	}

	public String getReporterUserId() {
		return reporterUserId;
	}

	public void setReporterUserId(final String reporterUserId) {
		this.reporterUserId = reporterUserId;
	}

	public String getAssignedUserId() {
		return assignedUserId;
	}

	public void setAssignedUserId(final String assignedUserId) {
		this.assignedUserId = assignedUserId;
	}

	public String getApplicantEmail() {
		return applicantEmail;
	}

	public void setApplicantEmail(final String applicantEmail) {
		this.applicantEmail = applicantEmail;
	}

	public String getPersonnummer() {
		return personnummer;
	}

	public void setPersonnummer(final String personnummer) {
		this.personnummer = personnummer;
	}

	public String getFastighetsbeteckning() {
		return fastighetsbeteckning;
	}

	public void setFastighetsbeteckning(final String fastighetsbeteckning) {
		this.fastighetsbeteckning = fastighetsbeteckning;
	}

	public String getPropertyAddress() {
		return propertyAddress;
	}

	public void setPropertyAddress(final String propertyAddress) {
		this.propertyAddress = propertyAddress;
	}

	public String getApplicantFirstName() {
		return applicantFirstName;
	}

	public void setApplicantFirstName(final String applicantFirstName) {
		this.applicantFirstName = applicantFirstName;
	}

	public String getApplicantLastName() {
		return applicantLastName;
	}

	public void setApplicantLastName(final String applicantLastName) {
		this.applicantLastName = applicantLastName;
	}

	public String getApplicantAddress() {
		return applicantAddress;
	}

	public void setApplicantAddress(final String applicantAddress) {
		this.applicantAddress = applicantAddress;
	}

	public String getApplicantZipCode() {
		return applicantZipCode;
	}

	public void setApplicantZipCode(final String applicantZipCode) {
		this.applicantZipCode = applicantZipCode;
	}

	public String getApplicantCity() {
		return applicantCity;
	}

	public void setApplicantCity(final String applicantCity) {
		this.applicantCity = applicantCity;
	}

	public String getApplicantCountry() {
		return applicantCountry;
	}

	public void setApplicantCountry(final String applicantCountry) {
		this.applicantCountry = applicantCountry;
	}

	public String getApplicantPhone() {
		return applicantPhone;
	}

	public void setApplicantPhone(final String applicantPhone) {
		this.applicantPhone = applicantPhone;
	}

	public List<Sotningsobjekt> getSotningsobjekt() {
		return sotningsobjekt;
	}

	public void setSotningsobjekt(final List<Sotningsobjekt> sotningsobjekt) {
		this.sotningsobjekt = sotningsobjekt;
	}
}
