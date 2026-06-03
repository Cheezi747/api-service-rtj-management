package se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.api.model;

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

class ExplosivGoodsProductTest {

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> OffsetDateTime.now().plusDays(new Random().nextInt(1000)), OffsetDateTime.class);
		registerValueGenerator(() -> BigDecimal.valueOf(new Random().nextInt(100000)), BigDecimal.class);
	}

	@Test
	void testBean() {
		assertThat(ExplosivGoodsProduct.class, allOf(
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

		final var result = ExplosivGoodsProduct.create()
			.withId("id")
			.withHazardClass("1.1")
			.withProductName("Dynamit")
			.withQuantity(new BigDecimal("250.000"))
			.withQuantityUnit("KG")
			.withStorageType("MAGAZINE")
			.withStorageLocation("UNDERGROUND")
			.withCreated(created)
			.withModified(modified);

		assertThat(result).hasNoNullFieldsOrProperties();
		assertThat(result.getId()).isEqualTo("id");
		assertThat(result.getHazardClass()).isEqualTo("1.1");
		assertThat(result.getProductName()).isEqualTo("Dynamit");
		assertThat(result.getQuantity()).isEqualByComparingTo("250.000");
		assertThat(result.getQuantityUnit()).isEqualTo("KG");
		assertThat(result.getStorageType()).isEqualTo("MAGAZINE");
		assertThat(result.getStorageLocation()).isEqualTo("UNDERGROUND");
		assertThat(result.getCreated()).isEqualTo(created);
		assertThat(result.getModified()).isEqualTo(modified);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(ExplosivGoodsProduct.create()).hasAllNullFieldsOrProperties();
	}
}
