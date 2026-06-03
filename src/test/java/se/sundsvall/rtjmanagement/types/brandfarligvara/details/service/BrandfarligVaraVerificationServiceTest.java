package se.sundsvall.rtjmanagement.types.brandfarligvara.details.service;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.attachments.integration.db.AttachmentRepository;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.types.brandfarligvara.details.integration.db.BrandfarligVaraDetailsRepository;
import se.sundsvall.rtjmanagement.types.brandfarligvara.details.integration.db.model.BrandfarligVaraDetailsEntity;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.integration.db.HazardousGoodsProductRepository;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.integration.db.model.HazardousGoodsProductEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class BrandfarligVaraVerificationServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "BRANDFARLIG_VARA";
	private static final String ERRAND_ID = "11111111-1111-1111-1111-111111111111";
	private static final String FASTIGHET = "Sundsvall Stenstaden 1:23";

	@Mock
	private ErrandRepository errandRepositoryMock;
	@Mock
	private BrandfarligVaraDetailsRepository detailsRepositoryMock;
	@Mock
	private HazardousGoodsProductRepository productRepositoryMock;
	@Mock
	private AttachmentRepository attachmentRepositoryMock;

	@InjectMocks
	private BrandfarligVaraVerificationService service;

	private static ErrandEntity brandfarligErrand() {
		return ErrandEntity.create().withId(ERRAND_ID).withTypeSlug("BRANDFARLIG_VARA");
	}

	private static BrandfarligVaraDetailsEntity details() {
		return BrandfarligVaraDetailsEntity.create().withErrandId(ERRAND_ID).withFastighetsbeteckning(FASTIGHET);
	}

	private static HazardousGoodsProductEntity product() {
		return HazardousGoodsProductEntity.create().withErrandId(ERRAND_ID).withCategory("GAS").withProductName("Gasol");
	}

	@Test
	void allPresentNeedsManualReview() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(brandfarligErrand()));
		when(attachmentRepositoryMock.countByErrandId(ERRAND_ID)).thenReturn(1L);
		when(productRepositoryMock.findByErrandIdOrderByCategoryAscProductNameAsc(ERRAND_ID)).thenReturn(List.of(product()));
		when(detailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.of(details()));

		final var result = service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getOutcome()).isEqualTo("NEEDS_MANUAL_REVIEW");
		assertThat(result.getBilagaPresent()).isTrue();
		assertThat(result.getProductsPresent()).isTrue();
		assertThat(result.getSupplementReason()).isNull();
		assertThat(result.getDecisionDescription())
			.contains("Gasol").contains("Sundsvall Stenstaden 1:23").contains("Länsstyrelsen i Västernorrlands län");
	}

	@Test
	void missingBilagaNeedsSupplement() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(brandfarligErrand()));
		when(attachmentRepositoryMock.countByErrandId(ERRAND_ID)).thenReturn(0L);
		when(productRepositoryMock.findByErrandIdOrderByCategoryAscProductNameAsc(ERRAND_ID)).thenReturn(List.of(product()));
		when(detailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.of(details()));

		final var result = service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getOutcome()).isEqualTo("NEEDS_SUPPLEMENT");
		assertThat(result.getBilagaPresent()).isFalse();
		assertThat(result.getSupplementReason()).contains("bilaga");
	}

	@Test
	void missingProductsNeedsSupplement() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(brandfarligErrand()));
		when(attachmentRepositoryMock.countByErrandId(ERRAND_ID)).thenReturn(1L);
		when(productRepositoryMock.findByErrandIdOrderByCategoryAscProductNameAsc(ERRAND_ID)).thenReturn(List.of());
		when(detailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.of(details()));

		final var result = service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getOutcome()).isEqualTo("NEEDS_SUPPLEMENT");
		assertThat(result.getProductsPresent()).isFalse();
		assertThat(result.getSupplementReason()).contains("brandfarlig vara");
	}

	@Test
	void missingDetailsNeedsSupplement() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.of(brandfarligErrand()));
		when(attachmentRepositoryMock.countByErrandId(ERRAND_ID)).thenReturn(1L);
		when(productRepositoryMock.findByErrandIdOrderByCategoryAscProductNameAsc(ERRAND_ID)).thenReturn(List.of(product()));
		when(detailsRepositoryMock.findByErrandId(ERRAND_ID)).thenReturn(Optional.empty());

		final var result = service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID);

		assertThat(result.getOutcome()).isEqualTo("NEEDS_SUPPLEMENT");
		assertThat(result.getSupplementReason()).contains("hanteringsplats");
	}

	@Test
	void errandMissingThrowsNotFound() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void wrongTypeThrowsBadRequest() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(ErrandEntity.create().withId(ERRAND_ID).withTypeSlug("EGENSOTNING")));

		assertThatThrownBy(() -> service.verify(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", BAD_REQUEST);
	}
}
