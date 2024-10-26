package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static sn.sonatel.dsi.ins.imoc.domain.AttestationFinStageAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage;
import sn.sonatel.dsi.ins.imoc.repository.AttestationFinStageRepository;
import sn.sonatel.dsi.ins.imoc.repository.EntityManager;
import sn.sonatel.dsi.ins.imoc.service.dto.AttestationFinStageDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.AttestationFinStageMapper;

/**
 * Integration tests for the {@link AttestationFinStageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AttestationFinStageResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_ISSUE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ISSUE_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_ISSUE_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_SIGNATURE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SIGNATURE_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_SIGNATURE_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/attestation-fin-stages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AttestationFinStageRepository attestationFinStageRepository;

    @Autowired
    private AttestationFinStageMapper attestationFinStageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private AttestationFinStage attestationFinStage;

    private AttestationFinStage insertedAttestationFinStage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AttestationFinStage createEntity() {
        return new AttestationFinStage()
            .reference(DEFAULT_REFERENCE)
            .issueDate(DEFAULT_ISSUE_DATE)
            .signatureDate(DEFAULT_SIGNATURE_DATE)
            .comments(DEFAULT_COMMENTS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AttestationFinStage createUpdatedEntity() {
        return new AttestationFinStage()
            .reference(UPDATED_REFERENCE)
            .issueDate(UPDATED_ISSUE_DATE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .comments(UPDATED_COMMENTS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(AttestationFinStage.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        attestationFinStage = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAttestationFinStage != null) {
            attestationFinStageRepository.delete(insertedAttestationFinStage).block();
            insertedAttestationFinStage = null;
        }
        deleteEntities(em);
    }

    @Test
    void createAttestationFinStage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AttestationFinStage
        AttestationFinStageDTO attestationFinStageDTO = attestationFinStageMapper.toDto(attestationFinStage);
        var returnedAttestationFinStageDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationFinStageDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AttestationFinStageDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AttestationFinStage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAttestationFinStage = attestationFinStageMapper.toEntity(returnedAttestationFinStageDTO);
        assertAttestationFinStageUpdatableFieldsEquals(
            returnedAttestationFinStage,
            getPersistedAttestationFinStage(returnedAttestationFinStage)
        );

        insertedAttestationFinStage = returnedAttestationFinStage;
    }

    @Test
    void createAttestationFinStageWithExistingId() throws Exception {
        // Create the AttestationFinStage with an existing ID
        attestationFinStage.setId(1L);
        AttestationFinStageDTO attestationFinStageDTO = attestationFinStageMapper.toDto(attestationFinStage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationFinStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AttestationFinStage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        attestationFinStage.setReference(null);

        // Create the AttestationFinStage, which fails.
        AttestationFinStageDTO attestationFinStageDTO = attestationFinStageMapper.toDto(attestationFinStage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationFinStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkIssueDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        attestationFinStage.setIssueDate(null);

        // Create the AttestationFinStage, which fails.
        AttestationFinStageDTO attestationFinStageDTO = attestationFinStageMapper.toDto(attestationFinStage);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationFinStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAttestationFinStages() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList
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
            .value(hasItem(attestationFinStage.getId().intValue()))
            .jsonPath("$.[*].reference")
            .value(hasItem(DEFAULT_REFERENCE))
            .jsonPath("$.[*].issueDate")
            .value(hasItem(DEFAULT_ISSUE_DATE.toString()))
            .jsonPath("$.[*].signatureDate")
            .value(hasItem(DEFAULT_SIGNATURE_DATE.toString()))
            .jsonPath("$.[*].comments")
            .value(hasItem(DEFAULT_COMMENTS));
    }

    @Test
    void getAttestationFinStage() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get the attestationFinStage
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, attestationFinStage.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(attestationFinStage.getId().intValue()))
            .jsonPath("$.reference")
            .value(is(DEFAULT_REFERENCE))
            .jsonPath("$.issueDate")
            .value(is(DEFAULT_ISSUE_DATE.toString()))
            .jsonPath("$.signatureDate")
            .value(is(DEFAULT_SIGNATURE_DATE.toString()))
            .jsonPath("$.comments")
            .value(is(DEFAULT_COMMENTS));
    }

    @Test
    void getAttestationFinStagesByIdFiltering() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        Long id = attestationFinStage.getId();

        defaultAttestationFinStageFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAttestationFinStageFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAttestationFinStageFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllAttestationFinStagesByReferenceIsEqualToSomething() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where reference equals to
        defaultAttestationFinStageFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    void getAllAttestationFinStagesByReferenceIsInShouldWork() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where reference in
        defaultAttestationFinStageFiltering(
            "reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE,
            "reference.in=" + UPDATED_REFERENCE
        );
    }

    @Test
    void getAllAttestationFinStagesByReferenceIsNullOrNotNull() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where reference is not null
        defaultAttestationFinStageFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    void getAllAttestationFinStagesByReferenceContainsSomething() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where reference contains
        defaultAttestationFinStageFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    void getAllAttestationFinStagesByReferenceNotContainsSomething() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where reference does not contain
        defaultAttestationFinStageFiltering(
            "reference.doesNotContain=" + UPDATED_REFERENCE,
            "reference.doesNotContain=" + DEFAULT_REFERENCE
        );
    }

    @Test
    void getAllAttestationFinStagesByIssueDateIsEqualToSomething() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where issueDate equals to
        defaultAttestationFinStageFiltering("issueDate.equals=" + DEFAULT_ISSUE_DATE, "issueDate.equals=" + UPDATED_ISSUE_DATE);
    }

    @Test
    void getAllAttestationFinStagesByIssueDateIsInShouldWork() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where issueDate in
        defaultAttestationFinStageFiltering(
            "issueDate.in=" + DEFAULT_ISSUE_DATE + "," + UPDATED_ISSUE_DATE,
            "issueDate.in=" + UPDATED_ISSUE_DATE
        );
    }

    @Test
    void getAllAttestationFinStagesByIssueDateIsNullOrNotNull() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where issueDate is not null
        defaultAttestationFinStageFiltering("issueDate.specified=true", "issueDate.specified=false");
    }

    @Test
    void getAllAttestationFinStagesByIssueDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where issueDate is greater than or equal to
        defaultAttestationFinStageFiltering(
            "issueDate.greaterThanOrEqual=" + DEFAULT_ISSUE_DATE,
            "issueDate.greaterThanOrEqual=" + UPDATED_ISSUE_DATE
        );
    }

    @Test
    void getAllAttestationFinStagesByIssueDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where issueDate is less than or equal to
        defaultAttestationFinStageFiltering(
            "issueDate.lessThanOrEqual=" + DEFAULT_ISSUE_DATE,
            "issueDate.lessThanOrEqual=" + SMALLER_ISSUE_DATE
        );
    }

    @Test
    void getAllAttestationFinStagesByIssueDateIsLessThanSomething() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where issueDate is less than
        defaultAttestationFinStageFiltering("issueDate.lessThan=" + UPDATED_ISSUE_DATE, "issueDate.lessThan=" + DEFAULT_ISSUE_DATE);
    }

    @Test
    void getAllAttestationFinStagesByIssueDateIsGreaterThanSomething() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where issueDate is greater than
        defaultAttestationFinStageFiltering("issueDate.greaterThan=" + SMALLER_ISSUE_DATE, "issueDate.greaterThan=" + DEFAULT_ISSUE_DATE);
    }

    @Test
    void getAllAttestationFinStagesBySignatureDateIsEqualToSomething() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where signatureDate equals to
        defaultAttestationFinStageFiltering(
            "signatureDate.equals=" + DEFAULT_SIGNATURE_DATE,
            "signatureDate.equals=" + UPDATED_SIGNATURE_DATE
        );
    }

    @Test
    void getAllAttestationFinStagesBySignatureDateIsInShouldWork() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where signatureDate in
        defaultAttestationFinStageFiltering(
            "signatureDate.in=" + DEFAULT_SIGNATURE_DATE + "," + UPDATED_SIGNATURE_DATE,
            "signatureDate.in=" + UPDATED_SIGNATURE_DATE
        );
    }

    @Test
    void getAllAttestationFinStagesBySignatureDateIsNullOrNotNull() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where signatureDate is not null
        defaultAttestationFinStageFiltering("signatureDate.specified=true", "signatureDate.specified=false");
    }

    @Test
    void getAllAttestationFinStagesBySignatureDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where signatureDate is greater than or equal to
        defaultAttestationFinStageFiltering(
            "signatureDate.greaterThanOrEqual=" + DEFAULT_SIGNATURE_DATE,
            "signatureDate.greaterThanOrEqual=" + UPDATED_SIGNATURE_DATE
        );
    }

    @Test
    void getAllAttestationFinStagesBySignatureDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where signatureDate is less than or equal to
        defaultAttestationFinStageFiltering(
            "signatureDate.lessThanOrEqual=" + DEFAULT_SIGNATURE_DATE,
            "signatureDate.lessThanOrEqual=" + SMALLER_SIGNATURE_DATE
        );
    }

    @Test
    void getAllAttestationFinStagesBySignatureDateIsLessThanSomething() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where signatureDate is less than
        defaultAttestationFinStageFiltering(
            "signatureDate.lessThan=" + UPDATED_SIGNATURE_DATE,
            "signatureDate.lessThan=" + DEFAULT_SIGNATURE_DATE
        );
    }

    @Test
    void getAllAttestationFinStagesBySignatureDateIsGreaterThanSomething() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where signatureDate is greater than
        defaultAttestationFinStageFiltering(
            "signatureDate.greaterThan=" + SMALLER_SIGNATURE_DATE,
            "signatureDate.greaterThan=" + DEFAULT_SIGNATURE_DATE
        );
    }

    @Test
    void getAllAttestationFinStagesByCommentsIsEqualToSomething() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where comments equals to
        defaultAttestationFinStageFiltering("comments.equals=" + DEFAULT_COMMENTS, "comments.equals=" + UPDATED_COMMENTS);
    }

    @Test
    void getAllAttestationFinStagesByCommentsIsInShouldWork() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where comments in
        defaultAttestationFinStageFiltering("comments.in=" + DEFAULT_COMMENTS + "," + UPDATED_COMMENTS, "comments.in=" + UPDATED_COMMENTS);
    }

    @Test
    void getAllAttestationFinStagesByCommentsIsNullOrNotNull() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where comments is not null
        defaultAttestationFinStageFiltering("comments.specified=true", "comments.specified=false");
    }

    @Test
    void getAllAttestationFinStagesByCommentsContainsSomething() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where comments contains
        defaultAttestationFinStageFiltering("comments.contains=" + DEFAULT_COMMENTS, "comments.contains=" + UPDATED_COMMENTS);
    }

    @Test
    void getAllAttestationFinStagesByCommentsNotContainsSomething() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        // Get all the attestationFinStageList where comments does not contain
        defaultAttestationFinStageFiltering("comments.doesNotContain=" + UPDATED_COMMENTS, "comments.doesNotContain=" + DEFAULT_COMMENTS);
    }

    private void defaultAttestationFinStageFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultAttestationFinStageShouldBeFound(shouldBeFound);
        defaultAttestationFinStageShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAttestationFinStageShouldBeFound(String filter) {
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
            .value(hasItem(attestationFinStage.getId().intValue()))
            .jsonPath("$.[*].reference")
            .value(hasItem(DEFAULT_REFERENCE))
            .jsonPath("$.[*].issueDate")
            .value(hasItem(DEFAULT_ISSUE_DATE.toString()))
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
    private void defaultAttestationFinStageShouldNotBeFound(String filter) {
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
    void getNonExistingAttestationFinStage() {
        // Get the attestationFinStage
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAttestationFinStage() throws Exception {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attestationFinStage
        AttestationFinStage updatedAttestationFinStage = attestationFinStageRepository.findById(attestationFinStage.getId()).block();
        updatedAttestationFinStage
            .reference(UPDATED_REFERENCE)
            .issueDate(UPDATED_ISSUE_DATE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .comments(UPDATED_COMMENTS);
        AttestationFinStageDTO attestationFinStageDTO = attestationFinStageMapper.toDto(updatedAttestationFinStage);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, attestationFinStageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationFinStageDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AttestationFinStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAttestationFinStageToMatchAllProperties(updatedAttestationFinStage);
    }

    @Test
    void putNonExistingAttestationFinStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationFinStage.setId(longCount.incrementAndGet());

        // Create the AttestationFinStage
        AttestationFinStageDTO attestationFinStageDTO = attestationFinStageMapper.toDto(attestationFinStage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, attestationFinStageDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationFinStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AttestationFinStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAttestationFinStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationFinStage.setId(longCount.incrementAndGet());

        // Create the AttestationFinStage
        AttestationFinStageDTO attestationFinStageDTO = attestationFinStageMapper.toDto(attestationFinStage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationFinStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AttestationFinStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAttestationFinStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationFinStage.setId(longCount.incrementAndGet());

        // Create the AttestationFinStage
        AttestationFinStageDTO attestationFinStageDTO = attestationFinStageMapper.toDto(attestationFinStage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(attestationFinStageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AttestationFinStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAttestationFinStageWithPatch() throws Exception {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attestationFinStage using partial update
        AttestationFinStage partialUpdatedAttestationFinStage = new AttestationFinStage();
        partialUpdatedAttestationFinStage.setId(attestationFinStage.getId());

        partialUpdatedAttestationFinStage.reference(UPDATED_REFERENCE).issueDate(UPDATED_ISSUE_DATE).signatureDate(UPDATED_SIGNATURE_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAttestationFinStage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAttestationFinStage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AttestationFinStage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAttestationFinStageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAttestationFinStage, attestationFinStage),
            getPersistedAttestationFinStage(attestationFinStage)
        );
    }

    @Test
    void fullUpdateAttestationFinStageWithPatch() throws Exception {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attestationFinStage using partial update
        AttestationFinStage partialUpdatedAttestationFinStage = new AttestationFinStage();
        partialUpdatedAttestationFinStage.setId(attestationFinStage.getId());

        partialUpdatedAttestationFinStage
            .reference(UPDATED_REFERENCE)
            .issueDate(UPDATED_ISSUE_DATE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .comments(UPDATED_COMMENTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAttestationFinStage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAttestationFinStage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AttestationFinStage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAttestationFinStageUpdatableFieldsEquals(
            partialUpdatedAttestationFinStage,
            getPersistedAttestationFinStage(partialUpdatedAttestationFinStage)
        );
    }

    @Test
    void patchNonExistingAttestationFinStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationFinStage.setId(longCount.incrementAndGet());

        // Create the AttestationFinStage
        AttestationFinStageDTO attestationFinStageDTO = attestationFinStageMapper.toDto(attestationFinStage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, attestationFinStageDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(attestationFinStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AttestationFinStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAttestationFinStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationFinStage.setId(longCount.incrementAndGet());

        // Create the AttestationFinStage
        AttestationFinStageDTO attestationFinStageDTO = attestationFinStageMapper.toDto(attestationFinStage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(attestationFinStageDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AttestationFinStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAttestationFinStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationFinStage.setId(longCount.incrementAndGet());

        // Create the AttestationFinStage
        AttestationFinStageDTO attestationFinStageDTO = attestationFinStageMapper.toDto(attestationFinStage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(attestationFinStageDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AttestationFinStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAttestationFinStage() {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.save(attestationFinStage).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the attestationFinStage
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, attestationFinStage.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return attestationFinStageRepository.count().block();
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

    protected AttestationFinStage getPersistedAttestationFinStage(AttestationFinStage attestationFinStage) {
        return attestationFinStageRepository.findById(attestationFinStage.getId()).block();
    }

    protected void assertPersistedAttestationFinStageToMatchAllProperties(AttestationFinStage expectedAttestationFinStage) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAttestationFinStageAllPropertiesEquals(expectedAttestationFinStage, getPersistedAttestationFinStage(expectedAttestationFinStage));
        assertAttestationFinStageUpdatableFieldsEquals(
            expectedAttestationFinStage,
            getPersistedAttestationFinStage(expectedAttestationFinStage)
        );
    }

    protected void assertPersistedAttestationFinStageToMatchUpdatableProperties(AttestationFinStage expectedAttestationFinStage) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAttestationFinStageAllUpdatablePropertiesEquals(expectedAttestationFinStage, getPersistedAttestationFinStage(expectedAttestationFinStage));
        assertAttestationFinStageUpdatableFieldsEquals(
            expectedAttestationFinStage,
            getPersistedAttestationFinStage(expectedAttestationFinStage)
        );
    }
}
