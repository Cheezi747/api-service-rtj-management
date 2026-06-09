package se.sundsvall.rtjmanagement.types.egensotning.application.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class EgensotningDistrictValidatorTest {

	private static final List<String> ALLOWED = List.of("Sundsvall", "Timrå", "Ånge");

	@Test
	void allowsConfiguredDistrictCaseInsensitively() {
		assertThatCode(() -> EgensotningDistrictValidator.assertInAllowedDistrict("Sundsvall Stenstaden 1:23", ALLOWED)).doesNotThrowAnyException();
		assertThatCode(() -> EgensotningDistrictValidator.assertInAllowedDistrict("ånge Prästbordet 1:1", ALLOWED)).doesNotThrowAnyException();
		assertThatCode(() -> EgensotningDistrictValidator.assertInAllowedDistrict("Timrå Böle 2:5", ALLOWED)).doesNotThrowAnyException();
	}

	@Test
	void rejectsDistrictOutsideArea() {
		assertThatThrownBy(() -> EgensotningDistrictValidator.assertInAllowedDistrict("Stockholm Vasastan 1:1", ALLOWED))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST);
	}

	@Test
	void rejectsBlankOrNullFastighetsbeteckning() {
		assertThatThrownBy(() -> EgensotningDistrictValidator.assertInAllowedDistrict(null, ALLOWED))
			.isInstanceOf(ThrowableProblem.class).hasFieldOrPropertyWithValue("status", BAD_REQUEST);
		assertThatThrownBy(() -> EgensotningDistrictValidator.assertInAllowedDistrict("   ", ALLOWED))
			.isInstanceOf(ThrowableProblem.class).hasFieldOrPropertyWithValue("status", BAD_REQUEST);
	}

	@Test
	void noRestrictionWhenAllowedListEmptyOrNull() {
		assertThatCode(() -> EgensotningDistrictValidator.assertInAllowedDistrict("Stockholm Vasastan 1:1", List.of())).doesNotThrowAnyException();
		assertThatCode(() -> EgensotningDistrictValidator.assertInAllowedDistrict("Stockholm Vasastan 1:1", null)).doesNotThrowAnyException();
	}
}
