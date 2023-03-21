package it.unimi.dllcm.migate.service;

import it.unimi.dllcm.migate.domain.*; // for static metamodels
import it.unimi.dllcm.migate.domain.WorkGroup;
import it.unimi.dllcm.migate.repository.WorkGroupRepository;
import it.unimi.dllcm.migate.repository.search.WorkGroupSearchRepository;
import it.unimi.dllcm.migate.service.criteria.WorkGroupCriteria;
import it.unimi.dllcm.migate.service.dto.WorkGroupDTO;
import it.unimi.dllcm.migate.service.mapper.WorkGroupMapper;
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
 * Service for executing complex queries for {@link WorkGroup} entities in the database.
 * The main input is a {@link WorkGroupCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link WorkGroupDTO} or a {@link Page} of {@link WorkGroupDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class WorkGroupQueryService extends QueryService<WorkGroup> {

    private final Logger log = LoggerFactory.getLogger(WorkGroupQueryService.class);

    private final WorkGroupRepository workGroupRepository;

    private final WorkGroupMapper workGroupMapper;

    private final WorkGroupSearchRepository workGroupSearchRepository;

    public WorkGroupQueryService(
        WorkGroupRepository workGroupRepository,
        WorkGroupMapper workGroupMapper,
        WorkGroupSearchRepository workGroupSearchRepository
    ) {
        this.workGroupRepository = workGroupRepository;
        this.workGroupMapper = workGroupMapper;
        this.workGroupSearchRepository = workGroupSearchRepository;
    }

    /**
     * Return a {@link List} of {@link WorkGroupDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<WorkGroupDTO> findByCriteria(WorkGroupCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<WorkGroup> specification = createSpecification(criteria);
        return workGroupMapper.toDto(workGroupRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link WorkGroupDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkGroupDTO> findByCriteria(WorkGroupCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<WorkGroup> specification = createSpecification(criteria);
        return workGroupRepository.findAll(specification, page).map(workGroupMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(WorkGroupCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<WorkGroup> specification = createSpecification(criteria);
        return workGroupRepository.count(specification);
    }

    /**
     * Function to convert {@link WorkGroupCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<WorkGroup> createSpecification(WorkGroupCriteria criteria) {
        Specification<WorkGroup> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), WorkGroup_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), WorkGroup_.name));
            }
            if (criteria.getWorkId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getWorkId(), root -> root.join(WorkGroup_.work, JoinType.LEFT).get(Work_.id))
                    );
            }
        }
        return specification;
    }
}
