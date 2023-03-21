package it.unimi.dllcm.migate.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.unimi.dllcm.migate.IntegrationTest;
import it.unimi.dllcm.migate.domain.Work;
import it.unimi.dllcm.migate.domain.WorkGroup;
import it.unimi.dllcm.migate.repository.WorkGroupRepository;
import it.unimi.dllcm.migate.repository.search.WorkGroupSearchRepository;
import it.unimi.dllcm.migate.service.WorkGroupService;
import it.unimi.dllcm.migate.service.criteria.WorkGroupCriteria;
import it.unimi.dllcm.migate.service.dto.WorkGroupDTO;
import it.unimi.dllcm.migate.service.mapper.WorkGroupMapper;
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
 * Integration tests for the {@link WorkGroupResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class WorkGroupResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/work-groups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/work-groups";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WorkGroupRepository workGroupRepository;

    @Mock
    private WorkGroupRepository workGroupRepositoryMock;

    @Autowired
    private WorkGroupMapper workGroupMapper;

    @Mock
    private WorkGroupService workGroupServiceMock;

    @Autowired
    private WorkGroupSearchRepository workGroupSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWorkGroupMockMvc;

    private WorkGroup workGroup;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkGroup createEntity(EntityManager em) {
        WorkGroup workGroup = new WorkGroup().name(DEFAULT_NAME);
        return workGroup;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkGroup createUpdatedEntity(EntityManager em) {
        WorkGroup workGroup = new WorkGroup().name(UPDATED_NAME);
        return workGroup;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        workGroupSearchRepository.deleteAll();
        assertThat(workGroupSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        workGroup = createEntity(em);
    }

    @Test
    @Transactional
    void createWorkGroup() throws Exception {
        int databaseSizeBeforeCreate = workGroupRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        // Create the WorkGroup
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);
        restWorkGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workGroupDTO)))
            .andExpect(status().isCreated());

        // Validate the WorkGroup in the database
        List<WorkGroup> workGroupList = workGroupRepository.findAll();
        assertThat(workGroupList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        WorkGroup testWorkGroup = workGroupList.get(workGroupList.size() - 1);
        assertThat(testWorkGroup.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createWorkGroupWithExistingId() throws Exception {
        // Create the WorkGroup with an existing ID
        workGroup.setId(1L);
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        int databaseSizeBeforeCreate = workGroupRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workGroupSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workGroupDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WorkGroup in the database
        List<WorkGroup> workGroupList = workGroupRepository.findAll();
        assertThat(workGroupList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = workGroupRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        // set the field null
        workGroup.setName(null);

        // Create the WorkGroup, which fails.
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        restWorkGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workGroupDTO)))
            .andExpect(status().isBadRequest());

        List<WorkGroup> workGroupList = workGroupRepository.findAll();
        assertThat(workGroupList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllWorkGroups() throws Exception {
        // Initialize the database
        workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList
        restWorkGroupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkGroupsWithEagerRelationshipsIsEnabled() throws Exception {
        when(workGroupServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWorkGroupMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(workGroupServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWorkGroupsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(workGroupServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restWorkGroupMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(workGroupRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getWorkGroup() throws Exception {
        // Initialize the database
        workGroupRepository.saveAndFlush(workGroup);

        // Get the workGroup
        restWorkGroupMockMvc
            .perform(get(ENTITY_API_URL_ID, workGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(workGroup.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getWorkGroupsByIdFiltering() throws Exception {
        // Initialize the database
        workGroupRepository.saveAndFlush(workGroup);

        Long id = workGroup.getId();

        defaultWorkGroupShouldBeFound("id.equals=" + id);
        defaultWorkGroupShouldNotBeFound("id.notEquals=" + id);

        defaultWorkGroupShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultWorkGroupShouldNotBeFound("id.greaterThan=" + id);

        defaultWorkGroupShouldBeFound("id.lessThanOrEqual=" + id);
        defaultWorkGroupShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWorkGroupsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where name equals to DEFAULT_NAME
        defaultWorkGroupShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the workGroupList where name equals to UPDATED_NAME
        defaultWorkGroupShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWorkGroupsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where name in DEFAULT_NAME or UPDATED_NAME
        defaultWorkGroupShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the workGroupList where name equals to UPDATED_NAME
        defaultWorkGroupShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWorkGroupsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where name is not null
        defaultWorkGroupShouldBeFound("name.specified=true");

        // Get all the workGroupList where name is null
        defaultWorkGroupShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkGroupsByNameContainsSomething() throws Exception {
        // Initialize the database
        workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where name contains DEFAULT_NAME
        defaultWorkGroupShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the workGroupList where name contains UPDATED_NAME
        defaultWorkGroupShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWorkGroupsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where name does not contain DEFAULT_NAME
        defaultWorkGroupShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the workGroupList where name does not contain UPDATED_NAME
        defaultWorkGroupShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWorkGroupsByWorkIsEqualToSomething() throws Exception {
        Work work;
        if (TestUtil.findAll(em, Work.class).isEmpty()) {
            workGroupRepository.saveAndFlush(workGroup);
            work = WorkResourceIT.createEntity(em);
        } else {
            work = TestUtil.findAll(em, Work.class).get(0);
        }
        em.persist(work);
        em.flush();
        workGroup.setWork(work);
        workGroupRepository.saveAndFlush(workGroup);
        Long workId = work.getId();

        // Get all the workGroupList where work equals to workId
        defaultWorkGroupShouldBeFound("workId.equals=" + workId);

        // Get all the workGroupList where work equals to (workId + 1)
        defaultWorkGroupShouldNotBeFound("workId.equals=" + (workId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWorkGroupShouldBeFound(String filter) throws Exception {
        restWorkGroupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restWorkGroupMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWorkGroupShouldNotBeFound(String filter) throws Exception {
        restWorkGroupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWorkGroupMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWorkGroup() throws Exception {
        // Get the workGroup
        restWorkGroupMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWorkGroup() throws Exception {
        // Initialize the database
        workGroupRepository.saveAndFlush(workGroup);

        int databaseSizeBeforeUpdate = workGroupRepository.findAll().size();
        workGroupSearchRepository.save(workGroup);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workGroupSearchRepository.findAll());

        // Update the workGroup
        WorkGroup updatedWorkGroup = workGroupRepository.findById(workGroup.getId()).get();
        // Disconnect from session so that the updates on updatedWorkGroup are not directly saved in db
        em.detach(updatedWorkGroup);
        updatedWorkGroup.name(UPDATED_NAME);
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(updatedWorkGroup);

        restWorkGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workGroupDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workGroupDTO))
            )
            .andExpect(status().isOk());

        // Validate the WorkGroup in the database
        List<WorkGroup> workGroupList = workGroupRepository.findAll();
        assertThat(workGroupList).hasSize(databaseSizeBeforeUpdate);
        WorkGroup testWorkGroup = workGroupList.get(workGroupList.size() - 1);
        assertThat(testWorkGroup.getName()).isEqualTo(UPDATED_NAME);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<WorkGroup> workGroupSearchList = IterableUtils.toList(workGroupSearchRepository.findAll());
                WorkGroup testWorkGroupSearch = workGroupSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testWorkGroupSearch.getName()).isEqualTo(UPDATED_NAME);
            });
    }

    @Test
    @Transactional
    void putNonExistingWorkGroup() throws Exception {
        int databaseSizeBeforeUpdate = workGroupRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        workGroup.setId(count.incrementAndGet());

        // Create the WorkGroup
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workGroupDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkGroup in the database
        List<WorkGroup> workGroupList = workGroupRepository.findAll();
        assertThat(workGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchWorkGroup() throws Exception {
        int databaseSizeBeforeUpdate = workGroupRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        workGroup.setId(count.incrementAndGet());

        // Create the WorkGroup
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(workGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkGroup in the database
        List<WorkGroup> workGroupList = workGroupRepository.findAll();
        assertThat(workGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWorkGroup() throws Exception {
        int databaseSizeBeforeUpdate = workGroupRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        workGroup.setId(count.incrementAndGet());

        // Create the WorkGroup
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkGroupMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(workGroupDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkGroup in the database
        List<WorkGroup> workGroupList = workGroupRepository.findAll();
        assertThat(workGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateWorkGroupWithPatch() throws Exception {
        // Initialize the database
        workGroupRepository.saveAndFlush(workGroup);

        int databaseSizeBeforeUpdate = workGroupRepository.findAll().size();

        // Update the workGroup using partial update
        WorkGroup partialUpdatedWorkGroup = new WorkGroup();
        partialUpdatedWorkGroup.setId(workGroup.getId());

        restWorkGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkGroup.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWorkGroup))
            )
            .andExpect(status().isOk());

        // Validate the WorkGroup in the database
        List<WorkGroup> workGroupList = workGroupRepository.findAll();
        assertThat(workGroupList).hasSize(databaseSizeBeforeUpdate);
        WorkGroup testWorkGroup = workGroupList.get(workGroupList.size() - 1);
        assertThat(testWorkGroup.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateWorkGroupWithPatch() throws Exception {
        // Initialize the database
        workGroupRepository.saveAndFlush(workGroup);

        int databaseSizeBeforeUpdate = workGroupRepository.findAll().size();

        // Update the workGroup using partial update
        WorkGroup partialUpdatedWorkGroup = new WorkGroup();
        partialUpdatedWorkGroup.setId(workGroup.getId());

        partialUpdatedWorkGroup.name(UPDATED_NAME);

        restWorkGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkGroup.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWorkGroup))
            )
            .andExpect(status().isOk());

        // Validate the WorkGroup in the database
        List<WorkGroup> workGroupList = workGroupRepository.findAll();
        assertThat(workGroupList).hasSize(databaseSizeBeforeUpdate);
        WorkGroup testWorkGroup = workGroupList.get(workGroupList.size() - 1);
        assertThat(testWorkGroup.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingWorkGroup() throws Exception {
        int databaseSizeBeforeUpdate = workGroupRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        workGroup.setId(count.incrementAndGet());

        // Create the WorkGroup
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, workGroupDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkGroup in the database
        List<WorkGroup> workGroupList = workGroupRepository.findAll();
        assertThat(workGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWorkGroup() throws Exception {
        int databaseSizeBeforeUpdate = workGroupRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        workGroup.setId(count.incrementAndGet());

        // Create the WorkGroup
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(workGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkGroup in the database
        List<WorkGroup> workGroupList = workGroupRepository.findAll();
        assertThat(workGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWorkGroup() throws Exception {
        int databaseSizeBeforeUpdate = workGroupRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        workGroup.setId(count.incrementAndGet());

        // Create the WorkGroup
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkGroupMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(workGroupDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkGroup in the database
        List<WorkGroup> workGroupList = workGroupRepository.findAll();
        assertThat(workGroupList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteWorkGroup() throws Exception {
        // Initialize the database
        workGroupRepository.saveAndFlush(workGroup);
        workGroupRepository.save(workGroup);
        workGroupSearchRepository.save(workGroup);

        int databaseSizeBeforeDelete = workGroupRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the workGroup
        restWorkGroupMockMvc
            .perform(delete(ENTITY_API_URL_ID, workGroup.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WorkGroup> workGroupList = workGroupRepository.findAll();
        assertThat(workGroupList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(workGroupSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchWorkGroup() throws Exception {
        // Initialize the database
        workGroup = workGroupRepository.saveAndFlush(workGroup);
        workGroupSearchRepository.save(workGroup);

        // Search the workGroup
        restWorkGroupMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + workGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
