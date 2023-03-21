package it.unimi.dllcm.migate.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import it.unimi.dllcm.migate.domain.Institution;
import it.unimi.dllcm.migate.repository.InstitutionRepository;
import it.unimi.dllcm.migate.repository.search.InstitutionSearchRepository;
import it.unimi.dllcm.migate.service.dto.InstitutionDTO;
import it.unimi.dllcm.migate.service.mapper.InstitutionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Institution}.
 */
@Service
@Transactional
public class InstitutionService {

    private final Logger log = LoggerFactory.getLogger(InstitutionService.class);

    private final InstitutionRepository institutionRepository;

    private final InstitutionMapper institutionMapper;

    private final InstitutionSearchRepository institutionSearchRepository;

    public InstitutionService(
        InstitutionRepository institutionRepository,
        InstitutionMapper institutionMapper,
        InstitutionSearchRepository institutionSearchRepository
    ) {
        this.institutionRepository = institutionRepository;
        this.institutionMapper = institutionMapper;
        this.institutionSearchRepository = institutionSearchRepository;
    }

    /**
     * Save a institution.
     *
     * @param institutionDTO the entity to save.
     * @return the persisted entity.
     */
    public InstitutionDTO save(InstitutionDTO institutionDTO) {
        log.debug("Request to save Institution : {}", institutionDTO);
        Institution institution = institutionMapper.toEntity(institutionDTO);
        institution = institutionRepository.save(institution);
        InstitutionDTO result = institutionMapper.toDto(institution);
        institutionSearchRepository.index(institution);
        return result;
    }

    /**
     * Update a institution.
     *
     * @param institutionDTO the entity to save.
     * @return the persisted entity.
     */
    public InstitutionDTO update(InstitutionDTO institutionDTO) {
        log.debug("Request to update Institution : {}", institutionDTO);
        Institution institution = institutionMapper.toEntity(institutionDTO);
        institution = institutionRepository.save(institution);
        InstitutionDTO result = institutionMapper.toDto(institution);
        institutionSearchRepository.index(institution);
        return result;
    }

    /**
     * Partially update a institution.
     *
     * @param institutionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<InstitutionDTO> partialUpdate(InstitutionDTO institutionDTO) {
        log.debug("Request to partially update Institution : {}", institutionDTO);

        return institutionRepository
            .findById(institutionDTO.getId())
            .map(existingInstitution -> {
                institutionMapper.partialUpdate(existingInstitution, institutionDTO);

                return existingInstitution;
            })
            .map(institutionRepository::save)
            .map(savedInstitution -> {
                institutionSearchRepository.save(savedInstitution);

                return savedInstitution;
            })
            .map(institutionMapper::toDto);
    }

    /**
     * Get all the institutions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<InstitutionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Institutions");
        return institutionRepository.findAll(pageable).map(institutionMapper::toDto);
    }

    /**
     * Get all the institutions with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<InstitutionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return institutionRepository.findAllWithEagerRelationships(pageable).map(institutionMapper::toDto);
    }

    /**
     * Get one institution by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InstitutionDTO> findOne(Long id) {
        log.debug("Request to get Institution : {}", id);
        return institutionRepository.findOneWithEagerRelationships(id).map(institutionMapper::toDto);
    }

    /**
     * Delete the institution by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Institution : {}", id);
        institutionRepository.deleteById(id);
        institutionSearchRepository.deleteById(id);
    }

    /**
     * Search for the institution corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<InstitutionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Institutions for query {}", query);
        return institutionSearchRepository.search(query, pageable).map(institutionMapper::toDto);
    }
}
