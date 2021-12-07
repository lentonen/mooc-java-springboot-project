package projekti;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);
    Account findByNickname(String nickname);
    Account findByUrlAddress(String url);
    List<Account> findByNicknameContainingIgnoreCase(String realname);
}
