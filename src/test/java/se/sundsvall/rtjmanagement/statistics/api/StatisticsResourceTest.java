package se.sundsvall.rtjmanagement.statistics.api;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.rtjmanagement.Application;
import se.sundsvall.rtjmanagement.statistics.api.model.StatisticsResponse;
import se.sundsvall.rtjmanagement.statistics.api.model.StatusCount;
import se.sundsvall.rtjmanagement.statistics.service.StatisticsService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class StatisticsResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "EGENSOTNING";
	private static final String PATH = "/{municipalityId}/{namespace}/statistics";

	@MockitoBean
	private StatisticsService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void getStatistics() {
		when(serviceMock.compute(eq(MUNICIPALITY_ID), eq(NAMESPACE), any(), any(), any()))
			.thenReturn(new StatisticsResponse(1, List.of(new StatusCount("DECIDED", 1)), List.of(), 0, 1, 7200L));

		webTestClient.get()
			.uri(builder -> builder.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE)))
			.exchange()
			.expectStatus().isOk()
			.expectBody()
			.jsonPath("$.total").isEqualTo(1)
			.jsonPath("$.decidedCount").isEqualTo(1)
			.jsonPath("$.averageHandlaggningstidSeconds").isEqualTo(7200);

		verify(serviceMock).compute(eq(MUNICIPALITY_ID), eq(NAMESPACE), any(), any(), any());
	}

	@Test
	void getStatisticsForwardsFilters() {
		when(serviceMock.compute(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq("EGENSOTNING"), any(), any()))
			.thenReturn(new StatisticsResponse(0, List.of(), List.of(), 0, 0, null));

		webTestClient.get()
			.uri(builder -> builder.path(PATH)
				.queryParam("typeSlug", "EGENSOTNING")
				.queryParam("from", "2026-01-01T00:00:00Z")
				.queryParam("to", "2026-12-31T23:59:59Z")
				.build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE)))
			.exchange()
			.expectStatus().isOk();

		verify(serviceMock).compute(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq("EGENSOTNING"),
			eq(OffsetDateTime.parse("2026-01-01T00:00:00Z")), eq(OffsetDateTime.parse("2026-12-31T23:59:59Z")));
	}
}
