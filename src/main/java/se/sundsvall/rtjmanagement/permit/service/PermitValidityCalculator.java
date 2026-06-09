package se.sundsvall.rtjmanagement.permit.service;

import java.time.LocalDate;

/**
 * Computes a permit's {@code validUntil} from its {@code permitType} and {@code validFrom}, encoding
 * MRF:s rule (LBE-processbeskrivningen §3.9.1):
 * <ul>
 * <li><b>Explosiv vara</b> — högst 3 år (19 b § LBE).</li>
 * <li><b>Brandfarlig vara</b> — 5 år (MRF-praxis; ingen lagstadgad gräns).</li>
 * </ul>
 * The base end-date ({@code validFrom + N år}) is then extended to the nästkommande fasta datum
 * (1 mars, 1 juni, 1 september eller 1 december) för att underlätta påminnelse-/uppföljningsutskick.
 */
public final class PermitValidityCalculator {

	static final String PERMIT_TYPE_EXPLOSIV = "EXPLOSIV_VARA";
	static final int YEARS_EXPLOSIV = 3;
	static final int YEARS_DEFAULT = 5;

	private static final int[] FIXED_MONTHS = {
		3, 6, 9, 12
	};

	private PermitValidityCalculator() {}

	public static int defaultYears(final String permitType) {
		if (PERMIT_TYPE_EXPLOSIV.equals(permitType)) {
			return YEARS_EXPLOSIV;
		}
		return YEARS_DEFAULT;
	}

	public static LocalDate computeValidUntil(final LocalDate validFrom, final String permitType) {
		final var base = validFrom.plusYears(defaultYears(permitType));
		return nextFixedDate(base);
	}

	/**
	 * Smallest fixed date (the 1st of March/June/September/December) that is on or after {@code from}.
	 */
	private static LocalDate nextFixedDate(final LocalDate from) {
		for (var yearOffset = 0; yearOffset <= 1; yearOffset++) {
			for (final var month : FIXED_MONTHS) {
				final var candidate = LocalDate.of(from.getYear() + yearOffset, month, 1);
				if (!candidate.isBefore(from)) {
					return candidate;
				}
			}
		}
		// Unreachable in practice (the next year's 1 March always qualifies), kept as a safe default.
		return LocalDate.of(from.getYear() + 1, 3, 1);
	}
}
