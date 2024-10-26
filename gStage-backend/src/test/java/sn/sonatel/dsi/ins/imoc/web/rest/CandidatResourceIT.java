package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static sn.sonatel.dsi.ins.imoc.domain.CandidatAsserts.*;
import static sn.sonatel.dsi.ins.imoc.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import sn.sonatel.dsi.ins.imoc.IntegrationTest;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.Manager;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.CandidateStatus;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.EducationLevel;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.repository.EntityManager;
import sn.sonatel.dsi.ins.imoc.repository.ManagerRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.CandidatDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.CandidatMapper;

/**
 * Integration tests for the {@link CandidatResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CandidatResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_BIRTH_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_NATIONALITY = "AAAAAAAAAA";
    private static final String UPDATED_NATIONALITY = "BBBBBBBBBB";

    private static final String DEFAULT_BIRTH_PLACE = "AAAAAAAAAA";
    private static final String UPDATED_BIRTH_PLACE = "BBBBBBBBBB";

    private static final String DEFAULT_ID_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_ID_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "N$@Gu.IfG6gb";
    private static final String UPDATED_EMAIL = "U/VvL@Z-~91.ia";

    private static final String DEFAULT_PHONE = "390343166";
    private static final String UPDATED_PHONE = "747056505";

    private static final EducationLevel DEFAULT_EDUCATION_LEVEL = EducationLevel.BAC;
    private static final EducationLevel UPDATED_EDUCATION_LEVEL = EducationLevel.BAC_PLUS_2;

    private static final String DEFAULT_SCHOOL = "AAAAAAAAAA";
    private static final String UPDATED_SCHOOL = "BBBBBBBBBB";

    private static final String DEFAULT_REGISTRATION_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_REGISTRATION_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENT_EDUCATION = "AAAAAAAAAA";
    private static final String UPDATED_CURRENT_EDUCATION = "BBBBBBBBBB";

    private static final CandidateStatus DEFAULT_STATUS = CandidateStatus.EN_ATTENTE;
    private static final CandidateStatus UPDATED_STATUS = CandidateStatus.ACCEPTE;

    private static final String ENTITY_API_URL = "/api/candidats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private CandidatMapper candidatMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Candidat candidat;

    private Candidat insertedCandidat;

    @Autowired
    private ManagerRepository managerRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Candidat createEntity() {
        return new Candidat()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .birthDate(DEFAULT_BIRTH_DATE)
            .nationality(DEFAULT_NATIONALITY)
            .birthPlace(DEFAULT_BIRTH_PLACE)
            .idNumber(DEFAULT_ID_NUMBER)
            .address(DEFAULT_ADDRESS)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .educationLevel(DEFAULT_EDUCATION_LEVEL)
            .school(DEFAULT_SCHOOL)
            .registrationNumber(DEFAULT_REGISTRATION_NUMBER)
            .currentEducation(DEFAULT_CURRENT_EDUCATION)
            .status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Candidat createUpdatedEntity() {
        return new Candidat()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .birthDate(UPDATED_BIRTH_DATE)
            .nationality(UPDATED_NATIONALITY)
            .birthPlace(UPDATED_BIRTH_PLACE)
            .idNumber(UPDATED_ID_NUMBER)
            .address(UPDATED_ADDRESS)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .educationLevel(UPDATED_EDUCATION_LEVEL)
            .school(UPDATED_SCHOOL)
            .registrationNumber(UPDATED_REGISTRATION_NUMBER)
            .currentEducation(UPDATED_CURRENT_EDUCATION)
            .status(UPDATED_STATUS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Candidat.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        candidat = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCandidat != null) {
            candidatRepository.delete(insertedCandidat).block();
            insertedCandidat = null;
        }
        deleteEntities(em);
    }

    @Test
    void createCandidat() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);
        var returnedCandidatDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(CandidatDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Candidat in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCandidat = candidatMapper.toEntity(returnedCandidatDTO);
        assertCandidatUpdatableFieldsEquals(returnedCandidat, getPersistedCandidat(returnedCandidat));

        insertedCandidat = returnedCandidat;
    }

    @Test
    void createCandidatWithExistingId() throws Exception {
        // Create the Candidat with an existing ID
        candidat.setId(1L);
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidat.setFirstName(null);

        // Create the Candidat, which fails.
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkLastNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidat.setLastName(null);

        // Create the Candidat, which fails.
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkBirthDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidat.setBirthDate(null);

        // Create the Candidat, which fails.
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkNationalityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidat.setNationality(null);

        // Create the Candidat, which fails.
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkBirthPlaceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidat.setBirthPlace(null);

        // Create the Candidat, which fails.
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkIdNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidat.setIdNumber(null);

        // Create the Candidat, which fails.
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkAddressIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidat.setAddress(null);

        // Create the Candidat, which fails.
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidat.setEmail(null);

        // Create the Candidat, which fails.
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPhoneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidat.setPhone(null);

        // Create the Candidat, which fails.
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEducationLevelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidat.setEducationLevel(null);

        // Create the Candidat, which fails.
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSchoolIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidat.setSchool(null);

        // Create the Candidat, which fails.
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCurrentEducationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidat.setCurrentEducation(null);

        // Create the Candidat, which fails.
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidat.setStatus(null);

        // Create the Candidat, which fails.
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllCandidats() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(candidat.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].birthDate")
            .value(hasItem(DEFAULT_BIRTH_DATE.toString()))
            .jsonPath("$.[*].nationality")
            .value(hasItem(DEFAULT_NATIONALITY))
            .jsonPath("$.[*].birthPlace")
            .value(hasItem(DEFAULT_BIRTH_PLACE))
            .jsonPath("$.[*].idNumber")
            .value(hasItem(DEFAULT_ID_NUMBER))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].educationLevel")
            .value(hasItem(DEFAULT_EDUCATION_LEVEL.toString()))
            .jsonPath("$.[*].school")
            .value(hasItem(DEFAULT_SCHOOL))
            .jsonPath("$.[*].registrationNumber")
            .value(hasItem(DEFAULT_REGISTRATION_NUMBER))
            .jsonPath("$.[*].currentEducation")
            .value(hasItem(DEFAULT_CURRENT_EDUCATION))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @Test
    void getCandidat() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get the candidat
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, candidat.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(candidat.getId().intValue()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.birthDate")
            .value(is(DEFAULT_BIRTH_DATE.toString()))
            .jsonPath("$.nationality")
            .value(is(DEFAULT_NATIONALITY))
            .jsonPath("$.birthPlace")
            .value(is(DEFAULT_BIRTH_PLACE))
            .jsonPath("$.idNumber")
            .value(is(DEFAULT_ID_NUMBER))
            .jsonPath("$.address")
            .value(is(DEFAULT_ADDRESS))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE))
            .jsonPath("$.educationLevel")
            .value(is(DEFAULT_EDUCATION_LEVEL.toString()))
            .jsonPath("$.school")
            .value(is(DEFAULT_SCHOOL))
            .jsonPath("$.registrationNumber")
            .value(is(DEFAULT_REGISTRATION_NUMBER))
            .jsonPath("$.currentEducation")
            .value(is(DEFAULT_CURRENT_EDUCATION))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getCandidatsByIdFiltering() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        Long id = candidat.getId();

        defaultCandidatFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCandidatFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCandidatFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllCandidatsByFirstNameIsEqualToSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where firstName equals to
        defaultCandidatFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllCandidatsByFirstNameIsInShouldWork() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where firstName in
        defaultCandidatFiltering("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME, "firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllCandidatsByFirstNameIsNullOrNotNull() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where firstName is not null
        defaultCandidatFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    void getAllCandidatsByFirstNameContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where firstName contains
        defaultCandidatFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllCandidatsByFirstNameNotContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where firstName does not contain
        defaultCandidatFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    @Test
    void getAllCandidatsByLastNameIsEqualToSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where lastName equals to
        defaultCandidatFiltering("lastName.equals=" + DEFAULT_LAST_NAME, "lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllCandidatsByLastNameIsInShouldWork() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where lastName in
        defaultCandidatFiltering("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME, "lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllCandidatsByLastNameIsNullOrNotNull() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where lastName is not null
        defaultCandidatFiltering("lastName.specified=true", "lastName.specified=false");
    }

    @Test
    void getAllCandidatsByLastNameContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where lastName contains
        defaultCandidatFiltering("lastName.contains=" + DEFAULT_LAST_NAME, "lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllCandidatsByLastNameNotContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where lastName does not contain
        defaultCandidatFiltering("lastName.doesNotContain=" + UPDATED_LAST_NAME, "lastName.doesNotContain=" + DEFAULT_LAST_NAME);
    }

    @Test
    void getAllCandidatsByBirthDateIsEqualToSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where birthDate equals to
        defaultCandidatFiltering("birthDate.equals=" + DEFAULT_BIRTH_DATE, "birthDate.equals=" + UPDATED_BIRTH_DATE);
    }

    @Test
    void getAllCandidatsByBirthDateIsInShouldWork() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where birthDate in
        defaultCandidatFiltering("birthDate.in=" + DEFAULT_BIRTH_DATE + "," + UPDATED_BIRTH_DATE, "birthDate.in=" + UPDATED_BIRTH_DATE);
    }

    @Test
    void getAllCandidatsByBirthDateIsNullOrNotNull() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where birthDate is not null
        defaultCandidatFiltering("birthDate.specified=true", "birthDate.specified=false");
    }

    @Test
    void getAllCandidatsByBirthDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where birthDate is greater than or equal to
        defaultCandidatFiltering(
            "birthDate.greaterThanOrEqual=" + DEFAULT_BIRTH_DATE,
            "birthDate.greaterThanOrEqual=" + UPDATED_BIRTH_DATE
        );
    }

    @Test
    void getAllCandidatsByBirthDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where birthDate is less than or equal to
        defaultCandidatFiltering("birthDate.lessThanOrEqual=" + DEFAULT_BIRTH_DATE, "birthDate.lessThanOrEqual=" + SMALLER_BIRTH_DATE);
    }

    @Test
    void getAllCandidatsByBirthDateIsLessThanSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where birthDate is less than
        defaultCandidatFiltering("birthDate.lessThan=" + UPDATED_BIRTH_DATE, "birthDate.lessThan=" + DEFAULT_BIRTH_DATE);
    }

    @Test
    void getAllCandidatsByBirthDateIsGreaterThanSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where birthDate is greater than
        defaultCandidatFiltering("birthDate.greaterThan=" + SMALLER_BIRTH_DATE, "birthDate.greaterThan=" + DEFAULT_BIRTH_DATE);
    }

    @Test
    void getAllCandidatsByNationalityIsEqualToSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where nationality equals to
        defaultCandidatFiltering("nationality.equals=" + DEFAULT_NATIONALITY, "nationality.equals=" + UPDATED_NATIONALITY);
    }

    @Test
    void getAllCandidatsByNationalityIsInShouldWork() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where nationality in
        defaultCandidatFiltering(
            "nationality.in=" + DEFAULT_NATIONALITY + "," + UPDATED_NATIONALITY,
            "nationality.in=" + UPDATED_NATIONALITY
        );
    }

    @Test
    void getAllCandidatsByNationalityIsNullOrNotNull() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where nationality is not null
        defaultCandidatFiltering("nationality.specified=true", "nationality.specified=false");
    }

    @Test
    void getAllCandidatsByNationalityContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where nationality contains
        defaultCandidatFiltering("nationality.contains=" + DEFAULT_NATIONALITY, "nationality.contains=" + UPDATED_NATIONALITY);
    }

    @Test
    void getAllCandidatsByNationalityNotContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where nationality does not contain
        defaultCandidatFiltering("nationality.doesNotContain=" + UPDATED_NATIONALITY, "nationality.doesNotContain=" + DEFAULT_NATIONALITY);
    }

    @Test
    void getAllCandidatsByBirthPlaceIsEqualToSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where birthPlace equals to
        defaultCandidatFiltering("birthPlace.equals=" + DEFAULT_BIRTH_PLACE, "birthPlace.equals=" + UPDATED_BIRTH_PLACE);
    }

    @Test
    void getAllCandidatsByBirthPlaceIsInShouldWork() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where birthPlace in
        defaultCandidatFiltering(
            "birthPlace.in=" + DEFAULT_BIRTH_PLACE + "," + UPDATED_BIRTH_PLACE,
            "birthPlace.in=" + UPDATED_BIRTH_PLACE
        );
    }

    @Test
    void getAllCandidatsByBirthPlaceIsNullOrNotNull() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where birthPlace is not null
        defaultCandidatFiltering("birthPlace.specified=true", "birthPlace.specified=false");
    }

    @Test
    void getAllCandidatsByBirthPlaceContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where birthPlace contains
        defaultCandidatFiltering("birthPlace.contains=" + DEFAULT_BIRTH_PLACE, "birthPlace.contains=" + UPDATED_BIRTH_PLACE);
    }

    @Test
    void getAllCandidatsByBirthPlaceNotContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where birthPlace does not contain
        defaultCandidatFiltering("birthPlace.doesNotContain=" + UPDATED_BIRTH_PLACE, "birthPlace.doesNotContain=" + DEFAULT_BIRTH_PLACE);
    }

    @Test
    void getAllCandidatsByIdNumberIsEqualToSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where idNumber equals to
        defaultCandidatFiltering("idNumber.equals=" + DEFAULT_ID_NUMBER, "idNumber.equals=" + UPDATED_ID_NUMBER);
    }

    @Test
    void getAllCandidatsByIdNumberIsInShouldWork() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where idNumber in
        defaultCandidatFiltering("idNumber.in=" + DEFAULT_ID_NUMBER + "," + UPDATED_ID_NUMBER, "idNumber.in=" + UPDATED_ID_NUMBER);
    }

    @Test
    void getAllCandidatsByIdNumberIsNullOrNotNull() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where idNumber is not null
        defaultCandidatFiltering("idNumber.specified=true", "idNumber.specified=false");
    }

    @Test
    void getAllCandidatsByIdNumberContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where idNumber contains
        defaultCandidatFiltering("idNumber.contains=" + DEFAULT_ID_NUMBER, "idNumber.contains=" + UPDATED_ID_NUMBER);
    }

    @Test
    void getAllCandidatsByIdNumberNotContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where idNumber does not contain
        defaultCandidatFiltering("idNumber.doesNotContain=" + UPDATED_ID_NUMBER, "idNumber.doesNotContain=" + DEFAULT_ID_NUMBER);
    }

    @Test
    void getAllCandidatsByAddressIsEqualToSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where address equals to
        defaultCandidatFiltering("address.equals=" + DEFAULT_ADDRESS, "address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    void getAllCandidatsByAddressIsInShouldWork() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where address in
        defaultCandidatFiltering("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS, "address.in=" + UPDATED_ADDRESS);
    }

    @Test
    void getAllCandidatsByAddressIsNullOrNotNull() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where address is not null
        defaultCandidatFiltering("address.specified=true", "address.specified=false");
    }

    @Test
    void getAllCandidatsByAddressContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where address contains
        defaultCandidatFiltering("address.contains=" + DEFAULT_ADDRESS, "address.contains=" + UPDATED_ADDRESS);
    }

    @Test
    void getAllCandidatsByAddressNotContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where address does not contain
        defaultCandidatFiltering("address.doesNotContain=" + UPDATED_ADDRESS, "address.doesNotContain=" + DEFAULT_ADDRESS);
    }

    @Test
    void getAllCandidatsByEmailIsEqualToSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where email equals to
        defaultCandidatFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    void getAllCandidatsByEmailIsInShouldWork() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where email in
        defaultCandidatFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    void getAllCandidatsByEmailIsNullOrNotNull() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where email is not null
        defaultCandidatFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    void getAllCandidatsByEmailContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where email contains
        defaultCandidatFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    void getAllCandidatsByEmailNotContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where email does not contain
        defaultCandidatFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    void getAllCandidatsByPhoneIsEqualToSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where phone equals to
        defaultCandidatFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    void getAllCandidatsByPhoneIsInShouldWork() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where phone in
        defaultCandidatFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    void getAllCandidatsByPhoneIsNullOrNotNull() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where phone is not null
        defaultCandidatFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    void getAllCandidatsByPhoneContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where phone contains
        defaultCandidatFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    void getAllCandidatsByPhoneNotContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where phone does not contain
        defaultCandidatFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    void getAllCandidatsByEducationLevelIsEqualToSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where educationLevel equals to
        defaultCandidatFiltering("educationLevel.equals=" + DEFAULT_EDUCATION_LEVEL, "educationLevel.equals=" + UPDATED_EDUCATION_LEVEL);
    }

    @Test
    void getAllCandidatsByEducationLevelIsInShouldWork() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where educationLevel in
        defaultCandidatFiltering(
            "educationLevel.in=" + DEFAULT_EDUCATION_LEVEL + "," + UPDATED_EDUCATION_LEVEL,
            "educationLevel.in=" + UPDATED_EDUCATION_LEVEL
        );
    }

    @Test
    void getAllCandidatsByEducationLevelIsNullOrNotNull() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where educationLevel is not null
        defaultCandidatFiltering("educationLevel.specified=true", "educationLevel.specified=false");
    }

    @Test
    void getAllCandidatsBySchoolIsEqualToSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where school equals to
        defaultCandidatFiltering("school.equals=" + DEFAULT_SCHOOL, "school.equals=" + UPDATED_SCHOOL);
    }

    @Test
    void getAllCandidatsBySchoolIsInShouldWork() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where school in
        defaultCandidatFiltering("school.in=" + DEFAULT_SCHOOL + "," + UPDATED_SCHOOL, "school.in=" + UPDATED_SCHOOL);
    }

    @Test
    void getAllCandidatsBySchoolIsNullOrNotNull() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where school is not null
        defaultCandidatFiltering("school.specified=true", "school.specified=false");
    }

    @Test
    void getAllCandidatsBySchoolContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where school contains
        defaultCandidatFiltering("school.contains=" + DEFAULT_SCHOOL, "school.contains=" + UPDATED_SCHOOL);
    }

    @Test
    void getAllCandidatsBySchoolNotContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where school does not contain
        defaultCandidatFiltering("school.doesNotContain=" + UPDATED_SCHOOL, "school.doesNotContain=" + DEFAULT_SCHOOL);
    }

    @Test
    void getAllCandidatsByRegistrationNumberIsEqualToSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where registrationNumber equals to
        defaultCandidatFiltering(
            "registrationNumber.equals=" + DEFAULT_REGISTRATION_NUMBER,
            "registrationNumber.equals=" + UPDATED_REGISTRATION_NUMBER
        );
    }

    @Test
    void getAllCandidatsByRegistrationNumberIsInShouldWork() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where registrationNumber in
        defaultCandidatFiltering(
            "registrationNumber.in=" + DEFAULT_REGISTRATION_NUMBER + "," + UPDATED_REGISTRATION_NUMBER,
            "registrationNumber.in=" + UPDATED_REGISTRATION_NUMBER
        );
    }

    @Test
    void getAllCandidatsByRegistrationNumberIsNullOrNotNull() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where registrationNumber is not null
        defaultCandidatFiltering("registrationNumber.specified=true", "registrationNumber.specified=false");
    }

    @Test
    void getAllCandidatsByRegistrationNumberContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where registrationNumber contains
        defaultCandidatFiltering(
            "registrationNumber.contains=" + DEFAULT_REGISTRATION_NUMBER,
            "registrationNumber.contains=" + UPDATED_REGISTRATION_NUMBER
        );
    }

    @Test
    void getAllCandidatsByRegistrationNumberNotContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where registrationNumber does not contain
        defaultCandidatFiltering(
            "registrationNumber.doesNotContain=" + UPDATED_REGISTRATION_NUMBER,
            "registrationNumber.doesNotContain=" + DEFAULT_REGISTRATION_NUMBER
        );
    }

    @Test
    void getAllCandidatsByCurrentEducationIsEqualToSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where currentEducation equals to
        defaultCandidatFiltering(
            "currentEducation.equals=" + DEFAULT_CURRENT_EDUCATION,
            "currentEducation.equals=" + UPDATED_CURRENT_EDUCATION
        );
    }

    @Test
    void getAllCandidatsByCurrentEducationIsInShouldWork() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where currentEducation in
        defaultCandidatFiltering(
            "currentEducation.in=" + DEFAULT_CURRENT_EDUCATION + "," + UPDATED_CURRENT_EDUCATION,
            "currentEducation.in=" + UPDATED_CURRENT_EDUCATION
        );
    }

    @Test
    void getAllCandidatsByCurrentEducationIsNullOrNotNull() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where currentEducation is not null
        defaultCandidatFiltering("currentEducation.specified=true", "currentEducation.specified=false");
    }

    @Test
    void getAllCandidatsByCurrentEducationContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where currentEducation contains
        defaultCandidatFiltering(
            "currentEducation.contains=" + DEFAULT_CURRENT_EDUCATION,
            "currentEducation.contains=" + UPDATED_CURRENT_EDUCATION
        );
    }

    @Test
    void getAllCandidatsByCurrentEducationNotContainsSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where currentEducation does not contain
        defaultCandidatFiltering(
            "currentEducation.doesNotContain=" + UPDATED_CURRENT_EDUCATION,
            "currentEducation.doesNotContain=" + DEFAULT_CURRENT_EDUCATION
        );
    }

    @Test
    void getAllCandidatsByStatusIsEqualToSomething() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where status equals to
        defaultCandidatFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    void getAllCandidatsByStatusIsInShouldWork() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where status in
        defaultCandidatFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    void getAllCandidatsByStatusIsNullOrNotNull() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        // Get all the candidatList where status is not null
        defaultCandidatFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    void getAllCandidatsByManagerIsEqualToSomething() {
        Manager manager = ManagerResourceIT.createEntity();
        managerRepository.save(manager).block();
        Long managerId = manager.getId();
        candidat.setManagerId(managerId);
        insertedCandidat = candidatRepository.save(candidat).block();
        // Get all the candidatList where manager equals to managerId
        defaultCandidatShouldBeFound("managerId.equals=" + managerId);

        // Get all the candidatList where manager equals to (managerId + 1)
        defaultCandidatShouldNotBeFound("managerId.equals=" + (managerId + 1));
    }

    private void defaultCandidatFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultCandidatShouldBeFound(shouldBeFound);
        defaultCandidatShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCandidatShouldBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(candidat.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].birthDate")
            .value(hasItem(DEFAULT_BIRTH_DATE.toString()))
            .jsonPath("$.[*].nationality")
            .value(hasItem(DEFAULT_NATIONALITY))
            .jsonPath("$.[*].birthPlace")
            .value(hasItem(DEFAULT_BIRTH_PLACE))
            .jsonPath("$.[*].idNumber")
            .value(hasItem(DEFAULT_ID_NUMBER))
            .jsonPath("$.[*].address")
            .value(hasItem(DEFAULT_ADDRESS))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].educationLevel")
            .value(hasItem(DEFAULT_EDUCATION_LEVEL.toString()))
            .jsonPath("$.[*].school")
            .value(hasItem(DEFAULT_SCHOOL))
            .jsonPath("$.[*].registrationNumber")
            .value(hasItem(DEFAULT_REGISTRATION_NUMBER))
            .jsonPath("$.[*].currentEducation")
            .value(hasItem(DEFAULT_CURRENT_EDUCATION))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));

        // Check, that the count call also returns 1
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(1));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCandidatShouldNotBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .isArray()
            .jsonPath("$")
            .isEmpty();

        // Check, that the count call also returns 0
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(0));
    }

    @Test
    void getNonExistingCandidat() {
        // Get the candidat
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingCandidat() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the candidat
        Candidat updatedCandidat = candidatRepository.findById(candidat.getId()).block();
        updatedCandidat
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .birthDate(UPDATED_BIRTH_DATE)
            .nationality(UPDATED_NATIONALITY)
            .birthPlace(UPDATED_BIRTH_PLACE)
            .idNumber(UPDATED_ID_NUMBER)
            .address(UPDATED_ADDRESS)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .educationLevel(UPDATED_EDUCATION_LEVEL)
            .school(UPDATED_SCHOOL)
            .registrationNumber(UPDATED_REGISTRATION_NUMBER)
            .currentEducation(UPDATED_CURRENT_EDUCATION)
            .status(UPDATED_STATUS);
        CandidatDTO candidatDTO = candidatMapper.toDto(updatedCandidat);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, candidatDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCandidatToMatchAllProperties(updatedCandidat);
    }

    @Test
    void putNonExistingCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, candidatDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCandidatWithPatch() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the candidat using partial update
        Candidat partialUpdatedCandidat = new Candidat();
        partialUpdatedCandidat.setId(candidat.getId());

        partialUpdatedCandidat
            .lastName(UPDATED_LAST_NAME)
            .nationality(UPDATED_NATIONALITY)
            .birthPlace(UPDATED_BIRTH_PLACE)
            .phone(UPDATED_PHONE)
            .educationLevel(UPDATED_EDUCATION_LEVEL)
            .registrationNumber(UPDATED_REGISTRATION_NUMBER)
            .currentEducation(UPDATED_CURRENT_EDUCATION)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCandidat.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCandidat))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Candidat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCandidatUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCandidat, candidat), getPersistedCandidat(candidat));
    }

    @Test
    void fullUpdateCandidatWithPatch() throws Exception {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the candidat using partial update
        Candidat partialUpdatedCandidat = new Candidat();
        partialUpdatedCandidat.setId(candidat.getId());

        partialUpdatedCandidat
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .birthDate(UPDATED_BIRTH_DATE)
            .nationality(UPDATED_NATIONALITY)
            .birthPlace(UPDATED_BIRTH_PLACE)
            .idNumber(UPDATED_ID_NUMBER)
            .address(UPDATED_ADDRESS)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .educationLevel(UPDATED_EDUCATION_LEVEL)
            .school(UPDATED_SCHOOL)
            .registrationNumber(UPDATED_REGISTRATION_NUMBER)
            .currentEducation(UPDATED_CURRENT_EDUCATION)
            .status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCandidat.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedCandidat))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Candidat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCandidatUpdatableFieldsEquals(partialUpdatedCandidat, getPersistedCandidat(partialUpdatedCandidat));
    }

    @Test
    void patchNonExistingCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, candidatDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidat.setId(longCount.incrementAndGet());

        // Create the Candidat
        CandidatDTO candidatDTO = candidatMapper.toDto(candidat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(candidatDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Candidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCandidat() {
        // Initialize the database
        insertedCandidat = candidatRepository.save(candidat).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the candidat
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, candidat.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return candidatRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Candidat getPersistedCandidat(Candidat candidat) {
        return candidatRepository.findById(candidat.getId()).block();
    }

    protected void assertPersistedCandidatToMatchAllProperties(Candidat expectedCandidat) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCandidatAllPropertiesEquals(expectedCandidat, getPersistedCandidat(expectedCandidat));
        assertCandidatUpdatableFieldsEquals(expectedCandidat, getPersistedCandidat(expectedCandidat));
    }

    protected void assertPersistedCandidatToMatchUpdatableProperties(Candidat expectedCandidat) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCandidatAllUpdatablePropertiesEquals(expectedCandidat, getPersistedCandidat(expectedCandidat));
        assertCandidatUpdatableFieldsEquals(expectedCandidat, getPersistedCandidat(expectedCandidat));
    }
}
