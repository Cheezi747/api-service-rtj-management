package se.sundsvall.rtjmanagement.types.brandfarligvara.details.service;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.types.brandfarligvara.details.api.model.BrandfarligVaraDetails;
import se.sundsvall.rtjmanagement.types.brandfarligvara.details.integration.db.BrandfarligVaraDetailsRepository;
import se.sundsvall.rtjmanagement.types.brandfarligvara.details.integration.db.model.BrandfarligVaraDetailsEntity;

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
class BrandfarligVaraDetailsServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "BRANDFARLIG_VARA";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String TYPE_SLUG = "BRANDFARLIG_VARA";

	@Mock
	private ErrandRepository errandRepositoryMock;

	@Mock
	private BrandfarligVaraDetailsRepository repositoryMock;

	@InjectMocks
	private BrandfarligVaraDetailsService service;

	private static ErrandEntity brandfarligVaraErrand() {
		return ErrandEntity.create().withId(ERRAND_ID).withTypeSlug(TYPE_SLUG);
	}

	private static BrandfarligVaraDetails sampleDetails() {
		return BrandfarligVaraDetails.create().withVerksamhetstyp("RESTAURANT").withProxy(true);
	}

	@Test
	void upsertCreatesWhenAbsent() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(brandfarligVaraErrand()));
		when(repositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.empty());

		service.upsert(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, sampleDetails());

		verify(repositoryMock).save(any(BrandfarligVaraDetailsEntity.class));
	}

	@Test
	void upsertReplacesWhenPresent() {
		final var existing = BrandfarligVaraDetailsEntity.create().withId(5L).withErrandId(ERRAND_ID).withVerksamhetstyp("OTHER");
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(brandfarligVaraErrand()));
		when(repositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.of(existing));

		service.upsert(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, sampleDetails());

		verify(repositoryMock).save(existing);
		assertThat(existing.getVerksamhetstyp()).isEqualTo("RESTAURANT");
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
			.thenReturn(Optional.of(brandfarligVaraErrand()));
		when(repositoryMock.findByErrandId(ERRAND_ID))
			.thenReturn(Optional.of(BrandfarligVaraDetailsEntity.create().withErrandId(ERRAND_ID).withVerksamhetstyp("RETAIL").withProxy(false)));

		final var result = service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getVerksamhetstyp()).isEqualTo("RETAIL");
		assertThat(result.isProxy()).isFalse();
	}

	@Test
	void readWhenDetailsMissingThrowsNotFound() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(brandfarligVaraErrand()));
		when(repositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void deleteRemovesByErrandId() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(brandfarligVaraErrand()));

		service.delete(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		verify(repositoryMock).deleteByErrandId(ERRAND_ID);
	}
}
