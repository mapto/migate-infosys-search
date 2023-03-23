package it.unimi.dllcm.migate.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import it.unimi.dllcm.migate.domain.Work;
import it.unimi.dllcm.migate.repository.WorkRepository;
import it.unimi.dllcm.migate.repository.search.WorkSearchRepository;
import it.unimi.dllcm.migate.service.dto.WorkDTO;
import it.unimi.dllcm.migate.service.mapper.WorkMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Work}.
 */
@Service
@Transactional
public class WorkService {

    private final Logger log = LoggerFactory.getLogger(WorkService.class);

    private final WorkRepository workRepository;

    private final WorkMapper workMapper;

    private final WorkSearchRepository workSearchRepository;

    public WorkService(WorkRepository workRepository, WorkMapper workMapper, WorkSearchRepository workSearchRepository) {
        this.workRepository = workRepository;
        this.workMapper = workMapper;
        this.workSearchRepository = workSearchRepository;
    }

    /**
     * Save a work.
     *
     * @param workDTO the entity to save.
     * @return the persisted entity.
     */
    public WorkDTO save(WorkDTO workDTO) {
        log.debug("Request to save Work : {}", workDTO);
        Work work = workMapper.toEntity(workDTO);
        work = workRepository.save(work);
        WorkDTO result = workMapper.toDto(work);
        workSearchRepository.index(work);
        return result;
    }

    /**
     * Update a work.
     *
     * @param workDTO the entity to save.
     * @return the persisted entity.
     */
    public WorkDTO update(WorkDTO workDTO) {
        log.debug("Request to update Work : {}", workDTO);
        Work work = workMapper.toEntity(workDTO);
        work = workRepository.save(work);
        WorkDTO result = workMapper.toDto(work);
        workSearchRepository.index(work);
        return result;
    }

    /**
     * Partially update a work.
     *
     * @param workDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WorkDTO> partialUpdate(WorkDTO workDTO) {
        log.debug("Request to partially update Work : {}", workDTO);

        return workRepository
            .findById(workDTO.getId())
            .map(existingWork -> {
                workMapper.partialUpdate(existingWork, workDTO);

                return existingWork;
            })
            .map(workRepository::save)
            .map(savedWork -> {
                workSearchRepository.save(savedWork);

                return savedWork;
            })
            .map(workMapper::toDto);
    }

    /**
     * Get all the works.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Works");
        return workRepository.findAll(pageable).map(workMapper::toDto);
    }

    /**
     * Get all the works with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<WorkDTO> findAllWithEagerRelationships(Pageable pageable) {
        return workRepository.findAllWithEagerRelationships(pageable).map(workMapper::toDto);
    }

    /**
     * Get one work by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WorkDTO> findOne(Long id) {
        log.debug("Request to get Work : {}", id);
        return workRepository.findOneWithEagerRelationships(id).map(workMapper::toDto);
    }

    /**
     * Delete the work by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Work : {}", id);
        workRepository.deleteById(id);
        workSearchRepository.deleteById(id);
    }

    /**
     * Search for the work corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Works for query {}", query);
        return workSearchRepository.search(query, pageable).map(workMapper::toDto);
    }
}
