package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.types.egensotning.details.api.model.EgensotningDetails;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.EgensotningDetailsRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;

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
class EgensotningDetailsServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String TYPE_SLUG = "EGENSOTNING";

	@Mock
	private ErrandRepository errandRepositoryMock;

	@Mock
	private EgensotningDetailsRepository repositoryMock;

	@InjectMocks
	private EgensotningDetailsService service;

	private static ErrandEntity egensotningErrand() {
		return ErrandEntity.create().withId(ERRAND_ID).withTypeSlug(TYPE_SLUG);
	}

	private static EgensotningDetails sampleDetails() {
		return EgensotningDetails.create().withPersonnummer("199001011234").withFastighetsbeteckning("Fast 1:1");
	}

	@Test
	void upsertCreatesWhenAbsent() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(egensotningErrand()));
		when(repositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.empty());

		service.upsert(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, sampleDetails());

		verify(repositoryMock).save(any(EgensotningDetailsEntity.class));
	}

	@Test
	void upsertReplacesWhenPresent() {
		final var existing = EgensotningDetailsEntity.create().withId(5L).withErrandId(ERRAND_ID).withFastighetsbeteckning("Old 0:0");
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(egensotningErrand()));
		when(repositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.of(existing));

		service.upsert(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, sampleDetails());

		verify(repositoryMock).save(existing);
		assertThat(existing.getFastighetsbeteckning()).isEqualTo("Fast 1:1");
		assertThat(existing.getPersonnummer()).isEqualTo("199001011234");
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
			.thenReturn(Optional.of(ErrandEntity.create().withId(ERRAND_ID).withTypeSlug("BRANDFARLIG_VARA")));

		assertThatThrownBy(() -> service.upsert(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, sampleDetails()))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST);

		verify(repositoryMock, never()).save(any());
	}

	@Test
	void readReturnsMappedDetails() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(egensotningErrand()));
		when(repositoryMock.findByErrandId(ERRAND_ID))
			.thenReturn(Optional.of(EgensotningDetailsEntity.create().withErrandId(ERRAND_ID).withFastighetsbeteckning("Fast 9:9")));

		final var result = service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getFastighetsbeteckning()).isEqualTo("Fast 9:9");
	}

	@Test
	void readWhenDetailsMissingThrowsNotFound() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(egensotningErrand()));
		when(repositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void deleteRemovesByErrandId() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(egensotningErrand()));

		service.delete(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		verify(repositoryMock).deleteByErrandId(ERRAND_ID);
	}
}
