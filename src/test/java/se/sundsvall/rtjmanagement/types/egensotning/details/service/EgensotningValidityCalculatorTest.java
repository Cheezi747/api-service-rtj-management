package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EgensotningValidityCalculatorTest {

	@Test
	void sixYearsSnappedToNextFixedDate() {
		// 2026-01-01 + 6 år = 2032-01-01 → nästa fasta datum = 2032-03-01
		assertThat(EgensotningValidityCalculator.computeValidUntil(LocalDate.of(2026, 1, 1), 6))
			.isEqualTo(LocalDate.of(2032, 3, 1));
	}

	@Test
	void baseExactlyOnFixedDateIsKept() {
		// 2026-06-01 + 6 år = 2032-06-01 (är ett fast datum) → behålls
		assertThat(EgensotningValidityCalculator.computeValidUntil(LocalDate.of(2026, 6, 1), 6))
			.isEqualTo(LocalDate.of(2032, 6, 1));
	}

	@Test
	void baseBetweenFixedDatesSnapsUp() {
		// 2026-04-10 + 6 år = 2032-04-10 → nästa fasta datum = 2032-06-01
		assertThat(EgensotningValidityCalculator.computeValidUntil(LocalDate.of(2026, 4, 10), 6))
			.isEqualTo(LocalDate.of(2032, 6, 1));
	}

	@Test
	void baseAfterDecemberRollsToNextYearMarch() {
		// 2026-12-15 + 6 år = 2032-12-15 → inget fast datum kvar 2032 → 2033-03-01
		assertThat(EgensotningValidityCalculator.computeValidUntil(LocalDate.of(2026, 12, 15), 6))
			.isEqualTo(LocalDate.of(2033, 3, 1));
	}

	@Test
	void leapDayBaseIsNormalisedThenSnapsUp() {
		// 2024-02-29 + 6 år = 2030-02-28 (skottdag normaliseras) → nästa fasta datum = 2030-03-01
		assertThat(EgensotningValidityCalculator.computeValidUntil(LocalDate.of(2024, 2, 29), 6))
			.isEqualTo(LocalDate.of(2030, 3, 1));
	}

	@Test
	void honoursConfigurableYearCount() {
		// 2026-01-01 + 1 år = 2027-01-01 → nästa fasta datum = 2027-03-01
		assertThat(EgensotningValidityCalculator.computeValidUntil(LocalDate.of(2026, 1, 1), 1))
			.isEqualTo(LocalDate.of(2027, 3, 1));
	}

	@Test
	void zeroYearsMeansTillsvidare() {
		assertThat(EgensotningValidityCalculator.computeValidUntil(LocalDate.of(2026, 1, 1), 0)).isNull();
	}

	@Test
	void negativeYearsMeansTillsvidare() {
		assertThat(EgensotningValidityCalculator.computeValidUntil(LocalDate.of(2026, 1, 1), -1)).isNull();
	}
}
