package se.sundsvall.rtjmanagement.conversation.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import se.sundsvall.dept44.common.validators.annotation.OneOf;

@Schema(description = "Ett nytt meddelande i ärendets konversation")
public record CreateMessage(
	@Schema(description = "Riktning: OUTBOUND = handläggare → sökande, INBOUND = sökande → handläggare", allowableValues = {
		"INBOUND", "OUTBOUND"
	}, examples = "OUTBOUND") @NotBlank @OneOf(value = {
		"INBOUND", "OUTBOUND"
	}) String direction,

	@Schema(description = "Meddelandetext", examples = "Vänligen komplettera med ett giltigt utbildningsintyg.") @NotBlank @Size(max = 8192) String body,

	@Schema(description = "Avsändarens id (handläggarens användar-id eller sökandens identifierare)", examples = "bsk-anders-svensson") @Size(max = 64) String author){
}
