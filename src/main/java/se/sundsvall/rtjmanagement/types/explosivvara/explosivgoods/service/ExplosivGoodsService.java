package se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.types.explosivvara.configuration.ExplosivVaraModuleConfig;
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.api.model.ExplosivGoodsProduct;
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.integration.db.ExplosivGoodsProductRepository;
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.integration.db.model.ExplosivGoodsProductEntity;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.util.StringUtils.hasText;
import static se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.service.mapper.ExplosivGoodsProductMapper.applyPatch;
import static se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.service.mapper.ExplosivGoodsProductMapper.toEntity;
import static se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.service.mapper.ExplosivGoodsProductMapper.toProduct;
import static se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.service.mapper.ExplosivGoodsProductMapper.toProductList;

@Service
@Transactional
public class ExplosivGoodsService {

	private static final String ERRAND_NOT_FOUND_MESSAGE = "No errand with id '%s' found in namespace '%s' for municipality id '%s'";
	private static final String PRODUCT_NOT_FOUND_MESSAGE = "No explosive-goods product with id '%s' found on errand '%s' in namespace '%s' for municipality id '%s'";
	private static final String WRONG_TYPE_MESSAGE = "Errand '%s' has typeSlug '%s'; explosive-goods require typeSlug '%s'";

	private final ErrandRepository errandRepository;
	private final ExplosivGoodsProductRepository repository;

	ExplosivGoodsService(final ErrandRepository errandRepository, final ExplosivGoodsProductRepository repository) {
		this.errandRepository = errandRepository;
		this.repository = repository;
	}

	public String create(final String municipalityId, final String namespace, final String errandId, final ExplosivGoodsProduct product) {
		findErrandWithType(municipalityId, namespace, errandId);
		final var saved = repository.save(toEntity(product, errandId));
		return saved.getId();
	}

	@Transactional(readOnly = true)
	public ExplosivGoodsProduct read(final String municipalityId, final String namespace, final String errandId, final String productId) {
		return toProduct(findProduct(municipalityId, namespace, errandId, productId));
	}

	@Transactional(readOnly = true)
	public List<ExplosivGoodsProduct> readAll(final String municipalityId, final String namespace, final String errandId, final String hazardClassFilter) {
		findErrandWithType(municipalityId, namespace, errandId);
		if (hasText(hazardClassFilter)) {
			return toProductList(repository.findByErrandIdAndHazardClassOrderByProductNameAsc(errandId, hazardClassFilter));
		}
		return toProductList(repository.findByErrandIdOrderByHazardClassAscProductNameAsc(errandId));
	}

	public void update(final String municipalityId, final String namespace, final String errandId, final String productId, final ExplosivGoodsProduct patch) {
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
		if (!ExplosivVaraModuleConfig.TYPE_SLUG.equals(errand.getTypeSlug())) {
			throw Problem.valueOf(BAD_REQUEST, WRONG_TYPE_MESSAGE.formatted(errandId, errand.getTypeSlug(), ExplosivVaraModuleConfig.TYPE_SLUG));
		}
		return errand;
	}

	private ExplosivGoodsProductEntity findProduct(final String municipalityId, final String namespace, final String errandId, final String productId) {
		findErrandWithType(municipalityId, namespace, errandId);
		return repository.findByErrandIdAndId(errandId, productId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, PRODUCT_NOT_FOUND_MESSAGE.formatted(productId, errandId, namespace, municipalityId)));
	}
}
