package se.sundsvall.rtjmanagement.types.explosivvara.application.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

/**
 * En person som anmäls i ansökan om explosiv vara. Till skillnad från brandfarlig vara ska personer
 * kopplade till explosiv vara <i>godkännas</i> (föreståndare, deltagare) eller anges som person med
 * betydande inflytande. Personens {@code role} avgör vilken stakeholder-roll den mappas till på submit.
 */
@Schema(description = "Person som anmäls i ansökan om explosiv vara (föreståndare, deltagare eller person med betydande inflytande).")
public class ExplosivApplicantPerson {

	@Schema(description = "Personens roll i ansökan", examples = "RESPONSIBLE_PERSON", allowableValues = {
		"RESPONSIBLE_PERSON", "PARTICIPANT", "SIGNIFICANT_INFLUENCE"
	})
	@OneOf(value = {
		"RESPONSIBLE_PERSON", "PARTICIPANT", "SIGNIFICANT_INFLUENCE"
	}, nullable = true)
	private String role;

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

	public static ExplosivApplicantPerson create() {
		return new ExplosivApplicantPerson();
	}

	public String getRole() {
		return role;
	}

	public void setRole(final String v) {
		this.role = v;
	}

	public ExplosivApplicantPerson withRole(final String v) {
		this.role = v;
		return this;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(final String v) {
		this.firstName = v;
	}

	public ExplosivApplicantPerson withFirstName(final String v) {
		this.firstName = v;
		return this;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(final String v) {
		this.lastName = v;
	}

	public ExplosivApplicantPerson withLastName(final String v) {
		this.lastName = v;
		return this;
	}

	public String getPersonnummer() {
		return personnummer;
	}

	public void setPersonnummer(final String v) {
		this.personnummer = v;
	}

	public ExplosivApplicantPerson withPersonnummer(final String v) {
		this.personnummer = v;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String v) {
		this.email = v;
	}

	public ExplosivApplicantPerson withEmail(final String v) {
		this.email = v;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(final String v) {
		this.phone = v;
	}

	public ExplosivApplicantPerson withPhone(final String v) {
		this.phone = v;
		return this;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		final ExplosivApplicantPerson that = (ExplosivApplicantPerson) o;
		return Objects.equals(role, that.role) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName)
			&& Objects.equals(personnummer, that.personnummer) && Objects.equals(email, that.email)
			&& Objects.equals(phone, that.phone);
	}

	@Override
	public int hashCode() {
		return Objects.hash(role, firstName, lastName, personnummer, email, phone);
	}

	@Override
	public String toString() {
		return "ExplosivApplicantPerson{role='" + role + "', firstName='" + firstName + "', lastName='" + lastName
			+ "', personnummer='" + personnummer + "', email='" + email + "', phone='" + phone + "'}";
	}
}
