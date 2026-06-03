package se.sundsvall.rtjmanagement.permit.service;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PermitValidityCalculatorTest {

	@Test
	void defaultYears() {
		assertThat(PermitValidityCalculator.defaultYears("EXPLOSIV_VARA")).isEqualTo(3);
		assertThat(PermitValidityCalculator.defaultYears("BRANDFARLIG_VARA")).isEqualTo(5);
		assertThat(PermitValidityCalculator.defaultYears(null)).isEqualTo(5);
	}

	@Test
	void brandfarligFiveYearsSnappedToNextFixedDate() {
		// 2026-01-01 + 5 år = 2031-01-01 → nästa fasta datum = 2031-03-01
		assertThat(PermitValidityCalculator.computeValidUntil(LocalDate.of(2026, 1, 1), "BRANDFARLIG_VARA"))
			.isEqualTo(LocalDate.of(2031, 3, 1));
	}

	@Test
	void explosivThreeYearsSnappedToNextFixedDate() {
		// 2026-01-01 + 3 år = 2029-01-01 → nästa fasta datum = 2029-03-01
		assertThat(PermitValidityCalculator.computeValidUntil(LocalDate.of(2026, 1, 1), "EXPLOSIV_VARA"))
			.isEqualTo(LocalDate.of(2029, 3, 1));
	}

	@Test
	void baseExactlyOnFixedDateIsKept() {
		// 2026-06-01 + 5 år = 2031-06-01 (är ett fast datum) → behålls
		assertThat(PermitValidityCalculator.computeValidUntil(LocalDate.of(2026, 6, 1), "BRANDFARLIG_VARA"))
			.isEqualTo(LocalDate.of(2031, 6, 1));
	}

	@Test
	void baseAfterDecemberRollsToNextYearMarch() {
		// 2026-12-15 + 5 år = 2031-12-15 → inget fast datum kvar 2031 → 2032-03-01
		assertThat(PermitValidityCalculator.computeValidUntil(LocalDate.of(2026, 12, 15), "BRANDFARLIG_VARA"))
			.isEqualTo(LocalDate.of(2032, 3, 1));
	}

	@Test
	void baseBetweenFixedDatesSnapsUp() {
		// 2026-04-10 + 5 år = 2031-04-10 → nästa fasta datum = 2031-06-01
		assertThat(PermitValidityCalculator.computeValidUntil(LocalDate.of(2026, 4, 10), "BRANDFARLIG_VARA"))
			.isEqualTo(LocalDate.of(2031, 6, 1));
	}
}
