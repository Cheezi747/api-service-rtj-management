package se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.types.brandfarligvara.configuration.BrandfarligVaraModuleConfig;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.api.model.HazardousGoodsProduct;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.integration.db.HazardousGoodsProductRepository;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.integration.db.model.HazardousGoodsProductEntity;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.util.StringUtils.hasText;
import static se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.service.mapper.HazardousGoodsProductMapper.applyPatch;
import static se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.service.mapper.HazardousGoodsProductMapper.toEntity;
import static se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.service.mapper.HazardousGoodsProductMapper.toProduct;
import static se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.service.mapper.HazardousGoodsProductMapper.toProductList;

@Service
@Transactional
public class HazardousGoodsService {

	private static final String ERRAND_NOT_FOUND_MESSAGE = "No errand with id '%s' found in namespace '%s' for municipality id '%s'";
	private static final String PRODUCT_NOT_FOUND_MESSAGE = "No hazardous-goods product with id '%s' found on errand '%s' in namespace '%s' for municipality id '%s'";
	private static final String WRONG_TYPE_MESSAGE = "Errand '%s' has typeSlug '%s'; hazardous-goods require typeSlug '%s'";

	private final ErrandRepository errandRepository;
	private final HazardousGoodsProductRepository repository;

	HazardousGoodsService(final ErrandRepository errandRepository, final HazardousGoodsProductRepository repository) {
		this.errandRepository = errandRepository;
		this.repository = repository;
	}

	public String create(final String municipalityId, final String namespace, final String errandId, final HazardousGoodsProduct product) {
		findErrandWithType(municipalityId, namespace, errandId);
		final var saved = repository.save(toEntity(product, errandId));
		return saved.getId();
	}

	@Transactional(readOnly = true)
	public HazardousGoodsProduct read(final String municipalityId, final String namespace, final String errandId, final String productId) {
		return toProduct(findProduct(municipalityId, namespace, errandId, productId));
	}

	@Transactional(readOnly = true)
	public List<HazardousGoodsProduct> readAll(final String municipalityId, final String namespace, final String errandId, final String categoryFilter) {
		findErrandWithType(municipalityId, namespace, errandId);
		if (hasText(categoryFilter)) {
			return toProductList(repository.findByErrandIdAndCategoryOrderByProductNameAsc(errandId, categoryFilter));
		}
		return toProductList(repository.findByErrandIdOrderByCategoryAscProductNameAsc(errandId));
	}

	public void update(final String municipalityId, final String namespace, final String errandId, final String productId, final HazardousGoodsProduct patch) {
		final var entity = findProduct(municipalityId, namespace, errandId, productId);
		applyPatch(entity, patch);
		repository.save(entity);
	}

	public void delete(final String municipalityId, final String namespace, final String errandId, final String productId) {
		final var entity = findProduct(municipalityId, namespace, errandId, productId);
		repository.delete(entity);
	}

	private ErrandEntity findErrandWithType(final String municipalityId, final String namespace, final String errandId) {
		final var errand = errandRepository.findByIdAndNamespaceAndMunicipalityId(errandId, namespace, municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERRAND_NOT_FOUND_MESSAGE.formatted(errandId, namespace, municipalityId)));
		if (!BrandfarligVaraModuleConfig.TYPE_SLUG.equals(errand.getTypeSlug())) {
			throw Problem.valueOf(BAD_REQUEST, WRONG_TYPE_MESSAGE.formatted(errandId, errand.getTypeSlug(), BrandfarligVaraModuleConfig.TYPE_SLUG));
		}
		return errand;
	}

	private HazardousGoodsProductEntity findProduct(final String municipalityId, final String namespace, final String errandId, final String productId) {
		findErrandWithType(municipalityId, namespace, errandId);
		return repository.findByErrandIdAndId(errandId, productId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, PRODUCT_NOT_FOUND_MESSAGE.formatted(productId, errandId, namespace, municipalityId)));
	}
}
