package se.sundsvall.rtjmanagement.types.explosivvara.details.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.types.explosivvara.configuration.ExplosivVaraModuleConfig;
import se.sundsvall.rtjmanagement.types.explosivvara.details.api.model.ExplosivVaraDetails;
import se.sundsvall.rtjmanagement.types.explosivvara.details.integration.db.ExplosivVaraDetailsRepository;
import se.sundsvall.rtjmanagement.types.explosivvara.details.service.mapper.ExplosivVaraDetailsMapper;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.rtjmanagement.types.explosivvara.details.service.mapper.ExplosivVaraDetailsMapper.applyPatch;
import static se.sundsvall.rtjmanagement.types.explosivvara.details.service.mapper.ExplosivVaraDetailsMapper.toEntity;

/**
 * 1:1 child of {@link ErrandEntity}. Holds EXPLOSIV_VARA-specific scalar fields that
 * don't belong on the core Errand envelope. The errand's {@code typeSlug} must be
 * {@code EXPLOSIV_VARA} — operations on other types return 400.
 */
@Service
@Transactional
public class ExplosivVaraDetailsService {

	private static final String ERRAND_NOT_FOUND_MESSAGE = "No errand with id '%s' found in namespace '%s' for municipality id '%s'";
	private static final String DETAILS_NOT_FOUND_MESSAGE = "No explosiv-vara details found on errand '%s' in namespace '%s' for municipality id '%s'";
	private static final String WRONG_TYPE_MESSAGE = "Errand '%s' has typeSlug '%s'; explosiv-vara details require typeSlug '%s'";

	private final ErrandRepository errandRepository;
	private final ExplosivVaraDetailsRepository repository;

	ExplosivVaraDetailsService(final ErrandRepository errandRepository, final ExplosivVaraDetailsRepository repository) {
		this.errandRepository = errandRepository;
		this.repository = repository;
	}

	/**
	 * Upsert. Creates the row on first call, replaces existing fields on subsequent calls.
	 * 1:1-tabellen får inte ha flera rader per errand, så PUT (idempotent) är rätt verb.
	 */
	public void upsert(final String municipalityId, final String namespace, final String errandId, final ExplosivVaraDetails details) {
		final var errand = findErrandWithType(municipalityId, namespace, errandId);
		final var existing = repository.findByErrandId(errand.getId());
		if (existing.isPresent()) {
			applyPatch(existing.get(), details);
			repository.save(existing.get());
		} else {
			repository.save(toEntity(details, errand.getId()));
		}
	}

	@Transactional(readOnly = true)
	public ExplosivVaraDetails read(final String municipalityId, final String namespace, final String errandId) {
		findErrandWithType(municipalityId, namespace, errandId);
		return repository.findByErrandId(errandId)
			.map(ExplosivVaraDetailsMapper::toDetails)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, DETAILS_NOT_FOUND_MESSAGE.formatted(errandId, namespace, municipalityId)));
	}

	public void delete(final String municipalityId, final String namespace, final String errandId) {
		findErrandWithType(municipalityId, namespace, errandId);
		repository.deleteByErrandId(errandId);
	}

	private ErrandEntity findErrandWithType(final String municipalityId, final String namespace, final String errandId) {
		final var errand = errandRepository.findByIdAndNamespaceAndMunicipalityId(errandId, namespace, municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERRAND_NOT_FOUND_MESSAGE.formatted(errandId, namespace, municipalityId)));
		if (!ExplosivVaraModuleConfig.TYPE_SLUG.equals(errand.getTypeSlug())) {
			throw Problem.valueOf(BAD_REQUEST, WRONG_TYPE_MESSAGE.formatted(errandId, errand.getTypeSlug(), ExplosivVaraModuleConfig.TYPE_SLUG));
		}
		return errand;
	}
}
