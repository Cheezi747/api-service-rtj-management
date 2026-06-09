package se.sundsvall.rtjmanagement.types.egensotning.details.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Begäran om att återkalla ett godkänt egensotningsmedgivande")
public record RevokeRequest(
	@Schema(description = "Anledning till återkallelsen", examples = "BSK_FAILED") @NotBlank @Size(max = 2048) String reason) {
}
