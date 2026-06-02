package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import generated.se.sundsvall.citizen.CitizenExtended;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

/**
 * Folkbokföringskontroll: is the applicant registered (folkbokförd) at the property the
 * application concerns? Matches the citizen's POPULATION_REGISTRATION_ADDRESS against the
 * municipality + fastighetsbeteckning. Mirrors the precheck service's address check.
 */
public final class EgensotningCheckUtil {

	private static final String POPULATION_REGISTRATION_ADDRESS = "POPULATION_REGISTRATION_ADDRESS";

	private EgensotningCheckUtil() {}

	public static boolean isRegisteredAtProperty(final CitizenExtended citizen, final String municipalityId, final String fastighetsbeteckning) {
		if (citizen == null || municipalityId == null || fastighetsbeteckning == null || fastighetsbeteckning.isBlank()) {
			return false;
		}
		final var normalizedProperty = fastighetsbeteckning.trim();
		return ofNullable(citizen.getAddresses()).orElse(emptyList()).stream()
			.filter(address -> POPULATION_REGISTRATION_ADDRESS.equalsIgnoreCase(address.getAddressType()))
			.anyMatch(address -> Objects.equals(municipalityId, address.getMunicipality())
				&& normalizedProperty.equalsIgnoreCase(ofNullable(address.getRealEstateDescription()).map(String::trim).orElse(null)));
	}
}
