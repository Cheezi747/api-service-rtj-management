package se.sundsvall.rtjmanagement.statistics.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Antal ärenden tilldelade en given handläggare")
public record HandlaggareCount(
	@Schema(description = "Handläggarens användar-id", examples = "bsk-anders-svensson") String handlaggare,
	@Schema(description = "Antal ärenden tilldelade handläggaren", examples = "5") long count) {
}
