package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static sn.sonatel.dsi.ins.imoc.domain.ContratAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.AssistantGWTE;
import sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.Contrat;
import sn.sonatel.dsi.ins.imoc.domain.Drh;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ContractStatus;
import sn.sonatel.dsi.ins.imoc.repository.AssistantGWTERepository;
import sn.sonatel.dsi.ins.imoc.repository.AttestationFinStageRepository;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.repository.ContratRepository;
import sn.sonatel.dsi.ins.imoc.repository.DrhRepository;
import sn.sonatel.dsi.ins.imoc.repository.EntityManager;
import sn.sonatel.dsi.ins.imoc.service.dto.ContratDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.ContratMapper;

/**
 * Integration tests for the {@link ContratResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ContratResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_DATE = LocalDate.ofEpochDay(-1L);

    private static final Double DEFAULT_COMPENSATION = 0D;
    private static final Double UPDATED_COMPENSATION = 1D;
    private static final Double SMALLER_COMPENSATION = 0D - 1D;

    private static final ContractStatus DEFAULT_STATUS = ContractStatus.EN_PREPARATION;
    private static final ContractStatus UPDATED_STATUS = ContractStatus.EN_SIGNATURE;

    private static final String DEFAULT_ASSIGNMENT_SITE = "AAAAAAAAAA";
    private static final String UPDATED_ASSIGNMENT_SITE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_SIGNATURE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SIGNATURE_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_SIGNATURE_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/contrats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private ContratMapper contratMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Contrat contrat;

    private Contrat insertedContrat;

    @Autowired
    private AttestationFinStageRepository attestationFinStageRepository;

    @Autowired
    private DrhRepository drhRepository;

    @Autowired
    private AssistantGWTERepository assistantGWTERepository;

    @Autowired
    private CandidatRepository candidatRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Contrat createEntity() {
        return new Contrat()
            .reference(DEFAULT_REFERENCE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .compensation(DEFAULT_COMPENSATION)
            .status(DEFAULT_STATUS)
            .assignmentSite(DEFAULT_ASSIGNMENT_SITE)
            .signatureDate(DEFAULT_SIGNATURE_DATE)
            .comments(DEFAULT_COMMENTS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Contrat createUpdatedEntity() {
        return new Contrat()
            .reference(UPDATED_REFERENCE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .compensation(UPDATED_COMPENSATION)
            .status(UPDATED_STATUS)
            .assignmentSite(UPDATED_ASSIGNMENT_SITE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .comments(UPDATED_COMMENTS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Contrat.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        contrat = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedContrat != null) {
            contratRepository.delete(insertedContrat).block();
            insertedContrat = null;
        }
        deleteEntities(em);
    }

    @Test
    void createContrat() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Contrat
        ContratDTO contratDTO = contratMapper.toDto(contrat);
        var returnedContratDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(contratDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ContratDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Contrat in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedContrat = contratMapper.toEntity(returnedContratDTO);
        assertContratUpdatableFieldsEquals(returnedContrat, getPersistedContrat(returnedContrat));

        insertedContrat = returnedContrat;
    }

    @Test
    void createContratWithExistingId() throws Exception {
        // Create the Contrat with an existing ID
        contrat.setId(1L);
        ContratDTO contratDTO = contratMapper.toDto(contrat);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(contratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Contrat in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contrat.setReference(null);

        // Create the Contrat, which fails.
        ContratDTO contratDTO = contratMapper.toDto(contrat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(contratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contrat.setStartDate(null);

        // Create the Contrat, which fails.
        ContratDTO contratDTO = contratMapper.toDto(contrat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(contratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contrat.setEndDate(null);

        // Create the Contrat, which fails.
        ContratDTO contratDTO = contratMapper.toDto(contrat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(contratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCompensationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contrat.setCompensation(null);

        // Create the Contrat, which fails.
        ContratDTO contratDTO = contratMapper.toDto(contrat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(contratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contrat.setStatus(null);

        // Create the Contrat, which fails.
        ContratDTO contratDTO = contratMapper.toDto(contrat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(contratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkAssignmentSiteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contrat.setAssignmentSite(null);

        // Create the Contrat, which fails.
        ContratDTO contratDTO = contratMapper.toDto(contrat);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(contratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllContrats() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList
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
            .value(hasItem(contrat.getId().intValue()))
            .jsonPath("$.[*].reference")
            .value(hasItem(DEFAULT_REFERENCE))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))
            .jsonPath("$.[*].compensation")
            .value(hasItem(DEFAULT_COMPENSATION.doubleValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].assignmentSite")
            .value(hasItem(DEFAULT_ASSIGNMENT_SITE))
            .jsonPath("$.[*].signatureDate")
            .value(hasItem(DEFAULT_SIGNATURE_DATE.toString()))
            .jsonPath("$.[*].comments")
            .value(hasItem(DEFAULT_COMMENTS));
    }

    @Test
    void getContrat() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get the contrat
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, contrat.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(contrat.getId().intValue()))
            .jsonPath("$.reference")
            .value(is(DEFAULT_REFERENCE))
            .jsonPath("$.startDate")
            .value(is(DEFAULT_START_DATE.toString()))
            .jsonPath("$.endDate")
            .value(is(DEFAULT_END_DATE.toString()))
            .jsonPath("$.compensation")
            .value(is(DEFAULT_COMPENSATION.doubleValue()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.assignmentSite")
            .value(is(DEFAULT_ASSIGNMENT_SITE))
            .jsonPath("$.signatureDate")
            .value(is(DEFAULT_SIGNATURE_DATE.toString()))
            .jsonPath("$.comments")
            .value(is(DEFAULT_COMMENTS));
    }

    @Test
    void getContratsByIdFiltering() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        Long id = contrat.getId();

        defaultContratFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultContratFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultContratFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllContratsByReferenceIsEqualToSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where reference equals to
        defaultContratFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    void getAllContratsByReferenceIsInShouldWork() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where reference in
        defaultContratFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    void getAllContratsByReferenceIsNullOrNotNull() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where reference is not null
        defaultContratFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    void getAllContratsByReferenceContainsSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where reference contains
        defaultContratFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    void getAllContratsByReferenceNotContainsSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where reference does not contain
        defaultContratFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    void getAllContratsByStartDateIsEqualToSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where startDate equals to
        defaultContratFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    void getAllContratsByStartDateIsInShouldWork() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where startDate in
        defaultContratFiltering("startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE, "startDate.in=" + UPDATED_START_DATE);
    }

    @Test
    void getAllContratsByStartDateIsNullOrNotNull() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where startDate is not null
        defaultContratFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    void getAllContratsByStartDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where startDate is greater than or equal to
        defaultContratFiltering("startDate.greaterThanOrEqual=" + DEFAULT_START_DATE, "startDate.greaterThanOrEqual=" + UPDATED_START_DATE);
    }

    @Test
    void getAllContratsByStartDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where startDate is less than or equal to
        defaultContratFiltering("startDate.lessThanOrEqual=" + DEFAULT_START_DATE, "startDate.lessThanOrEqual=" + SMALLER_START_DATE);
    }

    @Test
    void getAllContratsByStartDateIsLessThanSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where startDate is less than
        defaultContratFiltering("startDate.lessThan=" + UPDATED_START_DATE, "startDate.lessThan=" + DEFAULT_START_DATE);
    }

    @Test
    void getAllContratsByStartDateIsGreaterThanSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where startDate is greater than
        defaultContratFiltering("startDate.greaterThan=" + SMALLER_START_DATE, "startDate.greaterThan=" + DEFAULT_START_DATE);
    }

    @Test
    void getAllContratsByEndDateIsEqualToSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where endDate equals to
        defaultContratFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    void getAllContratsByEndDateIsInShouldWork() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where endDate in
        defaultContratFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    void getAllContratsByEndDateIsNullOrNotNull() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where endDate is not null
        defaultContratFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    void getAllContratsByEndDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where endDate is greater than or equal to
        defaultContratFiltering("endDate.greaterThanOrEqual=" + DEFAULT_END_DATE, "endDate.greaterThanOrEqual=" + UPDATED_END_DATE);
    }

    @Test
    void getAllContratsByEndDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where endDate is less than or equal to
        defaultContratFiltering("endDate.lessThanOrEqual=" + DEFAULT_END_DATE, "endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    void getAllContratsByEndDateIsLessThanSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where endDate is less than
        defaultContratFiltering("endDate.lessThan=" + UPDATED_END_DATE, "endDate.lessThan=" + DEFAULT_END_DATE);
    }

    @Test
    void getAllContratsByEndDateIsGreaterThanSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where endDate is greater than
        defaultContratFiltering("endDate.greaterThan=" + SMALLER_END_DATE, "endDate.greaterThan=" + DEFAULT_END_DATE);
    }

    @Test
    void getAllContratsByCompensationIsEqualToSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where compensation equals to
        defaultContratFiltering("compensation.equals=" + DEFAULT_COMPENSATION, "compensation.equals=" + UPDATED_COMPENSATION);
    }

    @Test
    void getAllContratsByCompensationIsInShouldWork() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where compensation in
        defaultContratFiltering(
            "compensation.in=" + DEFAULT_COMPENSATION + "," + UPDATED_COMPENSATION,
            "compensation.in=" + UPDATED_COMPENSATION
        );
    }

    @Test
    void getAllContratsByCompensationIsNullOrNotNull() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where compensation is not null
        defaultContratFiltering("compensation.specified=true", "compensation.specified=false");
    }

    @Test
    void getAllContratsByCompensationIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where compensation is greater than or equal to
        defaultContratFiltering(
            "compensation.greaterThanOrEqual=" + DEFAULT_COMPENSATION,
            "compensation.greaterThanOrEqual=" + UPDATED_COMPENSATION
        );
    }

    @Test
    void getAllContratsByCompensationIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where compensation is less than or equal to
        defaultContratFiltering(
            "compensation.lessThanOrEqual=" + DEFAULT_COMPENSATION,
            "compensation.lessThanOrEqual=" + SMALLER_COMPENSATION
        );
    }

    @Test
    void getAllContratsByCompensationIsLessThanSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where compensation is less than
        defaultContratFiltering("compensation.lessThan=" + UPDATED_COMPENSATION, "compensation.lessThan=" + DEFAULT_COMPENSATION);
    }

    @Test
    void getAllContratsByCompensationIsGreaterThanSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where compensation is greater than
        defaultContratFiltering("compensation.greaterThan=" + SMALLER_COMPENSATION, "compensation.greaterThan=" + DEFAULT_COMPENSATION);
    }

    @Test
    void getAllContratsByStatusIsEqualToSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where status equals to
        defaultContratFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    void getAllContratsByStatusIsInShouldWork() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where status in
        defaultContratFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    void getAllContratsByStatusIsNullOrNotNull() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where status is not null
        defaultContratFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    void getAllContratsByAssignmentSiteIsEqualToSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where assignmentSite equals to
        defaultContratFiltering("assignmentSite.equals=" + DEFAULT_ASSIGNMENT_SITE, "assignmentSite.equals=" + UPDATED_ASSIGNMENT_SITE);
    }

    @Test
    void getAllContratsByAssignmentSiteIsInShouldWork() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where assignmentSite in
        defaultContratFiltering(
            "assignmentSite.in=" + DEFAULT_ASSIGNMENT_SITE + "," + UPDATED_ASSIGNMENT_SITE,
            "assignmentSite.in=" + UPDATED_ASSIGNMENT_SITE
        );
    }

    @Test
    void getAllContratsByAssignmentSiteIsNullOrNotNull() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where assignmentSite is not null
        defaultContratFiltering("assignmentSite.specified=true", "assignmentSite.specified=false");
    }

    @Test
    void getAllContratsByAssignmentSiteContainsSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where assignmentSite contains
        defaultContratFiltering("assignmentSite.contains=" + DEFAULT_ASSIGNMENT_SITE, "assignmentSite.contains=" + UPDATED_ASSIGNMENT_SITE);
    }

    @Test
    void getAllContratsByAssignmentSiteNotContainsSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where assignmentSite does not contain
        defaultContratFiltering(
            "assignmentSite.doesNotContain=" + UPDATED_ASSIGNMENT_SITE,
            "assignmentSite.doesNotContain=" + DEFAULT_ASSIGNMENT_SITE
        );
    }

    @Test
    void getAllContratsBySignatureDateIsEqualToSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where signatureDate equals to
        defaultContratFiltering("signatureDate.equals=" + DEFAULT_SIGNATURE_DATE, "signatureDate.equals=" + UPDATED_SIGNATURE_DATE);
    }

    @Test
    void getAllContratsBySignatureDateIsInShouldWork() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where signatureDate in
        defaultContratFiltering(
            "signatureDate.in=" + DEFAULT_SIGNATURE_DATE + "," + UPDATED_SIGNATURE_DATE,
            "signatureDate.in=" + UPDATED_SIGNATURE_DATE
        );
    }

    @Test
    void getAllContratsBySignatureDateIsNullOrNotNull() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where signatureDate is not null
        defaultContratFiltering("signatureDate.specified=true", "signatureDate.specified=false");
    }

    @Test
    void getAllContratsBySignatureDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where signatureDate is greater than or equal to
        defaultContratFiltering(
            "signatureDate.greaterThanOrEqual=" + DEFAULT_SIGNATURE_DATE,
            "signatureDate.greaterThanOrEqual=" + UPDATED_SIGNATURE_DATE
        );
    }

    @Test
    void getAllContratsBySignatureDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where signatureDate is less than or equal to
        defaultContratFiltering(
            "signatureDate.lessThanOrEqual=" + DEFAULT_SIGNATURE_DATE,
            "signatureDate.lessThanOrEqual=" + SMALLER_SIGNATURE_DATE
        );
    }

    @Test
    void getAllContratsBySignatureDateIsLessThanSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where signatureDate is less than
        defaultContratFiltering("signatureDate.lessThan=" + UPDATED_SIGNATURE_DATE, "signatureDate.lessThan=" + DEFAULT_SIGNATURE_DATE);
    }

    @Test
    void getAllContratsBySignatureDateIsGreaterThanSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where signatureDate is greater than
        defaultContratFiltering(
            "signatureDate.greaterThan=" + SMALLER_SIGNATURE_DATE,
            "signatureDate.greaterThan=" + DEFAULT_SIGNATURE_DATE
        );
    }

    @Test
    void getAllContratsByCommentsIsEqualToSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where comments equals to
        defaultContratFiltering("comments.equals=" + DEFAULT_COMMENTS, "comments.equals=" + UPDATED_COMMENTS);
    }

    @Test
    void getAllContratsByCommentsIsInShouldWork() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where comments in
        defaultContratFiltering("comments.in=" + DEFAULT_COMMENTS + "," + UPDATED_COMMENTS, "comments.in=" + UPDATED_COMMENTS);
    }

    @Test
    void getAllContratsByCommentsIsNullOrNotNull() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where comments is not null
        defaultContratFiltering("comments.specified=true", "comments.specified=false");
    }

    @Test
    void getAllContratsByCommentsContainsSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where comments contains
        defaultContratFiltering("comments.contains=" + DEFAULT_COMMENTS, "comments.contains=" + UPDATED_COMMENTS);
    }

    @Test
    void getAllContratsByCommentsNotContainsSomething() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        // Get all the contratList where comments does not contain
        defaultContratFiltering("comments.doesNotContain=" + UPDATED_COMMENTS, "comments.doesNotContain=" + DEFAULT_COMMENTS);
    }

    @Test
    void getAllContratsByAttestationFinStageIsEqualToSomething() {
        AttestationFinStage attestationFinStage = AttestationFinStageResourceIT.createEntity();
        attestationFinStageRepository.save(attestationFinStage).block();
        Long attestationFinStageId = attestationFinStage.getId();
        contrat.setAttestationFinStageId(attestationFinStageId);
        insertedContrat = contratRepository.save(contrat).block();
        // Get all the contratList where attestationFinStage equals to attestationFinStageId
        defaultContratShouldBeFound("attestationFinStageId.equals=" + attestationFinStageId);

        // Get all the contratList where attestationFinStage equals to (attestationFinStageId + 1)
        defaultContratShouldNotBeFound("attestationFinStageId.equals=" + (attestationFinStageId + 1));
    }

    @Test
    void getAllContratsByDrhIsEqualToSomething() {
        Drh drh = DrhResourceIT.createEntity();
        drhRepository.save(drh).block();
        Long drhId = drh.getId();
        contrat.setDrhId(drhId);
        insertedContrat = contratRepository.save(contrat).block();
        // Get all the contratList where drh equals to drhId
        defaultContratShouldBeFound("drhId.equals=" + drhId);

        // Get all the contratList where drh equals to (drhId + 1)
        defaultContratShouldNotBeFound("drhId.equals=" + (drhId + 1));
    }

    @Test
    void getAllContratsByAssistantGWTECreatorIsEqualToSomething() {
        AssistantGWTE assistantGWTECreator = AssistantGWTEResourceIT.createEntity();
        assistantGWTERepository.save(assistantGWTECreator).block();
        Long assistantGWTECreatorId = assistantGWTECreator.getId();
        contrat.setAssistantGWTECreatorId(assistantGWTECreatorId);
        insertedContrat = contratRepository.save(contrat).block();
        // Get all the contratList where assistantGWTECreator equals to assistantGWTECreatorId
        defaultContratShouldBeFound("assistantGWTECreatorId.equals=" + assistantGWTECreatorId);

        // Get all the contratList where assistantGWTECreator equals to (assistantGWTECreatorId + 1)
        defaultContratShouldNotBeFound("assistantGWTECreatorId.equals=" + (assistantGWTECreatorId + 1));
    }

    @Test
    void getAllContratsByCandidatIsEqualToSomething() {
        Candidat candidat = CandidatResourceIT.createEntity();
        candidatRepository.save(candidat).block();
        Long candidatId = candidat.getId();
        contrat.setCandidatId(candidatId);
        insertedContrat = contratRepository.save(contrat).block();
        // Get all the contratList where candidat equals to candidatId
        defaultContratShouldBeFound("candidatId.equals=" + candidatId);

        // Get all the contratList where candidat equals to (candidatId + 1)
        defaultContratShouldNotBeFound("candidatId.equals=" + (candidatId + 1));
    }

    private void defaultContratFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultContratShouldBeFound(shouldBeFound);
        defaultContratShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultContratShouldBeFound(String filter) {
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
            .value(hasItem(contrat.getId().intValue()))
            .jsonPath("$.[*].reference")
            .value(hasItem(DEFAULT_REFERENCE))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))
            .jsonPath("$.[*].compensation")
            .value(hasItem(DEFAULT_COMPENSATION.doubleValue()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].assignmentSite")
            .value(hasItem(DEFAULT_ASSIGNMENT_SITE))
            .jsonPath("$.[*].signatureDate")
            .value(hasItem(DEFAULT_SIGNATURE_DATE.toString()))
            .jsonPath("$.[*].comments")
            .value(hasItem(DEFAULT_COMMENTS));

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
    private void defaultContratShouldNotBeFound(String filter) {
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
    void getNonExistingContrat() {
        // Get the contrat
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingContrat() throws Exception {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contrat
        Contrat updatedContrat = contratRepository.findById(contrat.getId()).block();
        updatedContrat
            .reference(UPDATED_REFERENCE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .compensation(UPDATED_COMPENSATION)
            .status(UPDATED_STATUS)
            .assignmentSite(UPDATED_ASSIGNMENT_SITE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .comments(UPDATED_COMMENTS);
        ContratDTO contratDTO = contratMapper.toDto(updatedContrat);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, contratDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(contratDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Contrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedContratToMatchAllProperties(updatedContrat);
    }

    @Test
    void putNonExistingContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contrat.setId(longCount.incrementAndGet());

        // Create the Contrat
        ContratDTO contratDTO = contratMapper.toDto(contrat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, contratDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(contratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Contrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contrat.setId(longCount.incrementAndGet());

        // Create the Contrat
        ContratDTO contratDTO = contratMapper.toDto(contrat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(contratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Contrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contrat.setId(longCount.incrementAndGet());

        // Create the Contrat
        ContratDTO contratDTO = contratMapper.toDto(contrat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(contratDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Contrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateContratWithPatch() throws Exception {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contrat using partial update
        Contrat partialUpdatedContrat = new Contrat();
        partialUpdatedContrat.setId(contrat.getId());

        partialUpdatedContrat.comments(UPDATED_COMMENTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedContrat.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedContrat))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Contrat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContratUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedContrat, contrat), getPersistedContrat(contrat));
    }

    @Test
    void fullUpdateContratWithPatch() throws Exception {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contrat using partial update
        Contrat partialUpdatedContrat = new Contrat();
        partialUpdatedContrat.setId(contrat.getId());

        partialUpdatedContrat
            .reference(UPDATED_REFERENCE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .compensation(UPDATED_COMPENSATION)
            .status(UPDATED_STATUS)
            .assignmentSite(UPDATED_ASSIGNMENT_SITE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .comments(UPDATED_COMMENTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedContrat.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedContrat))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Contrat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContratUpdatableFieldsEquals(partialUpdatedContrat, getPersistedContrat(partialUpdatedContrat));
    }

    @Test
    void patchNonExistingContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contrat.setId(longCount.incrementAndGet());

        // Create the Contrat
        ContratDTO contratDTO = contratMapper.toDto(contrat);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, contratDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(contratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Contrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contrat.setId(longCount.incrementAndGet());

        // Create the Contrat
        ContratDTO contratDTO = contratMapper.toDto(contrat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(contratDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Contrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contrat.setId(longCount.incrementAndGet());

        // Create the Contrat
        ContratDTO contratDTO = contratMapper.toDto(contrat);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(contratDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Contrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteContrat() {
        // Initialize the database
        insertedContrat = contratRepository.save(contrat).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the contrat
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, contrat.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return contratRepository.count().block();
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

    protected Contrat getPersistedContrat(Contrat contrat) {
        return contratRepository.findById(contrat.getId()).block();
    }

    protected void assertPersistedContratToMatchAllProperties(Contrat expectedContrat) {
        // Test fails because reactive api returns an empty object instead of null
        // assertContratAllPropertiesEquals(expectedContrat, getPersistedContrat(expectedContrat));
        assertContratUpdatableFieldsEquals(expectedContrat, getPersistedContrat(expectedContrat));
    }

    protected void assertPersistedContratToMatchUpdatableProperties(Contrat expectedContrat) {
        // Test fails because reactive api returns an empty object instead of null
        // assertContratAllUpdatablePropertiesEquals(expectedContrat, getPersistedContrat(expectedContrat));
        assertContratUpdatableFieldsEquals(expectedContrat, getPersistedContrat(expectedContrat));
    }
}
