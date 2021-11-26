package projekti;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowersRepository extends JpaRepository<Followers, Long> {
    ArrayList<Followers> findByFromId(Long id);
    Followers findByFromIdAndToId(Long from, Long to);
}
