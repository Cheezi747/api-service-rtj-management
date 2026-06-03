package se.sundsvall.rtjmanagement.permit.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.permit.api.model.Permit;
import se.sundsvall.rtjmanagement.permit.integration.db.PermitRepository;
import se.sundsvall.rtjmanagement.permit.integration.db.model.PermitEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class PermitServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "BRANDFARLIG_VARA";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String PERMIT_ID = "22222222-2222-2222-2222-222222222222";

	@Mock
	private ErrandRepository errandRepositoryMock;
	@Mock
	private PermitRepository permitRepositoryMock;

	@InjectMocks
	private PermitService service;

	private void stubErrand() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(ErrandEntity.create().withId(ERRAND_ID)));
	}

	@Test
	void issueComputesValidityAndDefaults() {
		stubErrand();
		when(permitRepositoryMock.save(any(PermitEntity.class))).thenAnswer(inv -> {
			final PermitEntity e = inv.getArgument(0);
			e.setId(PERMIT_ID);
			return e;
		});

		final var id = service.issue(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, Permit.create().withPermitType("BRANDFARLIG_VARA"));

		assertThat(id).isEqualTo(PERMIT_ID);
		final var captor = ArgumentCaptor.forClass(PermitEntity.class);
		verify(permitRepositoryMock).save(captor.capture());
		final var saved = captor.getValue();
		assertThat(saved.getValidFrom()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));
		assertThat(saved.getValidUntil()).isEqualTo(PermitValidityCalculator.computeValidUntil(saved.getValidFrom(), "BRANDFARLIG_VARA"));
		assertThat(saved.getStatus()).isEqualTo("ACTIVE");
	}

	@Test
	void issueRespectsProvidedValues() {
		stubErrand();
		when(permitRepositoryMock.save(any(PermitEntity.class))).thenAnswer(inv -> inv.getArgument(0));

		service.issue(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, Permit.create()
			.withPermitType("EXPLOSIV_VARA")
			.withValidFrom(LocalDate.of(2026, 6, 3))
			.withValidUntil(LocalDate.of(2030, 1, 1))
			.withStatus("ACTIVE"));

		final var captor = ArgumentCaptor.forClass(PermitEntity.class);
		verify(permitRepositoryMock).save(captor.capture());
		final var saved = captor.getValue();
		assertThat(saved.getValidFrom()).isEqualTo(LocalDate.of(2026, 6, 3));
		assertThat(saved.getValidUntil()).isEqualTo(LocalDate.of(2030, 1, 1)); // not recomputed
	}

	@Test
	void issueWithoutPermitTypeLeavesValidUntilNull() {
		stubErrand();
		when(permitRepositoryMock.save(any(PermitEntity.class))).thenAnswer(inv -> inv.getArgument(0));

		service.issue(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, Permit.create());

		final var captor = ArgumentCaptor.forClass(PermitEntity.class);
		verify(permitRepositoryMock).save(captor.capture());
		final var saved = captor.getValue();
		assertThat(saved.getValidUntil()).isNull();
		assertThat(saved.getStatus()).isEqualTo("ACTIVE");
		assertThat(saved.getValidFrom()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));
	}

	@Test
	void readAllReturnsList() {
		stubErrand();
		when(permitRepositoryMock.findByErrandIdOrderByCreatedDesc(ERRAND_ID))
			.thenReturn(List.of(PermitEntity.create().withId(PERMIT_ID).withPermitType("BRANDFARLIG_VARA")));

		final var result = service.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getId()).isEqualTo(PERMIT_ID);
	}

	@Test
	void readReturnsOne() {
		stubErrand();
		when(permitRepositoryMock.findByErrandIdAndId(ERRAND_ID, PERMIT_ID))
			.thenReturn(Optional.of(PermitEntity.create().withId(PERMIT_ID).withStatus("ACTIVE")));

		assertThat(service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PERMIT_ID).getStatus()).isEqualTo("ACTIVE");
	}

	@Test
	void revokeSetsStatusRevoked() {
		stubErrand();
		when(permitRepositoryMock.findByErrandIdAndId(ERRAND_ID, PERMIT_ID))
			.thenReturn(Optional.of(PermitEntity.create().withId(PERMIT_ID).withStatus("ACTIVE")));

		service.revoke(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PERMIT_ID);

		final var captor = ArgumentCaptor.forClass(PermitEntity.class);
		verify(permitRepositoryMock).save(captor.capture());
		assertThat(captor.getValue().getStatus()).isEqualTo("REVOKED");
	}

	@Test
	void revokeAllForErrandRevokesOnlyActiveOnes() {
		stubErrand();
		final var active = PermitEntity.create().withId("p1").withStatus("ACTIVE");
		final var alreadyRevoked = PermitEntity.create().withId("p2").withStatus("REVOKED");
		when(permitRepositoryMock.findByErrandIdOrderByCreatedDesc(ERRAND_ID)).thenReturn(List.of(active, alreadyRevoked));

		service.revokeAllForErrand(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		// Only the active permit is saved (the already-revoked one is skipped)
		final var captor = ArgumentCaptor.forClass(PermitEntity.class);
		verify(permitRepositoryMock).save(captor.capture());
		assertThat(captor.getValue().getId()).isEqualTo("p1");
		assertThat(captor.getValue().getStatus()).isEqualTo("REVOKED");
	}

	@Test
	void revokeAllForErrandErrandMissingThrowsNotFound() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.revokeAllForErrand(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void deleteRemovesPermit() {
		stubErrand();
		final var entity = PermitEntity.create().withId(PERMIT_ID);
		when(permitRepositoryMock.findByErrandIdAndId(ERRAND_ID, PERMIT_ID)).thenReturn(Optional.of(entity));

		service.delete(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PERMIT_ID);

		verify(permitRepositoryMock).delete(entity);
	}

	@Test
	void issueErrandMissingThrowsNotFound() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.issue(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, Permit.create().withPermitType("BRANDFARLIG_VARA")))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void readPermitMissingThrowsNotFound() {
		stubErrand();
		when(permitRepositoryMock.findByErrandIdAndId(ERRAND_ID, PERMIT_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PERMIT_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}
}
