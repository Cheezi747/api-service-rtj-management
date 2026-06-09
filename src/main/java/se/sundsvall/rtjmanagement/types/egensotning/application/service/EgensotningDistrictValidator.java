package se.sundsvall.rtjmanagement.types.egensotning.application.service;

import java.util.List;
import java.util.Optional;
import se.sundsvall.dept44.problem.Problem;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Kontrollerar att fastigheten ligger i ett tillåtet område (I4). Egensotning hanteras bara för
 * fastigheter inom Räddningstjänsten Medelpads förbundskommuner (Sundsvall, Timrå, Ånge); en ansökan
 * för en fastighet utanför området avvisas med 400.
 *
 * <p>
 * Matchar en fastighetsbetecknings inledande registerområde (första ordet, t.ex. "Sundsvall" i
 * "Sundsvall Stenstaden 1:23") mot de konfigurerade områdena. Det <b>primära</b> distriktsbeslutet tas
 * av {@link EgensotningPropertyValidator}, som slår upp den kanoniska beteckningen via Lantmäteriet och
 * matchar dess registerområde här. Den här heuristiken är <b>fallback</b> när Lantmäteriet inte hittar
 * någon träff eller är otillgängligt — då kontrolleras den inskickade beteckningens inledande ord i
 * stället, så ett upstream-avbrott aldrig blockerar inskick men ett uppenbart fel område ändå avvisas.
 * </p>
 */
public final class EgensotningDistrictValidator {

	private static final String WRONG_AREA_MESSAGE = "Fastigheten ligger i fel område — egensotning kan endast sökas för fastigheter i %s";

	private EgensotningDistrictValidator() {}

	public static void assertInAllowedDistrict(final String fastighetsbeteckning, final List<String> allowedDistricts) {
		if (allowedDistricts == null || allowedDistricts.isEmpty()) {
			return; // ingen begränsning konfigurerad
		}
		final var district = Optional.ofNullable(fastighetsbeteckning)
			.map(String::trim)
			.filter(value -> !value.isBlank())
			.map(value -> value.split("\\s+")[0])
			.orElse(null);

		final var allowed = district != null && allowedDistricts.stream()
			.anyMatch(allowedDistrict -> allowedDistrict.trim().equalsIgnoreCase(district));
		if (!allowed) {
			throw Problem.valueOf(BAD_REQUEST, WRONG_AREA_MESSAGE.formatted(String.join(", ", allowedDistricts)));
		}
	}
}
