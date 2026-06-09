package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import java.time.LocalDate;

/**
 * Computes an egensotning decision's {@code validUntil} from its {@code validFrom}.
 *
 * Ett egensotningsmedgivande gäller i ett konfigurerbart antal år från beslutsdatum (default sex år,
 * {@code egensotning.validity.years}). Slutdatumet ({@code validFrom + N år}) flyttas fram till
 * nästkommande fasta datum (1 mars, 1 juni, 1 september eller 1 december) så att
 * påminnelse-/förnyelseutskick kan batchas — samma upplägg som {@code PermitValidityCalculator}
 * i permit-modulen (medvetet en lokal kopia; modulerna är frikopplade). {@code years <= 0} betyder
 * "gäller tillsvidare" och ger inget utgångsdatum ({@code null}).
 */
public final class EgensotningValidityCalculator {

	private static final int[] FIXED_MONTHS = {
		3, 6, 9, 12
	};

	private EgensotningValidityCalculator() {}

	/**
	 * Beräknar {@code validUntil} givet {@code validFrom} och giltighetstid i år. Returnerar
	 * {@code null} när {@code years <= 0} (gäller tillsvidare — inget utgångsdatum).
	 */
	public static LocalDate computeValidUntil(final LocalDate validFrom, final int years) {
		if (years <= 0) {
			return null;
		}
		return nextFixedDate(validFrom.plusYears(years));
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
