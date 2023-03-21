package it.unimi.dllcm.migate.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.unimi.dllcm.migate.IntegrationTest;
import it.unimi.dllcm.migate.domain.Institution;
import it.unimi.dllcm.migate.domain.Person;
import it.unimi.dllcm.migate.domain.Role;
import it.unimi.dllcm.migate.domain.Work;
import it.unimi.dllcm.migate.domain.WorkGroup;
import it.unimi.dllcm.migate.repository.WorkRepository;
import it.unimi.dllcm.migate.repository.search.WorkSearchRepository;
import it.unimi.dllcm.migate.service.WorkService;
import it.unimi.dllcm.migate.service.criteria.WorkCriteria;
import it.unimi.dllcm.migate.service.dto.WorkDTO;
import it.unimi.dllcm.migate.service.mapper.WorkMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WorkResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class WorkResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_PUBLISHED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PUBLISHED = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_PUBLISHED = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/works";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/works";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WorkRepository workRepository;

    @Mock
    private WorkRepository workRepositoryMock;

    @Autowired
    private WorkMapper workMapper;

    @Mock
    private WorkService workServiceMock;

    @Autowired
    private WorkSearchRepository workSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWorkMockMvc;

    private Work work;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Work createEntity(EntityManager em) {
        Work work = new Work().name(DEFAULT_NAME).published(DEFAULT_PUBLISHED);
        return work;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Work createUpdatedEntity(EntityManager em) {
        Work work = new Work().name(UPDATED_NAME).published(UPDATED_PUBLISHED);
        return work;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        workSearchRepository.deleteAll();
        assertThat(workSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        work = createEntity(em);
    }

    @Test
    @Transactional
    void createWork() throws Exception {
        int databaseSizeBeforeCreate = workRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workSearchRepository.findAll());
        // Create the Work
        WorkDTO workDTO = workMapper.toDto(work);
        restWorkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workDTO)))
            .andExpect(status().isCreated());

        // Validate the Work in the database
        List<Work> workList = workRepository.findAll();
        assertThat(workList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(workSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Work testWork = workList.get(workList.size() - 1);
        assertThat(testWork.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testWork.getPublished()).isEqualTo(DEFAULT_PUBLISHED);
    }

    @Test
    @Transactional
    void createWorkWithExistingId() throws Exception {
        // Create the Work with an existing ID
        work.setId(1L);
        WorkDTO workDTO = workMapper.toDto(work);

        int databaseSizeBeforeCreate = workRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Work in the database
        List<Work> workList = workRepository.findAll();
        assertThat(workList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = workRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workSearchRepository.findAll());
        // set the field null
        work.setName(null);

        // Create the Work, which fails.
        WorkDTO workDTO = workMapper.toDto(work);

        restWorkMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workDTO)))
            .andExpect(status().isBadRequest());

        List<Work> workList = workRepository.findAll();
        assertThat(workList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllWorks() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get all the workList
        restWorkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(work.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorksWithEagerRelationshipsIsEnabled() throws Exception {
        when(workServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWorkMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(workServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(workServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWorkMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(workRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getWork() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get the work
        restWorkMockMvc
            .perform(get(ENTITY_API_URL_ID, work.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(work.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.published").value(DEFAULT_PUBLISHED.toString()));
    }

    @Test
    @Transactional
    void getWorksByIdFiltering() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        Long id = work.getId();

        defaultWorkShouldBeFound("id.equals=" + id);
        defaultWorkShouldNotBeFound("id.notEquals=" + id);

        defaultWorkShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultWorkShouldNotBeFound("id.greaterThan=" + id);

        defaultWorkShouldBeFound("id.lessThanOrEqual=" + id);
        defaultWorkShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWorksByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get all the workList where name equals to DEFAULT_NAME
        defaultWorkShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the workList where name equals to UPDATED_NAME
        defaultWorkShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWorksByNameIsInShouldWork() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get all the workList where name in DEFAULT_NAME or UPDATED_NAME
        defaultWorkShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the workList where name equals to UPDATED_NAME
        defaultWorkShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWorksByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get all the workList where name is not null
        defaultWorkShouldBeFound("name.specified=true");

        // Get all the workList where name is null
        defaultWorkShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllWorksByNameContainsSomething() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get all the workList where name contains DEFAULT_NAME
        defaultWorkShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the workList where name contains UPDATED_NAME
        defaultWorkShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWorksByNameNotContainsSomething() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get all the workList where name does not contain DEFAULT_NAME
        defaultWorkShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the workList where name does not contain UPDATED_NAME
        defaultWorkShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWorksByPublishedIsEqualToSomething() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get all the workList where published equals to DEFAULT_PUBLISHED
        defaultWorkShouldBeFound("published.equals=" + DEFAULT_PUBLISHED);

        // Get all the workList where published equals to UPDATED_PUBLISHED
        defaultWorkShouldNotBeFound("published.equals=" + UPDATED_PUBLISHED);
    }

    @Test
    @Transactional
    void getAllWorksByPublishedIsInShouldWork() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get all the workList where published in DEFAULT_PUBLISHED or UPDATED_PUBLISHED
        defaultWorkShouldBeFound("published.in=" + DEFAULT_PUBLISHED + "," + UPDATED_PUBLISHED);

        // Get all the workList where published equals to UPDATED_PUBLISHED
        defaultWorkShouldNotBeFound("published.in=" + UPDATED_PUBLISHED);
    }

    @Test
    @Transactional
    void getAllWorksByPublishedIsNullOrNotNull() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get all the workList where published is not null
        defaultWorkShouldBeFound("published.specified=true");

        // Get all the workList where published is null
        defaultWorkShouldNotBeFound("published.specified=false");
    }

    @Test
    @Transactional
    void getAllWorksByPublishedIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get all the workList where published is greater than or equal to DEFAULT_PUBLISHED
        defaultWorkShouldBeFound("published.greaterThanOrEqual=" + DEFAULT_PUBLISHED);

        // Get all the workList where published is greater than or equal to UPDATED_PUBLISHED
        defaultWorkShouldNotBeFound("published.greaterThanOrEqual=" + UPDATED_PUBLISHED);
    }

    @Test
    @Transactional
    void getAllWorksByPublishedIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get all the workList where published is less than or equal to DEFAULT_PUBLISHED
        defaultWorkShouldBeFound("published.lessThanOrEqual=" + DEFAULT_PUBLISHED);

        // Get all the workList where published is less than or equal to SMALLER_PUBLISHED
        defaultWorkShouldNotBeFound("published.lessThanOrEqual=" + SMALLER_PUBLISHED);
    }

    @Test
    @Transactional
    void getAllWorksByPublishedIsLessThanSomething() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get all the workList where published is less than DEFAULT_PUBLISHED
        defaultWorkShouldNotBeFound("published.lessThan=" + DEFAULT_PUBLISHED);

        // Get all the workList where published is less than UPDATED_PUBLISHED
        defaultWorkShouldBeFound("published.lessThan=" + UPDATED_PUBLISHED);
    }

    @Test
    @Transactional
    void getAllWorksByPublishedIsGreaterThanSomething() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        // Get all the workList where published is greater than DEFAULT_PUBLISHED
        defaultWorkShouldNotBeFound("published.greaterThan=" + DEFAULT_PUBLISHED);

        // Get all the workList where published is greater than SMALLER_PUBLISHED
        defaultWorkShouldBeFound("published.greaterThan=" + SMALLER_PUBLISHED);
    }

    @Test
    @Transactional
    void getAllWorksBySponsorIsEqualToSomething() throws Exception {
        Institution sponsor;
        if (TestUtil.findAll(em, Institution.class).isEmpty()) {
            workRepository.saveAndFlush(work);
            sponsor = InstitutionResourceIT.createEntity(em);
        } else {
            sponsor = TestUtil.findAll(em, Institution.class).get(0);
        }
        em.persist(sponsor);
        em.flush();
        work.addSponsor(sponsor);
        workRepository.saveAndFlush(work);
        Long sponsorId = sponsor.getId();

        // Get all the workList where sponsor equals to sponsorId
        defaultWorkShouldBeFound("sponsorId.equals=" + sponsorId);

        // Get all the workList where sponsor equals to (sponsorId + 1)
        defaultWorkShouldNotBeFound("sponsorId.equals=" + (sponsorId + 1));
    }

    @Test
    @Transactional
    void getAllWorksByWorkRoleNameIsEqualToSomething() throws Exception {
        Role workRoleName;
        if (TestUtil.findAll(em, Role.class).isEmpty()) {
            workRepository.saveAndFlush(work);
            workRoleName = RoleResourceIT.createEntity(em);
        } else {
            workRoleName = TestUtil.findAll(em, Role.class).get(0);
        }
        em.persist(workRoleName);
        em.flush();
        work.addWorkRoleName(workRoleName);
        workRepository.saveAndFlush(work);
        Long workRoleNameId = workRoleName.getId();

        // Get all the workList where workRoleName equals to workRoleNameId
        defaultWorkShouldBeFound("workRoleNameId.equals=" + workRoleNameId);

        // Get all the workList where workRoleName equals to (workRoleNameId + 1)
        defaultWorkShouldNotBeFound("workRoleNameId.equals=" + (workRoleNameId + 1));
    }

    @Test
    @Transactional
    void getAllWorksByCollectionIsEqualToSomething() throws Exception {
        WorkGroup collection;
        if (TestUtil.findAll(em, WorkGroup.class).isEmpty()) {
            workRepository.saveAndFlush(work);
            collection = WorkGroupResourceIT.createEntity(em);
        } else {
            collection = TestUtil.findAll(em, WorkGroup.class).get(0);
        }
        em.persist(collection);
        em.flush();
        work.addCollection(collection);
        workRepository.saveAndFlush(work);
        Long collectionId = collection.getId();

        // Get all the workList where collection equals to collectionId
        defaultWorkShouldBeFound("collectionId.equals=" + collectionId);

        // Get all the workList where collection equals to (collectionId + 1)
        defaultWorkShouldNotBeFound("collectionId.equals=" + (collectionId + 1));
    }

    @Test
    @Transactional
    void getAllWorksByResponsibleIsEqualToSomething() throws Exception {
        Person responsible;
        if (TestUtil.findAll(em, Person.class).isEmpty()) {
            workRepository.saveAndFlush(work);
            responsible = PersonResourceIT.createEntity(em);
        } else {
            responsible = TestUtil.findAll(em, Person.class).get(0);
        }
        em.persist(responsible);
        em.flush();
        work.addResponsible(responsible);
        workRepository.saveAndFlush(work);
        Long responsibleId = responsible.getId();

        // Get all the workList where responsible equals to responsibleId
        defaultWorkShouldBeFound("responsibleId.equals=" + responsibleId);

        // Get all the workList where responsible equals to (responsibleId + 1)
        defaultWorkShouldNotBeFound("responsibleId.equals=" + (responsibleId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWorkShouldBeFound(String filter) throws Exception {
        restWorkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(work.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED.toString())));

        // Check, that the count call also returns 1
        restWorkMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWorkShouldNotBeFound(String filter) throws Exception {
        restWorkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWorkMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWork() throws Exception {
        // Get the work
        restWorkMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWork() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        int databaseSizeBeforeUpdate = workRepository.findAll().size();
        workSearchRepository.save(work);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workSearchRepository.findAll());

        // Update the work
        Work updatedWork = workRepository.findById(work.getId()).get();
        // Disconnect from session so that the updates on updatedWork are not directly saved in db
        em.detach(updatedWork);
        updatedWork.name(UPDATED_NAME).published(UPDATED_PUBLISHED);
        WorkDTO workDTO = workMapper.toDto(updatedWork);

        restWorkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workDTO))
            )
            .andExpect(status().isOk());

        // Validate the Work in the database
        List<Work> workList = workRepository.findAll();
        assertThat(workList).hasSize(databaseSizeBeforeUpdate);
        Work testWork = workList.get(workList.size() - 1);
        assertThat(testWork.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWork.getPublished()).isEqualTo(UPDATED_PUBLISHED);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(workSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Work> workSearchList = IterableUtils.toList(workSearchRepository.findAll());
                Work testWorkSearch = workSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testWorkSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testWorkSearch.getPublished()).isEqualTo(UPDATED_PUBLISHED);
            });
    }

    @Test
    @Transactional
    void putNonExistingWork() throws Exception {
        int databaseSizeBeforeUpdate = workRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workSearchRepository.findAll());
        work.setId(count.incrementAndGet());

        // Create the Work
        WorkDTO workDTO = workMapper.toDto(work);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Work in the database
        List<Work> workList = workRepository.findAll();
        assertThat(workList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchWork() throws Exception {
        int databaseSizeBeforeUpdate = workRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workSearchRepository.findAll());
        work.setId(count.incrementAndGet());

        // Create the Work
        WorkDTO workDTO = workMapper.toDto(work);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Work in the database
        List<Work> workList = workRepository.findAll();
        assertThat(workList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWork() throws Exception {
        int databaseSizeBeforeUpdate = workRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workSearchRepository.findAll());
        work.setId(count.incrementAndGet());

        // Create the Work
        WorkDTO workDTO = workMapper.toDto(work);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Work in the database
        List<Work> workList = workRepository.findAll();
        assertThat(workList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateWorkWithPatch() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        int databaseSizeBeforeUpdate = workRepository.findAll().size();

        // Update the work using partial update
        Work partialUpdatedWork = new Work();
        partialUpdatedWork.setId(work.getId());

        partialUpdatedWork.name(UPDATED_NAME);

        restWorkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWork.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWork))
            )
            .andExpect(status().isOk());

        // Validate the Work in the database
        List<Work> workList = workRepository.findAll();
        assertThat(workList).hasSize(databaseSizeBeforeUpdate);
        Work testWork = workList.get(workList.size() - 1);
        assertThat(testWork.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWork.getPublished()).isEqualTo(DEFAULT_PUBLISHED);
    }

    @Test
    @Transactional
    void fullUpdateWorkWithPatch() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);

        int databaseSizeBeforeUpdate = workRepository.findAll().size();

        // Update the work using partial update
        Work partialUpdatedWork = new Work();
        partialUpdatedWork.setId(work.getId());

        partialUpdatedWork.name(UPDATED_NAME).published(UPDATED_PUBLISHED);

        restWorkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWork.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWork))
            )
            .andExpect(status().isOk());

        // Validate the Work in the database
        List<Work> workList = workRepository.findAll();
        assertThat(workList).hasSize(databaseSizeBeforeUpdate);
        Work testWork = workList.get(workList.size() - 1);
        assertThat(testWork.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWork.getPublished()).isEqualTo(UPDATED_PUBLISHED);
    }

    @Test
    @Transactional
    void patchNonExistingWork() throws Exception {
        int databaseSizeBeforeUpdate = workRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workSearchRepository.findAll());
        work.setId(count.incrementAndGet());

        // Create the Work
        WorkDTO workDTO = workMapper.toDto(work);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, workDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Work in the database
        List<Work> workList = workRepository.findAll();
        assertThat(workList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWork() throws Exception {
        int databaseSizeBeforeUpdate = workRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workSearchRepository.findAll());
        work.setId(count.incrementAndGet());

        // Create the Work
        WorkDTO workDTO = workMapper.toDto(work);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Work in the database
        List<Work> workList = workRepository.findAll();
        assertThat(workList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWork() throws Exception {
        int databaseSizeBeforeUpdate = workRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workSearchRepository.findAll());
        work.setId(count.incrementAndGet());

        // Create the Work
        WorkDTO workDTO = workMapper.toDto(work);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(workDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Work in the database
        List<Work> workList = workRepository.findAll();
        assertThat(workList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteWork() throws Exception {
        // Initialize the database
        workRepository.saveAndFlush(work);
        workRepository.save(work);
        workSearchRepository.save(work);

        int databaseSizeBeforeDelete = workRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the work
        restWorkMockMvc
            .perform(delete(ENTITY_API_URL_ID, work.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Work> workList = workRepository.findAll();
        assertThat(workList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchWork() throws Exception {
        // Initialize the database
        work = workRepository.saveAndFlush(work);
        workSearchRepository.save(work);

        // Search the work
        restWorkMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + work.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(work.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].published").value(hasItem(DEFAULT_PUBLISHED.toString())));
    }
}
