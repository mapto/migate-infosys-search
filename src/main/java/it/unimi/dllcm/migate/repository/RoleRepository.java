package it.unimi.dllcm.migate.repository;

import it.unimi.dllcm.migate.domain.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Role entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    default Optional<Role> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Role> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Role> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct role from Role role left join fetch role.sponsor left join fetch role.person left join fetch role.product",
        countQuery = "select count(distinct role) from Role role"
    )
    Page<Role> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct role from Role role left join fetch role.sponsor left join fetch role.person left join fetch role.product")
    List<Role> findAllWithToOneRelationships();

    @Query(
        "select role from Role role left join fetch role.sponsor left join fetch role.person left join fetch role.product where role.id =:id"
    )
    Optional<Role> findOneWithToOneRelationships(@Param("id") Long id);
}
