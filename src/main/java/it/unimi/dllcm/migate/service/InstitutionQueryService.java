package it.unimi.dllcm.migate.service;

import it.unimi.dllcm.migate.domain.*; // for static metamodels
import it.unimi.dllcm.migate.domain.Institution;
import it.unimi.dllcm.migate.repository.InstitutionRepository;
import it.unimi.dllcm.migate.repository.search.InstitutionSearchRepository;
import it.unimi.dllcm.migate.service.criteria.InstitutionCriteria;
import it.unimi.dllcm.migate.service.dto.InstitutionDTO;
import it.unimi.dllcm.migate.service.mapper.InstitutionMapper;
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
 * Service for executing complex queries for {@link Institution} entities in the database.
 * The main input is a {@link InstitutionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link InstitutionDTO} or a {@link Page} of {@link InstitutionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class InstitutionQueryService extends QueryService<Institution> {

    private final Logger log = LoggerFactory.getLogger(InstitutionQueryService.class);

    private final InstitutionRepository institutionRepository;

    private final InstitutionMapper institutionMapper;

    private final InstitutionSearchRepository institutionSearchRepository;

    public InstitutionQueryService(
        InstitutionRepository institutionRepository,
        InstitutionMapper institutionMapper,
        InstitutionSearchRepository institutionSearchRepository
    ) {
        this.institutionRepository = institutionRepository;
        this.institutionMapper = institutionMapper;
        this.institutionSearchRepository = institutionSearchRepository;
    }

    /**
     * Return a {@link List} of {@link InstitutionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<InstitutionDTO> findByCriteria(InstitutionCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Institution> specification = createSpecification(criteria);
        return institutionMapper.toDto(institutionRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link InstitutionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<InstitutionDTO> findByCriteria(InstitutionCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Institution> specification = createSpecification(criteria);
        return institutionRepository.findAll(specification, page).map(institutionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(InstitutionCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Institution> specification = createSpecification(criteria);
        return institutionRepository.count(specification);
    }

    /**
     * Function to convert {@link InstitutionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Institution> createSpecification(InstitutionCriteria criteria) {
        Specification<Institution> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Institution_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Institution_.name));
            }
            if (criteria.getCountry() != null) {
                specification = specification.and(buildSpecification(criteria.getCountry(), Institution_.country));
            }
            if (criteria.getSiteId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getSiteId(), root -> root.join(Institution_.sites, JoinType.LEFT).get(Location_.id))
                    );
            }
            if (criteria.getPositionId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getPositionId(), root -> root.join(Institution_.positions, JoinType.LEFT).get(Role_.id))
                    );
            }
        }
        return specification;
    }
}
