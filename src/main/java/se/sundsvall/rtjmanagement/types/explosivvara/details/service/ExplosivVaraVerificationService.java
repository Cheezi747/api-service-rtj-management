package se.sundsvall.rtjmanagement.types.explosivvara.details.service;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.rtjmanagement.attachments.integration.db.AttachmentRepository;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.types.explosivvara.configuration.ExplosivVaraModuleConfig;
import se.sundsvall.rtjmanagement.types.explosivvara.details.api.model.ExplosivVaraVerificationResult;
import se.sundsvall.rtjmanagement.types.explosivvara.details.integration.db.ExplosivVaraDetailsRepository;
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.integration.db.ExplosivGoodsProductRepository;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.util.StringUtils.hasText;

/**
 * Runs the automated explosiv-vara completeness check and computes the routing {@code outcome}.
 *
 * <p>
 * An explosiv-vara permit can <b>never</b> be auto-approved (LBE requires manual handläggning), so
 * this is a fullständighetskontroll, not a beslutsmotor: it routes to {@code NEEDS_SUPPLEMENT} when
 * the ansökan is missing data the handläggare needs to start handläggning (hanteringsplats, minst en
 * explosiv vara, eller minst en bilaga), otherwise to {@code NEEDS_MANUAL_REVIEW}.
 * </p>
 *
 * <p>
 * The formal beslutstext is generated here (from details + products) and returned as
 * {@code decisionDescription} so the BPMN can carry it as a process variable to the eventual
 * tillståndsbeslut — exactly the same carry-through as egensotning. v1 does not persist the result on
 * the details row (the routing + decisionDescription live in process variables); persisting for the
 * handläggar-UI is a later additive step.
 * </p>
 */
@Service
@Transactional(readOnly = true)
public class ExplosivVaraVerificationService {

	static final String OUTCOME_NEEDS_SUPPLEMENT = "NEEDS_SUPPLEMENT";
	static final String OUTCOME_NEEDS_MANUAL_REVIEW = "NEEDS_MANUAL_REVIEW";

	private static final String MISSING_DETAILS = "uppgifter om hanteringsplats";
	private static final String MISSING_PRODUCTS = "minst en explosiv vara";
	private static final String MISSING_BILAGA = "bilaga (t.ex. riskutredning, situationsplan)";

	private static final String ERRAND_NOT_FOUND_MESSAGE = "No errand with id '%s' found in namespace '%s' for municipality id '%s'";
	private static final String WRONG_TYPE_MESSAGE = "Errand '%s' has typeSlug '%s'; explosiv-vara verification requires typeSlug '%s'";

	private static final Logger LOG = LoggerFactory.getLogger(ExplosivVaraVerificationService.class);

	private final ErrandRepository errandRepository;
	private final ExplosivVaraDetailsRepository detailsRepository;
	private final ExplosivGoodsProductRepository productRepository;
	private final AttachmentRepository attachmentRepository;

	ExplosivVaraVerificationService(final ErrandRepository errandRepository, final ExplosivVaraDetailsRepository detailsRepository,
		final ExplosivGoodsProductRepository productRepository, final AttachmentRepository attachmentRepository) {
		this.errandRepository = errandRepository;
		this.detailsRepository = detailsRepository;
		this.productRepository = productRepository;
		this.attachmentRepository = attachmentRepository;
	}

	public ExplosivVaraVerificationResult verify(final String municipalityId, final String namespace, final String errandId) {
		findErrandWithType(municipalityId, namespace, errandId);

		final var bilagaPresent = attachmentRepository.countByErrandId(errandId) > 0;
		final var products = productRepository.findByErrandIdOrderByHazardClassAscProductNameAsc(errandId);
		final var productsPresent = !products.isEmpty();
		final var details = detailsRepository.findByErrandId(errandId).orElse(null);
		final var hanteringsplatsPresent = details != null && hasText(details.getFastighetsbeteckning());

		final var missing = new ArrayList<String>();
		if (!hanteringsplatsPresent) {
			missing.add(MISSING_DETAILS);
		}
		if (!productsPresent) {
			missing.add(MISSING_PRODUCTS);
		}
		if (!bilagaPresent) {
			missing.add(MISSING_BILAGA);
		}

		final String outcome;
		final String supplementReason;
		if (!missing.isEmpty()) {
			outcome = OUTCOME_NEEDS_SUPPLEMENT;
			supplementReason = String.join(", ", missing);
		} else {
			outcome = OUTCOME_NEEDS_MANUAL_REVIEW;
			supplementReason = null;
		}

		final var decisionDescription = ExplosivVaraDecisionTextBuilder.buildApprovalDescription(details, products);

		LOG.info("Verified explosiv-vara errand {}: outcome={} (bilaga={}, products={}, hanteringsplats={})",
			errandId, outcome, bilagaPresent, productsPresent, hanteringsplatsPresent);

		return ExplosivVaraVerificationResult.create()
			.withOutcome(outcome)
			.withBilagaPresent(bilagaPresent)
			.withProductsPresent(productsPresent)
			.withSupplementReason(supplementReason)
			.withDecisionDescription(decisionDescription);
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
