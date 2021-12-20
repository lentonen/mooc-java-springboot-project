package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Henri
 */
public interface MessageRepository  extends JpaRepository<Message, Long> {
    List<Message> findTop25ByAccountIdInAndEntityIdOrderByMessageDateDescMessageTimeDesc (List<Long> accountId, Long entityId);
    List<Message> findByEntityIdInOrderByMessageDateDescMessageTimeDesc (List<Long> entityId);
    List<Message> findByAccountIdAndEntityIdNotNull (Long accountId);
    Message getById(Long id);
}
        
