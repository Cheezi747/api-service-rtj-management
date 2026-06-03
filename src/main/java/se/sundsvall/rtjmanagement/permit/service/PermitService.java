package se.sundsvall.rtjmanagement.permit.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.permit.api.model.Permit;
import se.sundsvall.rtjmanagement.permit.integration.db.PermitRepository;
import se.sundsvall.rtjmanagement.permit.integration.db.model.PermitEntity;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.rtjmanagement.permit.service.mapper.PermitMapper.toPermit;
import static se.sundsvall.rtjmanagement.permit.service.mapper.PermitMapper.toPermitEntity;
import static se.sundsvall.rtjmanagement.permit.service.mapper.PermitMapper.toPermitList;

/**
 * Issues and manages LBE permits (tillstånd) on an errand. Type-agnostic and keyed by {@code errandId}.
 * On {@code issue}, fills the giltighetstid: {@code validFrom} defaults to today and {@code validUntil}
 * is computed from {@code permitType} when omitted (see {@link PermitValidityCalculator}).
 */
@Service
@Transactional
public class PermitService {

	static final String STATUS_ACTIVE = "ACTIVE";
	static final String STATUS_REVOKED = "REVOKED";

	private static final String ERRAND_NOT_FOUND_MESSAGE = "No errand with id '%s' found in namespace '%s' for municipality id '%s'";
	private static final String PERMIT_NOT_FOUND_MESSAGE = "No permit with id '%s' found on errand '%s' in namespace '%s' for municipality id '%s'";

	private final ErrandRepository errandRepository;
	private final PermitRepository permitRepository;

	PermitService(final ErrandRepository errandRepository, final PermitRepository permitRepository) {
		this.errandRepository = errandRepository;
		this.permitRepository = permitRepository;
	}

	public String issue(final String municipalityId, final String namespace, final String errandId, final Permit permit) {
		ensureErrandExists(municipalityId, namespace, errandId);
		final var entity = toPermitEntity(permit, errandId);
		if (entity.getValidFrom() == null) {
			entity.setValidFrom(LocalDate.now(ZoneId.systemDefault()));
		}
		if (entity.getValidUntil() == null && entity.getPermitType() != null) {
			entity.setValidUntil(PermitValidityCalculator.computeValidUntil(entity.getValidFrom(), entity.getPermitType()));
		}
		if (entity.getStatus() == null) {
			entity.setStatus(STATUS_ACTIVE);
		}
		return permitRepository.save(entity).getId();
	}

	@Transactional(readOnly = true)
	public Permit read(final String municipalityId, final String namespace, final String errandId, final String permitId) {
		return toPermit(findPermit(municipalityId, namespace, errandId, permitId));
	}

	@Transactional(readOnly = true)
	public List<Permit> readAll(final String municipalityId, final String namespace, final String errandId) {
		ensureErrandExists(municipalityId, namespace, errandId);
		return toPermitList(permitRepository.findByErrandIdOrderByCreatedDesc(errandId));
	}

	/**
	 * Återkallar ett tillstånd enligt 20 § LBE — sätter status till REVOKED.
	 */
	public void revoke(final String municipalityId, final String namespace, final String errandId, final String permitId) {
		final var entity = findPermit(municipalityId, namespace, errandId, permitId);
		entity.setStatus(STATUS_REVOKED);
		permitRepository.save(entity);
	}

	public void delete(final String municipalityId, final String namespace, final String errandId, final String permitId) {
		final var entity = findPermit(municipalityId, namespace, errandId, permitId);
		permitRepository.delete(entity);
	}

	private void ensureErrandExists(final String municipalityId, final String namespace, final String errandId) {
		errandRepository.findByIdAndNamespaceAndMunicipalityId(errandId, namespace, municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERRAND_NOT_FOUND_MESSAGE.formatted(errandId, namespace, municipalityId)));
	}

	private PermitEntity findPermit(final String municipalityId, final String namespace, final String errandId, final String permitId) {
		ensureErrandExists(municipalityId, namespace, errandId);
		return permitRepository.findByErrandIdAndId(errandId, permitId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, PERMIT_NOT_FOUND_MESSAGE.formatted(permitId, errandId, namespace, municipalityId)));
	}
}
