package se.sundsvall.rtjmanagement.remiss.service.mapper;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.remiss.api.model.Remiss;
import se.sundsvall.rtjmanagement.remiss.integration.db.model.RemissEntity;

import static org.assertj.core.api.Assertions.assertThat;

class RemissMapperTest {

	@Test
	void toRemissMapsAllFields() {
		final var entity = RemissEntity.create()
			.withId("id")
			.withErrandId("errand-1")
			.withInstans("MILJOKONTOR")
			.withRecipient("Miljökontoret Sundsvall")
			.withSentAt(LocalDate.of(2026, 6, 3))
			.withDueAt(LocalDate.of(2026, 7, 1))
			.withResponseText("svar")
			.withStatus("SENT")
			.withCreated(OffsetDateTime.now())
			.withModified(OffsetDateTime.now());

		final var remiss = RemissMapper.toRemiss(entity);

		assertThat(remiss.getId()).isEqualTo("id");
		assertThat(remiss.getInstans()).isEqualTo("MILJOKONTOR");
		assertThat(remiss.getRecipient()).isEqualTo("Miljökontoret Sundsvall");
		assertThat(remiss.getSentAt()).isEqualTo(LocalDate.of(2026, 6, 3));
		assertThat(remiss.getDueAt()).isEqualTo(LocalDate.of(2026, 7, 1));
		assertThat(remiss.getResponseText()).isEqualTo("svar");
		assertThat(remiss.getStatus()).isEqualTo("SENT");
		assertThat(remiss.getCreated()).isEqualTo(entity.getCreated());
		assertThat(remiss.getModified()).isEqualTo(entity.getModified());
	}

	@Test
	void toRemissEntityMapsAllFieldsAndErrandId() {
		final var remiss = Remiss.create()
			.withInstans("POLIS")
			.withRecipient("Polismyndigheten")
			.withSentAt(LocalDate.of(2026, 6, 3))
			.withDueAt(LocalDate.of(2026, 7, 1))
			.withResponseText("svar")
			.withStatus("SENT");

		final var entity = RemissMapper.toRemissEntity(remiss, "errand-9");

		assertThat(entity.getErrandId()).isEqualTo("errand-9");
		assertThat(entity.getInstans()).isEqualTo("POLIS");
		assertThat(entity.getRecipient()).isEqualTo("Polismyndigheten");
		assertThat(entity.getSentAt()).isEqualTo(LocalDate.of(2026, 6, 3));
		assertThat(entity.getDueAt()).isEqualTo(LocalDate.of(2026, 7, 1));
		assertThat(entity.getStatus()).isEqualTo("SENT");
	}

	@Test
	void nullsReturnNull() {
		assertThat(RemissMapper.toRemiss(null)).isNull();
		assertThat(RemissMapper.toRemissEntity(null, "errand-1")).isNull();
	}

	@Test
	void toRemissListMapsAllAndNullToEmpty() {
		assertThat(RemissMapper.toRemissList(null)).isEmpty();
		assertThat(RemissMapper.toRemissList(List.of(RemissEntity.create().withId("a"), RemissEntity.create().withId("b"))))
			.extracting(Remiss::getId).containsExactly("a", "b");
	}
}
