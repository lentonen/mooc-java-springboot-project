package projekti;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.AbstractPersistable;

/**
 *
 * @author Henri
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileObject extends AbstractPersistable<Long> {
    
    //@Lob
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] content;
    
    @ManyToOne
    private Account owner;
    
    private String description;
    
    @OneToMany(mappedBy = "picture")
    private List<PictureLike> likes;
}
