package it.unimi.dllcm.migate.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import it.unimi.dllcm.migate.domain.WorkGroup;
import it.unimi.dllcm.migate.repository.WorkGroupRepository;
import it.unimi.dllcm.migate.repository.search.WorkGroupSearchRepository;
import it.unimi.dllcm.migate.service.dto.WorkGroupDTO;
import it.unimi.dllcm.migate.service.mapper.WorkGroupMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link WorkGroup}.
 */
@Service
@Transactional
public class WorkGroupService {

    private final Logger log = LoggerFactory.getLogger(WorkGroupService.class);

    private final WorkGroupRepository workGroupRepository;

    private final WorkGroupMapper workGroupMapper;

    private final WorkGroupSearchRepository workGroupSearchRepository;

    public WorkGroupService(
        WorkGroupRepository workGroupRepository,
        WorkGroupMapper workGroupMapper,
        WorkGroupSearchRepository workGroupSearchRepository
    ) {
        this.workGroupRepository = workGroupRepository;
        this.workGroupMapper = workGroupMapper;
        this.workGroupSearchRepository = workGroupSearchRepository;
    }

    /**
     * Save a workGroup.
     *
     * @param workGroupDTO the entity to save.
     * @return the persisted entity.
     */
    public WorkGroupDTO save(WorkGroupDTO workGroupDTO) {
        log.debug("Request to save WorkGroup : {}", workGroupDTO);
        WorkGroup workGroup = workGroupMapper.toEntity(workGroupDTO);
        workGroup = workGroupRepository.save(workGroup);
        WorkGroupDTO result = workGroupMapper.toDto(workGroup);
        workGroupSearchRepository.index(workGroup);
        return result;
    }

    /**
     * Update a workGroup.
     *
     * @param workGroupDTO the entity to save.
     * @return the persisted entity.
     */
    public WorkGroupDTO update(WorkGroupDTO workGroupDTO) {
        log.debug("Request to update WorkGroup : {}", workGroupDTO);
        WorkGroup workGroup = workGroupMapper.toEntity(workGroupDTO);
        workGroup = workGroupRepository.save(workGroup);
        WorkGroupDTO result = workGroupMapper.toDto(workGroup);
        workGroupSearchRepository.index(workGroup);
        return result;
    }

    /**
     * Partially update a workGroup.
     *
     * @param workGroupDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<WorkGroupDTO> partialUpdate(WorkGroupDTO workGroupDTO) {
        log.debug("Request to partially update WorkGroup : {}", workGroupDTO);

        return workGroupRepository
            .findById(workGroupDTO.getId())
            .map(existingWorkGroup -> {
                workGroupMapper.partialUpdate(existingWorkGroup, workGroupDTO);

                return existingWorkGroup;
            })
            .map(workGroupRepository::save)
            .map(savedWorkGroup -> {
                workGroupSearchRepository.save(savedWorkGroup);

                return savedWorkGroup;
            })
            .map(workGroupMapper::toDto);
    }

    /**
     * Get all the workGroups.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkGroupDTO> findAll(Pageable pageable) {
        log.debug("Request to get all WorkGroups");
        return workGroupRepository.findAll(pageable).map(workGroupMapper::toDto);
    }

    /**
     * Get one workGroup by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<WorkGroupDTO> findOne(Long id) {
        log.debug("Request to get WorkGroup : {}", id);
        return workGroupRepository.findById(id).map(workGroupMapper::toDto);
    }

    /**
     * Delete the workGroup by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete WorkGroup : {}", id);
        workGroupRepository.deleteById(id);
        workGroupSearchRepository.deleteById(id);
    }

    /**
     * Search for the workGroup corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<WorkGroupDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of WorkGroups for query {}", query);
        return workGroupSearchRepository.search(query, pageable).map(workGroupMapper::toDto);
    }
}
