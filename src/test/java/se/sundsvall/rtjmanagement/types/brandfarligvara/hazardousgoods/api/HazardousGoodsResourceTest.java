package se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.api;

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
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.api.model.HazardousGoodsProduct;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.service.HazardousGoodsService;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("junit")
class HazardousGoodsResourceTest {

	private static final String MUNICIPALITY_ID = "2281";
	private static final String NAMESPACE = "brandfarlig-vara";
	private static final String ERRAND_ID = randomUUID().toString();
	private static final String PRODUCT_ID = randomUUID().toString();
	private static final String PATH = "/{municipalityId}/{namespace}/errands/{errandId}/hazardous-goods";

	@MockitoBean
	private HazardousGoodsService serviceMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void createProduct() {
		when(serviceMock.create(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), any(HazardousGoodsProduct.class))).thenReturn(PRODUCT_ID);

		webTestClient.post()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.bodyValue(HazardousGoodsProduct.create().withCategory("GAS").withProductName("Propan"))
			.exchange()
			.expectStatus().isCreated();

		verify(serviceMock).create(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), any(HazardousGoodsProduct.class));
	}

	@Test
	void readProductsWithoutFilter() {
		when(serviceMock.readAll(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), isNull())).thenReturn(List.of(HazardousGoodsProduct.create()));

		final var response = webTestClient.get()
			.uri(uri -> uri.path(PATH).build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(HazardousGoodsProduct.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		verify(serviceMock).readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, null);
	}

	@Test
	void readProductsWithCategoryFilter() {
		when(serviceMock.readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, "GAS")).thenReturn(List.of(HazardousGoodsProduct.create().withCategory("GAS")));

		webTestClient.get()
			.uri(uri -> uri.path(PATH).queryParam("category", "GAS").build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID)))
			.exchange()
			.expectStatus().isOk();

		verify(serviceMock).readAll(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, "GAS");
	}

	@Test
	void readProduct() {
		when(serviceMock.read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PRODUCT_ID)).thenReturn(HazardousGoodsProduct.create().withId(PRODUCT_ID));

		final var response = webTestClient.get()
			.uri(uri -> uri.path(PATH + "/{productId}").build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID, "productId", PRODUCT_ID)))
			.exchange()
			.expectStatus().isOk()
			.expectBody(HazardousGoodsProduct.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		verify(serviceMock).read(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PRODUCT_ID);
	}

	@Test
	void updateProduct() {
		webTestClient.patch()
			.uri(uri -> uri.path(PATH + "/{productId}").build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID, "productId", PRODUCT_ID)))
			.bodyValue(HazardousGoodsProduct.create().withProductName("Metan"))
			.exchange()
			.expectStatus().isNoContent();

		verify(serviceMock).update(eq(MUNICIPALITY_ID), eq(NAMESPACE), eq(ERRAND_ID), eq(PRODUCT_ID), any(HazardousGoodsProduct.class));
	}

	@Test
	void deleteProduct() {
		webTestClient.delete()
			.uri(uri -> uri.path(PATH + "/{productId}").build(Map.of("municipalityId", MUNICIPALITY_ID, "namespace", NAMESPACE, "errandId", ERRAND_ID, "productId", PRODUCT_ID)))
			.exchange()
			.expectStatus().isNoContent();

		verify(serviceMock).delete(MUNICIPALITY_ID, NAMESPACE, ERRAND_ID, PRODUCT_ID);
	}
}
