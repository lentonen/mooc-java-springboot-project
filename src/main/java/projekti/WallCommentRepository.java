package projekti;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Henri
 */
public interface WallCommentRepository  extends JpaRepository<WallComment, Long> {

    
}
        