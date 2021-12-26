package projekti;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Henri
 */
public interface MessageLikeRepository extends JpaRepository<MessageLike, Long> {
    boolean existsByAccountIdAndMessageId(Long accountId, Long messageId);
}
