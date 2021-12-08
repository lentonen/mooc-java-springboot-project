package projekti;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 *
 * @author Henri
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account extends AbstractPersistable<Long> {
    
    private String username;
    private String nickname;
    private String password;
    private String urlAddress;
    private Long profilePictureId;
    
    @OneToMany(mappedBy="to")
    private List<Followers> followers = new ArrayList<>();
    
    @OneToMany(mappedBy="from")
    private List<Followers> following;
    
    


    
    Account (String username, String realname, String password, String urlAddress) {
        this.setUsername(username);
        this.setNickname(realname);
        this.setPassword(password);
        this.setUrlAddress(urlAddress);
    }
    
}
