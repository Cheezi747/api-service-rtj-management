package se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.service.mapper;

import java.util.List;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.api.model.Sotningsobjekt;
import se.sundsvall.rtjmanagement.types.egensotning.sotningsobjekt.integration.db.model.SotningsobjektEntity;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

public final class SotningsobjektMapper {

	private SotningsobjektMapper() {}

	public static Sotningsobjekt toSotningsobjekt(final SotningsobjektEntity entity) {
		return ofNullable(entity)
			.map(e -> Sotningsobjekt.create()
				.withId(e.getId())
				.withTyp(e.getTyp())
				.withFabrikat(e.getFabrikat())
				.withTillverkningsar(e.getTillverkningsar())
				.withBransleslag(e.getBransleslag())
				.withBranslemangd(e.getBranslemangd())
				.withSotningsintervallVeckor(e.getSotningsintervallVeckor())
				.withCreated(e.getCreated())
				.withModified(e.getModified()))
			.orElse(null);
	}

	public static SotningsobjektEntity toEntity(final Sotningsobjekt sotningsobjekt, final String errandId) {
		return ofNullable(sotningsobjekt)
			.map(s -> SotningsobjektEntity.create()
				.withErrandId(errandId)
				.withTyp(s.getTyp())
				.withFabrikat(s.getFabrikat())
				.withTillverkningsar(s.getTillverkningsar())
				.withBransleslag(s.getBransleslag())
				.withBranslemangd(s.getBranslemangd())
				.withSotningsintervallVeckor(s.getSotningsintervallVeckor()))
			.orElse(null);
	}

	public static List<Sotningsobjekt> toSotningsobjektList(final List<SotningsobjektEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(SotningsobjektMapper::toSotningsobjekt)
			.toList();
	}

	/**
	 * PATCH semantics: only non-null fields on {@code patch} are applied to {@code target}.
	 */
	public static SotningsobjektEntity applyPatch(final SotningsobjektEntity target, final Sotningsobjekt patch) {
		if (target == null || patch == null) {
			return target;
		}
		ofNullable(patch.getTyp()).ifPresent(target::setTyp);
		ofNullable(patch.getFabrikat()).ifPresent(target::setFabrikat);
		ofNullable(patch.getTillverkningsar()).ifPresent(target::setTillverkningsar);
		ofNullable(patch.getBransleslag()).ifPresent(target::setBransleslag);
		ofNullable(patch.getBranslemangd()).ifPresent(target::setBranslemangd);
		ofNullable(patch.getSotningsintervallVeckor()).ifPresent(target::setSotningsintervallVeckor);
		return target;
	}
}
