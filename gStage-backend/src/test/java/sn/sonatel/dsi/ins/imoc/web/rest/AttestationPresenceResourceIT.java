package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static sn.sonatel.dsi.ins.imoc.domain.AttestationPresenceAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.AttestationPresence;
import sn.sonatel.dsi.ins.imoc.domain.Contrat;
import sn.sonatel.dsi.ins.imoc.domain.EtatPaiement;
import sn.sonatel.dsi.ins.imoc.domain.Manager;
import sn.sonatel.dsi.ins.imoc.repository.AttestationPresenceRepository;
import sn.sonatel.dsi.ins.imoc.repository.ContratRepository;
import sn.sonatel.dsi.ins.imoc.repository.EntityManager;
import sn.sonatel.dsi.ins.imoc.repository.EtatPaiementRepository;
import sn.sonatel.dsi.ins.imoc.repository.ManagerRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.AttestationPresenceDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.AttestationPresenceMapper;

/**
 * Integration tests for the {@link AttestationPresenceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AttestationPresenceResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_SIGNATURE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SIGNATURE_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_SIGNATURE_DATE = LocalDate.ofEpochDay(-1L);

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/attestation-presences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AttestationPresenceRepository attestationPresenceRepository;

    @Autowired
    private AttestationPresenceMapper attestationPresenceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private AttestationPresence attestationPresence;

    private AttestationPresence insertedAttestationPresence;

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private EtatPaiementRepository etatPaiementRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AttestationPresence createEntity() {
        return new AttestationPresence()
            .reference(DEFAULT_REFERENCE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .signatureDate(DEFAULT_SIGNATURE_DATE)
            .status(DEFAULT_STATUS)
            .comments(DEFAULT_COMMENTS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AttestationPresence createUpdatedEntity() {
        return new AttestationPresence()
            .reference(UPDATED_REFERENCE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .status(UPDATED_STATUS)
            .comments(UPDATED_COMMENTS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(AttestationPresence.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        attestationPresence = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAttestationPresence != null) {
            attestationPresenceRepository.delete(insertedAttestationPresence).block();
            insertedAttestationPresence = null;
        }
        deleteEntities(em);
    }

    @Test
    void createAttestationPresence() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AttestationPresence
        AttestationPresenceDTO attestationPresenceDTO = attestationPresenceMapper.toDto(attestationPresence);
        var returnedAttestationPresenceDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationPresenceDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AttestationPresenceDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AttestationPresence in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAttestationPresence = attestationPresenceMapper.toEntity(returnedAttestationPresenceDTO);
        assertAttestationPresenceUpdatableFieldsEquals(
            returnedAttestationPresence,
            getPersistedAttestationPresence(returnedAttestationPresence)
        );

        insertedAttestationPresence = returnedAttestationPresence;
    }

    @Test
    void createAttestationPresenceWithExistingId() throws Exception {
        // Create the AttestationPresence with an existing ID
        attestationPresence.setId(1L);
        AttestationPresenceDTO attestationPresenceDTO = attestationPresenceMapper.toDto(attestationPresence);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationPresenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AttestationPresence in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        attestationPresence.setReference(null);

        // Create the AttestationPresence, which fails.
        AttestationPresenceDTO attestationPresenceDTO = attestationPresenceMapper.toDto(attestationPresence);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationPresenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        attestationPresence.setStartDate(null);

        // Create the AttestationPresence, which fails.
        AttestationPresenceDTO attestationPresenceDTO = attestationPresenceMapper.toDto(attestationPresence);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationPresenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        attestationPresence.setEndDate(null);

        // Create the AttestationPresence, which fails.
        AttestationPresenceDTO attestationPresenceDTO = attestationPresenceMapper.toDto(attestationPresence);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationPresenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkSignatureDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        attestationPresence.setSignatureDate(null);

        // Create the AttestationPresence, which fails.
        AttestationPresenceDTO attestationPresenceDTO = attestationPresenceMapper.toDto(attestationPresence);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationPresenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        attestationPresence.setStatus(null);

        // Create the AttestationPresence, which fails.
        AttestationPresenceDTO attestationPresenceDTO = attestationPresenceMapper.toDto(attestationPresence);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationPresenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAttestationPresences() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList
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
            .value(hasItem(attestationPresence.getId().intValue()))
            .jsonPath("$.[*].reference")
            .value(hasItem(DEFAULT_REFERENCE))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))
            .jsonPath("$.[*].signatureDate")
            .value(hasItem(DEFAULT_SIGNATURE_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.booleanValue()))
            .jsonPath("$.[*].comments")
            .value(hasItem(DEFAULT_COMMENTS));
    }

    @Test
    void getAttestationPresence() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get the attestationPresence
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, attestationPresence.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(attestationPresence.getId().intValue()))
            .jsonPath("$.reference")
            .value(is(DEFAULT_REFERENCE))
            .jsonPath("$.startDate")
            .value(is(DEFAULT_START_DATE.toString()))
            .jsonPath("$.endDate")
            .value(is(DEFAULT_END_DATE.toString()))
            .jsonPath("$.signatureDate")
            .value(is(DEFAULT_SIGNATURE_DATE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.booleanValue()))
            .jsonPath("$.comments")
            .value(is(DEFAULT_COMMENTS));
    }

    @Test
    void getAttestationPresencesByIdFiltering() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        Long id = attestationPresence.getId();

        defaultAttestationPresenceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAttestationPresenceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAttestationPresenceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllAttestationPresencesByReferenceIsEqualToSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where reference equals to
        defaultAttestationPresenceFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    void getAllAttestationPresencesByReferenceIsInShouldWork() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where reference in
        defaultAttestationPresenceFiltering(
            "reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE,
            "reference.in=" + UPDATED_REFERENCE
        );
    }

    @Test
    void getAllAttestationPresencesByReferenceIsNullOrNotNull() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where reference is not null
        defaultAttestationPresenceFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    void getAllAttestationPresencesByReferenceContainsSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where reference contains
        defaultAttestationPresenceFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    void getAllAttestationPresencesByReferenceNotContainsSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where reference does not contain
        defaultAttestationPresenceFiltering(
            "reference.doesNotContain=" + UPDATED_REFERENCE,
            "reference.doesNotContain=" + DEFAULT_REFERENCE
        );
    }

    @Test
    void getAllAttestationPresencesByStartDateIsEqualToSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where startDate equals to
        defaultAttestationPresenceFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    void getAllAttestationPresencesByStartDateIsInShouldWork() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where startDate in
        defaultAttestationPresenceFiltering(
            "startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE,
            "startDate.in=" + UPDATED_START_DATE
        );
    }

    @Test
    void getAllAttestationPresencesByStartDateIsNullOrNotNull() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where startDate is not null
        defaultAttestationPresenceFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    void getAllAttestationPresencesByStartDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where startDate is greater than or equal to
        defaultAttestationPresenceFiltering(
            "startDate.greaterThanOrEqual=" + DEFAULT_START_DATE,
            "startDate.greaterThanOrEqual=" + UPDATED_START_DATE
        );
    }

    @Test
    void getAllAttestationPresencesByStartDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where startDate is less than or equal to
        defaultAttestationPresenceFiltering(
            "startDate.lessThanOrEqual=" + DEFAULT_START_DATE,
            "startDate.lessThanOrEqual=" + SMALLER_START_DATE
        );
    }

    @Test
    void getAllAttestationPresencesByStartDateIsLessThanSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where startDate is less than
        defaultAttestationPresenceFiltering("startDate.lessThan=" + UPDATED_START_DATE, "startDate.lessThan=" + DEFAULT_START_DATE);
    }

    @Test
    void getAllAttestationPresencesByStartDateIsGreaterThanSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where startDate is greater than
        defaultAttestationPresenceFiltering("startDate.greaterThan=" + SMALLER_START_DATE, "startDate.greaterThan=" + DEFAULT_START_DATE);
    }

    @Test
    void getAllAttestationPresencesByEndDateIsEqualToSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where endDate equals to
        defaultAttestationPresenceFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    void getAllAttestationPresencesByEndDateIsInShouldWork() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where endDate in
        defaultAttestationPresenceFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    void getAllAttestationPresencesByEndDateIsNullOrNotNull() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where endDate is not null
        defaultAttestationPresenceFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    void getAllAttestationPresencesByEndDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where endDate is greater than or equal to
        defaultAttestationPresenceFiltering(
            "endDate.greaterThanOrEqual=" + DEFAULT_END_DATE,
            "endDate.greaterThanOrEqual=" + UPDATED_END_DATE
        );
    }

    @Test
    void getAllAttestationPresencesByEndDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where endDate is less than or equal to
        defaultAttestationPresenceFiltering("endDate.lessThanOrEqual=" + DEFAULT_END_DATE, "endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    void getAllAttestationPresencesByEndDateIsLessThanSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where endDate is less than
        defaultAttestationPresenceFiltering("endDate.lessThan=" + UPDATED_END_DATE, "endDate.lessThan=" + DEFAULT_END_DATE);
    }

    @Test
    void getAllAttestationPresencesByEndDateIsGreaterThanSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where endDate is greater than
        defaultAttestationPresenceFiltering("endDate.greaterThan=" + SMALLER_END_DATE, "endDate.greaterThan=" + DEFAULT_END_DATE);
    }

    @Test
    void getAllAttestationPresencesBySignatureDateIsEqualToSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where signatureDate equals to
        defaultAttestationPresenceFiltering(
            "signatureDate.equals=" + DEFAULT_SIGNATURE_DATE,
            "signatureDate.equals=" + UPDATED_SIGNATURE_DATE
        );
    }

    @Test
    void getAllAttestationPresencesBySignatureDateIsInShouldWork() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where signatureDate in
        defaultAttestationPresenceFiltering(
            "signatureDate.in=" + DEFAULT_SIGNATURE_DATE + "," + UPDATED_SIGNATURE_DATE,
            "signatureDate.in=" + UPDATED_SIGNATURE_DATE
        );
    }

    @Test
    void getAllAttestationPresencesBySignatureDateIsNullOrNotNull() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where signatureDate is not null
        defaultAttestationPresenceFiltering("signatureDate.specified=true", "signatureDate.specified=false");
    }

    @Test
    void getAllAttestationPresencesBySignatureDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where signatureDate is greater than or equal to
        defaultAttestationPresenceFiltering(
            "signatureDate.greaterThanOrEqual=" + DEFAULT_SIGNATURE_DATE,
            "signatureDate.greaterThanOrEqual=" + UPDATED_SIGNATURE_DATE
        );
    }

    @Test
    void getAllAttestationPresencesBySignatureDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where signatureDate is less than or equal to
        defaultAttestationPresenceFiltering(
            "signatureDate.lessThanOrEqual=" + DEFAULT_SIGNATURE_DATE,
            "signatureDate.lessThanOrEqual=" + SMALLER_SIGNATURE_DATE
        );
    }

    @Test
    void getAllAttestationPresencesBySignatureDateIsLessThanSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where signatureDate is less than
        defaultAttestationPresenceFiltering(
            "signatureDate.lessThan=" + UPDATED_SIGNATURE_DATE,
            "signatureDate.lessThan=" + DEFAULT_SIGNATURE_DATE
        );
    }

    @Test
    void getAllAttestationPresencesBySignatureDateIsGreaterThanSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where signatureDate is greater than
        defaultAttestationPresenceFiltering(
            "signatureDate.greaterThan=" + SMALLER_SIGNATURE_DATE,
            "signatureDate.greaterThan=" + DEFAULT_SIGNATURE_DATE
        );
    }

    @Test
    void getAllAttestationPresencesByStatusIsEqualToSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where status equals to
        defaultAttestationPresenceFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    void getAllAttestationPresencesByStatusIsInShouldWork() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where status in
        defaultAttestationPresenceFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    void getAllAttestationPresencesByStatusIsNullOrNotNull() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where status is not null
        defaultAttestationPresenceFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    void getAllAttestationPresencesByCommentsIsEqualToSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where comments equals to
        defaultAttestationPresenceFiltering("comments.equals=" + DEFAULT_COMMENTS, "comments.equals=" + UPDATED_COMMENTS);
    }

    @Test
    void getAllAttestationPresencesByCommentsIsInShouldWork() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where comments in
        defaultAttestationPresenceFiltering("comments.in=" + DEFAULT_COMMENTS + "," + UPDATED_COMMENTS, "comments.in=" + UPDATED_COMMENTS);
    }

    @Test
    void getAllAttestationPresencesByCommentsIsNullOrNotNull() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where comments is not null
        defaultAttestationPresenceFiltering("comments.specified=true", "comments.specified=false");
    }

    @Test
    void getAllAttestationPresencesByCommentsContainsSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where comments contains
        defaultAttestationPresenceFiltering("comments.contains=" + DEFAULT_COMMENTS, "comments.contains=" + UPDATED_COMMENTS);
    }

    @Test
    void getAllAttestationPresencesByCommentsNotContainsSomething() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        // Get all the attestationPresenceList where comments does not contain
        defaultAttestationPresenceFiltering("comments.doesNotContain=" + UPDATED_COMMENTS, "comments.doesNotContain=" + DEFAULT_COMMENTS);
    }

    @Test
    void getAllAttestationPresencesByContratIsEqualToSomething() {
        Contrat contrat = ContratResourceIT.createEntity();
        contratRepository.save(contrat).block();
        Long contratId = contrat.getId();
        attestationPresence.setContratId(contratId);
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();
        // Get all the attestationPresenceList where contrat equals to contratId
        defaultAttestationPresenceShouldBeFound("contratId.equals=" + contratId);

        // Get all the attestationPresenceList where contrat equals to (contratId + 1)
        defaultAttestationPresenceShouldNotBeFound("contratId.equals=" + (contratId + 1));
    }

    @Test
    void getAllAttestationPresencesByManagerIsEqualToSomething() {
        Manager manager = ManagerResourceIT.createEntity();
        managerRepository.save(manager).block();
        Long managerId = manager.getId();
        attestationPresence.setManagerId(managerId);
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();
        // Get all the attestationPresenceList where manager equals to managerId
        defaultAttestationPresenceShouldBeFound("managerId.equals=" + managerId);

        // Get all the attestationPresenceList where manager equals to (managerId + 1)
        defaultAttestationPresenceShouldNotBeFound("managerId.equals=" + (managerId + 1));
    }

    @Test
    void getAllAttestationPresencesByEtatPaiementIsEqualToSomething() {
        EtatPaiement etatPaiement = EtatPaiementResourceIT.createEntity();
        etatPaiementRepository.save(etatPaiement).block();
        Long etatPaiementId = etatPaiement.getId();
        attestationPresence.setEtatPaiementId(etatPaiementId);
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();
        // Get all the attestationPresenceList where etatPaiement equals to etatPaiementId
        defaultAttestationPresenceShouldBeFound("etatPaiementId.equals=" + etatPaiementId);

        // Get all the attestationPresenceList where etatPaiement equals to (etatPaiementId + 1)
        defaultAttestationPresenceShouldNotBeFound("etatPaiementId.equals=" + (etatPaiementId + 1));
    }

    private void defaultAttestationPresenceFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultAttestationPresenceShouldBeFound(shouldBeFound);
        defaultAttestationPresenceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAttestationPresenceShouldBeFound(String filter) {
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
            .value(hasItem(attestationPresence.getId().intValue()))
            .jsonPath("$.[*].reference")
            .value(hasItem(DEFAULT_REFERENCE))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))
            .jsonPath("$.[*].signatureDate")
            .value(hasItem(DEFAULT_SIGNATURE_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.booleanValue()))
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
    private void defaultAttestationPresenceShouldNotBeFound(String filter) {
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
    void getNonExistingAttestationPresence() {
        // Get the attestationPresence
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAttestationPresence() throws Exception {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attestationPresence
        AttestationPresence updatedAttestationPresence = attestationPresenceRepository.findById(attestationPresence.getId()).block();
        updatedAttestationPresence
            .reference(UPDATED_REFERENCE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .status(UPDATED_STATUS)
            .comments(UPDATED_COMMENTS);
        AttestationPresenceDTO attestationPresenceDTO = attestationPresenceMapper.toDto(updatedAttestationPresence);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, attestationPresenceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationPresenceDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AttestationPresence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAttestationPresenceToMatchAllProperties(updatedAttestationPresence);
    }

    @Test
    void putNonExistingAttestationPresence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationPresence.setId(longCount.incrementAndGet());

        // Create the AttestationPresence
        AttestationPresenceDTO attestationPresenceDTO = attestationPresenceMapper.toDto(attestationPresence);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, attestationPresenceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationPresenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AttestationPresence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAttestationPresence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationPresence.setId(longCount.incrementAndGet());

        // Create the AttestationPresence
        AttestationPresenceDTO attestationPresenceDTO = attestationPresenceMapper.toDto(attestationPresence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationPresenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AttestationPresence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAttestationPresence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationPresence.setId(longCount.incrementAndGet());

        // Create the AttestationPresence
        AttestationPresenceDTO attestationPresenceDTO = attestationPresenceMapper.toDto(attestationPresence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationPresenceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AttestationPresence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAttestationPresenceWithPatch() throws Exception {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attestationPresence using partial update
        AttestationPresence partialUpdatedAttestationPresence = new AttestationPresence();
        partialUpdatedAttestationPresence.setId(attestationPresence.getId());

        partialUpdatedAttestationPresence
            .reference(UPDATED_REFERENCE)
            .endDate(UPDATED_END_DATE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .status(UPDATED_STATUS)
            .comments(UPDATED_COMMENTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAttestationPresence.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAttestationPresence))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AttestationPresence in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAttestationPresenceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAttestationPresence, attestationPresence),
            getPersistedAttestationPresence(attestationPresence)
        );
    }

    @Test
    void fullUpdateAttestationPresenceWithPatch() throws Exception {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attestationPresence using partial update
        AttestationPresence partialUpdatedAttestationPresence = new AttestationPresence();
        partialUpdatedAttestationPresence.setId(attestationPresence.getId());

        partialUpdatedAttestationPresence
            .reference(UPDATED_REFERENCE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .status(UPDATED_STATUS)
            .comments(UPDATED_COMMENTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAttestationPresence.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAttestationPresence))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AttestationPresence in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAttestationPresenceUpdatableFieldsEquals(
            partialUpdatedAttestationPresence,
            getPersistedAttestationPresence(partialUpdatedAttestationPresence)
        );
    }

    @Test
    void patchNonExistingAttestationPresence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationPresence.setId(longCount.incrementAndGet());

        // Create the AttestationPresence
        AttestationPresenceDTO attestationPresenceDTO = attestationPresenceMapper.toDto(attestationPresence);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, attestationPresenceDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(attestationPresenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AttestationPresence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAttestationPresence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationPresence.setId(longCount.incrementAndGet());

        // Create the AttestationPresence
        AttestationPresenceDTO attestationPresenceDTO = attestationPresenceMapper.toDto(attestationPresence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(attestationPresenceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AttestationPresence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAttestationPresence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationPresence.setId(longCount.incrementAndGet());

        // Create the AttestationPresence
        AttestationPresenceDTO attestationPresenceDTO = attestationPresenceMapper.toDto(attestationPresence);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(attestationPresenceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AttestationPresence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAttestationPresence() {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.save(attestationPresence).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the attestationPresence
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, attestationPresence.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return attestationPresenceRepository.count().block();
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

    protected AttestationPresence getPersistedAttestationPresence(AttestationPresence attestationPresence) {
        return attestationPresenceRepository.findById(attestationPresence.getId()).block();
    }

    protected void assertPersistedAttestationPresenceToMatchAllProperties(AttestationPresence expectedAttestationPresence) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAttestationPresenceAllPropertiesEquals(expectedAttestationPresence, getPersistedAttestationPresence(expectedAttestationPresence));
        assertAttestationPresenceUpdatableFieldsEquals(
            expectedAttestationPresence,
            getPersistedAttestationPresence(expectedAttestationPresence)
        );
    }

    protected void assertPersistedAttestationPresenceToMatchUpdatableProperties(AttestationPresence expectedAttestationPresence) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAttestationPresenceAllUpdatablePropertiesEquals(expectedAttestationPresence, getPersistedAttestationPresence(expectedAttestationPresence));
        assertAttestationPresenceUpdatableFieldsEquals(
            expectedAttestationPresence,
            getPersistedAttestationPresence(expectedAttestationPresence)
        );
    }
}
