package se.sundsvall.rtjmanagement.shared;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DecisionRecordedTest {

	@Test
	void accessors() {
		final var timestamp = OffsetDateTime.now();
		final var event = new DecisionRecorded("decision-1", "errand-1", "EGENSOTNING",
			"APPROVED", "Ansökan godkänd.", "operaton", timestamp);

		assertThat(event.decisionId()).isEqualTo("decision-1");
		assertThat(event.errandId()).isEqualTo("errand-1");
		assertThat(event.typeSlug()).isEqualTo("EGENSOTNING");
		assertThat(event.outcome()).isEqualTo("APPROVED");
		assertThat(event.description()).isEqualTo("Ansökan godkänd.");
		assertThat(event.decidedBy()).isEqualTo("operaton");
		assertThat(event.timestamp()).isEqualTo(timestamp);
	}
}
