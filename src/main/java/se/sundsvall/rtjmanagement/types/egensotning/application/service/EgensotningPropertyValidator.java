package se.sundsvall.rtjmanagement.types.egensotning.application.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet.LantmaterietIntegration;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.lantmateriet.model.Registerbeteckningsreferens;

/**
 * Validerar att en ansökans fastighet ligger i ett tillåtet område (I4).
 *
 * <p>
 * Slår upp fastighetsbeteckningen mot Lantmäteriets registerbeteckning-API och kontrollerar den
 * <b>kanoniska</b> beteckningens registerområde mot de konfigurerade kommunerna. Hittar Lantmäteriet
 * ingen träff (okänd beteckning, eller API-avbrott) faller kontrollen tillbaka på den lokala
 * heuristiken mot den inskickade beteckningen — så ett upstream-avbrott aldrig blockerar inskick,
 * samtidigt som ett bekräftat fel område alltid avvisas.
 * </p>
 */
@Component
public class EgensotningPropertyValidator {

	private final LantmaterietIntegration lantmaterietIntegration;
	private final List<String> allowedDistricts;

	EgensotningPropertyValidator(final LantmaterietIntegration lantmaterietIntegration,
		@Value("${egensotning.allowed-districts:Sundsvall,Timrå,Ånge}") final List<String> allowedDistricts) {
		this.lantmaterietIntegration = lantmaterietIntegration;
		this.allowedDistricts = allowedDistricts;
	}

	/**
	 * Throws {@code 400 Bad Request} ("fel område") when the property is outside the allowed districts.
	 * Uses the canonical beteckning from Lantmäteriet when available, otherwise the submitted value.
	 */
	public void assertValid(final String fastighetsbeteckning) {
		final var beteckningToCheck = lantmaterietIntegration.findReferens(fastighetsbeteckning)
			.map(Registerbeteckningsreferens::beteckning)
			.filter(StringUtils::hasText)
			.orElse(fastighetsbeteckning);

		EgensotningDistrictValidator.assertInAllowedDistrict(beteckningToCheck, allowedDistricts);
	}
}
