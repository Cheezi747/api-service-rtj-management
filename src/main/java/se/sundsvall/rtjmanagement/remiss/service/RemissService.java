package se.sundsvall.rtjmanagement.remiss.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.remiss.api.model.Remiss;
import se.sundsvall.rtjmanagement.remiss.integration.db.RemissRepository;
import se.sundsvall.rtjmanagement.remiss.integration.db.model.RemissEntity;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static se.sundsvall.rtjmanagement.remiss.service.mapper.RemissMapper.toRemiss;
import static se.sundsvall.rtjmanagement.remiss.service.mapper.RemissMapper.toRemissEntity;
import static se.sundsvall.rtjmanagement.remiss.service.mapper.RemissMapper.toRemissList;

/**
 * Skapar och hanterar remisser/samråd (14 § FBE) på ett ärende. Type-agnostic and keyed by
 * {@code errandId}. On {@code create}, {@code status} defaults to SENT and {@code sentAt} to today.
 */
@Service
@Transactional
public class RemissService {

	static final String STATUS_SENT = "SENT";
	static final String STATUS_RESPONDED = "RESPONDED";

	private static final String ERRAND_NOT_FOUND_MESSAGE = "No errand with id '%s' found in namespace '%s' for municipality id '%s'";
	private static final String REMISS_NOT_FOUND_MESSAGE = "No remiss with id '%s' found on errand '%s' in namespace '%s' for municipality id '%s'";

	private final ErrandRepository errandRepository;
	private final RemissRepository remissRepository;

	RemissService(final ErrandRepository errandRepository, final RemissRepository remissRepository) {
		this.errandRepository = errandRepository;
		this.remissRepository = remissRepository;
	}

	public String create(final String municipalityId, final String namespace, final String errandId, final Remiss remiss) {
		ensureErrandExists(municipalityId, namespace, errandId);
		final var entity = toRemissEntity(remiss, errandId);
		if (entity.getStatus() == null) {
			entity.setStatus(STATUS_SENT);
		}
		if (entity.getSentAt() == null) {
			entity.setSentAt(LocalDate.now(ZoneId.systemDefault()));
		}
		return remissRepository.save(entity).getId();
	}

	@Transactional(readOnly = true)
	public Remiss read(final String municipalityId, final String namespace, final String errandId, final String remissId) {
		return toRemiss(findRemiss(municipalityId, namespace, errandId, remissId));
	}

	@Transactional(readOnly = true)
	public List<Remiss> readAll(final String municipalityId, final String namespace, final String errandId) {
		ensureErrandExists(municipalityId, namespace, errandId);
		return toRemissList(remissRepository.findByErrandIdOrderByCreatedDesc(errandId));
	}

	/**
	 * Registrerar svaret på en remiss — sätter responseText och status RESPONDED.
	 */
	public void registerResponse(final String municipalityId, final String namespace, final String errandId, final String remissId, final String responseText) {
		final var entity = findRemiss(municipalityId, namespace, errandId, remissId);
		entity.setResponseText(responseText);
		entity.setStatus(STATUS_RESPONDED);
		remissRepository.save(entity);
	}

	public void delete(final String municipalityId, final String namespace, final String errandId, final String remissId) {
		final var entity = findRemiss(municipalityId, namespace, errandId, remissId);
		remissRepository.delete(entity);
	}

	private void ensureErrandExists(final String municipalityId, final String namespace, final String errandId) {
		errandRepository.findByIdAndNamespaceAndMunicipalityId(errandId, namespace, municipalityId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, ERRAND_NOT_FOUND_MESSAGE.formatted(errandId, namespace, municipalityId)));
	}

	private RemissEntity findRemiss(final String municipalityId, final String namespace, final String errandId, final String remissId) {
		ensureErrandExists(municipalityId, namespace, errandId);
		return remissRepository.findByErrandIdAndId(errandId, remissId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, REMISS_NOT_FOUND_MESSAGE.formatted(remissId, errandId, namespace, municipalityId)));
	}
}
