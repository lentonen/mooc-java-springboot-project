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
    FileObject findTop1ByIdLessThanAndOwnerIdEqualsOrderByIdDesc(Long id, Long accountId);
    
    // TODO: muuta nämä JPA:ksi
    @Query(value = "SELECT TOP 1 fo.id FROM file_object fo WHERE fo.id < :id AND fo.owner_id = :accountId ORDER BY fo.id DESC", nativeQuery = true)
    Long getPreviousId(@Param("id") Long id, @Param("accountId") Long accountId);
    
    @Query(value = "SELECT TOP 1 fo.id FROM file_object fo WHERE fo.id > :id AND fo.owner_id = :accountId ORDER BY fo.id ASC", nativeQuery = true)
    Long getNextId(@Param("id") Long id, @Param("accountId") Long accountId);
    
    @Query(value = "SELECT COUNT(id) FROM file_object fo WHERE fo.owner_id = :accountId", nativeQuery = true)
    Long getPictureCount(@Param("accountId") Long accountId);
    
    @Query(value = "SELECT COUNT(id) FROM file_object fo WHERE fo.owner_id = :accountId AND fo.id = :id", nativeQuery = true)
    Long countByAccountIdAndPictureId(@Param("accountId") Long accountId, @Param("id") Long id);
}
        
