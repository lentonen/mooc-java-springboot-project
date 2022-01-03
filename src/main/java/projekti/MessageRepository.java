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
    List<Message> findTop10ByEntityIdInOrderByMessageDateDescMessageTimeDesc (List<Long> entityId);
    List<Message> findTop10ByEntityIdOrderByMessageDateAscMessageTimeAsc (Long entityId);
    List<Message> findByEntityIdIn (List<Long> accountId);
    List<Message> findByAccountIdInAndEntityIdNull (List<Long> accountId); 
    Message getById(Long id);
}
        
