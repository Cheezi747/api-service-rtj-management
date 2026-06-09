package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import generated.se.sundsvall.messaging.EmailRequest;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.core.api.model.PatchErrand;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.core.service.ErrandService;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.EgensotningDetailsRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.messaging.MessagingClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class EgensotningRevocationServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";

	@Mock
	private ErrandRepository errandRepositoryMock;
	@Mock
	private EgensotningDetailsRepository detailsRepositoryMock;
	@Mock
	private ErrandService errandServiceMock;
	@Mock
	private MessagingClient messagingClientMock;

	@InjectMocks
	private EgensotningRevocationService service;

	private static ErrandEntity egensotningErrand(final String status) {
		return ErrandEntity.create().withId(ERRAND_ID).withMunicipalityId(MUNICIPALITY_ID).withNamespace(NAMESPACE)
			.withTypeSlug("EGENSOTNING").withStatus(status).withApplicantEmail("agaren@example.com");
	}

	@Test
	void revokeSetsStatusStampsDetailsAndEmails() {
		final var details = EgensotningDetailsEntity.create().withErrandId(ERRAND_ID).withFastighetsbeteckning("Fast 1:1")
			.withValidUntil(LocalDate.now().plusYears(5));
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(egensotningErrand("DECIDED")));
		when(detailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.of(details));

		service.revoke(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, "BSK_FAILED");

		final var patchCaptor = ArgumentCaptor.forClass(PatchErrand.class);
		verify(errandServiceMock).updateErrand(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), patchCaptor.capture());
		assertThat(patchCaptor.getValue().getStatus()).isEqualTo("REVOKED");
		assertThat(details.getValidUntil()).isNull();
		assertThat(details.getRevokedAt()).isNotNull();
		assertThat(details.getRevocationReason()).isEqualTo("BSK_FAILED");
		verify(detailsRepositoryMock).save(details);
		verify(messagingClientMock).sendEmail(eq(MUNICIPALITY_ID), any(EmailRequest.class));
	}

	@Test
	void revokeWithoutApplicantEmailSkipsMessaging() {
		final var details = EgensotningDetailsEntity.create().withErrandId(ERRAND_ID).withFastighetsbeteckning("Fast 1:1");
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(egensotningErrand("DECIDED").withApplicantEmail(null)));
		when(detailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.of(details));

		service.revoke(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, "ADDRESS_CHANGED");

		verify(detailsRepositoryMock).save(details);
		verify(messagingClientMock, never()).sendEmail(any(), any());
	}

	@Test
	void emailFailureIsSwallowedAndRevocationStands() {
		final var details = EgensotningDetailsEntity.create().withErrandId(ERRAND_ID).withFastighetsbeteckning("Fast 1:1");
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(egensotningErrand("DECIDED")));
		when(detailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.of(details));
		when(messagingClientMock.sendEmail(any(), any())).thenThrow(se.sundsvall.dept44.problem.Problem.valueOf(BAD_GATEWAY, "messaging down"));

		assertThatNoException().isThrownBy(() -> service.revoke(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, "ADDRESS_CHANGED"));

		assertThat(details.getRevocationReason()).isEqualTo("ADDRESS_CHANGED");
		verify(detailsRepositoryMock).save(details);
	}

	@Test
	void revokeWhenErrandMissingThrowsNotFound() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.revoke(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, "x"))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);

		verifyNoInteractions(errandServiceMock, messagingClientMock);
	}

	@Test
	void revokeWhenWrongTypeThrowsBadRequest() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(ErrandEntity.create().withId(ERRAND_ID).withTypeSlug("BRANDFARLIG_VARA").withStatus("DECIDED")));

		assertThatThrownBy(() -> service.revoke(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, "x"))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST);

		verifyNoInteractions(errandServiceMock, messagingClientMock);
	}

	@Test
	void revokeWhenNotDecidedThrowsConflict() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(egensotningErrand("UNDER_MANUAL_REVIEW")));

		assertThatThrownBy(() -> service.revoke(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, "x"))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", CONFLICT);

		verifyNoInteractions(errandServiceMock, messagingClientMock);
	}
}
