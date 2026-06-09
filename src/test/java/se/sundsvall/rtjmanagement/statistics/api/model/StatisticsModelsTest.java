package se.sundsvall.rtjmanagement.statistics.api.model;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatisticsModelsTest {

	@Test
	void statusCountAccessorsAndValueSemantics() {
		final var count = new StatusCount("DECIDED", 3);

		assertThat(count.status()).isEqualTo("DECIDED");
		assertThat(count.count()).isEqualTo(3);
		assertThat(count).isEqualTo(new StatusCount("DECIDED", 3)).hasSameHashCodeAs(new StatusCount("DECIDED", 3));
		assertThat(count.toString()).contains("DECIDED");
	}

	@Test
	void handlaggareCountAccessorsAndValueSemantics() {
		final var count = new HandlaggareCount("bsk1", 5);

		assertThat(count.handlaggare()).isEqualTo("bsk1");
		assertThat(count.count()).isEqualTo(5);
		assertThat(count).isEqualTo(new HandlaggareCount("bsk1", 5)).hasSameHashCodeAs(new HandlaggareCount("bsk1", 5));
		assertThat(count.toString()).contains("bsk1");
	}

	@Test
	void statisticsResponseExposesAllFields() {
		final var response = new StatisticsResponse(5, List.of(new StatusCount("DECIDED", 2)),
			List.of(new HandlaggareCount("bsk1", 1)), 1, 2, 100L);

		assertThat(response.total()).isEqualTo(5);
		assertThat(response.byStatus()).containsExactly(new StatusCount("DECIDED", 2));
		assertThat(response.byHandlaggare()).containsExactly(new HandlaggareCount("bsk1", 1));
		assertThat(response.unassigned()).isEqualTo(1);
		assertThat(response.decidedCount()).isEqualTo(2);
		assertThat(response.averageHandlaggningstidSeconds()).isEqualTo(100L);
	}
}
