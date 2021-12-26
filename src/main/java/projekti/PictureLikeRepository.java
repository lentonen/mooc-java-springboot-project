package projekti;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Henri
 */
public interface PictureLikeRepository extends JpaRepository<PictureLike, Long> {
    boolean existsByAccountIdAndPictureId(Long accountId, Long messageId);
}
