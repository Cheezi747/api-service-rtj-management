package se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.service;

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
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.api.model.ExplosivGoodsProduct;
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.integration.db.ExplosivGoodsProductRepository;
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.integration.db.model.ExplosivGoodsProductEntity;

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
class ExplosivGoodsServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EXPLOSIV_VARA";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String PRODUCT_ID = "22222222-2222-2222-2222-222222222222";
	private static final String TYPE_SLUG = "EXPLOSIV_VARA";

	@Mock
	private ErrandRepository errandRepositoryMock;

	@Mock
	private ExplosivGoodsProductRepository repositoryMock;

	@InjectMocks
	private ExplosivGoodsService service;

	private static ErrandEntity explosivVaraErrand() {
		return ErrandEntity.create().withId(ERRAND_ID).withTypeSlug(TYPE_SLUG);
	}

	private static ExplosivGoodsProduct sampleProduct() {
		return ExplosivGoodsProduct.create().withHazardClass("1.1").withProductName("Dynamit");
	}

	@Test
	void createReturnsId() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(explosivVaraErrand()));
		when(repositoryMock.save(any(ExplosivGoodsProductEntity.class)))
			.thenReturn(ExplosivGoodsProductEntity.create().withId(PRODUCT_ID));

		final var id = service.create(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, sampleProduct());

		assertThat(id).isEqualTo(PRODUCT_ID);
	}

	@Test
	void createWhenErrandMissingThrowsNotFound() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.create(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, sampleProduct()))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);

		verifyNoInteractions(repositoryMock);
	}

	@Test
	void createWhenWrongTypeThrowsBadRequest() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(ErrandEntity.create().withId(ERRAND_ID).withTypeSlug("EGENSOTNING")));

		assertThatThrownBy(() -> service.create(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, sampleProduct()))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST);

		verify(repositoryMock, never()).save(any());
	}

	@Test
	void readReturnsMappedProduct() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(explosivVaraErrand()));
		when(repositoryMock.findByErrandIdAndId(ERRAND_ID, PRODUCT_ID))
			.thenReturn(Optional.of(ExplosivGoodsProductEntity.create().withId(PRODUCT_ID).withHazardClass("1.1").withProductName("Dynamit")));

		final var result = service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PRODUCT_ID);

		assertThat(result.getId()).isEqualTo(PRODUCT_ID);
		assertThat(result.getHazardClass()).isEqualTo("1.1");
	}

	@Test
	void readWhenProductMissingThrowsNotFound() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(explosivVaraErrand()));
		when(repositoryMock.findByErrandIdAndId(ERRAND_ID, PRODUCT_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PRODUCT_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void readAllWithoutFilterReturnsList() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(explosivVaraErrand()));
		when(repositoryMock.findByErrandIdOrderByHazardClassAscProductNameAsc(ERRAND_ID))
			.thenReturn(List.of(ExplosivGoodsProductEntity.create().withId(PRODUCT_ID)));

		assertThat(service.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, null))
			.hasSize(1)
			.first()
			.hasFieldOrPropertyWithValue("id", PRODUCT_ID);
	}

	@Test
	void readAllWithHazardClassFilterDelegatesToFilteredQuery() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(explosivVaraErrand()));
		when(repositoryMock.findByErrandIdAndHazardClassOrderByProductNameAsc(ERRAND_ID, "1.1"))
			.thenReturn(List.of(ExplosivGoodsProductEntity.create().withId(PRODUCT_ID).withHazardClass("1.1")));

		assertThat(service.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, "1.1")).hasSize(1);

		verify(repositoryMock).findByErrandIdAndHazardClassOrderByProductNameAsc(ERRAND_ID, "1.1");
	}

	@Test
	void updateAppliesPatchAndSaves() {
		final var existing = ExplosivGoodsProductEntity.create().withId(PRODUCT_ID).withHazardClass("1.1").withProductName("Dynamit");
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(explosivVaraErrand()));
		when(repositoryMock.findByErrandIdAndId(ERRAND_ID, PRODUCT_ID)).thenReturn(Optional.of(existing));

		service.update(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PRODUCT_ID, ExplosivGoodsProduct.create().withProductName("Sprängdeg"));

		verify(repositoryMock).save(existing);
		assertThat(existing.getProductName()).isEqualTo("Sprängdeg");
	}

	@Test
	void deleteRemovesEntity() {
		final var existing = ExplosivGoodsProductEntity.create().withId(PRODUCT_ID);
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(explosivVaraErrand()));
		when(repositoryMock.findByErrandIdAndId(ERRAND_ID, PRODUCT_ID)).thenReturn(Optional.of(existing));

		service.delete(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PRODUCT_ID);

		verify(repositoryMock).delete(existing);
	}
}
