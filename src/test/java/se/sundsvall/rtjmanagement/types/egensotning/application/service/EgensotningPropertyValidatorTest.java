package se.sundsvall.rtjmanagement.types.egensotning.application.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet.LantmaterietIntegration;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet.model.Registerbeteckningsreferens;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
class EgensotningPropertyValidatorTest {

	private static final List<String> ALLOWED = List.of("Sundsvall", "Timrå", "Ånge");

	@Mock
	private LantmaterietIntegration lantmaterietIntegrationMock;

	private EgensotningPropertyValidator validator;

	@BeforeEach
	void setUp() {
		validator = new EgensotningPropertyValidator(lantmaterietIntegrationMock, ALLOWED);
	}

	@Test
	void allowsWhenLantmaterietConfirmsAllowedDistrict() {
		when(lantmaterietIntegrationMock.findReferens("sundsvall stenstaden 1:23"))
			.thenReturn(Optional.of(new Registerbeteckningsreferens("id", "enh", "SUNDSVALL STENSTADEN 1:23")));

		assertThatCode(() -> validator.assertValid("sundsvall stenstaden 1:23")).doesNotThrowAnyException();
	}

	@Test
	void rejectsWhenLantmaterietResolvesToWrongDistrict() {
		// Even if the applicant typed something that looks local, the canonical Lantmäteriet område rules.
		when(lantmaterietIntegrationMock.findReferens(any()))
			.thenReturn(Optional.of(new Registerbeteckningsreferens("id", "enh", "STOCKHOLM VASASTAN 1:1")));

		assertThatThrownBy(() -> validator.assertValid("Sundsvall (felstavat) 1:1"))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST);
	}

	@Test
	void fallsBackToHeuristicAndAllowsWhenNoMatch() {
		when(lantmaterietIntegrationMock.findReferens(any())).thenReturn(Optional.empty());

		assertThatCode(() -> validator.assertValid("Timrå Böle 2:5")).doesNotThrowAnyException();
	}

	@Test
	void fallsBackToHeuristicAndRejectsWhenNoMatchAndWrongArea() {
		when(lantmaterietIntegrationMock.findReferens(any())).thenReturn(Optional.empty());

		assertThatThrownBy(() -> validator.assertValid("Stockholm Vasastan 1:1"))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST);
	}
}
