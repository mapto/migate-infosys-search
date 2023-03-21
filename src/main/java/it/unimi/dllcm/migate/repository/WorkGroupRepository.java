package it.unimi.dllcm.migate.repository;

import it.unimi.dllcm.migate.domain.WorkGroup;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WorkGroup entity.
 */
@Repository
public interface WorkGroupRepository extends JpaRepository<WorkGroup, Long>, JpaSpecificationExecutor<WorkGroup> {
    default Optional<WorkGroup> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<WorkGroup> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<WorkGroup> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct workGroup from WorkGroup workGroup left join fetch workGroup.work",
        countQuery = "select count(distinct workGroup) from WorkGroup workGroup"
    )
    Page<WorkGroup> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct workGroup from WorkGroup workGroup left join fetch workGroup.work")
    List<WorkGroup> findAllWithToOneRelationships();

    @Query("select workGroup from WorkGroup workGroup left join fetch workGroup.work where workGroup.id =:id")
    Optional<WorkGroup> findOneWithToOneRelationships(@Param("id") Long id);
}
