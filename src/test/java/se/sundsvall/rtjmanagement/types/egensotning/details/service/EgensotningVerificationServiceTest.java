package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import generated.se.sundsvall.citizen.CitizenAddress;
import generated.se.sundsvall.citizen.CitizenExtended;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.exception.ClientProblem;
import se.sundsvall.dept44.exception.ServerProblem;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.attachments.integration.db.AttachmentRepository;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.citizen.CitizenClient;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.EgensotningDetailsRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.SotningsobjektRepository;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.model.SotningsobjektEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.CATEGORY_BRANDSKYDDSKONTROLL;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.CATEGORY_UTBILDNINGSINTYG;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_DECIDED;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_REGISTERED;
import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_REJECTED;

@ExtendWith(MockitoExtension.class)
class EgensotningVerificationServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String PNR = "199001011234";
	private static final String GUID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
	private static final String FASTIGHET = "Sundsvall Stenstaden 1:23";

	@Mock
	private ErrandRepository errandRepositoryMock;
	@Mock
	private EgensotningDetailsRepository detailsRepositoryMock;
	@Mock
	private AttachmentRepository attachmentRepositoryMock;
	@Mock
	private SotningsobjektRepository sotningsobjektRepositoryMock;
	@Mock
	private CitizenClient citizenClientMock;

	@InjectMocks
	private EgensotningVerificationService service;

	private static ErrandEntity egensotningErrand() {
		return ErrandEntity.create().withId(ERRAND_ID).withTypeSlug("EGENSOTNING").withStatus(STATUS_REGISTERED);
	}

	private static EgensotningDetailsEntity details() {
		return EgensotningDetailsEntity.create().withErrandId(ERRAND_ID).withPersonnummer(PNR).withFastighetsbeteckning(FASTIGHET);
	}

	private static SotningsobjektEntity sampleObjekt() {
		return SotningsobjektEntity.create().withErrandId(ERRAND_ID).withTyp("Värmepanna").withFabrikat("CTC").withSotningsintervallVeckor(8);
	}

	private static CitizenExtended registeredCitizen() {
		return new CitizenExtended().addresses(List.of(
			new CitizenAddress().addressType("POPULATION_REGISTRATION_ADDRESS").municipality(MUNICIPALITY_ID).realEstateDescription(FASTIGHET)));
	}

	private static CitizenExtended citizenAtOtherProperty() {
		return new CitizenExtended().addresses(List.of(
			new CitizenAddress().addressType("POPULATION_REGISTRATION_ADDRESS").municipality(MUNICIPALITY_ID).realEstateDescription("Sundsvall Annan 9:9")));
	}

	private void stubErrandAndDetails() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(egensotningErrand()));
		when(detailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.of(details()));
	}

	// Both required bilaga types present.
	private void stubBilagorPresent() {
		when(attachmentRepositoryMock.existsByErrandIdAndCategory(ERRAND_ID, CATEGORY_BRANDSKYDDSKONTROLL)).thenReturn(true);
		when(attachmentRepositoryMock.existsByErrandIdAndCategory(ERRAND_ID, CATEGORY_UTBILDNINGSINTYG)).thenReturn(true);
	}

	private void stubObjektPresent() {
		when(sotningsobjektRepositoryMock.findByErrandIdOrderByTypAscFabrikatAsc(ERRAND_ID)).thenReturn(List.of(sampleObjekt()));
	}

	private void stubRegistered() {
		when(citizenClientMock.getGuid(MUNICIPALITY_ID, PNR)).thenReturn(GUID);
		when(citizenClientMock.getCitizen(MUNICIPALITY_ID, GUID)).thenReturn(registeredCitizen());
	}

	private void stubNoPriorApplications() {
		when(detailsRepositoryMock.findByPersonnummerAndErrandIdNot(PNR, ERRAND_ID)).thenReturn(List.of());
	}

	@Test
	void allGreenAutoApprovesAndPersists() {
		stubErrandAndDetails();
		stubBilagorPresent();
		stubObjektPresent();
		stubRegistered();
		stubNoPriorApplications();

		final var result = service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getOutcome()).isEqualTo("AUTO_APPROVE");
		assertThat(result.getBilagaPresent()).isTrue();
		assertThat(result.getRegisteredAtProperty()).isTrue();
		assertThat(result.getReapplicationOk()).isTrue();
		assertThat(result.getManualReviewReason()).isNull();
		assertThat(result.getDecisionDescription())
			.contains("Värmepanna").contains("8 veckor").contains("Länsstyrelsen i Västernorrlands län");

		final var saved = ArgumentCaptor.forClass(EgensotningDetailsEntity.class);
		verify(detailsRepositoryMock).save(saved.capture());
		assertThat(saved.getValue().getLastOutcome()).isEqualTo("AUTO_APPROVE");
		assertThat(saved.getValue().getLastVerifiedAt()).isNotNull();
	}

	@Test
	void missingBilagorNeedsSupplement() {
		// No bilaga types present (existsByErrandIdAndCategory defaults to false) → not complete.
		stubErrandAndDetails();
		stubObjektPresent();
		stubRegistered();
		stubNoPriorApplications();

		final var result = service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getOutcome()).isEqualTo("NEEDS_SUPPLEMENT");
		assertThat(result.getBilagaPresent()).isFalse();
		assertThat(result.getRegisteredAtProperty()).isTrue();
	}

	@Test
	void onlyBrandskyddskontrollNeedsSupplement() {
		// Only one of the two required bilaga types present → not complete.
		stubErrandAndDetails();
		when(attachmentRepositoryMock.existsByErrandIdAndCategory(ERRAND_ID, CATEGORY_BRANDSKYDDSKONTROLL)).thenReturn(true);
		stubObjektPresent();
		stubRegistered();
		stubNoPriorApplications();

		final var result = service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getOutcome()).isEqualTo("NEEDS_SUPPLEMENT");
		assertThat(result.getBilagaPresent()).isFalse();
	}

	@Test
	void missingSotningsobjektNeedsSupplement() {
		stubErrandAndDetails();
		stubBilagorPresent();
		when(sotningsobjektRepositoryMock.findByErrandIdOrderByTypAscFabrikatAsc(ERRAND_ID)).thenReturn(List.of());
		stubRegistered();
		stubNoPriorApplications();

		final var result = service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getOutcome()).isEqualTo("NEEDS_SUPPLEMENT");
		assertThat(result.getBilagaPresent()).isTrue();
	}

	@Test
	void notRegisteredAtPropertyNeedsManualReview() {
		stubErrandAndDetails();
		stubBilagorPresent();
		stubObjektPresent();
		when(citizenClientMock.getGuid(MUNICIPALITY_ID, PNR)).thenReturn(GUID);
		when(citizenClientMock.getCitizen(MUNICIPALITY_ID, GUID)).thenReturn(citizenAtOtherProperty());
		stubNoPriorApplications();

		final var result = service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getOutcome()).isEqualTo("NEEDS_MANUAL_REVIEW");
		assertThat(result.getRegisteredAtProperty()).isFalse();
		assertThat(result.getManualReviewReason()).isEqualTo("NOT_REGISTERED");
	}

	@Test
	void citizenClientErrorTreatedAsNotRegistered() {
		// Citizen 4xx (e.g. unknown/malformed personnummer) → ClientProblem → route to manual review, not crash.
		stubErrandAndDetails();
		stubBilagorPresent();
		stubObjektPresent();
		when(citizenClientMock.getGuid(MUNICIPALITY_ID, PNR)).thenThrow(new ClientProblem(BAD_GATEWAY, "citizen error: Invalid format or checksum"));
		stubNoPriorApplications();

		final var result = service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getOutcome()).isEqualTo("NEEDS_MANUAL_REVIEW");
		assertThat(result.getRegisteredAtProperty()).isFalse();
		assertThat(result.getManualReviewReason()).isEqualTo("NOT_REGISTERED");
	}

	@Test
	void citizenServerErrorBubblesUp() {
		// Citizen 5xx (outage) → ServerProblem → must propagate so the worker retries via incident.
		stubErrandAndDetails();
		stubBilagorPresent();
		stubObjektPresent();
		when(citizenClientMock.getGuid(MUNICIPALITY_ID, PNR)).thenThrow(new ServerProblem(BAD_GATEWAY, "citizen down"));

		assertThatThrownBy(() -> service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID))
			.isInstanceOf(ThrowableProblem.class);
	}

	@Test
	void reapplicationPreviouslyApprovedAutoApproves() {
		stubErrandAndDetails();
		stubBilagorPresent();
		stubObjektPresent();
		stubRegistered();
		when(detailsRepositoryMock.findByPersonnummerAndErrandIdNot(PNR, ERRAND_ID))
			.thenReturn(List.of(EgensotningDetailsEntity.create().withErrandId("prior-1").withPersonnummer(PNR)));
		when(errandRepositoryMock.findById("prior-1")).thenReturn(Optional.of(ErrandEntity.create().withId("prior-1").withStatus(STATUS_DECIDED)));

		final var result = service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getOutcome()).isEqualTo("AUTO_APPROVE");
		assertThat(result.getReapplicationOk()).isTrue();
	}

	@Test
	void reapplicationPreviouslyRejectedNeedsManualReview() {
		stubErrandAndDetails();
		stubBilagorPresent();
		stubObjektPresent();
		stubRegistered();
		when(detailsRepositoryMock.findByPersonnummerAndErrandIdNot(PNR, ERRAND_ID))
			.thenReturn(List.of(EgensotningDetailsEntity.create().withErrandId("prior-1").withPersonnummer(PNR)));
		when(errandRepositoryMock.findById("prior-1")).thenReturn(Optional.of(ErrandEntity.create().withId("prior-1").withStatus(STATUS_REJECTED)));

		final var result = service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getOutcome()).isEqualTo("NEEDS_MANUAL_REVIEW");
		assertThat(result.getReapplicationOk()).isFalse();
		assertThat(result.getManualReviewReason()).isEqualTo("REAPPLICATION_REJECTED");
	}

	@Test
	void reapplicationWithOngoingErrandNeedsManualReview() {
		stubErrandAndDetails();
		stubBilagorPresent();
		stubObjektPresent();
		stubRegistered();
		when(detailsRepositoryMock.findByPersonnummerAndErrandIdNot(PNR, ERRAND_ID))
			.thenReturn(List.of(EgensotningDetailsEntity.create().withErrandId("prior-1").withPersonnummer(PNR)));
		when(errandRepositoryMock.findById("prior-1")).thenReturn(Optional.of(ErrandEntity.create().withId("prior-1").withStatus(STATUS_REGISTERED)));

		final var result = service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getOutcome()).isEqualTo("NEEDS_MANUAL_REVIEW");
		assertThat(result.getReapplicationOk()).isFalse();
		assertThat(result.getManualReviewReason()).isEqualTo("REAPPLICATION_ONGOING");
	}

	@Test
	void notRegisteredTakesPrecedenceOverMissingBilaga() {
		stubErrandAndDetails();
		stubObjektPresent();
		when(citizenClientMock.getGuid(MUNICIPALITY_ID, PNR)).thenReturn(GUID);
		when(citizenClientMock.getCitizen(MUNICIPALITY_ID, GUID)).thenReturn(citizenAtOtherProperty());
		stubNoPriorApplications();

		final var result = service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getOutcome()).isEqualTo("NEEDS_MANUAL_REVIEW");
		assertThat(result.getManualReviewReason()).isEqualTo("NOT_REGISTERED");
		assertThat(result.getBilagaPresent()).isFalse();
	}

	@Test
	void detailsMissingNeedsSupplement() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(egensotningErrand()));
		when(detailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.empty());

		final var result = service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getOutcome()).isEqualTo("NEEDS_SUPPLEMENT");
		assertThat(result.getRegisteredAtProperty()).isFalse();
	}

	@Test
	void errandMissingThrowsNotFound() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void wrongTypeThrowsBadRequest() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(ErrandEntity.create().withId(ERRAND_ID).withTypeSlug("BRANDFARLIG_VARA")));

		assertThatThrownBy(() -> service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST);
	}
}
