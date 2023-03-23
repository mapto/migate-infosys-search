package it.unimi.dllcm.migate.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.unimi.dllcm.migate.IntegrationTest;
import it.unimi.dllcm.migate.domain.Institution;
import it.unimi.dllcm.migate.domain.Location;
import it.unimi.dllcm.migate.domain.Role;
import it.unimi.dllcm.migate.domain.enumeration.Country;
import it.unimi.dllcm.migate.repository.InstitutionRepository;
import it.unimi.dllcm.migate.repository.search.InstitutionSearchRepository;
import it.unimi.dllcm.migate.service.InstitutionService;
import it.unimi.dllcm.migate.service.criteria.InstitutionCriteria;
import it.unimi.dllcm.migate.service.dto.InstitutionDTO;
import it.unimi.dllcm.migate.service.mapper.InstitutionMapper;
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
 * Integration tests for the {@link InstitutionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InstitutionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Country DEFAULT_COUNTRY = Country.UNITED_STATES;
    private static final Country UPDATED_COUNTRY = Country.UNITED_KINGDOM;

    private static final String ENTITY_API_URL = "/api/institutions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/institutions";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InstitutionRepository institutionRepository;

    @Mock
    private InstitutionRepository institutionRepositoryMock;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Mock
    private InstitutionService institutionServiceMock;

    @Autowired
    private InstitutionSearchRepository institutionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInstitutionMockMvc;

    private Institution institution;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Institution createEntity(EntityManager em) {
        Institution institution = new Institution().name(DEFAULT_NAME).country(DEFAULT_COUNTRY);
        return institution;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Institution createUpdatedEntity(EntityManager em) {
        Institution institution = new Institution().name(UPDATED_NAME).country(UPDATED_COUNTRY);
        return institution;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        institutionSearchRepository.deleteAll();
        assertThat(institutionSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        institution = createEntity(em);
    }

    @Test
    @Transactional
    void createInstitution() throws Exception {
        int databaseSizeBeforeCreate = institutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);
        restInstitutionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(institutionDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll();
        assertThat(institutionList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Institution testInstitution = institutionList.get(institutionList.size() - 1);
        assertThat(testInstitution.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testInstitution.getCountry()).isEqualTo(DEFAULT_COUNTRY);
    }

    @Test
    @Transactional
    void createInstitutionWithExistingId() throws Exception {
        // Create the Institution with an existing ID
        institution.setId(1L);
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        int databaseSizeBeforeCreate = institutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restInstitutionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(institutionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll();
        assertThat(institutionList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = institutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        // set the field null
        institution.setName(null);

        // Create the Institution, which fails.
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        restInstitutionMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(institutionDTO))
            )
            .andExpect(status().isBadRequest());

        List<Institution> institutionList = institutionRepository.findAll();
        assertThat(institutionList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllInstitutions() throws Exception {
        // Initialize the database
        institutionRepository.saveAndFlush(institution);

        // Get all the institutionList
        restInstitutionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(institution.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInstitutionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(institutionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInstitutionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(institutionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInstitutionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(institutionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInstitutionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(institutionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInstitution() throws Exception {
        // Initialize the database
        institutionRepository.saveAndFlush(institution);

        // Get the institution
        restInstitutionMockMvc
            .perform(get(ENTITY_API_URL_ID, institution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(institution.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY.toString()));
    }

    @Test
    @Transactional
    void getInstitutionsByIdFiltering() throws Exception {
        // Initialize the database
        institutionRepository.saveAndFlush(institution);

        Long id = institution.getId();

        defaultInstitutionShouldBeFound("id.equals=" + id);
        defaultInstitutionShouldNotBeFound("id.notEquals=" + id);

        defaultInstitutionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultInstitutionShouldNotBeFound("id.greaterThan=" + id);

        defaultInstitutionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultInstitutionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInstitutionsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        institutionRepository.saveAndFlush(institution);

        // Get all the institutionList where name equals to DEFAULT_NAME
        defaultInstitutionShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the institutionList where name equals to UPDATED_NAME
        defaultInstitutionShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllInstitutionsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        institutionRepository.saveAndFlush(institution);

        // Get all the institutionList where name in DEFAULT_NAME or UPDATED_NAME
        defaultInstitutionShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the institutionList where name equals to UPDATED_NAME
        defaultInstitutionShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllInstitutionsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        institutionRepository.saveAndFlush(institution);

        // Get all the institutionList where name is not null
        defaultInstitutionShouldBeFound("name.specified=true");

        // Get all the institutionList where name is null
        defaultInstitutionShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllInstitutionsByNameContainsSomething() throws Exception {
        // Initialize the database
        institutionRepository.saveAndFlush(institution);

        // Get all the institutionList where name contains DEFAULT_NAME
        defaultInstitutionShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the institutionList where name contains UPDATED_NAME
        defaultInstitutionShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllInstitutionsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        institutionRepository.saveAndFlush(institution);

        // Get all the institutionList where name does not contain DEFAULT_NAME
        defaultInstitutionShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the institutionList where name does not contain UPDATED_NAME
        defaultInstitutionShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllInstitutionsByCountryIsEqualToSomething() throws Exception {
        // Initialize the database
        institutionRepository.saveAndFlush(institution);

        // Get all the institutionList where country equals to DEFAULT_COUNTRY
        defaultInstitutionShouldBeFound("country.equals=" + DEFAULT_COUNTRY);

        // Get all the institutionList where country equals to UPDATED_COUNTRY
        defaultInstitutionShouldNotBeFound("country.equals=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    void getAllInstitutionsByCountryIsInShouldWork() throws Exception {
        // Initialize the database
        institutionRepository.saveAndFlush(institution);

        // Get all the institutionList where country in DEFAULT_COUNTRY or UPDATED_COUNTRY
        defaultInstitutionShouldBeFound("country.in=" + DEFAULT_COUNTRY + "," + UPDATED_COUNTRY);

        // Get all the institutionList where country equals to UPDATED_COUNTRY
        defaultInstitutionShouldNotBeFound("country.in=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    void getAllInstitutionsByCountryIsNullOrNotNull() throws Exception {
        // Initialize the database
        institutionRepository.saveAndFlush(institution);

        // Get all the institutionList where country is not null
        defaultInstitutionShouldBeFound("country.specified=true");

        // Get all the institutionList where country is null
        defaultInstitutionShouldNotBeFound("country.specified=false");
    }

    @Test
    @Transactional
    void getAllInstitutionsBySiteIsEqualToSomething() throws Exception {
        Location site;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            institutionRepository.saveAndFlush(institution);
            site = LocationResourceIT.createEntity(em);
        } else {
            site = TestUtil.findAll(em, Location.class).get(0);
        }
        em.persist(site);
        em.flush();
        institution.addSite(site);
        institutionRepository.saveAndFlush(institution);
        Long siteId = site.getId();

        // Get all the institutionList where site equals to siteId
        defaultInstitutionShouldBeFound("siteId.equals=" + siteId);

        // Get all the institutionList where site equals to (siteId + 1)
        defaultInstitutionShouldNotBeFound("siteId.equals=" + (siteId + 1));
    }

    @Test
    @Transactional
    void getAllInstitutionsByPositionIsEqualToSomething() throws Exception {
        Role position;
        if (TestUtil.findAll(em, Role.class).isEmpty()) {
            institutionRepository.saveAndFlush(institution);
            position = RoleResourceIT.createEntity(em);
        } else {
            position = TestUtil.findAll(em, Role.class).get(0);
        }
        em.persist(position);
        em.flush();
        institution.setPosition(position);
        institutionRepository.saveAndFlush(institution);
        Long positionId = position.getId();

        // Get all the institutionList where position equals to positionId
        defaultInstitutionShouldBeFound("positionId.equals=" + positionId);

        // Get all the institutionList where position equals to (positionId + 1)
        defaultInstitutionShouldNotBeFound("positionId.equals=" + (positionId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInstitutionShouldBeFound(String filter) throws Exception {
        restInstitutionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(institution.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())));

        // Check, that the count call also returns 1
        restInstitutionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInstitutionShouldNotBeFound(String filter) throws Exception {
        restInstitutionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInstitutionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInstitution() throws Exception {
        // Get the institution
        restInstitutionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInstitution() throws Exception {
        // Initialize the database
        institutionRepository.saveAndFlush(institution);

        int databaseSizeBeforeUpdate = institutionRepository.findAll().size();
        institutionSearchRepository.save(institution);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());

        // Update the institution
        Institution updatedInstitution = institutionRepository.findById(institution.getId()).get();
        // Disconnect from session so that the updates on updatedInstitution are not directly saved in db
        em.detach(updatedInstitution);
        updatedInstitution.name(UPDATED_NAME).country(UPDATED_COUNTRY);
        InstitutionDTO institutionDTO = institutionMapper.toDto(updatedInstitution);

        restInstitutionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, institutionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(institutionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
        Institution testInstitution = institutionList.get(institutionList.size() - 1);
        assertThat(testInstitution.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testInstitution.getCountry()).isEqualTo(UPDATED_COUNTRY);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Institution> institutionSearchList = IterableUtils.toList(institutionSearchRepository.findAll());
                Institution testInstitutionSearch = institutionSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testInstitutionSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testInstitutionSearch.getCountry()).isEqualTo(UPDATED_COUNTRY);
            });
    }

    @Test
    @Transactional
    void putNonExistingInstitution() throws Exception {
        int databaseSizeBeforeUpdate = institutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        institution.setId(count.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstitutionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, institutionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(institutionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchInstitution() throws Exception {
        int databaseSizeBeforeUpdate = institutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        institution.setId(count.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstitutionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(institutionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInstitution() throws Exception {
        int databaseSizeBeforeUpdate = institutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        institution.setId(count.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstitutionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(institutionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateInstitutionWithPatch() throws Exception {
        // Initialize the database
        institutionRepository.saveAndFlush(institution);

        int databaseSizeBeforeUpdate = institutionRepository.findAll().size();

        // Update the institution using partial update
        Institution partialUpdatedInstitution = new Institution();
        partialUpdatedInstitution.setId(institution.getId());

        partialUpdatedInstitution.name(UPDATED_NAME);

        restInstitutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstitution.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInstitution))
            )
            .andExpect(status().isOk());

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
        Institution testInstitution = institutionList.get(institutionList.size() - 1);
        assertThat(testInstitution.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testInstitution.getCountry()).isEqualTo(DEFAULT_COUNTRY);
    }

    @Test
    @Transactional
    void fullUpdateInstitutionWithPatch() throws Exception {
        // Initialize the database
        institutionRepository.saveAndFlush(institution);

        int databaseSizeBeforeUpdate = institutionRepository.findAll().size();

        // Update the institution using partial update
        Institution partialUpdatedInstitution = new Institution();
        partialUpdatedInstitution.setId(institution.getId());

        partialUpdatedInstitution.name(UPDATED_NAME).country(UPDATED_COUNTRY);

        restInstitutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInstitution.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInstitution))
            )
            .andExpect(status().isOk());

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
        Institution testInstitution = institutionList.get(institutionList.size() - 1);
        assertThat(testInstitution.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testInstitution.getCountry()).isEqualTo(UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    void patchNonExistingInstitution() throws Exception {
        int databaseSizeBeforeUpdate = institutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        institution.setId(count.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInstitutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, institutionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(institutionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInstitution() throws Exception {
        int databaseSizeBeforeUpdate = institutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        institution.setId(count.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstitutionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(institutionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInstitution() throws Exception {
        int databaseSizeBeforeUpdate = institutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        institution.setId(count.incrementAndGet());

        // Create the Institution
        InstitutionDTO institutionDTO = institutionMapper.toDto(institution);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInstitutionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(institutionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Institution in the database
        List<Institution> institutionList = institutionRepository.findAll();
        assertThat(institutionList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteInstitution() throws Exception {
        // Initialize the database
        institutionRepository.saveAndFlush(institution);
        institutionRepository.save(institution);
        institutionSearchRepository.save(institution);

        int databaseSizeBeforeDelete = institutionRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the institution
        restInstitutionMockMvc
            .perform(delete(ENTITY_API_URL_ID, institution.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Institution> institutionList = institutionRepository.findAll();
        assertThat(institutionList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(institutionSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchInstitution() throws Exception {
        // Initialize the database
        institution = institutionRepository.saveAndFlush(institution);
        institutionSearchRepository.save(institution);

        // Search the institution
        restInstitutionMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + institution.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(institution.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())));
    }
}
