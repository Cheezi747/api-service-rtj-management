package se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.service;

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
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.api.model.HazardousGoodsProduct;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.integration.db.HazardousGoodsProductRepository;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.integration.db.model.HazardousGoodsProductEntity;

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
class HazardousGoodsServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "BRANDFARLIG_VARA";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String PRODUCT_ID = "22222222-2222-2222-2222-222222222222";
	private static final String TYPE_SLUG = "BRANDFARLIG_VARA";

	@Mock
	private ErrandRepository errandRepositoryMock;

	@Mock
	private HazardousGoodsProductRepository repositoryMock;

	@InjectMocks
	private HazardousGoodsService service;

	private static ErrandEntity brandfarligVaraErrand() {
		return ErrandEntity.create().withId(ERRAND_ID).withTypeSlug(TYPE_SLUG);
	}

	private static HazardousGoodsProduct sampleProduct() {
		return HazardousGoodsProduct.create().withCategory("GAS").withProductName("Propan");
	}

	@Test
	void createReturnsId() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(brandfarligVaraErrand()));
		when(repositoryMock.save(any(HazardousGoodsProductEntity.class)))
			.thenReturn(HazardousGoodsProductEntity.create().withId(PRODUCT_ID));

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
			.thenReturn(Optional.of(brandfarligVaraErrand()));
		when(repositoryMock.findByErrandIdAndId(ERRAND_ID, PRODUCT_ID))
			.thenReturn(Optional.of(HazardousGoodsProductEntity.create().withId(PRODUCT_ID).withCategory("GAS").withProductName("Propan")));

		final var result = service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PRODUCT_ID);

		assertThat(result.getId()).isEqualTo(PRODUCT_ID);
		assertThat(result.getCategory()).isEqualTo("GAS");
	}

	@Test
	void readWhenProductMissingThrowsNotFound() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(brandfarligVaraErrand()));
		when(repositoryMock.findByErrandIdAndId(ERRAND_ID, PRODUCT_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PRODUCT_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void readAllWithoutFilterReturnsList() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(brandfarligVaraErrand()));
		when(repositoryMock.findByErrandIdOrderByCategoryAscProductNameAsc(ERRAND_ID))
			.thenReturn(List.of(HazardousGoodsProductEntity.create().withId(PRODUCT_ID)));

		assertThat(service.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, null))
			.hasSize(1)
			.first()
			.hasFieldOrPropertyWithValue("id", PRODUCT_ID);
	}

	@Test
	void readAllWithCategoryFilterDelegatesToFilteredQuery() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(brandfarligVaraErrand()));
		when(repositoryMock.findByErrandIdAndCategoryOrderByProductNameAsc(ERRAND_ID, "GAS"))
			.thenReturn(List.of(HazardousGoodsProductEntity.create().withId(PRODUCT_ID).withCategory("GAS")));

		assertThat(service.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, "GAS")).hasSize(1);

		verify(repositoryMock).findByErrandIdAndCategoryOrderByProductNameAsc(ERRAND_ID, "GAS");
	}

	@Test
	void updateAppliesPatchAndSaves() {
		final var existing = HazardousGoodsProductEntity.create().withId(PRODUCT_ID).withCategory("GAS").withProductName("Propan");
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(brandfarligVaraErrand()));
		when(repositoryMock.findByErrandIdAndId(ERRAND_ID, PRODUCT_ID)).thenReturn(Optional.of(existing));

		service.update(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PRODUCT_ID, HazardousGoodsProduct.create().withProductName("Metan"));

		verify(repositoryMock).save(existing);
		assertThat(existing.getProductName()).isEqualTo("Metan");
	}

	@Test
	void deleteRemovesEntity() {
		final var existing = HazardousGoodsProductEntity.create().withId(PRODUCT_ID);
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(brandfarligVaraErrand()));
		when(repositoryMock.findByErrandIdAndId(ERRAND_ID, PRODUCT_ID)).thenReturn(Optional.of(existing));

		service.delete(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PRODUCT_ID);

		verify(repositoryMock).delete(existing);
	}
}
