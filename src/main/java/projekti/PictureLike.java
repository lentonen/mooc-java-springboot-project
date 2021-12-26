package projekti;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
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
public class PictureLike extends AbstractPersistable<Long>{
    
    @ManyToOne
    private Account account;
    
    @ManyToOne
    private FileObject picture;
    
}
