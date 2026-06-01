package se.sundsvall.rtjmanagement.notifications.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import se.sundsvall.dept44.problem.ThrowableProblem;
import se.sundsvall.rtjmanagement.core.integration.db.ErrandRepository;
import se.sundsvall.rtjmanagement.core.integration.db.model.ErrandEntity;
import se.sundsvall.rtjmanagement.notifications.api.model.Notification;
import se.sundsvall.rtjmanagement.notifications.integration.db.NotificationRepository;
import se.sundsvall.rtjmanagement.notifications.integration.db.model.NotificationEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String ERRAND_ID = "errand-1";
	private static final String NOTIFICATION_ID = "notification-1";

	@Mock
	private ErrandRepository errandRepositoryMock;

	@Mock
	private NotificationRepository notificationRepositoryMock;

	private NotificationService service;

	@BeforeEach
	void setUp() {
		service = new NotificationService(errandRepositoryMock, notificationRepositoryMock, new NotificationProperties(Duration.ofDays(30)));
	}

	private void errandExists() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID))
			.thenReturn(Optional.of(mock(ErrandEntity.class)));
	}

	@Test
	void createMarksSelfCreatedAsAcknowledged() {
		errandExists();
		when(notificationRepositoryMock.save(any())).thenReturn(NotificationEntity.create().withId(NOTIFICATION_ID));

		final var id = service.create(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID,
			Notification.create().withOwnerId("user-1").withCreatedBy("user-1").withType("CREATE").withSubType("ERRAND"));

		assertThat(id).isEqualTo(NOTIFICATION_ID);
		final var captor = ArgumentCaptor.forClass(NotificationEntity.class);
		verify(notificationRepositoryMock).save(captor.capture());
		assertThat(captor.getValue().isAcknowledged()).isTrue();
		assertThat(captor.getValue().getExpires()).isNotNull();
	}

	@Test
	void createDoesNotAcknowledgeWhenCreatedByOther() {
		errandExists();
		when(notificationRepositoryMock.save(any())).thenReturn(NotificationEntity.create().withId(NOTIFICATION_ID));

		service.create(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID,
			Notification.create().withOwnerId("user-1").withCreatedBy("system"));

		final var captor = ArgumentCaptor.forClass(NotificationEntity.class);
		verify(notificationRepositoryMock).save(captor.capture());
		assertThat(captor.getValue().isAcknowledged()).isFalse();
	}

	@Test
	void createThrowsWhenErrandMissing() {
		when(errandRepositoryMock.findByIdAndNamespaceAndMunicipalityId(ERRAND_ID, NAMESPACE, MUNICIPALITY_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.create(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, Notification.create().withOwnerId("u")))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void readReturnsMappedNotification() {
		errandExists();
		when(notificationRepositoryMock.findByIdAndNamespaceAndMunicipalityIdAndErrandId(NOTIFICATION_ID, NAMESPACE, MUNICIPALITY_ID, ERRAND_ID))
			.thenReturn(Optional.of(NotificationEntity.create().withId(NOTIFICATION_ID).withErrandId(ERRAND_ID).withDescription("hello")));

		final var result = service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, NOTIFICATION_ID);

		assertThat(result.getId()).isEqualTo(NOTIFICATION_ID);
		assertThat(result.getDescription()).isEqualTo("hello");
	}

	@Test
	void readThrowsWhenNotificationMissing() {
		errandExists();
		when(notificationRepositoryMock.findByIdAndNamespaceAndMunicipalityIdAndErrandId(NOTIFICATION_ID, NAMESPACE, MUNICIPALITY_ID, ERRAND_ID))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, NOTIFICATION_ID))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", NOT_FOUND);
	}

	@Test
	void readAllByErrandReturnsMappedList() {
		errandExists();
		when(notificationRepositoryMock.findAllByNamespaceAndMunicipalityIdAndErrandId(eq(NAMESPACE), eq(MUNICIPALITY_ID), eq(ERRAND_ID), any(Sort.class)))
			.thenReturn(List.of(NotificationEntity.create().withId("n1"), NotificationEntity.create().withId("n2")));

		final var result = service.readAllByErrand(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, Sort.unsorted());

		assertThat(result).hasSize(2).extracting(Notification::getId).containsExactly("n1", "n2");
	}

	@Test
	void readAllByOwnerReturnsMappedList() {
		when(notificationRepositoryMock.findAllByNamespaceAndMunicipalityIdAndOwnerId(eq(NAMESPACE), eq(MUNICIPALITY_ID), eq("owner-1"), any(Sort.class)))
			.thenReturn(List.of(NotificationEntity.create().withId("n1")));

		final var result = service.readAllByOwner(MUNICIPALITY_ID, NAMESPACE, "owner-1", Sort.unsorted());

		assertThat(result).hasSize(1);
	}

	@Test
	void updateAppliesPatchAndSaves() {
		errandExists();
		final var existing = NotificationEntity.create().withId(NOTIFICATION_ID).withErrandId(ERRAND_ID).withDescription("old");
		when(notificationRepositoryMock.findByIdAndNamespaceAndMunicipalityIdAndErrandId(NOTIFICATION_ID, NAMESPACE, MUNICIPALITY_ID, ERRAND_ID))
			.thenReturn(Optional.of(existing));

		service.update(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, NOTIFICATION_ID, Notification.create().withDescription("new"));

		final var captor = ArgumentCaptor.forClass(NotificationEntity.class);
		verify(notificationRepositoryMock).save(captor.capture());
		assertThat(captor.getValue().getDescription()).isEqualTo("new");
	}

	@Test
	void deleteRemovesNotification() {
		errandExists();
		final var existing = NotificationEntity.create().withId(NOTIFICATION_ID).withErrandId(ERRAND_ID);
		when(notificationRepositoryMock.findByIdAndNamespaceAndMunicipalityIdAndErrandId(NOTIFICATION_ID, NAMESPACE, MUNICIPALITY_ID, ERRAND_ID))
			.thenReturn(Optional.of(existing));

		service.delete(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, NOTIFICATION_ID);

		verify(notificationRepositoryMock).delete(existing);
	}

	@Test
	void acknowledgeAllDelegatesToRepository() {
		errandExists();
		when(notificationRepositoryMock.acknowledgeAllByErrand(NAMESPACE, MUNICIPALITY_ID, ERRAND_ID)).thenReturn(3);

		assertThat(service.acknowledgeAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID)).isEqualTo(3);
	}
}
