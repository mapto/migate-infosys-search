package it.unimi.dllcm.migate.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.unimi.dllcm.migate.IntegrationTest;
import it.unimi.dllcm.migate.domain.Person;
import it.unimi.dllcm.migate.domain.Role;
import it.unimi.dllcm.migate.domain.Work;
import it.unimi.dllcm.migate.domain.enumeration.Country;
import it.unimi.dllcm.migate.domain.enumeration.Gender;
import it.unimi.dllcm.migate.domain.enumeration.Language;
import it.unimi.dllcm.migate.repository.PersonRepository;
import it.unimi.dllcm.migate.repository.search.PersonSearchRepository;
import it.unimi.dllcm.migate.service.PersonService;
import it.unimi.dllcm.migate.service.criteria.PersonCriteria;
import it.unimi.dllcm.migate.service.dto.PersonDTO;
import it.unimi.dllcm.migate.service.mapper.PersonMapper;
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
 * Integration tests for the {@link PersonResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PersonResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final LocalDate DEFAULT_DOB = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DOB = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DOB = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DOD = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DOD = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DOD = LocalDate.ofEpochDay(-1L);

    private static final Country DEFAULT_COUNTRY = Country.UNITED_STATES;
    private static final Country UPDATED_COUNTRY = Country.UNITED_KINGDOM;

    private static final Language DEFAULT_LANGUAGE = Language.ENGLISH;
    private static final Language UPDATED_LANGUAGE = Language.SPANISH;

    private static final String ENTITY_API_URL = "/api/people";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/people";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PersonRepository personRepository;

    @Mock
    private PersonRepository personRepositoryMock;

    @Autowired
    private PersonMapper personMapper;

    @Mock
    private PersonService personServiceMock;

    @Autowired
    private PersonSearchRepository personSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPersonMockMvc;

    private Person person;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Person createEntity(EntityManager em) {
        Person person = new Person()
            .name(DEFAULT_NAME)
            .gender(DEFAULT_GENDER)
            .dob(DEFAULT_DOB)
            .dod(DEFAULT_DOD)
            .country(DEFAULT_COUNTRY)
            .language(DEFAULT_LANGUAGE);
        return person;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Person createUpdatedEntity(EntityManager em) {
        Person person = new Person()
            .name(UPDATED_NAME)
            .gender(UPDATED_GENDER)
            .dob(UPDATED_DOB)
            .dod(UPDATED_DOD)
            .country(UPDATED_COUNTRY)
            .language(UPDATED_LANGUAGE);
        return person;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        personSearchRepository.deleteAll();
        assertThat(personSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        person = createEntity(em);
    }

    @Test
    @Transactional
    void createPerson() throws Exception {
        int databaseSizeBeforeCreate = personRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personSearchRepository.findAll());
        // Create the Person
        PersonDTO personDTO = personMapper.toDto(person);
        restPersonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(personDTO)))
            .andExpect(status().isCreated());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(personSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Person testPerson = personList.get(personList.size() - 1);
        assertThat(testPerson.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPerson.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testPerson.getDob()).isEqualTo(DEFAULT_DOB);
        assertThat(testPerson.getDod()).isEqualTo(DEFAULT_DOD);
        assertThat(testPerson.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testPerson.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
    }

    @Test
    @Transactional
    void createPersonWithExistingId() throws Exception {
        // Create the Person with an existing ID
        person.setId(1L);
        PersonDTO personDTO = personMapper.toDto(person);

        int databaseSizeBeforeCreate = personRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPersonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(personDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = personRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personSearchRepository.findAll());
        // set the field null
        person.setName(null);

        // Create the Person, which fails.
        PersonDTO personDTO = personMapper.toDto(person);

        restPersonMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(personDTO)))
            .andExpect(status().isBadRequest());

        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllPeople() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList
        restPersonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(person.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())))
            .andExpect(jsonPath("$.[*].dod").value(hasItem(DEFAULT_DOD.toString())))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPeopleWithEagerRelationshipsIsEnabled() throws Exception {
        when(personServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPersonMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(personServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPeopleWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(personServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPersonMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(personRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPerson() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get the person
        restPersonMockMvc
            .perform(get(ENTITY_API_URL_ID, person.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(person.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.dob").value(DEFAULT_DOB.toString()))
            .andExpect(jsonPath("$.dod").value(DEFAULT_DOD.toString()))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY.toString()))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()));
    }

    @Test
    @Transactional
    void getPeopleByIdFiltering() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        Long id = person.getId();

        defaultPersonShouldBeFound("id.equals=" + id);
        defaultPersonShouldNotBeFound("id.notEquals=" + id);

        defaultPersonShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPersonShouldNotBeFound("id.greaterThan=" + id);

        defaultPersonShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPersonShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPeopleByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where name equals to DEFAULT_NAME
        defaultPersonShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the personList where name equals to UPDATED_NAME
        defaultPersonShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPeopleByNameIsInShouldWork() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPersonShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the personList where name equals to UPDATED_NAME
        defaultPersonShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPeopleByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where name is not null
        defaultPersonShouldBeFound("name.specified=true");

        // Get all the personList where name is null
        defaultPersonShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllPeopleByNameContainsSomething() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where name contains DEFAULT_NAME
        defaultPersonShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the personList where name contains UPDATED_NAME
        defaultPersonShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPeopleByNameNotContainsSomething() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where name does not contain DEFAULT_NAME
        defaultPersonShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the personList where name does not contain UPDATED_NAME
        defaultPersonShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPeopleByGenderIsEqualToSomething() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where gender equals to DEFAULT_GENDER
        defaultPersonShouldBeFound("gender.equals=" + DEFAULT_GENDER);

        // Get all the personList where gender equals to UPDATED_GENDER
        defaultPersonShouldNotBeFound("gender.equals=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllPeopleByGenderIsInShouldWork() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where gender in DEFAULT_GENDER or UPDATED_GENDER
        defaultPersonShouldBeFound("gender.in=" + DEFAULT_GENDER + "," + UPDATED_GENDER);

        // Get all the personList where gender equals to UPDATED_GENDER
        defaultPersonShouldNotBeFound("gender.in=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllPeopleByGenderIsNullOrNotNull() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where gender is not null
        defaultPersonShouldBeFound("gender.specified=true");

        // Get all the personList where gender is null
        defaultPersonShouldNotBeFound("gender.specified=false");
    }

    @Test
    @Transactional
    void getAllPeopleByDobIsEqualToSomething() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where dob equals to DEFAULT_DOB
        defaultPersonShouldBeFound("dob.equals=" + DEFAULT_DOB);

        // Get all the personList where dob equals to UPDATED_DOB
        defaultPersonShouldNotBeFound("dob.equals=" + UPDATED_DOB);
    }

    @Test
    @Transactional
    void getAllPeopleByDobIsInShouldWork() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where dob in DEFAULT_DOB or UPDATED_DOB
        defaultPersonShouldBeFound("dob.in=" + DEFAULT_DOB + "," + UPDATED_DOB);

        // Get all the personList where dob equals to UPDATED_DOB
        defaultPersonShouldNotBeFound("dob.in=" + UPDATED_DOB);
    }

    @Test
    @Transactional
    void getAllPeopleByDobIsNullOrNotNull() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where dob is not null
        defaultPersonShouldBeFound("dob.specified=true");

        // Get all the personList where dob is null
        defaultPersonShouldNotBeFound("dob.specified=false");
    }

    @Test
    @Transactional
    void getAllPeopleByDobIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where dob is greater than or equal to DEFAULT_DOB
        defaultPersonShouldBeFound("dob.greaterThanOrEqual=" + DEFAULT_DOB);

        // Get all the personList where dob is greater than or equal to UPDATED_DOB
        defaultPersonShouldNotBeFound("dob.greaterThanOrEqual=" + UPDATED_DOB);
    }

    @Test
    @Transactional
    void getAllPeopleByDobIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where dob is less than or equal to DEFAULT_DOB
        defaultPersonShouldBeFound("dob.lessThanOrEqual=" + DEFAULT_DOB);

        // Get all the personList where dob is less than or equal to SMALLER_DOB
        defaultPersonShouldNotBeFound("dob.lessThanOrEqual=" + SMALLER_DOB);
    }

    @Test
    @Transactional
    void getAllPeopleByDobIsLessThanSomething() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where dob is less than DEFAULT_DOB
        defaultPersonShouldNotBeFound("dob.lessThan=" + DEFAULT_DOB);

        // Get all the personList where dob is less than UPDATED_DOB
        defaultPersonShouldBeFound("dob.lessThan=" + UPDATED_DOB);
    }

    @Test
    @Transactional
    void getAllPeopleByDobIsGreaterThanSomething() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where dob is greater than DEFAULT_DOB
        defaultPersonShouldNotBeFound("dob.greaterThan=" + DEFAULT_DOB);

        // Get all the personList where dob is greater than SMALLER_DOB
        defaultPersonShouldBeFound("dob.greaterThan=" + SMALLER_DOB);
    }

    @Test
    @Transactional
    void getAllPeopleByDodIsEqualToSomething() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where dod equals to DEFAULT_DOD
        defaultPersonShouldBeFound("dod.equals=" + DEFAULT_DOD);

        // Get all the personList where dod equals to UPDATED_DOD
        defaultPersonShouldNotBeFound("dod.equals=" + UPDATED_DOD);
    }

    @Test
    @Transactional
    void getAllPeopleByDodIsInShouldWork() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where dod in DEFAULT_DOD or UPDATED_DOD
        defaultPersonShouldBeFound("dod.in=" + DEFAULT_DOD + "," + UPDATED_DOD);

        // Get all the personList where dod equals to UPDATED_DOD
        defaultPersonShouldNotBeFound("dod.in=" + UPDATED_DOD);
    }

    @Test
    @Transactional
    void getAllPeopleByDodIsNullOrNotNull() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where dod is not null
        defaultPersonShouldBeFound("dod.specified=true");

        // Get all the personList where dod is null
        defaultPersonShouldNotBeFound("dod.specified=false");
    }

    @Test
    @Transactional
    void getAllPeopleByDodIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where dod is greater than or equal to DEFAULT_DOD
        defaultPersonShouldBeFound("dod.greaterThanOrEqual=" + DEFAULT_DOD);

        // Get all the personList where dod is greater than or equal to UPDATED_DOD
        defaultPersonShouldNotBeFound("dod.greaterThanOrEqual=" + UPDATED_DOD);
    }

    @Test
    @Transactional
    void getAllPeopleByDodIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where dod is less than or equal to DEFAULT_DOD
        defaultPersonShouldBeFound("dod.lessThanOrEqual=" + DEFAULT_DOD);

        // Get all the personList where dod is less than or equal to SMALLER_DOD
        defaultPersonShouldNotBeFound("dod.lessThanOrEqual=" + SMALLER_DOD);
    }

    @Test
    @Transactional
    void getAllPeopleByDodIsLessThanSomething() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where dod is less than DEFAULT_DOD
        defaultPersonShouldNotBeFound("dod.lessThan=" + DEFAULT_DOD);

        // Get all the personList where dod is less than UPDATED_DOD
        defaultPersonShouldBeFound("dod.lessThan=" + UPDATED_DOD);
    }

    @Test
    @Transactional
    void getAllPeopleByDodIsGreaterThanSomething() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where dod is greater than DEFAULT_DOD
        defaultPersonShouldNotBeFound("dod.greaterThan=" + DEFAULT_DOD);

        // Get all the personList where dod is greater than SMALLER_DOD
        defaultPersonShouldBeFound("dod.greaterThan=" + SMALLER_DOD);
    }

    @Test
    @Transactional
    void getAllPeopleByCountryIsEqualToSomething() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where country equals to DEFAULT_COUNTRY
        defaultPersonShouldBeFound("country.equals=" + DEFAULT_COUNTRY);

        // Get all the personList where country equals to UPDATED_COUNTRY
        defaultPersonShouldNotBeFound("country.equals=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    void getAllPeopleByCountryIsInShouldWork() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where country in DEFAULT_COUNTRY or UPDATED_COUNTRY
        defaultPersonShouldBeFound("country.in=" + DEFAULT_COUNTRY + "," + UPDATED_COUNTRY);

        // Get all the personList where country equals to UPDATED_COUNTRY
        defaultPersonShouldNotBeFound("country.in=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    void getAllPeopleByCountryIsNullOrNotNull() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where country is not null
        defaultPersonShouldBeFound("country.specified=true");

        // Get all the personList where country is null
        defaultPersonShouldNotBeFound("country.specified=false");
    }

    @Test
    @Transactional
    void getAllPeopleByLanguageIsEqualToSomething() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where language equals to DEFAULT_LANGUAGE
        defaultPersonShouldBeFound("language.equals=" + DEFAULT_LANGUAGE);

        // Get all the personList where language equals to UPDATED_LANGUAGE
        defaultPersonShouldNotBeFound("language.equals=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllPeopleByLanguageIsInShouldWork() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where language in DEFAULT_LANGUAGE or UPDATED_LANGUAGE
        defaultPersonShouldBeFound("language.in=" + DEFAULT_LANGUAGE + "," + UPDATED_LANGUAGE);

        // Get all the personList where language equals to UPDATED_LANGUAGE
        defaultPersonShouldNotBeFound("language.in=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void getAllPeopleByLanguageIsNullOrNotNull() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        // Get all the personList where language is not null
        defaultPersonShouldBeFound("language.specified=true");

        // Get all the personList where language is null
        defaultPersonShouldNotBeFound("language.specified=false");
    }

    @Test
    @Transactional
    void getAllPeopleByWorkIsEqualToSomething() throws Exception {
        Work work;
        if (TestUtil.findAll(em, Work.class).isEmpty()) {
            personRepository.saveAndFlush(person);
            work = WorkResourceIT.createEntity(em);
        } else {
            work = TestUtil.findAll(em, Work.class).get(0);
        }
        em.persist(work);
        em.flush();
        person.addWork(work);
        personRepository.saveAndFlush(person);
        Long workId = work.getId();

        // Get all the personList where work equals to workId
        defaultPersonShouldBeFound("workId.equals=" + workId);

        // Get all the personList where work equals to (workId + 1)
        defaultPersonShouldNotBeFound("workId.equals=" + (workId + 1));
    }

    @Test
    @Transactional
    void getAllPeopleByResponsibilityIsEqualToSomething() throws Exception {
        Role responsibility;
        if (TestUtil.findAll(em, Role.class).isEmpty()) {
            personRepository.saveAndFlush(person);
            responsibility = RoleResourceIT.createEntity(em);
        } else {
            responsibility = TestUtil.findAll(em, Role.class).get(0);
        }
        em.persist(responsibility);
        em.flush();
        person.addResponsibility(responsibility);
        personRepository.saveAndFlush(person);
        Long responsibilityId = responsibility.getId();

        // Get all the personList where responsibility equals to responsibilityId
        defaultPersonShouldBeFound("responsibilityId.equals=" + responsibilityId);

        // Get all the personList where responsibility equals to (responsibilityId + 1)
        defaultPersonShouldNotBeFound("responsibilityId.equals=" + (responsibilityId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPersonShouldBeFound(String filter) throws Exception {
        restPersonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(person.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())))
            .andExpect(jsonPath("$.[*].dod").value(hasItem(DEFAULT_DOD.toString())))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())));

        // Check, that the count call also returns 1
        restPersonMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPersonShouldNotBeFound(String filter) throws Exception {
        restPersonMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPersonMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPerson() throws Exception {
        // Get the person
        restPersonMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPerson() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        int databaseSizeBeforeUpdate = personRepository.findAll().size();
        personSearchRepository.save(person);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personSearchRepository.findAll());

        // Update the person
        Person updatedPerson = personRepository.findById(person.getId()).get();
        // Disconnect from session so that the updates on updatedPerson are not directly saved in db
        em.detach(updatedPerson);
        updatedPerson
            .name(UPDATED_NAME)
            .gender(UPDATED_GENDER)
            .dob(UPDATED_DOB)
            .dod(UPDATED_DOD)
            .country(UPDATED_COUNTRY)
            .language(UPDATED_LANGUAGE);
        PersonDTO personDTO = personMapper.toDto(updatedPerson);

        restPersonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, personDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personDTO))
            )
            .andExpect(status().isOk());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
        Person testPerson = personList.get(personList.size() - 1);
        assertThat(testPerson.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPerson.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testPerson.getDob()).isEqualTo(UPDATED_DOB);
        assertThat(testPerson.getDod()).isEqualTo(UPDATED_DOD);
        assertThat(testPerson.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testPerson.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(personSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Person> personSearchList = IterableUtils.toList(personSearchRepository.findAll());
                Person testPersonSearch = personSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testPersonSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testPersonSearch.getGender()).isEqualTo(UPDATED_GENDER);
                assertThat(testPersonSearch.getDob()).isEqualTo(UPDATED_DOB);
                assertThat(testPersonSearch.getDod()).isEqualTo(UPDATED_DOD);
                assertThat(testPersonSearch.getCountry()).isEqualTo(UPDATED_COUNTRY);
                assertThat(testPersonSearch.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
            });
    }

    @Test
    @Transactional
    void putNonExistingPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personSearchRepository.findAll());
        person.setId(count.incrementAndGet());

        // Create the Person
        PersonDTO personDTO = personMapper.toDto(person);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, personDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personSearchRepository.findAll());
        person.setId(count.incrementAndGet());

        // Create the Person
        PersonDTO personDTO = personMapper.toDto(person);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(personDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personSearchRepository.findAll());
        person.setId(count.incrementAndGet());

        // Create the Person
        PersonDTO personDTO = personMapper.toDto(person);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(personDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdatePersonWithPatch() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        int databaseSizeBeforeUpdate = personRepository.findAll().size();

        // Update the person using partial update
        Person partialUpdatedPerson = new Person();
        partialUpdatedPerson.setId(person.getId());

        partialUpdatedPerson.dob(UPDATED_DOB);

        restPersonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPerson.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPerson))
            )
            .andExpect(status().isOk());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
        Person testPerson = personList.get(personList.size() - 1);
        assertThat(testPerson.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPerson.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testPerson.getDob()).isEqualTo(UPDATED_DOB);
        assertThat(testPerson.getDod()).isEqualTo(DEFAULT_DOD);
        assertThat(testPerson.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testPerson.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
    }

    @Test
    @Transactional
    void fullUpdatePersonWithPatch() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);

        int databaseSizeBeforeUpdate = personRepository.findAll().size();

        // Update the person using partial update
        Person partialUpdatedPerson = new Person();
        partialUpdatedPerson.setId(person.getId());

        partialUpdatedPerson
            .name(UPDATED_NAME)
            .gender(UPDATED_GENDER)
            .dob(UPDATED_DOB)
            .dod(UPDATED_DOD)
            .country(UPDATED_COUNTRY)
            .language(UPDATED_LANGUAGE);

        restPersonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPerson.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPerson))
            )
            .andExpect(status().isOk());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
        Person testPerson = personList.get(personList.size() - 1);
        assertThat(testPerson.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPerson.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testPerson.getDob()).isEqualTo(UPDATED_DOB);
        assertThat(testPerson.getDod()).isEqualTo(UPDATED_DOD);
        assertThat(testPerson.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testPerson.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    void patchNonExistingPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personSearchRepository.findAll());
        person.setId(count.incrementAndGet());

        // Create the Person
        PersonDTO personDTO = personMapper.toDto(person);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPersonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, personDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(personDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personSearchRepository.findAll());
        person.setId(count.incrementAndGet());

        // Create the Person
        PersonDTO personDTO = personMapper.toDto(person);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(personDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPerson() throws Exception {
        int databaseSizeBeforeUpdate = personRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personSearchRepository.findAll());
        person.setId(count.incrementAndGet());

        // Create the Person
        PersonDTO personDTO = personMapper.toDto(person);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPersonMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(personDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Person in the database
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deletePerson() throws Exception {
        // Initialize the database
        personRepository.saveAndFlush(person);
        personRepository.save(person);
        personSearchRepository.save(person);

        int databaseSizeBeforeDelete = personRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(personSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the person
        restPersonMockMvc
            .perform(delete(ENTITY_API_URL_ID, person.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Person> personList = personRepository.findAll();
        assertThat(personList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(personSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchPerson() throws Exception {
        // Initialize the database
        person = personRepository.saveAndFlush(person);
        personSearchRepository.save(person);

        // Search the person
        restPersonMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + person.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(person.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].dob").value(hasItem(DEFAULT_DOB.toString())))
            .andExpect(jsonPath("$.[*].dod").value(hasItem(DEFAULT_DOD.toString())))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())));
    }
}
