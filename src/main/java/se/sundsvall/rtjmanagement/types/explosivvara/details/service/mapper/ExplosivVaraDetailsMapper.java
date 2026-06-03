package se.sundsvall.rtjmanagement.types.explosivvara.details.service.mapper;

import se.sundsvall.rtjmanagement.types.explosivvara.details.api.model.ExplosivVaraDetails;
import se.sundsvall.rtjmanagement.types.explosivvara.details.integration.db.model.ExplosivVaraDetailsEntity;

import static java.util.Optional.ofNullable;

public final class ExplosivVaraDetailsMapper {

	private ExplosivVaraDetailsMapper() {}

	public static ExplosivVaraDetails toDetails(final ExplosivVaraDetailsEntity entity) {
		return ofNullable(entity)
			.map(e -> ExplosivVaraDetails.create()
				.withTypAvHantering(e.getTypAvHantering())
				.withProxy(e.isProxy())
				.withFastighetsbeteckning(e.getFastighetsbeteckning())
				.withHandlingLocationAddress(e.getHandlingLocationAddress())
				.withHandlingLocationZipCode(e.getHandlingLocationZipCode())
				.withHandlingLocationCity(e.getHandlingLocationCity())
				.withCreated(e.getCreated())
				.withModified(e.getModified()))
			.orElse(null);
	}

	public static ExplosivVaraDetailsEntity toEntity(final ExplosivVaraDetails details, final String errandId) {
		return ofNullable(details)
			.map(d -> ExplosivVaraDetailsEntity.create()
				.withErrandId(errandId)
				.withTypAvHantering(d.getTypAvHantering())
				.withProxy(d.isProxy())
				.withFastighetsbeteckning(d.getFastighetsbeteckning())
				.withHandlingLocationAddress(d.getHandlingLocationAddress())
				.withHandlingLocationZipCode(d.getHandlingLocationZipCode())
				.withHandlingLocationCity(d.getHandlingLocationCity()))
			.orElse(null);
	}

	/**
	 * PATCH semantics: only non-null fields on {@code patch} are applied to {@code target}.
	 */
	public static ExplosivVaraDetailsEntity applyPatch(final ExplosivVaraDetailsEntity target, final ExplosivVaraDetails patch) {
		if (target == null || patch == null) {
			return target;
		}
		ofNullable(patch.getTypAvHantering()).ifPresent(target::setTypAvHantering);
		target.setProxy(patch.isProxy());
		ofNullable(patch.getFastighetsbeteckning()).ifPresent(target::setFastighetsbeteckning);
		ofNullable(patch.getHandlingLocationAddress()).ifPresent(target::setHandlingLocationAddress);
		ofNullable(patch.getHandlingLocationZipCode()).ifPresent(target::setHandlingLocationZipCode);
		ofNullable(patch.getHandlingLocationCity()).ifPresent(target::setHandlingLocationCity);
		return target;
	}
}
