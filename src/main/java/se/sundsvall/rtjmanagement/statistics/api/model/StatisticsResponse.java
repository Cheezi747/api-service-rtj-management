package se.sundsvall.rtjmanagement.statistics.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Aggregerad ärendestatistik för handläggargränssnittet")
public record StatisticsResponse(
	@Schema(description = "Totalt antal ärenden i urvalet", examples = "42") long total,
	@Schema(description = "Antal ärenden per status") List<StatusCount> byStatus,
	@Schema(description = "Antal ärenden per tilldelad handläggare") List<HandlaggareCount> byHandlaggare,
	@Schema(description = "Antal ärenden utan tilldelad handläggare", examples = "7") long unassigned,
	@Schema(description = "Antal beslutade (DECIDED) ärenden i urvalet", examples = "20") long decidedCount,
	@Schema(
		description = "Genomsnittlig aktiv handläggningstid i sekunder för beslutade/avslutade ärenden, exklusive tid då ärendet legat hos sökanden (AWAITING_*). Null om underlag saknas.",
		examples = "86400") Long averageHandlaggningstidSeconds) {
}
