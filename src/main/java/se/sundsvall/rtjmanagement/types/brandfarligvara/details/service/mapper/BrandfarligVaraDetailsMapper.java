package se.sundsvall.rtjmanagement.types.brandfarligvara.details.service.mapper;

import se.sundsvall.rtjmanagement.types.brandfarligvara.details.api.model.BrandfarligVaraDetails;
import se.sundsvall.rtjmanagement.types.brandfarligvara.details.integration.db.model.BrandfarligVaraDetailsEntity;

import static java.util.Optional.ofNullable;

public final class BrandfarligVaraDetailsMapper {

	private BrandfarligVaraDetailsMapper() {}

	public static BrandfarligVaraDetails toDetails(final BrandfarligVaraDetailsEntity entity) {
		return ofNullable(entity)
			.map(e -> BrandfarligVaraDetails.create()
				.withVerksamhetstyp(e.getVerksamhetstyp())
				.withAnlaggningTyp(e.getAnlaggningTyp())
				.withProxy(e.isProxy())
				.withFastighetsbeteckning(e.getFastighetsbeteckning())
				.withHandlingLocationAddress(e.getHandlingLocationAddress())
				.withHandlingLocationZipCode(e.getHandlingLocationZipCode())
				.withHandlingLocationCity(e.getHandlingLocationCity())
				.withCreated(e.getCreated())
				.withModified(e.getModified()))
			.orElse(null);
	}

	public static BrandfarligVaraDetailsEntity toEntity(final BrandfarligVaraDetails details, final String errandId) {
		return ofNullable(details)
			.map(d -> BrandfarligVaraDetailsEntity.create()
				.withErrandId(errandId)
				.withVerksamhetstyp(d.getVerksamhetstyp())
				.withAnlaggningTyp(d.getAnlaggningTyp())
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
	public static BrandfarligVaraDetailsEntity applyPatch(final BrandfarligVaraDetailsEntity target, final BrandfarligVaraDetails patch) {
		if (target == null || patch == null) {
			return target;
		}
		ofNullable(patch.getVerksamhetstyp()).ifPresent(target::setVerksamhetstyp);
		ofNullable(patch.getAnlaggningTyp()).ifPresent(target::setAnlaggningTyp);
		target.setProxy(patch.isProxy());
		ofNullable(patch.getFastighetsbeteckning()).ifPresent(target::setFastighetsbeteckning);
		ofNullable(patch.getHandlingLocationAddress()).ifPresent(target::setHandlingLocationAddress);
		ofNullable(patch.getHandlingLocationZipCode()).ifPresent(target::setHandlingLocationZipCode);
		ofNullable(patch.getHandlingLocationCity()).ifPresent(target::setHandlingLocationCity);
		return target;
	}
}
