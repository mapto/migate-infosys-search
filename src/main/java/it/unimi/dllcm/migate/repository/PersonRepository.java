package it.unimi.dllcm.migate.repository;

import it.unimi.dllcm.migate.domain.Person;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Person entity.
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    default Optional<Person> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Person> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Person> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct person from Person person left join fetch person.job",
        countQuery = "select count(distinct person) from Person person"
    )
    Page<Person> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct person from Person person left join fetch person.job")
    List<Person> findAllWithToOneRelationships();

    @Query("select person from Person person left join fetch person.job where person.id =:id")
    Optional<Person> findOneWithToOneRelationships(@Param("id") Long id);
}
