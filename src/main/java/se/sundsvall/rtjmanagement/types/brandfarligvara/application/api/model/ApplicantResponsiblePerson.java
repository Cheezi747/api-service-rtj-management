package se.sundsvall.rtjmanagement.types.brandfarligvara.application.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.Objects;

/**
 * Föreståndare for the verksamhet, as supplied in the brandfarlig-vara application. Mapped to a
 * RESPONSIBLE_PERSON stakeholder on submit. For brandfarlig vara the föreståndare is only
 * <i>anmäld</i> (not separately godkänd as for explosiv vara) — lämpligheten vägs in i
 * tillståndsbeslutet (LBE 3.9.3).
 */
@Schema(description = "Föreståndare som anmäls i ansökan om brandfarlig vara.")
public class ApplicantResponsiblePerson {

	@Schema(description = "Förnamn", examples = "Anna")
	private String firstName;

	@Schema(description = "Efternamn", examples = "Karlsson")
	private String lastName;

	@Schema(description = "Personnummer", examples = "198507231234")
	@Size(max = 16)
	private String personnummer;

	@Schema(description = "E-post", examples = "anna.karlsson@example.se")
	private String email;

	@Schema(description = "Telefonnummer", examples = "+46701234567")
	private String phone;

	public static ApplicantResponsiblePerson create() {
		return new ApplicantResponsiblePerson();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String v) {
		this.firstName = v;
	}

	public ApplicantResponsiblePerson withFirstName(final String v) {
		this.firstName = v;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String v) {
		this.lastName = v;
	}

	public ApplicantResponsiblePerson withLastName(final String v) {
		this.lastName = v;
		return this;
	}

	public String getPersonnummer() {
		return personnummer;
	}

	public void setPersonnummer(final String v) {
		this.personnummer = v;
	}

	public ApplicantResponsiblePerson withPersonnummer(final String v) {
		this.personnummer = v;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String v) {
		this.email = v;
	}

	public ApplicantResponsiblePerson withEmail(final String v) {
		this.email = v;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(final String v) {
		this.phone = v;
	}

	public ApplicantResponsiblePerson withPhone(final String v) {
		this.phone = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final ApplicantResponsiblePerson that = (ApplicantResponsiblePerson) o;
		return Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName)
			&& Objects.equals(personnummer, that.personnummer) && Objects.equals(email, that.email)
			&& Objects.equals(phone, that.phone);
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstName, lastName, personnummer, email, phone);
	}

	@Override
	public String toString() {
		return "ApplicantResponsiblePerson{firstName='" + firstName + "', lastName='" + lastName
			+ "', personnummer='" + personnummer + "', email='" + email + "', phone='" + phone + "'}";
	}
}
