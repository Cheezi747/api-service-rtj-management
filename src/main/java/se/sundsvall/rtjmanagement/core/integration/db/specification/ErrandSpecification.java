package se.sundsvall.rtjmanagement.core.integration.db.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;

import static java.util.Optional.ofNullable;

public interface ErrandSpecification {

	static Specification<ErrandEntity> withNamespaceAndMunicipalityId(final String namespace, final String municipalityId) {
		return (root, _, cb) -> cb.and(
			cb.equal(root.get("namespace"), namespace),
			cb.equal(root.get("municipalityId"), municipalityId));
	}

	static Specification<ErrandEntity> withStatus(final String status) {
		return (root, _, cb) -> ofNullable(status)
			.<Predicate>map(value -> cb.equal(root.get("status"), value))
			.orElseGet(cb::conjunction);
	}

	static Specification<ErrandEntity> withTypeSlug(final String typeSlug) {
		return (root, _, cb) -> ofNullable(typeSlug)
			.<Predicate>map(value -> cb.equal(root.get("typeSlug"), value))
			.orElseGet(cb::conjunction);
	}

	/**
	 * Free-text ("sök") across the errand envelope's own searchable columns — errand number, title,
	 * description and applicant email — case-insensitive substring. Type-agnostic (every errand type
	 * shares these envelope fields), so it composes with the type/status spring-filter for the single
	 * cross-type admin list. Blank/absent {@code q} is a no-op.
	 *
	 * Note: applicant name, personnummer and fastighetsbeteckning live in the stakeholder/type-detail
	 * tables (separate Spring Modulith modules), so core cannot query them here without inverting the
	 * module dependency; searching those would require denormalising them onto the envelope.
	 */
	static Specification<ErrandEntity> withFreeText(final String q) {
		return (root, _, cb) -> ofNullable(q)
			.filter(value -> !value.isBlank())
			.<Predicate>map(value -> {
				final var like = "%" + value.toLowerCase() + "%";
				return cb.or(
					cb.like(cb.lower(root.get("errandNumber")), like),
					cb.like(cb.lower(root.get("title")), like),
					cb.like(cb.lower(root.get("description")), like),
					cb.like(cb.lower(root.get("applicantEmail")), like));
			})
			.orElseGet(cb::conjunction);
	}
}
