package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.model.SotningsobjektEntity;

import static org.assertj.core.api.Assertions.assertThat;

class EgensotningDecisionTextBuilderTest {

	private static EgensotningDetailsEntity details() {
		return EgensotningDetailsEntity.create().withFastighetsbeteckning("Sundsvall Stenstaden 1:23");
	}

	@Test
	void includesObjektLagstodVillkorAndOverklagande() {
		final var objekt = List.of(
			SotningsobjektEntity.create().withTyp("Värmepanna").withFabrikat("CTC").withBransleslag("Ved").withSotningsintervallVeckor(8),
			SotningsobjektEntity.create().withTyp("Vedspis").withSotningsintervallVeckor(52));

		final var text = EgensotningDecisionTextBuilder.buildApprovalDescription(details(), objekt);

		assertThat(text)
			.contains("Sundsvall Stenstaden 1:23")
			.contains("3 kap. 4 §")
			.contains("Värmepanna").contains("fabrikat CTC").contains("sotningsintervall 8 veckor")
			.contains("Vedspis").contains("sotningsintervall 52 veckor")
			.contains("ägarbyte")
			.contains("Länsstyrelsen i Västernorrlands län");
	}

	@Test
	void noObjektNotesNone() {
		final var text = EgensotningDecisionTextBuilder.buildApprovalDescription(details(), List.of());

		assertThat(text).contains("inga objekt angivna").contains("Länsstyrelsen i Västernorrlands län");
	}

	@Test
	void nullDetailsAndObjektDoesNotFail() {
		final var text = EgensotningDecisionTextBuilder.buildApprovalDescription(null, null);

		assertThat(text).contains("Ansökan om egensotning godkänd").contains("inga objekt angivna");
	}
}
