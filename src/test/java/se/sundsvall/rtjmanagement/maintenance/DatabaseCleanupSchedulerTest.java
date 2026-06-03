package se.sundsvall.rtjmanagement.maintenance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseCleanupSchedulerTest {

	@Mock
	private JdbcTemplate jdbcTemplateMock;

	@Mock
	private Connection connectionMock;

	@Mock
	private Statement statementMock;

	@Mock
	private ResultSet resultSetMock;

	@InjectMocks
	private DatabaseCleanupScheduler scheduler;

	@BeforeEach
	void wireSingleConnection() throws SQLException {
		// Run the ConnectionCallback the scheduler passes to jdbcTemplate against our mocked connection.
		when(jdbcTemplateMock.execute(any(ConnectionCallback.class)))
			.thenAnswer(invocation -> invocation.getArgument(0, ConnectionCallback.class).doInConnection(connectionMock));
		when(connectionMock.createStatement()).thenReturn(statementMock);
		when(statementMock.executeQuery(anyString())).thenReturn(resultSetMock);
	}

	@Test
	void resetDemoData_deletesNonPreservedTablesAndTogglesForeignKeyChecks() throws SQLException {
		// information_schema returns two transactional tables and one preserved table (lookup).
		when(resultSetMock.next()).thenReturn(true, true, true, false);
		when(resultSetMock.getString(1)).thenReturn("errand", "attachment", "lookup");
		when(statementMock.executeUpdate(anyString())).thenReturn(2, 5);

		scheduler.resetDemoData();

		final var inOrder = inOrder(statementMock);
		inOrder.verify(statementMock).execute("SET FOREIGN_KEY_CHECKS = 0");
		inOrder.verify(statementMock).executeUpdate("DELETE FROM `errand`");
		inOrder.verify(statementMock).executeUpdate("DELETE FROM `attachment`");
		inOrder.verify(statementMock).execute("SET FOREIGN_KEY_CHECKS = 1");
		// The preserved table is never deleted.
		verify(statementMock, never()).executeUpdate("DELETE FROM `lookup`");
	}

	@Test
	void resetDemoData_restoresForeignKeyChecksWhenDeleteFails() throws SQLException {
		when(resultSetMock.next()).thenReturn(true, false);
		when(resultSetMock.getString(1)).thenReturn("errand");
		when(statementMock.executeUpdate(anyString())).thenThrow(new SQLException("delete blew up"));

		assertThatThrownBy(() -> scheduler.resetDemoData()).hasMessageContaining("delete blew up");

		// Even on failure, foreign-key checks are restored before the connection returns to the pool.
		verify(statementMock).execute("SET FOREIGN_KEY_CHECKS = 1");
	}
}
