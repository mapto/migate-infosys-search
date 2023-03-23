package it.unimi.dllcm.migate.repository;

import it.unimi.dllcm.migate.domain.Institution;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Institution entity.
 */
@Repository
public interface InstitutionRepository extends JpaRepository<Institution, Long>, JpaSpecificationExecutor<Institution> {
    default Optional<Institution> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Institution> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Institution> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct institution from Institution institution left join fetch institution.position",
        countQuery = "select count(distinct institution) from Institution institution"
    )
    Page<Institution> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct institution from Institution institution left join fetch institution.position")
    List<Institution> findAllWithToOneRelationships();

    @Query("select institution from Institution institution left join fetch institution.position where institution.id =:id")
    Optional<Institution> findOneWithToOneRelationships(@Param("id") Long id);
}
