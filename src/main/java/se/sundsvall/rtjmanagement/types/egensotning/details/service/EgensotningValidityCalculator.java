package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import java.time.LocalDate;

/**
 * Computes an egensotning decision's {@code validUntil} from its {@code validFrom}.
 *
 * Ett egensotningsmedgivande är tidsbegränsat till <b>sex år</b> från beslutsdatum (ersätter den
 * tidigare "gäller tillsvidare"-praxisen). Slutdatumet ({@code validFrom + 6 år}) flyttas fram till
 * nästkommande fasta datum (1 mars, 1 juni, 1 september eller 1 december) så att
 * påminnelse-/förnyelseutskick kan batchas — samma upplägg som {@code PermitValidityCalculator}
 * i permit-modulen (medvetet en lokal kopia; modulerna är frikopplade).
 */
public final class EgensotningValidityCalculator {

	static final int YEARS = 6;

	private static final int[] FIXED_MONTHS = {
		3, 6, 9, 12
	};

	private EgensotningValidityCalculator() {}

	public static LocalDate computeValidUntil(final LocalDate validFrom) {
		return nextFixedDate(validFrom.plusYears(YEARS));
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
