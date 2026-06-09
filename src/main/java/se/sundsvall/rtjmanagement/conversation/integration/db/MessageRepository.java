package se.sundsvall.rtjmanagement.conversation.integration.db;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.rtjmanagement.conversation.integration.db.model.MessageEntity;

public interface MessageRepository extends JpaRepository<MessageEntity, String> {

	List<MessageEntity> findByErrandIdOrderByCreatedAsc(String errandId);
}
