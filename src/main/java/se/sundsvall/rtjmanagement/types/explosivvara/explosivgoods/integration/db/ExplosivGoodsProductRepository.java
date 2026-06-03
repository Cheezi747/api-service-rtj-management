package se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.integration.db;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.rtjmanagement.types.explosivvara.explosivgoods.integration.db.model.ExplosivGoodsProductEntity;

@CircuitBreaker(name = "explosivGoodsProductRepository")
public interface ExplosivGoodsProductRepository extends JpaRepository<ExplosivGoodsProductEntity, String> {

	List<ExplosivGoodsProductEntity> findByErrandIdOrderByHazardClassAscProductNameAsc(String errandId);

	List<ExplosivGoodsProductEntity> findByErrandIdAndHazardClassOrderByProductNameAsc(String errandId, String hazardClass);

	Optional<ExplosivGoodsProductEntity> findByErrandIdAndId(String errandId, String id);

	long deleteByErrandId(String errandId);
}
