package se.sundsvall.rtjmanagement.stakeholders.service;

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
import se.sundsvall.rtjmanagement.stakeholders.api.model.Stakeholder;
import se.sundsvall.rtjmanagement.stakeholders.integration.db.StakeholderRepository;
import se.sundsvall.rtjmanagement.stakeholders.integration.db.model.StakeholderEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class StakeholderServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = "errand-1";
	private static final String STAKEHOLDER_ID = "stakeholder-1";

	@Mock
	private ErrandRepository errandRepositoryMock;

	@Mock
	private StakeholderRepository stakeholderRepositoryMock;

	@InjectMocks
	private StakeholderService service;

	private void errandExists() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(mock(ErrandEntity.class)));
	}

	@Test
	void createSavesAndReturnsId() {
		errandExists();
		when(stakeholderRepositoryMock.save(any())).thenReturn(StakeholderEntity.create().withId(STAKEHOLDER_ID));

		final var id = service.create(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, Stakeholder.create().withRole("BSK").withFirstName("BSK"));

		assertThat(id).isEqualTo(STAKEHOLDER_ID);
		final var captor = ArgumentCaptor.forClass(StakeholderEntity.class);
		verify(stakeholderRepositoryMock).save(captor.capture());
		assertThat(captor.getValue().getErrandId()).isEqualTo(ERRAND_ID);
		assertThat(captor.getValue().getRole()).isEqualTo("BSK");
	}

	@Test
	void createThrowsWhenErrandMissing() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.create(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, Stakeholder.create()))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void readReturnsMappedStakeholder() {
		errandExists();
		when(stakeholderRepositoryMock.findById(STAKEHOLDER_ID))
			.thenReturn(Optional.of(StakeholderEntity.create().withId(STAKEHOLDER_ID).withErrandId(ERRAND_ID).withRole("APPLICANT").withFirstName("Anna")));

		final var result = service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, STAKEHOLDER_ID);

		assertThat(result.getId()).isEqualTo(STAKEHOLDER_ID);
		assertThat(result.getRole()).isEqualTo("APPLICANT");
		assertThat(result.getFirstName()).isEqualTo("Anna");
	}

	@Test
	void readThrowsWhenStakeholderBelongsToOtherErrand() {
		errandExists();
		when(stakeholderRepositoryMock.findById(STAKEHOLDER_ID))
			.thenReturn(Optional.of(StakeholderEntity.create().withId(STAKEHOLDER_ID).withErrandId("other-errand")));

		assertThatThrownBy(() -> service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, STAKEHOLDER_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void readAllReturnsMappedList() {
		errandExists();
		when(stakeholderRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(List.of(
			StakeholderEntity.create().withId("s1").withErrandId(ERRAND_ID).withRole("APPLICANT"),
			StakeholderEntity.create().withId("s2").withErrandId(ERRAND_ID).withRole("BSK")));

		final var result = service.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result).hasSize(2).extracting(Stakeholder::getRole).containsExactly("APPLICANT", "BSK");
	}

	@Test
	void updateAppliesPatchAndSaves() {
		errandExists();
		final var existing = StakeholderEntity.create().withId(STAKEHOLDER_ID).withErrandId(ERRAND_ID).withRole("APPLICANT");
		when(stakeholderRepositoryMock.findById(STAKEHOLDER_ID)).thenReturn(Optional.of(existing));

		service.update(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, STAKEHOLDER_ID, Stakeholder.create().withRole("BSK"));

		final var captor = ArgumentCaptor.forClass(StakeholderEntity.class);
		verify(stakeholderRepositoryMock).save(captor.capture());
		assertThat(captor.getValue().getRole()).isEqualTo("BSK");
	}

	@Test
	void deleteRemovesStakeholder() {
		errandExists();
		final var existing = StakeholderEntity.create().withId(STAKEHOLDER_ID).withErrandId(ERRAND_ID);
		when(stakeholderRepositoryMock.findById(STAKEHOLDER_ID)).thenReturn(Optional.of(existing));

		service.delete(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, STAKEHOLDER_ID);

		verify(stakeholderRepositoryMock).delete(existing);
	}
}
