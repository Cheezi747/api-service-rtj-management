package se.sundsvall.rtjmanagement.statushistory.service.event;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.rtjmanagement.core.service.event.ErrandStatusChanged;
import se.sundsvall.rtjmanagement.statushistory.integration.db.StatusHistoryRepository;
import se.sundsvall.rtjmanagement.statushistory.integration.db.model.StatusHistoryEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StatusHistoryListenerTest {

	@Mock
	private StatusHistoryRepository repositoryMock;

	@InjectMocks
	private StatusHistoryListener listener;

	@Test
	void onErrandStatusChangedPersistsHistoryRow() {
		final var timestamp = OffsetDateTime.now();

		listener.on(new ErrandStatusChanged("errand-1", "EGENSOTNING", "2281", "ns", "REGISTERED", "DECIDED", "BSK", timestamp));

		final var captor = ArgumentCaptor.forClass(StatusHistoryEntity.class);
		verify(repositoryMock).save(captor.capture());

		final var saved = captor.getValue();
		assertThat(saved.getErrandId()).isEqualTo("errand-1");
		assertThat(saved.getFromStatus()).isEqualTo("REGISTERED");
		assertThat(saved.getToStatus()).isEqualTo("DECIDED");
		assertThat(saved.getChangedBy()).isEqualTo("BSK");
		assertThat(saved.getChangedAt()).isEqualTo(timestamp);
	}
}
