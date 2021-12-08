package projekti;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Henri
 */
public interface PictureRepository  extends JpaRepository<FileObject, Long> {
//
    @Query(value = "SELECT TOP 1 fo.id FROM file_object fo WHERE fo.id < :id ORDER BY fo.id DESC", nativeQuery = true)
    Long getPreviousId(@Param("id") Long id);
    
    @Query(value = "SELECT TOP 1 fo.id FROM file_object fo WHERE fo.id > :id ORDER BY fo.id ASC", nativeQuery = true)
    Long getNextId(@Param("id") Long id);
}
        
