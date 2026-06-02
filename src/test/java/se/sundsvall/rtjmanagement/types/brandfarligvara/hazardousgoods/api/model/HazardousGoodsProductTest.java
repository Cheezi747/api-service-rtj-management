package se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.api.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

class HazardousGoodsProductTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt(1000)), OffsetDateTime.class);
		registerValueGenerator(() -> BigDecimal.valueOf(new Random().nextInt(100000)), BigDecimal.class);
	}

	@Test
	void testBean() {
		assertThat(HazardousGoodsProduct.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testBuilderMethods() {
		final var created = OffsetDateTime.now();
		final var modified = OffsetDateTime.now();

		final var result = HazardousGoodsProduct.create()
			.withId("id")
			.withCategory("LIQUID")
			.withProductName("Aspen 4")
			.withQuantity(new BigDecimal("1500.000"))
			.withQuantityUnit("L")
			.withStorageType("CISTERN")
			.withStorageLocation("INDOOR")
			.withFlashPoint(new BigDecimal("23.00"))
			.withCreated(created)
			.withModified(modified);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo("id");
		assertThat(result.getCategory()).isEqualTo("LIQUID");
		assertThat(result.getProductName()).isEqualTo("Aspen 4");
		assertThat(result.getQuantity()).isEqualByComparingTo("1500.000");
		assertThat(result.getQuantityUnit()).isEqualTo("L");
		assertThat(result.getStorageType()).isEqualTo("CISTERN");
		assertThat(result.getStorageLocation()).isEqualTo("INDOOR");
		assertThat(result.getFlashPoint()).isEqualByComparingTo("23.00");
		assertThat(result.getCreated()).isEqualTo(created);
		assertThat(result.getModified()).isEqualTo(modified);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(HazardousGoodsProduct.create()).hasAllNullFieldsOrProperties();
	}
}
