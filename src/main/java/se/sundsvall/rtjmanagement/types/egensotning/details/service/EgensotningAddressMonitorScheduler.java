package se.sundsvall.rtjmanagement.types.egensotning.details.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import se.sundsvall.dept44.scheduling.Dept44Scheduled;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.citizen.CitizenClient;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.EgensotningDetailsRepository;
import se.sundsvall.rtjmanagement.types.egensotning.details.integration.db.model.EgensotningDetailsEntity;

import static se.sundsvall.rtjmanagement.types.egensotning.configuration.EgensotningModuleConfig.STATUS_DECIDED;

/**
 * Daglig övervakning av adressändringar (R6). För varje godkänt (DECIDED) egensotningsärende körs
 * folkbokföringskontrollen om — har sökanden flyttat och inte längre är folkbokförd på fastigheten
 * återkallas medgivandet automatiskt.
 *
 * Säkerhet före allt: en återkallelse sker <b>endast vid en bekräftad avvikelse</b>. Kan
 * folkbokföringen inte bekräftas (okänt personnummer, citizen-utfall eller -fel) lämnas ärendet
 * orört, så ett tillfälligt integrationsfel aldrig leder till en felaktig återkallelse.
 */
@Component
class EgensotningAddressMonitorScheduler {

	private static final Logger LOG = LoggerFactory.getLogger(EgensotningAddressMonitorScheduler.class);
	private static final String REASON_ADDRESS_CHANGED = "ADDRESS_CHANGED";

	private final EgensotningDetailsRepository detailsRepository;
	private final ErrandRepository errandRepository;
	private final CitizenClient citizenClient;
	private final EgensotningRevocationService revocationService;

	EgensotningAddressMonitorScheduler(final EgensotningDetailsRepository detailsRepository, final ErrandRepository errandRepository,
		final CitizenClient citizenClient, final EgensotningRevocationService revocationService) {
		this.detailsRepository = detailsRepository;
		this.errandRepository = errandRepository;
		this.citizenClient = citizenClient;
		this.revocationService = revocationService;
	}

	@Dept44Scheduled(cron = "${scheduler.egensotning-address-monitor.cron}",
		name = "${scheduler.egensotning-address-monitor.name}",
		lockAtMostFor = "${scheduler.egensotning-address-monitor.shedlock-lock-at-most-for}",
		maximumExecutionTime = "${scheduler.egensotning-address-monitor.maximum-execution-time}")
	void monitorAddressChanges() {
		final var decided = detailsRepository.findByValidFromIsNotNull();
		LOG.info("Monitoring {} issued egensotning decision(s) for address changes", decided.size());

		var revoked = 0;
		for (final var details : decided) {
			if (revokeIfMoved(details)) {
				revoked++;
			}
		}
		LOG.info("Address monitor revoked {} egensotning decision(s)", revoked);
	}

	private boolean revokeIfMoved(final EgensotningDetailsEntity details) {
		try {
			final var errand = errandRepository.findById(details.getErrandId()).orElse(null);
			if (errand == null || !STATUS_DECIDED.equals(errand.getStatus())) {
				return false; // only active (DECIDED) decisions are monitored
			}
			if (stillRegistered(errand.getMunicipalityId(), details)) {
				return false;
			}
			revocationService.revoke(errand.getMunicipalityId(), errand.getNamespace(), errand.getId(), REASON_ADDRESS_CHANGED);
			return true;
		} catch (final RuntimeException e) {
			LOG.error("Failed to evaluate address change for egensotning errand {}", details.getErrandId(), e);
			return false;
		}
	}

	/**
	 * True unless the citizen registry CONFIRMS the applicant is no longer folkbokförd at the property.
	 * Any uncertainty (missing personnummer, blank guid, lookup failure) returns true so the decision is
	 * left untouched — better a missed revocation than a wrongful one.
	 */
	private boolean stillRegistered(final String municipalityId, final EgensotningDetailsEntity details) {
		if (!StringUtils.hasText(details.getPersonnummer())) {
			return true;
		}
		final String personId;
		try {
			personId = citizenClient.getGuid(municipalityId, details.getPersonnummer());
		} catch (final RuntimeException e) {
			LOG.info("Citizen lookup failed for errand {} — leaving decision untouched", details.getErrandId());
			return true;
		}
		if (!StringUtils.hasText(personId)) {
			return true;
		}
		final var citizen = citizenClient.getCitizen(personId);
		return EgensotningCheckUtil.isRegisteredAtProperty(citizen, municipalityId, details.getFastighetsbeteckning());
	}
}
