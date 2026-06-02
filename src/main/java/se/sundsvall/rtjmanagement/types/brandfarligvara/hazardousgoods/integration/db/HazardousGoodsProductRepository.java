package se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.rtjmanagement.types.brandfarligvara.hazardousgoods.integration.db.model.HazardousGoodsProductEntity;

@CircuitBreaker(name = "hazardousGoodsProductRepository")
public interface HazardousGoodsProductRepository extends JpaRepository<HazardousGoodsProductEntity, String> {

	List<HazardousGoodsProductEntity> findByErrandIdOrderByCategoryAscProductNameAsc(String errandId);

	List<HazardousGoodsProductEntity> findByErrandIdAndCategoryOrderByProductNameAsc(String errandId, String category);

	Optional<HazardousGoodsProductEntity> findByErrandIdAndId(String errandId, String id);

	long deleteByErrandId(String errandId);
}
