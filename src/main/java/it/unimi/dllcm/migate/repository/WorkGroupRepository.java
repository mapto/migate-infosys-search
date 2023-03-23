package it.unimi.dllcm.migate.repository;

import it.unimi.dllcm.migate.domain.WorkGroup;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WorkGroup entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WorkGroupRepository extends JpaRepository<WorkGroup, Long>, JpaSpecificationExecutor<WorkGroup> {}
