package it.unimi.dllcm.migate.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import it.unimi.dllcm.migate.repository.WorkGroupRepository;
import it.unimi.dllcm.migate.service.WorkGroupQueryService;
import it.unimi.dllcm.migate.service.WorkGroupService;
import it.unimi.dllcm.migate.service.criteria.WorkGroupCriteria;
import it.unimi.dllcm.migate.service.dto.WorkGroupDTO;
import it.unimi.dllcm.migate.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link it.unimi.dllcm.migate.domain.WorkGroup}.
 */
@RestController
@RequestMapping("/api")
public class WorkGroupResource {

    private final Logger log = LoggerFactory.getLogger(WorkGroupResource.class);

    private static final String ENTITY_NAME = "workGroup";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkGroupService workGroupService;

    private final WorkGroupRepository workGroupRepository;

    private final WorkGroupQueryService workGroupQueryService;

    public WorkGroupResource(
        WorkGroupService workGroupService,
        WorkGroupRepository workGroupRepository,
        WorkGroupQueryService workGroupQueryService
    ) {
        this.workGroupService = workGroupService;
        this.workGroupRepository = workGroupRepository;
        this.workGroupQueryService = workGroupQueryService;
    }

    /**
     * {@code POST  /work-groups} : Create a new workGroup.
     *
     * @param workGroupDTO the workGroupDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workGroupDTO, or with status {@code 400 (Bad Request)} if the workGroup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/work-groups")
    public ResponseEntity<WorkGroupDTO> createWorkGroup(@Valid @RequestBody WorkGroupDTO workGroupDTO) throws URISyntaxException {
        log.debug("REST request to save WorkGroup : {}", workGroupDTO);
        if (workGroupDTO.getId() != null) {
            throw new BadRequestAlertException("A new workGroup cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WorkGroupDTO result = workGroupService.save(workGroupDTO);
        return ResponseEntity
            .created(new URI("/api/work-groups/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /work-groups/:id} : Updates an existing workGroup.
     *
     * @param id the id of the workGroupDTO to save.
     * @param workGroupDTO the workGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workGroupDTO,
     * or with status {@code 400 (Bad Request)} if the workGroupDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/work-groups/{id}")
    public ResponseEntity<WorkGroupDTO> updateWorkGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WorkGroupDTO workGroupDTO
    ) throws URISyntaxException {
        log.debug("REST request to update WorkGroup : {}, {}", id, workGroupDTO);
        if (workGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workGroupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        WorkGroupDTO result = workGroupService.update(workGroupDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, workGroupDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /work-groups/:id} : Partial updates given fields of an existing workGroup, field will ignore if it is null
     *
     * @param id the id of the workGroupDTO to save.
     * @param workGroupDTO the workGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workGroupDTO,
     * or with status {@code 400 (Bad Request)} if the workGroupDTO is not valid,
     * or with status {@code 404 (Not Found)} if the workGroupDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the workGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/work-groups/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WorkGroupDTO> partialUpdateWorkGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WorkGroupDTO workGroupDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update WorkGroup partially : {}, {}", id, workGroupDTO);
        if (workGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workGroupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WorkGroupDTO> result = workGroupService.partialUpdate(workGroupDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, workGroupDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /work-groups} : get all the workGroups.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of workGroups in body.
     */
    @GetMapping("/work-groups")
    public ResponseEntity<List<WorkGroupDTO>> getAllWorkGroups(
        WorkGroupCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get WorkGroups by criteria: {}", criteria);
        Page<WorkGroupDTO> page = workGroupQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /work-groups/count} : count all the workGroups.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/work-groups/count")
    public ResponseEntity<Long> countWorkGroups(WorkGroupCriteria criteria) {
        log.debug("REST request to count WorkGroups by criteria: {}", criteria);
        return ResponseEntity.ok().body(workGroupQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /work-groups/:id} : get the "id" workGroup.
     *
     * @param id the id of the workGroupDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workGroupDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/work-groups/{id}")
    public ResponseEntity<WorkGroupDTO> getWorkGroup(@PathVariable Long id) {
        log.debug("REST request to get WorkGroup : {}", id);
        Optional<WorkGroupDTO> workGroupDTO = workGroupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(workGroupDTO);
    }

    /**
     * {@code DELETE  /work-groups/:id} : delete the "id" workGroup.
     *
     * @param id the id of the workGroupDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/work-groups/{id}")
    public ResponseEntity<Void> deleteWorkGroup(@PathVariable Long id) {
        log.debug("REST request to delete WorkGroup : {}", id);
        workGroupService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/work-groups?query=:query} : search for the workGroup corresponding
     * to the query.
     *
     * @param query the query of the workGroup search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/work-groups")
    public ResponseEntity<List<WorkGroupDTO>> searchWorkGroups(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of WorkGroups for query {}", query);
        Page<WorkGroupDTO> page = workGroupService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
