package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage;
import sn.sonatel.dsi.ins.imoc.domain.AttestationPresence;
import sn.sonatel.dsi.ins.imoc.domain.Contrat;
import sn.sonatel.dsi.ins.imoc.domain.Validation;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ValidationStatus;
import sn.sonatel.dsi.ins.imoc.repository.AppUserRepository;
import sn.sonatel.dsi.ins.imoc.repository.AttestationFinStageRepository;
import sn.sonatel.dsi.ins.imoc.repository.AttestationPresenceRepository;
import sn.sonatel.dsi.ins.imoc.repository.ContratRepository;
import sn.sonatel.dsi.ins.imoc.repository.EntityManager;
import sn.sonatel.dsi.ins.imoc.repository.ValidationRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.ValidationDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.ValidationMapper;

/**
 * Integration tests for the {@link ValidationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ValidationResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_VALIDATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_VALIDATION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_VALIDATION_DATE = LocalDate.ofEpochDay(-1L);

    private static final ValidationStatus DEFAULT_STATUS = ValidationStatus.En_ATTENTE;
    private static final ValidationStatus UPDATED_STATUS = ValidationStatus.VISE_GWTE;

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final String DEFAULT_VALIDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_VALIDATED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/validations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ValidationRepository validationRepository;

    @Autowired
    private ValidationMapper validationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Validation validation;

    private Validation insertedValidation;

    @Autowired
    private AttestationPresenceRepository attestationPresenceRepository;

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private AttestationFinStageRepository attestationFinStageRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Validation createEntity() {
        return new Validation()
            .reference(DEFAULT_REFERENCE)
            .validationDate(DEFAULT_VALIDATION_DATE)
            .status(DEFAULT_STATUS)
            .comments(DEFAULT_COMMENTS)
            .validatedBy(DEFAULT_VALIDATED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Validation createUpdatedEntity() {
        return new Validation()
            .reference(UPDATED_REFERENCE)
            .validationDate(UPDATED_VALIDATION_DATE)
            .status(UPDATED_STATUS)
            .comments(UPDATED_COMMENTS)
            .validatedBy(UPDATED_VALIDATED_BY);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Validation.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        validation = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedValidation != null) {
            validationRepository.delete(insertedValidation).block();
            insertedValidation = null;
        }
        deleteEntities(em);
    }

    @Test
    void createValidation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Validation
        ValidationDTO validationDTO = validationMapper.toDto(validation);
        var returnedValidationDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(validationDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ValidationDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Validation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedValidation = validationMapper.toEntity(returnedValidationDTO);
        assertValidationUpdatableFieldsEquals(returnedValidation, getPersistedValidation(returnedValidation));

        insertedValidation = returnedValidation;
    }

    @Test
    void createValidationWithExistingId() throws Exception {
        // Create the Validation with an existing ID
        validation.setId(1L);
        ValidationDTO validationDTO = validationMapper.toDto(validation);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(validationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Validation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        validation.setReference(null);

        // Create the Validation, which fails.
        ValidationDTO validationDTO = validationMapper.toDto(validation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(validationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkValidationDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        validation.setValidationDate(null);

        // Create the Validation, which fails.
        ValidationDTO validationDTO = validationMapper.toDto(validation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(validationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        validation.setStatus(null);

        // Create the Validation, which fails.
        ValidationDTO validationDTO = validationMapper.toDto(validation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(validationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkValidatedByIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        validation.setValidatedBy(null);

        // Create the Validation, which fails.
        ValidationDTO validationDTO = validationMapper.toDto(validation);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(validationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllValidations() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList
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
            .value(hasItem(validation.getId().intValue()))
            .jsonPath("$.[*].reference")
            .value(hasItem(DEFAULT_REFERENCE))
            .jsonPath("$.[*].validationDate")
            .value(hasItem(DEFAULT_VALIDATION_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].comments")
            .value(hasItem(DEFAULT_COMMENTS))
            .jsonPath("$.[*].validatedBy")
            .value(hasItem(DEFAULT_VALIDATED_BY));
    }

    @Test
    void getValidation() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get the validation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, validation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(validation.getId().intValue()))
            .jsonPath("$.reference")
            .value(is(DEFAULT_REFERENCE))
            .jsonPath("$.validationDate")
            .value(is(DEFAULT_VALIDATION_DATE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.comments")
            .value(is(DEFAULT_COMMENTS))
            .jsonPath("$.validatedBy")
            .value(is(DEFAULT_VALIDATED_BY));
    }

    @Test
    void getValidationsByIdFiltering() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        Long id = validation.getId();

        defaultValidationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultValidationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultValidationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllValidationsByReferenceIsEqualToSomething() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where reference equals to
        defaultValidationFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    void getAllValidationsByReferenceIsInShouldWork() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where reference in
        defaultValidationFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    void getAllValidationsByReferenceIsNullOrNotNull() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where reference is not null
        defaultValidationFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    void getAllValidationsByReferenceContainsSomething() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where reference contains
        defaultValidationFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    void getAllValidationsByReferenceNotContainsSomething() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where reference does not contain
        defaultValidationFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    void getAllValidationsByValidationDateIsEqualToSomething() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where validationDate equals to
        defaultValidationFiltering("validationDate.equals=" + DEFAULT_VALIDATION_DATE, "validationDate.equals=" + UPDATED_VALIDATION_DATE);
    }

    @Test
    void getAllValidationsByValidationDateIsInShouldWork() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where validationDate in
        defaultValidationFiltering(
            "validationDate.in=" + DEFAULT_VALIDATION_DATE + "," + UPDATED_VALIDATION_DATE,
            "validationDate.in=" + UPDATED_VALIDATION_DATE
        );
    }

    @Test
    void getAllValidationsByValidationDateIsNullOrNotNull() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where validationDate is not null
        defaultValidationFiltering("validationDate.specified=true", "validationDate.specified=false");
    }

    @Test
    void getAllValidationsByValidationDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where validationDate is greater than or equal to
        defaultValidationFiltering(
            "validationDate.greaterThanOrEqual=" + DEFAULT_VALIDATION_DATE,
            "validationDate.greaterThanOrEqual=" + UPDATED_VALIDATION_DATE
        );
    }

    @Test
    void getAllValidationsByValidationDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where validationDate is less than or equal to
        defaultValidationFiltering(
            "validationDate.lessThanOrEqual=" + DEFAULT_VALIDATION_DATE,
            "validationDate.lessThanOrEqual=" + SMALLER_VALIDATION_DATE
        );
    }

    @Test
    void getAllValidationsByValidationDateIsLessThanSomething() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where validationDate is less than
        defaultValidationFiltering(
            "validationDate.lessThan=" + UPDATED_VALIDATION_DATE,
            "validationDate.lessThan=" + DEFAULT_VALIDATION_DATE
        );
    }

    @Test
    void getAllValidationsByValidationDateIsGreaterThanSomething() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where validationDate is greater than
        defaultValidationFiltering(
            "validationDate.greaterThan=" + SMALLER_VALIDATION_DATE,
            "validationDate.greaterThan=" + DEFAULT_VALIDATION_DATE
        );
    }

    @Test
    void getAllValidationsByStatusIsEqualToSomething() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where status equals to
        defaultValidationFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    void getAllValidationsByStatusIsInShouldWork() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where status in
        defaultValidationFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    void getAllValidationsByStatusIsNullOrNotNull() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where status is not null
        defaultValidationFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    void getAllValidationsByCommentsIsEqualToSomething() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where comments equals to
        defaultValidationFiltering("comments.equals=" + DEFAULT_COMMENTS, "comments.equals=" + UPDATED_COMMENTS);
    }

    @Test
    void getAllValidationsByCommentsIsInShouldWork() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where comments in
        defaultValidationFiltering("comments.in=" + DEFAULT_COMMENTS + "," + UPDATED_COMMENTS, "comments.in=" + UPDATED_COMMENTS);
    }

    @Test
    void getAllValidationsByCommentsIsNullOrNotNull() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where comments is not null
        defaultValidationFiltering("comments.specified=true", "comments.specified=false");
    }

    @Test
    void getAllValidationsByCommentsContainsSomething() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where comments contains
        defaultValidationFiltering("comments.contains=" + DEFAULT_COMMENTS, "comments.contains=" + UPDATED_COMMENTS);
    }

    @Test
    void getAllValidationsByCommentsNotContainsSomething() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where comments does not contain
        defaultValidationFiltering("comments.doesNotContain=" + UPDATED_COMMENTS, "comments.doesNotContain=" + DEFAULT_COMMENTS);
    }

    @Test
    void getAllValidationsByValidatedByIsEqualToSomething() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where validatedBy equals to
        defaultValidationFiltering("validatedBy.equals=" + DEFAULT_VALIDATED_BY, "validatedBy.equals=" + UPDATED_VALIDATED_BY);
    }

    @Test
    void getAllValidationsByValidatedByIsInShouldWork() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where validatedBy in
        defaultValidationFiltering(
            "validatedBy.in=" + DEFAULT_VALIDATED_BY + "," + UPDATED_VALIDATED_BY,
            "validatedBy.in=" + UPDATED_VALIDATED_BY
        );
    }

    @Test
    void getAllValidationsByValidatedByIsNullOrNotNull() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where validatedBy is not null
        defaultValidationFiltering("validatedBy.specified=true", "validatedBy.specified=false");
    }

    @Test
    void getAllValidationsByValidatedByContainsSomething() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where validatedBy contains
        defaultValidationFiltering("validatedBy.contains=" + DEFAULT_VALIDATED_BY, "validatedBy.contains=" + UPDATED_VALIDATED_BY);
    }

    @Test
    void getAllValidationsByValidatedByNotContainsSomething() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        // Get all the validationList where validatedBy does not contain
        defaultValidationFiltering(
            "validatedBy.doesNotContain=" + UPDATED_VALIDATED_BY,
            "validatedBy.doesNotContain=" + DEFAULT_VALIDATED_BY
        );
    }

    @Test
    void getAllValidationsByAttestationPresenceIsEqualToSomething() {
        AttestationPresence attestationPresence = AttestationPresenceResourceIT.createEntity();
        attestationPresenceRepository.save(attestationPresence).block();
        Long attestationPresenceId = attestationPresence.getId();
        validation.setAttestationPresenceId(attestationPresenceId);
        insertedValidation = validationRepository.save(validation).block();
        // Get all the validationList where attestationPresence equals to attestationPresenceId
        defaultValidationShouldBeFound("attestationPresenceId.equals=" + attestationPresenceId);

        // Get all the validationList where attestationPresence equals to (attestationPresenceId + 1)
        defaultValidationShouldNotBeFound("attestationPresenceId.equals=" + (attestationPresenceId + 1));
    }

    @Test
    void getAllValidationsByContratIsEqualToSomething() {
        Contrat contrat = ContratResourceIT.createEntity();
        contratRepository.save(contrat).block();
        Long contratId = contrat.getId();
        validation.setContratId(contratId);
        insertedValidation = validationRepository.save(validation).block();
        // Get all the validationList where contrat equals to contratId
        defaultValidationShouldBeFound("contratId.equals=" + contratId);

        // Get all the validationList where contrat equals to (contratId + 1)
        defaultValidationShouldNotBeFound("contratId.equals=" + (contratId + 1));
    }

    @Test
    void getAllValidationsByAttestationFinStageIsEqualToSomething() {
        AttestationFinStage attestationFinStage = AttestationFinStageResourceIT.createEntity();
        attestationFinStageRepository.save(attestationFinStage).block();
        Long attestationFinStageId = attestationFinStage.getId();
        validation.setAttestationFinStageId(attestationFinStageId);
        insertedValidation = validationRepository.save(validation).block();
        // Get all the validationList where attestationFinStage equals to attestationFinStageId
        defaultValidationShouldBeFound("attestationFinStageId.equals=" + attestationFinStageId);

        // Get all the validationList where attestationFinStage equals to (attestationFinStageId + 1)
        defaultValidationShouldNotBeFound("attestationFinStageId.equals=" + (attestationFinStageId + 1));
    }

    @Test
    void getAllValidationsByUserIsEqualToSomething() {
        AppUser user = AppUserResourceIT.createEntity();
        appUserRepository.save(user).block();
        Long userId = user.getId();
        validation.setUserId(userId);
        insertedValidation = validationRepository.save(validation).block();
        // Get all the validationList where user equals to userId
        defaultValidationShouldBeFound("userId.equals=" + userId);

        // Get all the validationList where user equals to (userId + 1)
        defaultValidationShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    private void defaultValidationFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultValidationShouldBeFound(shouldBeFound);
        defaultValidationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultValidationShouldBeFound(String filter) {
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
            .value(hasItem(validation.getId().intValue()))
            .jsonPath("$.[*].reference")
            .value(hasItem(DEFAULT_REFERENCE))
            .jsonPath("$.[*].validationDate")
            .value(hasItem(DEFAULT_VALIDATION_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].comments")
            .value(hasItem(DEFAULT_COMMENTS))
            .jsonPath("$.[*].validatedBy")
            .value(hasItem(DEFAULT_VALIDATED_BY));

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
    private void defaultValidationShouldNotBeFound(String filter) {
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
    void getNonExistingValidation() {
        // Get the validation
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingValidation() throws Exception {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the validation
        Validation updatedValidation = validationRepository.findById(validation.getId()).block();
        updatedValidation
            .reference(UPDATED_REFERENCE)
            .validationDate(UPDATED_VALIDATION_DATE)
            .status(UPDATED_STATUS)
            .comments(UPDATED_COMMENTS)
            .validatedBy(UPDATED_VALIDATED_BY);
        ValidationDTO validationDTO = validationMapper.toDto(updatedValidation);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, validationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(validationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Validation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedValidationToMatchAllProperties(updatedValidation);
    }

    @Test
    void putNonExistingValidation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validation.setId(longCount.incrementAndGet());

        // Create the Validation
        ValidationDTO validationDTO = validationMapper.toDto(validation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, validationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(validationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Validation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchValidation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validation.setId(longCount.incrementAndGet());

        // Create the Validation
        ValidationDTO validationDTO = validationMapper.toDto(validation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(validationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Validation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamValidation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validation.setId(longCount.incrementAndGet());

        // Create the Validation
        ValidationDTO validationDTO = validationMapper.toDto(validation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(validationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Validation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateValidationWithPatch() throws Exception {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the validation using partial update
        Validation partialUpdatedValidation = new Validation();
        partialUpdatedValidation.setId(validation.getId());

        partialUpdatedValidation.comments(UPDATED_COMMENTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedValidation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedValidation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Validation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertValidationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedValidation, validation),
            getPersistedValidation(validation)
        );
    }

    @Test
    void fullUpdateValidationWithPatch() throws Exception {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the validation using partial update
        Validation partialUpdatedValidation = new Validation();
        partialUpdatedValidation.setId(validation.getId());

        partialUpdatedValidation
            .reference(UPDATED_REFERENCE)
            .validationDate(UPDATED_VALIDATION_DATE)
            .status(UPDATED_STATUS)
            .comments(UPDATED_COMMENTS)
            .validatedBy(UPDATED_VALIDATED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedValidation.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedValidation))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Validation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertValidationUpdatableFieldsEquals(partialUpdatedValidation, getPersistedValidation(partialUpdatedValidation));
    }

    @Test
    void patchNonExistingValidation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validation.setId(longCount.incrementAndGet());

        // Create the Validation
        ValidationDTO validationDTO = validationMapper.toDto(validation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, validationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(validationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Validation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchValidation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validation.setId(longCount.incrementAndGet());

        // Create the Validation
        ValidationDTO validationDTO = validationMapper.toDto(validation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(validationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Validation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamValidation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validation.setId(longCount.incrementAndGet());

        // Create the Validation
        ValidationDTO validationDTO = validationMapper.toDto(validation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(validationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Validation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteValidation() {
        // Initialize the database
        insertedValidation = validationRepository.save(validation).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the validation
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, validation.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return validationRepository.count().block();
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

    protected Validation getPersistedValidation(Validation validation) {
        return validationRepository.findById(validation.getId()).block();
    }

    protected void assertPersistedValidationToMatchAllProperties(Validation expectedValidation) {
        // Test fails because reactive api returns an empty object instead of null
        // assertValidationAllPropertiesEquals(expectedValidation, getPersistedValidation(expectedValidation));
        assertValidationUpdatableFieldsEquals(expectedValidation, getPersistedValidation(expectedValidation));
    }

    protected void assertPersistedValidationToMatchUpdatableProperties(Validation expectedValidation) {
        // Test fails because reactive api returns an empty object instead of null
        // assertValidationAllUpdatablePropertiesEquals(expectedValidation, getPersistedValidation(expectedValidation));
        assertValidationUpdatableFieldsEquals(expectedValidation, getPersistedValidation(expectedValidation));
    }
}
