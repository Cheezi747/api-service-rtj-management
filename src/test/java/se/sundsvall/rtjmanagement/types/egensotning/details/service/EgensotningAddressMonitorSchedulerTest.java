package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import generated.se.sundsvall.citizen.CitizenAddress;
import generated.se.sundsvall.citizen.CitizenExtended;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.exception.ClientProblem;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.citizen.CitizenClient;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.EgensotningDetailsRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@ExtendWith(MockitoExtension.class)
class EgensotningAddressMonitorSchedulerTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String PNR = "199001011234";
	private static final String GUID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
	private static final String FASTIGHET = "Sundsvall Stenstaden 1:23";

	@Mock
	private EgensotningDetailsRepository detailsRepositoryMock;
	@Mock
	private ErrandRepository errandRepositoryMock;
	@Mock
	private CitizenClient citizenClientMock;
	@Mock
	private EgensotningRevocationService revocationServiceMock;

	private EgensotningAddressMonitorScheduler scheduler;

	@BeforeEach
	void setUp() {
		scheduler = new EgensotningAddressMonitorScheduler(detailsRepositoryMock, errandRepositoryMock, citizenClientMock, revocationServiceMock);
	}

	private static EgensotningDetailsEntity decidedDetails() {
		return EgensotningDetailsEntity.create().withErrandId(ERRAND_ID).withPersonnummer(PNR).withFastighetsbeteckning(FASTIGHET)
			.withValidFrom(LocalDate.now());
	}

	private static ErrandEntity decidedErrand() {
		return ErrandEntity.create().withId(ERRAND_ID).withMunicipalityId(MUNICIPALITY_ID).withNamespace(NAMESPACE).withStatus("DECIDED");
	}

	private static CitizenExtended citizenAt(final String realEstate) {
		return new CitizenExtended().addresses(List.of(
			new CitizenAddress().addressType("POPULATION_REGISTRATION_ADDRESS").municipality(MUNICIPALITY_ID).realEstateDescription(realEstate)));
	}

	@Test
	void revokesWhenApplicantNoLongerRegisteredAtProperty() {
		when(detailsRepositoryMock.findByValidFromIsNotNull()).thenReturn(List.of(decidedDetails()));
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(decidedErrand()));
		when(citizenClientMock.getGuid(MUNICIPALITY_ID, PNR)).thenReturn(GUID);
		when(citizenClientMock.getCitizen(GUID)).thenReturn(citizenAt("Sundsvall Annan 9:9"));

		scheduler.monitorAddressChanges();

		verify(revocationServiceMock).revoke(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, "ADDRESS_CHANGED");
	}

	@Test
	void doesNotRevokeWhenStillRegistered() {
		when(detailsRepositoryMock.findByValidFromIsNotNull()).thenReturn(List.of(decidedDetails()));
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(decidedErrand()));
		when(citizenClientMock.getGuid(MUNICIPALITY_ID, PNR)).thenReturn(GUID);
		when(citizenClientMock.getCitizen(GUID)).thenReturn(citizenAt(FASTIGHET));

		scheduler.monitorAddressChanges();

		verify(revocationServiceMock, never()).revoke(any(), any(), any(), any());
	}

	@Test
	void skipsErrandNoLongerDecided() {
		when(detailsRepositoryMock.findByValidFromIsNotNull()).thenReturn(List.of(decidedDetails()));
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(decidedErrand().withStatus("REVOKED")));

		scheduler.monitorAddressChanges();

		verify(revocationServiceMock, never()).revoke(any(), any(), any(), any());
	}

	@Test
	void doesNotRevokeWhenCitizenLookupFails() {
		when(detailsRepositoryMock.findByValidFromIsNotNull()).thenReturn(List.of(decidedDetails()));
		when(errandRepositoryMock.findById(ERRAND_ID)).thenReturn(Optional.of(decidedErrand()));
		when(citizenClientMock.getGuid(MUNICIPALITY_ID, PNR)).thenThrow(new ClientProblem(BAD_GATEWAY, "citizen error"));

		scheduler.monitorAddressChanges();

		verify(revocationServiceMock, never()).revoke(any(), any(), any(), any());
	}
}
