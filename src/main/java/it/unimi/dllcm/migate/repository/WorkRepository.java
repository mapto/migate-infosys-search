package it.unimi.dllcm.migate.repository;

import it.unimi.dllcm.migate.domain.Work;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Work entity.
 */
@Repository
public interface WorkRepository extends JpaRepository<Work, Long>, JpaSpecificationExecutor<Work> {
    default Optional<Work> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Work> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Work> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct work from Work work left join fetch work.collection left join fetch work.responsibility",
        countQuery = "select count(distinct work) from Work work"
    )
    Page<Work> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct work from Work work left join fetch work.collection left join fetch work.responsibility")
    List<Work> findAllWithToOneRelationships();

    @Query("select work from Work work left join fetch work.collection left join fetch work.responsibility where work.id =:id")
    Optional<Work> findOneWithToOneRelationships(@Param("id") Long id);
}
