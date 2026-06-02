package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import generated.se.sundsvall.citizen.CitizenAddress;
import generated.se.sundsvall.citizen.CitizenExtended;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EgensotningCheckUtilTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String FASTIGHET = "Sundsvall Stenstaden 1:23";

	private static CitizenExtended citizenWith(final CitizenAddress... addresses) {
		return new CitizenExtended().addresses(List.of(addresses));
	}

	private static CitizenAddress address(final String type, final String municipality, final String realEstate) {
		return new CitizenAddress().addressType(type).municipality(municipality).realEstateDescription(realEstate);
	}

	@Test
	void nullCitizenReturnsFalse() {
		assertThat(EgensotningCheckUtil.isRegisteredAtProperty(null, MUNICIPALITY_ID, FASTIGHET)).isFalse();
	}

	@Test
	void nullMunicipalityReturnsFalse() {
		assertThat(EgensotningCheckUtil.isRegisteredAtProperty(citizenWith(address("POPULATION_REGISTRATION_ADDRESS", MUNICIPALITY_ID, FASTIGHET)), null, FASTIGHET)).isFalse();
	}

	@Test
	void blankFastighetReturnsFalse() {
		assertThat(EgensotningCheckUtil.isRegisteredAtProperty(citizenWith(address("POPULATION_REGISTRATION_ADDRESS", MUNICIPALITY_ID, FASTIGHET)), MUNICIPALITY_ID, "  ")).isFalse();
	}

	@Test
	void noAddressesReturnsFalse() {
		assertThat(EgensotningCheckUtil.isRegisteredAtProperty(new CitizenExtended(), MUNICIPALITY_ID, FASTIGHET)).isFalse();
	}

	@Test
	void wrongAddressTypeReturnsFalse() {
		assertThat(EgensotningCheckUtil.isRegisteredAtProperty(citizenWith(address("SPECIAL_ADDRESS", MUNICIPALITY_ID, FASTIGHET)), MUNICIPALITY_ID, FASTIGHET)).isFalse();
	}

	@Test
	void wrongMunicipalityReturnsFalse() {
		assertThat(EgensotningCheckUtil.isRegisteredAtProperty(citizenWith(address("POPULATION_REGISTRATION_ADDRESS", "1480", FASTIGHET)), MUNICIPALITY_ID, FASTIGHET)).isFalse();
	}

	@Test
	void wrongRealEstateReturnsFalse() {
		assertThat(EgensotningCheckUtil.isRegisteredAtProperty(citizenWith(address("POPULATION_REGISTRATION_ADDRESS", MUNICIPALITY_ID, "Sundsvall Annan 9:9")), MUNICIPALITY_ID, FASTIGHET)).isFalse();
	}

	@Test
	void matchingAddressReturnsTrue() {
		assertThat(EgensotningCheckUtil.isRegisteredAtProperty(citizenWith(address("POPULATION_REGISTRATION_ADDRESS", MUNICIPALITY_ID, FASTIGHET)), MUNICIPALITY_ID, FASTIGHET)).isTrue();
	}

	@Test
	void matchIsCaseInsensitiveAndTrimmed() {
		assertThat(EgensotningCheckUtil.isRegisteredAtProperty(
			citizenWith(address("population_registration_address", MUNICIPALITY_ID, "  sundsvall stenstaden 1:23  ")), MUNICIPALITY_ID, FASTIGHET)).isTrue();
	}

	@Test
	void matchesWhenOneOfSeveralAddressesQualifies() {
		assertThat(EgensotningCheckUtil.isRegisteredAtProperty(citizenWith(
			address("SPECIAL_ADDRESS", MUNICIPALITY_ID, "Sundsvall Annan 9:9"),
			address("POPULATION_REGISTRATION_ADDRESS", MUNICIPALITY_ID, FASTIGHET)), MUNICIPALITY_ID, FASTIGHET)).isTrue();
	}
}
