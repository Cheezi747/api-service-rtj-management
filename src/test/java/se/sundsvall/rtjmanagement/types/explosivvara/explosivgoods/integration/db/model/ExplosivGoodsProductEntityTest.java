package se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.integration.db.model;

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

class ExplosivGoodsProductEntityTest {

	@BeforeAll
	static void setup() {
		BeanMatchers.registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
		BeanMatchers.registerValueGenerator(() -> BigDecimal.valueOf(new Random().nextInt(100000)), BigDecimal.class);
	}

	@Test
	void testBean() {
		assertThat(ExplosivGoodsProductEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals()));
	}

	@Test
	void testToString() {
		final var entity = ExplosivGoodsProductEntity.create().withId("id").withErrandId("e1").withHazardClass("1.1").withProductName("Dynamit");
		org.assertj.core.api.Assertions.assertThat(entity.toString())
			.contains("ExplosivGoodsProductEntity{").contains("id='id'").contains("errandId='e1'").contains("hazardClass='1.1'");
	}

	@Test
	void testBuilderMethods() {
		final var created = now();
		final var modified = now();

		final var entity = ExplosivGoodsProductEntity.create()
			.withId("id")
			.withErrandId("errand")
			.withHazardClass("1.1")
			.withProductName("Dynamit")
			.withQuantity(new BigDecimal("250.000"))
			.withQuantityUnit("KG")
			.withStorageType("MAGAZINE")
			.withStorageLocation("UNDERGROUND")
			.withCreated(created)
			.withModified(modified);

		assertThat(entity).hasNoNullFieldsOrProperties();
		org.assertj.core.api.Assertions.assertThat(entity.getId()).isEqualTo("id");
		org.assertj.core.api.Assertions.assertThat(entity.getErrandId()).isEqualTo("errand");
		org.assertj.core.api.Assertions.assertThat(entity.getHazardClass()).isEqualTo("1.1");
		org.assertj.core.api.Assertions.assertThat(entity.getProductName()).isEqualTo("Dynamit");
		org.assertj.core.api.Assertions.assertThat(entity.getQuantity()).isEqualByComparingTo("250.000");
		org.assertj.core.api.Assertions.assertThat(entity.getQuantityUnit()).isEqualTo("KG");
		org.assertj.core.api.Assertions.assertThat(entity.getStorageType()).isEqualTo("MAGAZINE");
		org.assertj.core.api.Assertions.assertThat(entity.getStorageLocation()).isEqualTo("UNDERGROUND");
		org.assertj.core.api.Assertions.assertThat(entity.getCreated()).isEqualTo(created);
		org.assertj.core.api.Assertions.assertThat(entity.getModified()).isEqualTo(modified);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		org.assertj.core.api.Assertions.assertThat(ExplosivGoodsProductEntity.create()).hasAllNullFieldsOrProperties();
		org.assertj.core.api.Assertions.assertThat(new ExplosivGoodsProductEntity()).hasAllNullFieldsOrProperties();
	}
}
