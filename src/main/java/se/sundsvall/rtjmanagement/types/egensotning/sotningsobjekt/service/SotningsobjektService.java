package se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig;
import se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningMutationGuard;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.api.model.Sotningsobjekt;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.SotningsobjektRepository;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.model.SotningsobjektEntity;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.service.mapper.SotningsobjektMapper.applyPatch;
import static se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.service.mapper.SotningsobjektMapper.toEntity;
import static se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.service.mapper.SotningsobjektMapper.toSotningsobjekt;
import static se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.service.mapper.SotningsobjektMapper.toSotningsobjektList;

@Service
@Transactional
public class SotningsobjektService {

	private static final String ERRAND_NOT_FOUND_MESSAGE = "No errand with id '%s' found in namespace '%s' for municipality id '%s'";
	private static final String OBJEKT_NOT_FOUND_MESSAGE = "No sotningsobjekt with id '%s' found on errand '%s' in namespace '%s' for municipality id '%s'";
	private static final String WRONG_TYPE_MESSAGE = "Errand '%s' has typeSlug '%s'; sotningsobjekt require typeSlug '%s'";

	private final ErrandRepository errandRepository;
	private final SotningsobjektRepository repository;

	SotningsobjektService(final ErrandRepository errandRepository, final SotningsobjektRepository repository) {
		this.errandRepository = errandRepository;
		this.repository = repository;
	}

	public String create(final String municipalityId, final String namespace, final String errandId, final Sotningsobjekt sotningsobjekt) {
		EgensotningMutationGuard.assertMutable(findErrandWithType(municipalityId, namespace, errandId));
		return repository.save(toEntity(sotningsobjekt, errandId)).getId();
	}

	@Transactional(readOnly = true)
	public Sotningsobjekt read(final String municipalityId, final String namespace, final String errandId, final String objektId) {
		return toSotningsobjekt(findObjekt(municipalityId, namespace, errandId, objektId));
	}

	@Transactional(readOnly = true)
	public List<Sotningsobjekt> readAll(final String municipalityId, final String namespace, final String errandId) {
		findErrandWithType(municipalityId, namespace, errandId);
		return toSotningsobjektList(repository.findByErrandIdOrderByTypAscFabrikatAsc(errandId));
	}

	public void update(final String municipalityId, final String namespace, final String errandId, final String objektId, final Sotningsobjekt patch) {
		EgensotningMutationGuard.assertMutable(findErrandWithType(municipalityId, namespace, errandId));
		final var entity = findObjekt(municipalityId, namespace, errandId, objektId);
		applyPatch(entity, patch);
		repository.save(entity);
	}

	public void delete(final String municipalityId, final String namespace, final String errandId, final String objektId) {
		EgensotningMutationGuard.assertMutable(findErrandWithType(municipalityId, namespace, errandId));
		repository.delete(findObjekt(municipalityId, namespace, errandId, objektId));
	}

	private ErrandEntity findErrandWithType(final String municipalityId, final String namespace, final String errandId) {
		final var errand = errandRepository.findByIdAndNamespaceAndMunicipalityId(errandId, namespace, municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERRAND_NOT_FOUND_MESSAGE.formatted(errandId, namespace, municipalityId)));
		if (!EgensotningModuleConfig.TYPE_SLUG.equals(errand.getTypeSlug())) {
			throw Problem.valueOf(BAD_REQUEST, WRONG_TYPE_MESSAGE.formatted(errandId, errand.getTypeSlug(), EgensotningModuleConfig.TYPE_SLUG));
		}
		return errand;
	}

	private SotningsobjektEntity findObjekt(final String municipalityId, final String namespace, final String errandId, final String objektId) {
		findErrandWithType(municipalityId, namespace, errandId);
		return repository.findByErrandIdAndId(errandId, objektId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, OBJEKT_NOT_FOUND_MESSAGE.formatted(objektId, errandId, namespace, municipalityId)));
	}
}
