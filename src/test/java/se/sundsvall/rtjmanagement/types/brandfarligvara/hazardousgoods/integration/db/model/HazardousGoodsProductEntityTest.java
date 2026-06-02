package se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.integration.db.model;

import com.google.code.beanmatchers.BeanMatchers;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

class HazardousGoodsProductEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
		BeanMatchers.registerValueGenerator(() -> BigDecimal.valueOf(new Random().nextInt(100000)), BigDecimal.class);
	}

	@Test
	void testBean() {
		assertThat(HazardousGoodsProductEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals()));
	}

	@Test
	void testToString() {
		final var entity = HazardousGoodsProductEntity.create().withId("id").withErrandId("e1").withCategory("GAS").withProductName("Propan");
		org.assertj.core.api.Assertions.assertThat(entity.toString())
			.contains("HazardousGoodsProductEntity{").contains("id='id'").contains("errandId='e1'").contains("category='GAS'");
	}

	@Test
	void testBuilderMethods() {
		final var created = now();
		final var modified = now();

		final var entity = HazardousGoodsProductEntity.create()
			.withId("id")
			.withErrandId("errand")
			.withCategory("LIQUID")
			.withProductName("Aspen 4")
			.withQuantity(new BigDecimal("1500.000"))
			.withQuantityUnit("L")
			.withStorageType("CISTERN")
			.withStorageLocation("INDOOR")
			.withFlashPoint(new BigDecimal("23.00"))
			.withCreated(created)
			.withModified(modified);

		assertThat(entity).hasNoNullFieldsOrProperties();
		org.assertj.core.api.Assertions.assertThat(entity.getId()).isEqualTo("id");
		org.assertj.core.api.Assertions.assertThat(entity.getErrandId()).isEqualTo("errand");
		org.assertj.core.api.Assertions.assertThat(entity.getCategory()).isEqualTo("LIQUID");
		org.assertj.core.api.Assertions.assertThat(entity.getProductName()).isEqualTo("Aspen 4");
		org.assertj.core.api.Assertions.assertThat(entity.getQuantity()).isEqualByComparingTo("1500.000");
		org.assertj.core.api.Assertions.assertThat(entity.getQuantityUnit()).isEqualTo("L");
		org.assertj.core.api.Assertions.assertThat(entity.getStorageType()).isEqualTo("CISTERN");
		org.assertj.core.api.Assertions.assertThat(entity.getStorageLocation()).isEqualTo("INDOOR");
		org.assertj.core.api.Assertions.assertThat(entity.getFlashPoint()).isEqualByComparingTo("23.00");
		org.assertj.core.api.Assertions.assertThat(entity.getCreated()).isEqualTo(created);
		org.assertj.core.api.Assertions.assertThat(entity.getModified()).isEqualTo(modified);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		org.assertj.core.api.Assertions.assertThat(HazardousGoodsProductEntity.create()).hasAllNullFieldsOrProperties();
		org.assertj.core.api.Assertions.assertThat(new HazardousGoodsProductEntity()).hasAllNullFieldsOrProperties();
	}
}
