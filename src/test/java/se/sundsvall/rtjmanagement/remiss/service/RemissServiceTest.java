package se.sundsvall.rtjmanagement.remiss.service;

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
import se.sundsvall.rtjmanagement.remiss.api.model.Remiss;
import se.sundsvall.rtjmanagement.remiss.integration.db.RemissRepository;
import se.sundsvall.rtjmanagement.remiss.integration.db.model.RemissEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class RemissServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "BRANDFARLIG_VARA";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String REMISS_ID = "22222222-2222-2222-2222-222222222222";

	@Mock
	private ErrandRepository errandRepositoryMock;
	@Mock
	private RemissRepository remissRepositoryMock;

	@InjectMocks
	private RemissService service;

	private void stubErrand() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(ErrandEntity.create().withId(ERRAND_ID)));
	}

	@Test
	void createDefaultsStatusAndSentAt() {
		stubErrand();
		when(remissRepositoryMock.save(any(RemissEntity.class))).thenAnswer(inv -> {
			final RemissEntity e = inv.getArgument(0);
			e.setId(REMISS_ID);
			return e;
		});

		final var id = service.create(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, Remiss.create().withInstans("MILJOKONTOR"));

		assertThat(id).isEqualTo(REMISS_ID);
		final var captor = ArgumentCaptor.forClass(RemissEntity.class);
		verify(remissRepositoryMock).save(captor.capture());
		final var saved = captor.getValue();
		assertThat(saved.getStatus()).isEqualTo("SENT");
		assertThat(saved.getSentAt()).isEqualTo(LocalDate.now(ZoneId.systemDefault()));
	}

	@Test
	void createRespectsProvidedValues() {
		stubErrand();
		when(remissRepositoryMock.save(any(RemissEntity.class))).thenAnswer(inv -> inv.getArgument(0));

		service.create(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, Remiss.create()
			.withInstans("POLIS")
			.withSentAt(LocalDate.of(2026, 6, 3))
			.withStatus("RESPONDED"));

		final var captor = ArgumentCaptor.forClass(RemissEntity.class);
		verify(remissRepositoryMock).save(captor.capture());
		final var saved = captor.getValue();
		assertThat(saved.getSentAt()).isEqualTo(LocalDate.of(2026, 6, 3));
		assertThat(saved.getStatus()).isEqualTo("RESPONDED"); // not defaulted
	}

	@Test
	void readAllReturnsList() {
		stubErrand();
		when(remissRepositoryMock.findByErrandIdOrderByCreatedDesc(ERRAND_ID))
			.thenReturn(List.of(RemissEntity.create().withId(REMISS_ID).withInstans("MILJOKONTOR")));

		final var result = service.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result).hasSize(1);
		assertThat(result.getFirst().getId()).isEqualTo(REMISS_ID);
	}

	@Test
	void readReturnsOne() {
		stubErrand();
		when(remissRepositoryMock.findByErrandIdAndId(ERRAND_ID, REMISS_ID))
			.thenReturn(Optional.of(RemissEntity.create().withId(REMISS_ID).withStatus("SENT")));

		assertThat(service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, REMISS_ID).getStatus()).isEqualTo("SENT");
	}

	@Test
	void registerResponseSetsTextAndStatus() {
		stubErrand();
		when(remissRepositoryMock.findByErrandIdAndId(ERRAND_ID, REMISS_ID))
			.thenReturn(Optional.of(RemissEntity.create().withId(REMISS_ID).withStatus("SENT")));

		service.registerResponse(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, REMISS_ID, "Inget att invända");

		final var captor = ArgumentCaptor.forClass(RemissEntity.class);
		verify(remissRepositoryMock).save(captor.capture());
		assertThat(captor.getValue().getResponseText()).isEqualTo("Inget att invända");
		assertThat(captor.getValue().getStatus()).isEqualTo("RESPONDED");
	}

	@Test
	void deleteRemovesRemiss() {
		stubErrand();
		final var entity = RemissEntity.create().withId(REMISS_ID);
		when(remissRepositoryMock.findByErrandIdAndId(ERRAND_ID, REMISS_ID)).thenReturn(Optional.of(entity));

		service.delete(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, REMISS_ID);

		verify(remissRepositoryMock).delete(entity);
	}

	@Test
	void createErrandMissingThrowsNotFound() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.create(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, Remiss.create().withInstans("MILJOKONTOR")))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void readRemissMissingThrowsNotFound() {
		stubErrand();
		when(remissRepositoryMock.findByErrandIdAndId(ERRAND_ID, REMISS_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, REMISS_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}
}
