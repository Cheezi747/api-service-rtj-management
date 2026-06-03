package se.sundsvall.rtjmanagement.remiss.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Body when registering a svar on a remiss — just the fritextsvar. The remiss status flips to
 * RESPONDED in the service.
 */
@Schema(description = "Svar på en remiss.")
public record RemissResponseRequest(

	@Schema(description = "Svar på remissen", examples = "Miljökontoret har inget att invända.") @NotBlank @Size(max = 4096) String responseText) {
}
