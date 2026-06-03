package se.sundsvall.rtjmanagement.types.explosivvara.details.service;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.types.explosivvara.details.api.model.ExplosivVaraDetails;
import se.sundsvall.rtjmanagement.types.explosivvara.details.integration.db.ExplosivVaraDetailsRepository;
import se.sundsvall.rtjmanagement.types.explosivvara.details.integration.db.model.ExplosivVaraDetailsEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class ExplosivVaraDetailsServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EXPLOSIV_VARA";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String TYPE_SLUG = "EXPLOSIV_VARA";

	@Mock
	private ErrandRepository errandRepositoryMock;

	@Mock
	private ExplosivVaraDetailsRepository repositoryMock;

	@InjectMocks
	private ExplosivVaraDetailsService service;

	private static ErrandEntity explosivVaraErrand() {
		return ErrandEntity.create().withId(ERRAND_ID).withTypeSlug(TYPE_SLUG);
	}

	private static ExplosivVaraDetails sampleDetails() {
		return ExplosivVaraDetails.create().withTypAvHantering("STORAGE").withProxy(true);
	}

	@Test
	void upsertCreatesWhenAbsent() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(explosivVaraErrand()));
		when(repositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.empty());

		service.upsert(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, sampleDetails());

		verify(repositoryMock).save(any(ExplosivVaraDetailsEntity.class));
	}

	@Test
	void upsertReplacesWhenPresent() {
		final var existing = ExplosivVaraDetailsEntity.create().withId(5L).withErrandId(ERRAND_ID).withTypAvHantering("TRADE");
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(explosivVaraErrand()));
		when(repositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.of(existing));

		service.upsert(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, sampleDetails());

		verify(repositoryMock).save(existing);
		assertThat(existing.getTypAvHantering()).isEqualTo("STORAGE");
		assertThat(existing.isProxy()).isTrue();
	}

	@Test
	void upsertWhenErrandMissingThrowsNotFound() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.upsert(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, sampleDetails()))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);

		verifyNoInteractions(repositoryMock);
	}

	@Test
	void upsertWhenWrongTypeThrowsBadRequest() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(ErrandEntity.create().withId(ERRAND_ID).withTypeSlug("EGENSOTNING")));

		assertThatThrownBy(() -> service.upsert(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, sampleDetails()))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST);

		verify(repositoryMock, never()).save(any());
	}

	@Test
	void readReturnsMappedDetails() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(explosivVaraErrand()));
		when(repositoryMock.findByErrandId(ERRAND_ID))
			.thenReturn(Optional.of(ExplosivVaraDetailsEntity.create().withErrandId(ERRAND_ID).withTypAvHantering("TRANSFER").withProxy(false)));

		final var result = service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getTypAvHantering()).isEqualTo("TRANSFER");
		assertThat(result.isProxy()).isFalse();
	}

	@Test
	void readWhenDetailsMissingThrowsNotFound() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(explosivVaraErrand()));
		when(repositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void deleteRemovesByErrandId() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(explosivVaraErrand()));

		service.delete(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		verify(repositoryMock).deleteByErrandId(ERRAND_ID);
	}
}
