package projekti;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
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
public class Message extends AbstractPersistable<Long> {
    private String content;
    @ManyToOne
    private Account account;

    @OneToMany(mappedBy = "message")
    private List<MessageLike> likes;
    
    LocalDate messageDate = java.time.LocalDate.now();
    LocalTime messageTime = java.time.LocalTime.now();
    
    // kertoo mihin kuvaan tai kommenttiin message liittyy. Tämä on null, jos kyseessä käyttäjän oma seinäviesti.
    // Jos kyseessä on kommetti kuvaan tai seinäviestiin, entityId osoittaa kyseisen entiteetin ID:n tietokannassa.
    private Long entityId;  
}
