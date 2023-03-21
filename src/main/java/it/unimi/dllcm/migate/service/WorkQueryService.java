package it.unimi.dllcm.migate.service;

import it.unimi.dllcm.migate.domain.*; // for static metamodels
import it.unimi.dllcm.migate.domain.Work;
import it.unimi.dllcm.migate.repository.WorkRepository;
import it.unimi.dllcm.migate.repository.search.WorkSearchRepository;
import it.unimi.dllcm.migate.service.criteria.WorkCriteria;
import it.unimi.dllcm.migate.service.dto.WorkDTO;
import it.unimi.dllcm.migate.service.mapper.WorkMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Work} entities in the database.
 * The main input is a {@link WorkCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link WorkDTO} or a {@link Page} of {@link WorkDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WorkQueryService extends QueryService<Work> {

    private final Logger log = LoggerFactory.getLogger(WorkQueryService.class);

    private final WorkRepository workRepository;

    private final WorkMapper workMapper;

    private final WorkSearchRepository workSearchRepository;

    public WorkQueryService(WorkRepository workRepository, WorkMapper workMapper, WorkSearchRepository workSearchRepository) {
        this.workRepository = workRepository;
        this.workMapper = workMapper;
        this.workSearchRepository = workSearchRepository;
    }

    /**
     * Return a {@link List} of {@link WorkDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<WorkDTO> findByCriteria(WorkCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Work> specification = createSpecification(criteria);
        return workMapper.toDto(workRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link WorkDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkDTO> findByCriteria(WorkCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Work> specification = createSpecification(criteria);
        return workRepository.findAll(specification, page).map(workMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WorkCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Work> specification = createSpecification(criteria);
        return workRepository.count(specification);
    }

    /**
     * Function to convert {@link WorkCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Work> createSpecification(WorkCriteria criteria) {
        Specification<Work> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Work_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Work_.name));
            }
            if (criteria.getPublished() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPublished(), Work_.published));
            }
            if (criteria.getSponsorId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getSponsorId(), root -> root.join(Work_.sponsors, JoinType.LEFT).get(Institution_.id))
                    );
            }
            if (criteria.getWorkRoleNameId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getWorkRoleNameId(),
                            root -> root.join(Work_.workRoleNames, JoinType.LEFT).get(Role_.id)
                        )
                    );
            }
            if (criteria.getCollectionId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCollectionId(),
                            root -> root.join(Work_.collections, JoinType.LEFT).get(WorkGroup_.id)
                        )
                    );
            }
            if (criteria.getResponsibleId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getResponsibleId(),
                            root -> root.join(Work_.responsibles, JoinType.LEFT).get(Person_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
