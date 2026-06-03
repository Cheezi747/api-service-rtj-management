package se.sundsvall.rtjmanagement.permit.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.rtjmanagement.core.service.event.ErrandDeleted;
import se.sundsvall.rtjmanagement.permit.integration.db.PermitRepository;

import static java.time.OffsetDateTime.now;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PermitErrandDeletedListenerTest {

	@Mock
	private PermitRepository repositoryMock;

	@InjectMocks
	private PermitErrandDeletedListener listener;

	@Test
	void deletesPermitsForErrand() {
		listener.on(new ErrandDeleted("errand-1", "BRANDFARLIG_VARA", "2281", "NS", null, now()));

		verify(repositoryMock).deleteByErrandId("errand-1");
	}
}
