package se.sundsvall.rtjmanagement.statistics.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Antal ärenden i en given status")
public record StatusCount(
	@Schema(description = "Status", examples = "DECIDED") String status,
	@Schema(description = "Antal ärenden i statusen", examples = "12") long count) {
}
