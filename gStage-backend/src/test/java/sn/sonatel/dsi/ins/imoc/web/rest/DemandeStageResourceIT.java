package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static sn.sonatel.dsi.ins.imoc.domain.DemandeStageAsserts.*;
import static sn.sonatel.dsi.ins.imoc.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
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
import sn.sonatel.dsi.ins.imoc.domain.AssistantGWTE;
import sn.sonatel.dsi.ins.imoc.domain.BusinessUnit;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.Departement;
import sn.sonatel.dsi.ins.imoc.domain.Manager;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipType;
import sn.sonatel.dsi.ins.imoc.repository.AssistantGWTERepository;
import sn.sonatel.dsi.ins.imoc.repository.BusinessUnitRepository;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.repository.DepartementRepository;
import sn.sonatel.dsi.ins.imoc.repository.EntityManager;
import sn.sonatel.dsi.ins.imoc.repository.ManagerRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.DemandeStageDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.DemandeStageMapper;

/**
 * Integration tests for the {@link DemandeStageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DemandeStageResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CREATION_DATE = LocalDate.ofEpochDay(-1L);

    private static final InternshipStatus DEFAULT_STATUS = InternshipStatus.EN_ATTENTE;
    private static final InternshipStatus UPDATED_STATUS = InternshipStatus.ACCEPTE;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final InternshipType DEFAULT_INTERNSHIP_TYPE = InternshipType.ACADEMIQUE;
    private static final InternshipType UPDATED_INTERNSHIP_TYPE = InternshipType.PROFESSIONNEL;

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_DATE = LocalDate.ofEpochDay(-1L);

    private static final byte[] DEFAULT_RESUME = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_RESUME = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_RESUME_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_RESUME_CONTENT_TYPE = "image/png";

    private static final byte[] DEFAULT_COVER_LETTER = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_COVER_LETTER = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_COVER_LETTER_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_COVER_LETTER_CONTENT_TYPE = "image/png";

    private static final Boolean DEFAULT_VALIDATED = false;
    private static final Boolean UPDATED_VALIDATED = true;

    private static final String ENTITY_API_URL = "/api/demande-stages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DemandeStageRepository demandeStageRepository;

    @Autowired
    private DemandeStageMapper demandeStageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private DemandeStage demandeStage;

    private DemandeStage insertedDemandeStage;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private AssistantGWTERepository assistantGWTERepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private DepartementRepository departementRepository;

    @Autowired
    private BusinessUnitRepository businessUnitRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DemandeStage createEntity() {
        return new DemandeStage()
            .reference(DEFAULT_REFERENCE)
            .creationDate(DEFAULT_CREATION_DATE)
            .status(DEFAULT_STATUS)
            .description(DEFAULT_DESCRIPTION)
            .internshipType(DEFAULT_INTERNSHIP_TYPE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .resume(DEFAULT_RESUME)
            .resumeContentType(DEFAULT_RESUME_CONTENT_TYPE)
            .coverLetter(DEFAULT_COVER_LETTER)
            .coverLetterContentType(DEFAULT_COVER_LETTER_CONTENT_TYPE)
            .validated(DEFAULT_VALIDATED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DemandeStage createUpdatedEntity() {
        return new DemandeStage()
            .reference(UPDATED_REFERENCE)
            .creationDate(UPDATED_CREATION_DATE)
            .status(UPDATED_STATUS)
            .description(UPDATED_DESCRIPTION)
            .internshipType(UPDATED_INTERNSHIP_TYPE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .resume(UPDATED_RESUME)
            .resumeContentType(UPDATED_RESUME_CONTENT_TYPE)
            .coverLetter(UPDATED_COVER_LETTER)
            .coverLetterContentType(UPDATED_COVER_LETTER_CONTENT_TYPE)
            .validated(UPDATED_VALIDATED);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(DemandeStage.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        demandeStage = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedDemandeStage != null) {
            demandeStageRepository.delete(insertedDemandeStage).block();
            insertedDemandeStage = null;
        }
        deleteEntities(em);
    }

    @Test
    void createDemandeStage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DemandeStage
        DemandeStageDTO demandeStageDTO = demandeStageMapper.toDto(demandeStage);
        var returnedDemandeStageDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(demandeStageDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DemandeStageDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the DemandeStage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDemandeStage = demandeStageMapper.toEntity(returnedDemandeStageDTO);
        assertDemandeStageUpdatableFieldsEquals(returnedDemandeStage, getPersistedDemandeStage(returnedDemandeStage));

        insertedDemandeStage = returnedDemandeStage;
    }

    @Test
    void createDemandeStageWithExistingId() throws Exception {
        // Create the DemandeStage with an existing ID
        demandeStage.setId(1L);
        DemandeStageDTO demandeStageDTO = demandeStageMapper.toDto(demandeStage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(demandeStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DemandeStage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        demandeStage.setReference(null);

        // Create the DemandeStage, which fails.
        DemandeStageDTO demandeStageDTO = demandeStageMapper.toDto(demandeStage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(demandeStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreationDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        demandeStage.setCreationDate(null);

        // Create the DemandeStage, which fails.
        DemandeStageDTO demandeStageDTO = demandeStageMapper.toDto(demandeStage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(demandeStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        demandeStage.setStatus(null);

        // Create the DemandeStage, which fails.
        DemandeStageDTO demandeStageDTO = demandeStageMapper.toDto(demandeStage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(demandeStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkInternshipTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        demandeStage.setInternshipType(null);

        // Create the DemandeStage, which fails.
        DemandeStageDTO demandeStageDTO = demandeStageMapper.toDto(demandeStage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(demandeStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        demandeStage.setStartDate(null);

        // Create the DemandeStage, which fails.
        DemandeStageDTO demandeStageDTO = demandeStageMapper.toDto(demandeStage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(demandeStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        demandeStage.setEndDate(null);

        // Create the DemandeStage, which fails.
        DemandeStageDTO demandeStageDTO = demandeStageMapper.toDto(demandeStage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(demandeStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllDemandeStages() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList
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
            .value(hasItem(demandeStage.getId().intValue()))
            .jsonPath("$.[*].reference")
            .value(hasItem(DEFAULT_REFERENCE))
            .jsonPath("$.[*].creationDate")
            .value(hasItem(DEFAULT_CREATION_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].internshipType")
            .value(hasItem(DEFAULT_INTERNSHIP_TYPE.toString()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))
            .jsonPath("$.[*].resumeContentType")
            .value(hasItem(DEFAULT_RESUME_CONTENT_TYPE))
            .jsonPath("$.[*].resume")
            .value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_RESUME)))
            .jsonPath("$.[*].coverLetterContentType")
            .value(hasItem(DEFAULT_COVER_LETTER_CONTENT_TYPE))
            .jsonPath("$.[*].coverLetter")
            .value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_COVER_LETTER)))
            .jsonPath("$.[*].validated")
            .value(hasItem(DEFAULT_VALIDATED.booleanValue()));
    }

    @Test
    void getDemandeStage() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get the demandeStage
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, demandeStage.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(demandeStage.getId().intValue()))
            .jsonPath("$.reference")
            .value(is(DEFAULT_REFERENCE))
            .jsonPath("$.creationDate")
            .value(is(DEFAULT_CREATION_DATE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.internshipType")
            .value(is(DEFAULT_INTERNSHIP_TYPE.toString()))
            .jsonPath("$.startDate")
            .value(is(DEFAULT_START_DATE.toString()))
            .jsonPath("$.endDate")
            .value(is(DEFAULT_END_DATE.toString()))
            .jsonPath("$.resumeContentType")
            .value(is(DEFAULT_RESUME_CONTENT_TYPE))
            .jsonPath("$.resume")
            .value(is(Base64.getEncoder().encodeToString(DEFAULT_RESUME)))
            .jsonPath("$.coverLetterContentType")
            .value(is(DEFAULT_COVER_LETTER_CONTENT_TYPE))
            .jsonPath("$.coverLetter")
            .value(is(Base64.getEncoder().encodeToString(DEFAULT_COVER_LETTER)))
            .jsonPath("$.validated")
            .value(is(DEFAULT_VALIDATED.booleanValue()));
    }

    @Test
    void getDemandeStagesByIdFiltering() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        Long id = demandeStage.getId();

        defaultDemandeStageFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDemandeStageFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDemandeStageFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllDemandeStagesByReferenceIsEqualToSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where reference equals to
        defaultDemandeStageFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    void getAllDemandeStagesByReferenceIsInShouldWork() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where reference in
        defaultDemandeStageFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    void getAllDemandeStagesByReferenceIsNullOrNotNull() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where reference is not null
        defaultDemandeStageFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    void getAllDemandeStagesByReferenceContainsSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where reference contains
        defaultDemandeStageFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    void getAllDemandeStagesByReferenceNotContainsSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where reference does not contain
        defaultDemandeStageFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    void getAllDemandeStagesByCreationDateIsEqualToSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where creationDate equals to
        defaultDemandeStageFiltering("creationDate.equals=" + DEFAULT_CREATION_DATE, "creationDate.equals=" + UPDATED_CREATION_DATE);
    }

    @Test
    void getAllDemandeStagesByCreationDateIsInShouldWork() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where creationDate in
        defaultDemandeStageFiltering(
            "creationDate.in=" + DEFAULT_CREATION_DATE + "," + UPDATED_CREATION_DATE,
            "creationDate.in=" + UPDATED_CREATION_DATE
        );
    }

    @Test
    void getAllDemandeStagesByCreationDateIsNullOrNotNull() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where creationDate is not null
        defaultDemandeStageFiltering("creationDate.specified=true", "creationDate.specified=false");
    }

    @Test
    void getAllDemandeStagesByCreationDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where creationDate is greater than or equal to
        defaultDemandeStageFiltering(
            "creationDate.greaterThanOrEqual=" + DEFAULT_CREATION_DATE,
            "creationDate.greaterThanOrEqual=" + UPDATED_CREATION_DATE
        );
    }

    @Test
    void getAllDemandeStagesByCreationDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where creationDate is less than or equal to
        defaultDemandeStageFiltering(
            "creationDate.lessThanOrEqual=" + DEFAULT_CREATION_DATE,
            "creationDate.lessThanOrEqual=" + SMALLER_CREATION_DATE
        );
    }

    @Test
    void getAllDemandeStagesByCreationDateIsLessThanSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where creationDate is less than
        defaultDemandeStageFiltering("creationDate.lessThan=" + UPDATED_CREATION_DATE, "creationDate.lessThan=" + DEFAULT_CREATION_DATE);
    }

    @Test
    void getAllDemandeStagesByCreationDateIsGreaterThanSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where creationDate is greater than
        defaultDemandeStageFiltering(
            "creationDate.greaterThan=" + SMALLER_CREATION_DATE,
            "creationDate.greaterThan=" + DEFAULT_CREATION_DATE
        );
    }

    @Test
    void getAllDemandeStagesByStatusIsEqualToSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where status equals to
        defaultDemandeStageFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    void getAllDemandeStagesByStatusIsInShouldWork() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where status in
        defaultDemandeStageFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    void getAllDemandeStagesByStatusIsNullOrNotNull() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where status is not null
        defaultDemandeStageFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    void getAllDemandeStagesByInternshipTypeIsEqualToSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where internshipType equals to
        defaultDemandeStageFiltering(
            "internshipType.equals=" + DEFAULT_INTERNSHIP_TYPE,
            "internshipType.equals=" + UPDATED_INTERNSHIP_TYPE
        );
    }

    @Test
    void getAllDemandeStagesByInternshipTypeIsInShouldWork() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where internshipType in
        defaultDemandeStageFiltering(
            "internshipType.in=" + DEFAULT_INTERNSHIP_TYPE + "," + UPDATED_INTERNSHIP_TYPE,
            "internshipType.in=" + UPDATED_INTERNSHIP_TYPE
        );
    }

    @Test
    void getAllDemandeStagesByInternshipTypeIsNullOrNotNull() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where internshipType is not null
        defaultDemandeStageFiltering("internshipType.specified=true", "internshipType.specified=false");
    }

    @Test
    void getAllDemandeStagesByStartDateIsEqualToSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where startDate equals to
        defaultDemandeStageFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    void getAllDemandeStagesByStartDateIsInShouldWork() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where startDate in
        defaultDemandeStageFiltering("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE, "startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    void getAllDemandeStagesByStartDateIsNullOrNotNull() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where startDate is not null
        defaultDemandeStageFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    void getAllDemandeStagesByStartDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where startDate is greater than or equal to
        defaultDemandeStageFiltering(
            "startDate.greaterThanOrEqual=" + DEFAULT_START_DATE,
            "startDate.greaterThanOrEqual=" + UPDATED_START_DATE
        );
    }

    @Test
    void getAllDemandeStagesByStartDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where startDate is less than or equal to
        defaultDemandeStageFiltering("startDate.lessThanOrEqual=" + DEFAULT_START_DATE, "startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    void getAllDemandeStagesByStartDateIsLessThanSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where startDate is less than
        defaultDemandeStageFiltering("startDate.lessThan=" + UPDATED_START_DATE, "startDate.lessThan=" + DEFAULT_START_DATE);
    }

    @Test
    void getAllDemandeStagesByStartDateIsGreaterThanSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where startDate is greater than
        defaultDemandeStageFiltering("startDate.greaterThan=" + SMALLER_START_DATE, "startDate.greaterThan=" + DEFAULT_START_DATE);
    }

    @Test
    void getAllDemandeStagesByEndDateIsEqualToSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where endDate equals to
        defaultDemandeStageFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    void getAllDemandeStagesByEndDateIsInShouldWork() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where endDate in
        defaultDemandeStageFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    void getAllDemandeStagesByEndDateIsNullOrNotNull() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where endDate is not null
        defaultDemandeStageFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    void getAllDemandeStagesByEndDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where endDate is greater than or equal to
        defaultDemandeStageFiltering("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE, "endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    void getAllDemandeStagesByEndDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where endDate is less than or equal to
        defaultDemandeStageFiltering("endDate.lessThanOrEqual=" + DEFAULT_END_DATE, "endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    void getAllDemandeStagesByEndDateIsLessThanSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where endDate is less than
        defaultDemandeStageFiltering("endDate.lessThan=" + UPDATED_END_DATE, "endDate.lessThan=" + DEFAULT_END_DATE);
    }

    @Test
    void getAllDemandeStagesByEndDateIsGreaterThanSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where endDate is greater than
        defaultDemandeStageFiltering("endDate.greaterThan=" + SMALLER_END_DATE, "endDate.greaterThan=" + DEFAULT_END_DATE);
    }

    @Test
    void getAllDemandeStagesByValidatedIsEqualToSomething() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where validated equals to
        defaultDemandeStageFiltering("validated.equals=" + DEFAULT_VALIDATED, "validated.equals=" + UPDATED_VALIDATED);
    }

    @Test
    void getAllDemandeStagesByValidatedIsInShouldWork() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where validated in
        defaultDemandeStageFiltering("validated.in=" + DEFAULT_VALIDATED + "," + UPDATED_VALIDATED, "validated.in=" + UPDATED_VALIDATED);
    }

    @Test
    void getAllDemandeStagesByValidatedIsNullOrNotNull() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        // Get all the demandeStageList where validated is not null
        defaultDemandeStageFiltering("validated.specified=true", "validated.specified=false");
    }

    @Test
    void getAllDemandeStagesByCandidatIsEqualToSomething() {
        Candidat candidat = CandidatResourceIT.createEntity();
        candidatRepository.save(candidat).block();
        Long candidatId = candidat.getId();
        demandeStage.setCandidatId(candidatId);
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();
        // Get all the demandeStageList where candidat equals to candidatId
        defaultDemandeStageShouldBeFound("candidatId.equals=" + candidatId);

        // Get all the demandeStageList where candidat equals to (candidatId + 1)
        defaultDemandeStageShouldNotBeFound("candidatId.equals=" + (candidatId + 1));
    }

    @Test
    void getAllDemandeStagesByAssistantGWTEIsEqualToSomething() {
        AssistantGWTE assistantGWTE = AssistantGWTEResourceIT.createEntity();
        assistantGWTERepository.save(assistantGWTE).block();
        Long assistantGWTEId = assistantGWTE.getId();
        demandeStage.setAssistantGWTEId(assistantGWTEId);
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();
        // Get all the demandeStageList where assistantGWTE equals to assistantGWTEId
        defaultDemandeStageShouldBeFound("assistantGWTEId.equals=" + assistantGWTEId);

        // Get all the demandeStageList where assistantGWTE equals to (assistantGWTEId + 1)
        defaultDemandeStageShouldNotBeFound("assistantGWTEId.equals=" + (assistantGWTEId + 1));
    }

    @Test
    void getAllDemandeStagesByManagerIsEqualToSomething() {
        Manager manager = ManagerResourceIT.createEntity();
        managerRepository.save(manager).block();
        Long managerId = manager.getId();
        demandeStage.setManagerId(managerId);
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();
        // Get all the demandeStageList where manager equals to managerId
        defaultDemandeStageShouldBeFound("managerId.equals=" + managerId);

        // Get all the demandeStageList where manager equals to (managerId + 1)
        defaultDemandeStageShouldNotBeFound("managerId.equals=" + (managerId + 1));
    }

    @Test
    void getAllDemandeStagesByDepartementIsEqualToSomething() {
        Departement departement = DepartementResourceIT.createEntity();
        departementRepository.save(departement).block();
        Long departementId = departement.getId();
        demandeStage.setDepartementId(departementId);
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();
        // Get all the demandeStageList where departement equals to departementId
        defaultDemandeStageShouldBeFound("departementId.equals=" + departementId);

        // Get all the demandeStageList where departement equals to (departementId + 1)
        defaultDemandeStageShouldNotBeFound("departementId.equals=" + (departementId + 1));
    }

    @Test
    void getAllDemandeStagesByBusinessUnitIsEqualToSomething() {
        BusinessUnit businessUnit = BusinessUnitResourceIT.createEntity();
        businessUnitRepository.save(businessUnit).block();
        Long businessUnitId = businessUnit.getId();
        demandeStage.setBusinessUnitId(businessUnitId);
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();
        // Get all the demandeStageList where businessUnit equals to businessUnitId
        defaultDemandeStageShouldBeFound("businessUnitId.equals=" + businessUnitId);

        // Get all the demandeStageList where businessUnit equals to (businessUnitId + 1)
        defaultDemandeStageShouldNotBeFound("businessUnitId.equals=" + (businessUnitId + 1));
    }

    private void defaultDemandeStageFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultDemandeStageShouldBeFound(shouldBeFound);
        defaultDemandeStageShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDemandeStageShouldBeFound(String filter) {
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
            .value(hasItem(demandeStage.getId().intValue()))
            .jsonPath("$.[*].reference")
            .value(hasItem(DEFAULT_REFERENCE))
            .jsonPath("$.[*].creationDate")
            .value(hasItem(DEFAULT_CREATION_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].internshipType")
            .value(hasItem(DEFAULT_INTERNSHIP_TYPE.toString()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))
            .jsonPath("$.[*].resumeContentType")
            .value(hasItem(DEFAULT_RESUME_CONTENT_TYPE))
            .jsonPath("$.[*].resume")
            .value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_RESUME)))
            .jsonPath("$.[*].coverLetterContentType")
            .value(hasItem(DEFAULT_COVER_LETTER_CONTENT_TYPE))
            .jsonPath("$.[*].coverLetter")
            .value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_COVER_LETTER)))
            .jsonPath("$.[*].validated")
            .value(hasItem(DEFAULT_VALIDATED.booleanValue()));

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
    private void defaultDemandeStageShouldNotBeFound(String filter) {
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
    void getNonExistingDemandeStage() {
        // Get the demandeStage
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDemandeStage() throws Exception {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the demandeStage
        DemandeStage updatedDemandeStage = demandeStageRepository.findById(demandeStage.getId()).block();
        updatedDemandeStage
            .reference(UPDATED_REFERENCE)
            .creationDate(UPDATED_CREATION_DATE)
            .status(UPDATED_STATUS)
            .description(UPDATED_DESCRIPTION)
            .internshipType(UPDATED_INTERNSHIP_TYPE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .resume(UPDATED_RESUME)
            .resumeContentType(UPDATED_RESUME_CONTENT_TYPE)
            .coverLetter(UPDATED_COVER_LETTER)
            .coverLetterContentType(UPDATED_COVER_LETTER_CONTENT_TYPE)
            .validated(UPDATED_VALIDATED);
        DemandeStageDTO demandeStageDTO = demandeStageMapper.toDto(updatedDemandeStage);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, demandeStageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(demandeStageDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DemandeStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDemandeStageToMatchAllProperties(updatedDemandeStage);
    }

    @Test
    void putNonExistingDemandeStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandeStage.setId(longCount.incrementAndGet());

        // Create the DemandeStage
        DemandeStageDTO demandeStageDTO = demandeStageMapper.toDto(demandeStage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, demandeStageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(demandeStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DemandeStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDemandeStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandeStage.setId(longCount.incrementAndGet());

        // Create the DemandeStage
        DemandeStageDTO demandeStageDTO = demandeStageMapper.toDto(demandeStage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(demandeStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DemandeStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDemandeStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandeStage.setId(longCount.incrementAndGet());

        // Create the DemandeStage
        DemandeStageDTO demandeStageDTO = demandeStageMapper.toDto(demandeStage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(demandeStageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DemandeStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDemandeStageWithPatch() throws Exception {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the demandeStage using partial update
        DemandeStage partialUpdatedDemandeStage = new DemandeStage();
        partialUpdatedDemandeStage.setId(demandeStage.getId());

        partialUpdatedDemandeStage
            .reference(UPDATED_REFERENCE)
            .description(UPDATED_DESCRIPTION)
            .internshipType(UPDATED_INTERNSHIP_TYPE)
            .resume(UPDATED_RESUME)
            .resumeContentType(UPDATED_RESUME_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDemandeStage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDemandeStage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DemandeStage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDemandeStageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDemandeStage, demandeStage),
            getPersistedDemandeStage(demandeStage)
        );
    }

    @Test
    void fullUpdateDemandeStageWithPatch() throws Exception {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the demandeStage using partial update
        DemandeStage partialUpdatedDemandeStage = new DemandeStage();
        partialUpdatedDemandeStage.setId(demandeStage.getId());

        partialUpdatedDemandeStage
            .reference(UPDATED_REFERENCE)
            .creationDate(UPDATED_CREATION_DATE)
            .status(UPDATED_STATUS)
            .description(UPDATED_DESCRIPTION)
            .internshipType(UPDATED_INTERNSHIP_TYPE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .resume(UPDATED_RESUME)
            .resumeContentType(UPDATED_RESUME_CONTENT_TYPE)
            .coverLetter(UPDATED_COVER_LETTER)
            .coverLetterContentType(UPDATED_COVER_LETTER_CONTENT_TYPE)
            .validated(UPDATED_VALIDATED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDemandeStage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDemandeStage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the DemandeStage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDemandeStageUpdatableFieldsEquals(partialUpdatedDemandeStage, getPersistedDemandeStage(partialUpdatedDemandeStage));
    }

    @Test
    void patchNonExistingDemandeStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandeStage.setId(longCount.incrementAndGet());

        // Create the DemandeStage
        DemandeStageDTO demandeStageDTO = demandeStageMapper.toDto(demandeStage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, demandeStageDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(demandeStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DemandeStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDemandeStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandeStage.setId(longCount.incrementAndGet());

        // Create the DemandeStage
        DemandeStageDTO demandeStageDTO = demandeStageMapper.toDto(demandeStage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(demandeStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the DemandeStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDemandeStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandeStage.setId(longCount.incrementAndGet());

        // Create the DemandeStage
        DemandeStageDTO demandeStageDTO = demandeStageMapper.toDto(demandeStage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(demandeStageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the DemandeStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDemandeStage() {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.save(demandeStage).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the demandeStage
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, demandeStage.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return demandeStageRepository.count().block();
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

    protected DemandeStage getPersistedDemandeStage(DemandeStage demandeStage) {
        return demandeStageRepository.findById(demandeStage.getId()).block();
    }

    protected void assertPersistedDemandeStageToMatchAllProperties(DemandeStage expectedDemandeStage) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDemandeStageAllPropertiesEquals(expectedDemandeStage, getPersistedDemandeStage(expectedDemandeStage));
        assertDemandeStageUpdatableFieldsEquals(expectedDemandeStage, getPersistedDemandeStage(expectedDemandeStage));
    }

    protected void assertPersistedDemandeStageToMatchUpdatableProperties(DemandeStage expectedDemandeStage) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDemandeStageAllUpdatablePropertiesEquals(expectedDemandeStage, getPersistedDemandeStage(expectedDemandeStage));
        assertDemandeStageUpdatableFieldsEquals(expectedDemandeStage, getPersistedDemandeStage(expectedDemandeStage));
    }
}
