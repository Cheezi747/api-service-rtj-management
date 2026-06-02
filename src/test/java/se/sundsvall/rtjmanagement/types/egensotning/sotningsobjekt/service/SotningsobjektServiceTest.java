package se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.api.model.Sotningsobjekt;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.SotningsobjektRepository;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.model.SotningsobjektEntity;

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
class SotningsobjektServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String OBJEKT_ID = "22222222-2222-2222-2222-222222222222";

	@Mock
	private ErrandRepository errandRepositoryMock;

	@Mock
	private SotningsobjektRepository repositoryMock;

	@InjectMocks
	private SotningsobjektService service;

	private static ErrandEntity egensotningErrand() {
		return ErrandEntity.create().withId(ERRAND_ID).withTypeSlug("EGENSOTNING");
	}

	private static Sotningsobjekt sampleObjekt() {
		return Sotningsobjekt.create().withTyp("Värmepanna").withSotningsintervallVeckor(8);
	}

	@Test
	void createReturnsId() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(egensotningErrand()));
		when(repositoryMock.save(any(SotningsobjektEntity.class))).thenReturn(SotningsobjektEntity.create().withId(OBJEKT_ID));

		assertThat(service.create(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, sampleObjekt())).isEqualTo(OBJEKT_ID);
	}

	@Test
	void createWhenErrandMissingThrowsNotFound() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.create(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, sampleObjekt()))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);

		verifyNoInteractions(repositoryMock);
	}

	@Test
	void createWhenWrongTypeThrowsBadRequest() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(ErrandEntity.create().withId(ERRAND_ID).withTypeSlug("BRANDFARLIG_VARA")));

		assertThatThrownBy(() -> service.create(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, sampleObjekt()))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST);

		verify(repositoryMock, never()).save(any());
	}

	@Test
	void readReturnsMappedObjekt() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(egensotningErrand()));
		when(repositoryMock.findByErrandIdAndId(ERRAND_ID, OBJEKT_ID))
			.thenReturn(Optional.of(SotningsobjektEntity.create().withId(OBJEKT_ID).withTyp("Vedspis")));

		final var result = service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, OBJEKT_ID);

		assertThat(result.getId()).isEqualTo(OBJEKT_ID);
		assertThat(result.getTyp()).isEqualTo("Vedspis");
	}

	@Test
	void readWhenObjektMissingThrowsNotFound() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(egensotningErrand()));
		when(repositoryMock.findByErrandIdAndId(ERRAND_ID, OBJEKT_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, OBJEKT_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void readAllReturnsList() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(egensotningErrand()));
		when(repositoryMock.findByErrandIdOrderByTypAscFabrikatAsc(ERRAND_ID))
			.thenReturn(List.of(SotningsobjektEntity.create().withId(OBJEKT_ID)));

		assertThat(service.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID)).hasSize(1).first().hasFieldOrPropertyWithValue("id", OBJEKT_ID);
	}

	@Test
	void updateAppliesPatchAndSaves() {
		final var existing = SotningsobjektEntity.create().withId(OBJEKT_ID).withTyp("Vedspis").withSotningsintervallVeckor(52);
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(egensotningErrand()));
		when(repositoryMock.findByErrandIdAndId(ERRAND_ID, OBJEKT_ID)).thenReturn(Optional.of(existing));

		service.update(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, OBJEKT_ID, Sotningsobjekt.create().withSotningsintervallVeckor(8));

		verify(repositoryMock).save(existing);
		assertThat(existing.getSotningsintervallVeckor()).isEqualTo(8);
		assertThat(existing.getTyp()).isEqualTo("Vedspis");
	}

	@Test
	void deleteRemovesEntity() {
		final var existing = SotningsobjektEntity.create().withId(OBJEKT_ID);
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(egensotningErrand()));
		when(repositoryMock.findByErrandIdAndId(ERRAND_ID, OBJEKT_ID)).thenReturn(Optional.of(existing));

		service.delete(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, OBJEKT_ID);

		verify(repositoryMock).delete(existing);
	}
}
