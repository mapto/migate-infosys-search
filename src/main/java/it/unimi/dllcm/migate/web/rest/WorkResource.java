package it.unimi.dllcm.migate.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import it.unimi.dllcm.migate.repository.WorkRepository;
import it.unimi.dllcm.migate.service.WorkQueryService;
import it.unimi.dllcm.migate.service.WorkService;
import it.unimi.dllcm.migate.service.criteria.WorkCriteria;
import it.unimi.dllcm.migate.service.dto.WorkDTO;
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
 * REST controller for managing {@link it.unimi.dllcm.migate.domain.Work}.
 */
@RestController
@RequestMapping("/api")
public class WorkResource {

    private final Logger log = LoggerFactory.getLogger(WorkResource.class);

    private static final String ENTITY_NAME = "work";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkService workService;

    private final WorkRepository workRepository;

    private final WorkQueryService workQueryService;

    public WorkResource(WorkService workService, WorkRepository workRepository, WorkQueryService workQueryService) {
        this.workService = workService;
        this.workRepository = workRepository;
        this.workQueryService = workQueryService;
    }

    /**
     * {@code POST  /works} : Create a new work.
     *
     * @param workDTO the workDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workDTO, or with status {@code 400 (Bad Request)} if the work has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/works")
    public ResponseEntity<WorkDTO> createWork(@Valid @RequestBody WorkDTO workDTO) throws URISyntaxException {
        log.debug("REST request to save Work : {}", workDTO);
        if (workDTO.getId() != null) {
            throw new BadRequestAlertException("A new work cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WorkDTO result = workService.save(workDTO);
        return ResponseEntity
            .created(new URI("/api/works/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /works/:id} : Updates an existing work.
     *
     * @param id the id of the workDTO to save.
     * @param workDTO the workDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workDTO,
     * or with status {@code 400 (Bad Request)} if the workDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/works/{id}")
    public ResponseEntity<WorkDTO> updateWork(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WorkDTO workDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Work : {}, {}", id, workDTO);
        if (workDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        WorkDTO result = workService.update(workDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, workDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /works/:id} : Partial updates given fields of an existing work, field will ignore if it is null
     *
     * @param id the id of the workDTO to save.
     * @param workDTO the workDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workDTO,
     * or with status {@code 400 (Bad Request)} if the workDTO is not valid,
     * or with status {@code 404 (Not Found)} if the workDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the workDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/works/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WorkDTO> partialUpdateWork(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WorkDTO workDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Work partially : {}, {}", id, workDTO);
        if (workDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WorkDTO> result = workService.partialUpdate(workDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, workDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /works} : get all the works.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of works in body.
     */
    @GetMapping("/works")
    public ResponseEntity<List<WorkDTO>> getAllWorks(
        WorkCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Works by criteria: {}", criteria);
        Page<WorkDTO> page = workQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /works/count} : count all the works.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/works/count")
    public ResponseEntity<Long> countWorks(WorkCriteria criteria) {
        log.debug("REST request to count Works by criteria: {}", criteria);
        return ResponseEntity.ok().body(workQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /works/:id} : get the "id" work.
     *
     * @param id the id of the workDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/works/{id}")
    public ResponseEntity<WorkDTO> getWork(@PathVariable Long id) {
        log.debug("REST request to get Work : {}", id);
        Optional<WorkDTO> workDTO = workService.findOne(id);
        return ResponseUtil.wrapOrNotFound(workDTO);
    }

    /**
     * {@code DELETE  /works/:id} : delete the "id" work.
     *
     * @param id the id of the workDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/works/{id}")
    public ResponseEntity<Void> deleteWork(@PathVariable Long id) {
        log.debug("REST request to delete Work : {}", id);
        workService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/works?query=:query} : search for the work corresponding
     * to the query.
     *
     * @param query the query of the work search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/works")
    public ResponseEntity<List<WorkDTO>> searchWorks(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Works for query {}", query);
        Page<WorkDTO> page = workService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
