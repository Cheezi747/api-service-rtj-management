package se.sundsvall.rtjmanagement.notifications.service.mapper;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import se.sundsvall.rtjmanagement.notifications.api.model.Notification;
import se.sundsvall.rtjmanagement.notifications.integration.db.model.NotificationEntity;
import se.sundsvall.rtjmanagement.notifications.integration.db.model.NotificationSubType;
import se.sundsvall.rtjmanagement.notifications.integration.db.model.NotificationType;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationMapperTest {

	@Test
	void toEntityMapsAllFields() {
		final var expires = OffsetDateTime.now();
		final var result = NotificationMapper.toEntity(Notification.create()
			.withOwnerId("owner")
			.withCreatedBy("creator")
			.withType("CREATE")
			.withSubType("ERRAND")
			.withDescription("desc")
			.withContent("content")
			.withAcknowledged(true), "2281", "ns", "errand-1", expires);

		assertThat(result).isNotNull();
		assertThat(result.getErrandId()).isEqualTo("errand-1");
		assertThat(result.getMunicipalityId()).isEqualTo("2281");
		assertThat(result.getNamespace()).isEqualTo("ns");
		assertThat(result.getOwnerId()).isEqualTo("owner");
		assertThat(result.getCreatedBy()).isEqualTo("creator");
		assertThat(result.getType()).isEqualTo(NotificationType.CREATE);
		assertThat(result.getSubType()).isEqualTo(NotificationSubType.ERRAND);
		assertThat(result.getDescription()).isEqualTo("desc");
		assertThat(result.getContent()).isEqualTo("content");
		assertThat(result.isAcknowledged()).isTrue();
		assertThat(result.getExpires()).isEqualTo(expires);
	}

	@Test
	void toEntityHandlesNullTypeAndAcknowledged() {
		final var result = NotificationMapper.toEntity(Notification.create().withOwnerId("o"), "2281", "ns", "e", null);

		assertThat(result).isNotNull();
		assertThat(result.getType()).isNull();
		assertThat(result.getSubType()).isNull();
		assertThat(result.isAcknowledged()).isFalse();
	}

	@Test
	void toEntityNullReturnsNull() {
		assertThat(NotificationMapper.toEntity(null, "2281", "ns", "e", null)).isNull();
	}

	@Test
	void toDtoMapsAllFields() {
		final var expires = OffsetDateTime.now().plusDays(1);
		final var result = NotificationMapper.toDto(NotificationEntity.create()
			.withId("n1")
			.withErrandId("e1")
			.withOwnerId("o")
			.withCreatedBy("c")
			.withType(NotificationType.UPDATE)
			.withSubType(NotificationSubType.DECISION)
			.withDescription("d")
			.withContent("co")
			.withAcknowledged(true)
			.withExpires(expires));

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo("n1");
		assertThat(result.getErrandId()).isEqualTo("e1");
		assertThat(result.getOwnerId()).isEqualTo("o");
		assertThat(result.getCreatedBy()).isEqualTo("c");
		assertThat(result.getType()).isEqualTo("UPDATE");
		assertThat(result.getSubType()).isEqualTo("DECISION");
		assertThat(result.getDescription()).isEqualTo("d");
		assertThat(result.getContent()).isEqualTo("co");
		assertThat(result.getAcknowledged()).isTrue();
		assertThat(result.getExpires()).isEqualTo(expires);
	}

	@Test
	void toDtoHandlesNullTypeAndSubType() {
		final var result = NotificationMapper.toDto(NotificationEntity.create().withId("n1"));

		assertThat(result).isNotNull();
		assertThat(result.getType()).isNull();
		assertThat(result.getSubType()).isNull();
	}

	@Test
	void toDtoNullReturnsNull() {
		assertThat(NotificationMapper.toDto(null)).isNull();
	}

	@Test
	void applyPatchUpdatesOnlyNonNullFields() {
		final var entity = NotificationEntity.create().withType(NotificationType.CREATE).withDescription("old").withContent("oldContent");

		NotificationMapper.applyPatch(entity, Notification.create()
			.withType("DELETE")
			.withSubType("SYSTEM")
			.withContent("newContent")
			.withAcknowledged(true));

		assertThat(entity.getType()).isEqualTo(NotificationType.DELETE);
		assertThat(entity.getSubType()).isEqualTo(NotificationSubType.SYSTEM);
		assertThat(entity.getDescription()).isEqualTo("old");
		assertThat(entity.getContent()).isEqualTo("newContent");
		assertThat(entity.isAcknowledged()).isTrue();
	}

	@Test
	void applyPatchNullTargetOrPatchIsNoop() {
		NotificationMapper.applyPatch(null, Notification.create());

		final var entity = NotificationEntity.create().withDescription("keep");
		NotificationMapper.applyPatch(entity, null);

		assertThat(entity.getDescription()).isEqualTo("keep");
	}
}
