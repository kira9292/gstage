package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaireAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaire;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.repository.EntityManager;
import sn.sonatel.dsi.ins.imoc.repository.RestaurationStagiaireRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.RestaurationStagiaireDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.RestaurationStagiaireMapper;

/**
 * Integration tests for the {@link RestaurationStagiaireResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RestaurationStagiaireResourceIT {

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_START_DATE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_END_DATE = LocalDate.ofEpochDay(-1L);

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String DEFAULT_CARD_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CARD_NUMBER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/restauration-stagiaires";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RestaurationStagiaireRepository restaurationStagiaireRepository;

    @Autowired
    private RestaurationStagiaireMapper restaurationStagiaireMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RestaurationStagiaire restaurationStagiaire;

    private RestaurationStagiaire insertedRestaurationStagiaire;

    @Autowired
    private CandidatRepository candidatRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RestaurationStagiaire createEntity() {
        return new RestaurationStagiaire()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .status(DEFAULT_STATUS)
            .cardNumber(DEFAULT_CARD_NUMBER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RestaurationStagiaire createUpdatedEntity() {
        return new RestaurationStagiaire()
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .cardNumber(UPDATED_CARD_NUMBER);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RestaurationStagiaire.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        restaurationStagiaire = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedRestaurationStagiaire != null) {
            restaurationStagiaireRepository.delete(insertedRestaurationStagiaire).block();
            insertedRestaurationStagiaire = null;
        }
        deleteEntities(em);
    }

    @Test
    void createRestaurationStagiaire() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RestaurationStagiaire
        RestaurationStagiaireDTO restaurationStagiaireDTO = restaurationStagiaireMapper.toDto(restaurationStagiaire);
        var returnedRestaurationStagiaireDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurationStagiaireDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(RestaurationStagiaireDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the RestaurationStagiaire in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRestaurationStagiaire = restaurationStagiaireMapper.toEntity(returnedRestaurationStagiaireDTO);
        assertRestaurationStagiaireUpdatableFieldsEquals(
            returnedRestaurationStagiaire,
            getPersistedRestaurationStagiaire(returnedRestaurationStagiaire)
        );

        insertedRestaurationStagiaire = returnedRestaurationStagiaire;
    }

    @Test
    void createRestaurationStagiaireWithExistingId() throws Exception {
        // Create the RestaurationStagiaire with an existing ID
        restaurationStagiaire.setId(1L);
        RestaurationStagiaireDTO restaurationStagiaireDTO = restaurationStagiaireMapper.toDto(restaurationStagiaire);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurationStagiaireDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RestaurationStagiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurationStagiaire.setStartDate(null);

        // Create the RestaurationStagiaire, which fails.
        RestaurationStagiaireDTO restaurationStagiaireDTO = restaurationStagiaireMapper.toDto(restaurationStagiaire);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurationStagiaireDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurationStagiaire.setEndDate(null);

        // Create the RestaurationStagiaire, which fails.
        RestaurationStagiaireDTO restaurationStagiaireDTO = restaurationStagiaireMapper.toDto(restaurationStagiaire);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurationStagiaireDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurationStagiaire.setStatus(null);

        // Create the RestaurationStagiaire, which fails.
        RestaurationStagiaireDTO restaurationStagiaireDTO = restaurationStagiaireMapper.toDto(restaurationStagiaire);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurationStagiaireDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllRestaurationStagiaires() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList
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
            .value(hasItem(restaurationStagiaire.getId().intValue()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.booleanValue()))
            .jsonPath("$.[*].cardNumber")
            .value(hasItem(DEFAULT_CARD_NUMBER));
    }

    @Test
    void getRestaurationStagiaire() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get the restaurationStagiaire
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, restaurationStagiaire.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(restaurationStagiaire.getId().intValue()))
            .jsonPath("$.startDate")
            .value(is(DEFAULT_START_DATE.toString()))
            .jsonPath("$.endDate")
            .value(is(DEFAULT_END_DATE.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.booleanValue()))
            .jsonPath("$.cardNumber")
            .value(is(DEFAULT_CARD_NUMBER));
    }

    @Test
    void getRestaurationStagiairesByIdFiltering() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        Long id = restaurationStagiaire.getId();

        defaultRestaurationStagiaireFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRestaurationStagiaireFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRestaurationStagiaireFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllRestaurationStagiairesByStartDateIsEqualToSomething() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where startDate equals to
        defaultRestaurationStagiaireFiltering("startDate.equals=" + DEFAULT_START_DATE, "startDate.equals=" + UPDATED_START_DATE);
    }

    @Test
    void getAllRestaurationStagiairesByStartDateIsInShouldWork() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where startDate in
        defaultRestaurationStagiaireFiltering(
            "startDate.in=" + DEFAULT_START_DATE + "," + UPDATED_START_DATE,
            "startDate.in=" + UPDATED_START_DATE
        );
    }

    @Test
    void getAllRestaurationStagiairesByStartDateIsNullOrNotNull() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where startDate is not null
        defaultRestaurationStagiaireFiltering("startDate.specified=true", "startDate.specified=false");
    }

    @Test
    void getAllRestaurationStagiairesByStartDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where startDate is greater than or equal to
        defaultRestaurationStagiaireFiltering(
            "startDate.greaterThanOrEqual=" + DEFAULT_START_DATE,
            "startDate.greaterThanOrEqual=" + UPDATED_START_DATE
        );
    }

    @Test
    void getAllRestaurationStagiairesByStartDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where startDate is less than or equal to
        defaultRestaurationStagiaireFiltering(
            "startDate.lessThanOrEqual=" + DEFAULT_START_DATE,
            "startDate.lessThanOrEqual=" + SMALLER_START_DATE
        );
    }

    @Test
    void getAllRestaurationStagiairesByStartDateIsLessThanSomething() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where startDate is less than
        defaultRestaurationStagiaireFiltering("startDate.lessThan=" + UPDATED_START_DATE, "startDate.lessThan=" + DEFAULT_START_DATE);
    }

    @Test
    void getAllRestaurationStagiairesByStartDateIsGreaterThanSomething() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where startDate is greater than
        defaultRestaurationStagiaireFiltering("startDate.greaterThan=" + SMALLER_START_DATE, "startDate.greaterThan=" + DEFAULT_START_DATE);
    }

    @Test
    void getAllRestaurationStagiairesByEndDateIsEqualToSomething() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where endDate equals to
        defaultRestaurationStagiaireFiltering("endDate.equals=" + DEFAULT_END_DATE, "endDate.equals=" + UPDATED_END_DATE);
    }

    @Test
    void getAllRestaurationStagiairesByEndDateIsInShouldWork() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where endDate in
        defaultRestaurationStagiaireFiltering("endDate.in=" + DEFAULT_END_DATE + "," + UPDATED_END_DATE, "endDate.in=" + UPDATED_END_DATE);
    }

    @Test
    void getAllRestaurationStagiairesByEndDateIsNullOrNotNull() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where endDate is not null
        defaultRestaurationStagiaireFiltering("endDate.specified=true", "endDate.specified=false");
    }

    @Test
    void getAllRestaurationStagiairesByEndDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where endDate is greater than or equal to
        defaultRestaurationStagiaireFiltering(
            "endDate.greaterThanOrEqual=" + DEFAULT_END_DATE,
            "endDate.greaterThanOrEqual=" + UPDATED_END_DATE
        );
    }

    @Test
    void getAllRestaurationStagiairesByEndDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where endDate is less than or equal to
        defaultRestaurationStagiaireFiltering("endDate.lessThanOrEqual=" + DEFAULT_END_DATE, "endDate.lessThanOrEqual=" + SMALLER_END_DATE);
    }

    @Test
    void getAllRestaurationStagiairesByEndDateIsLessThanSomething() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where endDate is less than
        defaultRestaurationStagiaireFiltering("endDate.lessThan=" + UPDATED_END_DATE, "endDate.lessThan=" + DEFAULT_END_DATE);
    }

    @Test
    void getAllRestaurationStagiairesByEndDateIsGreaterThanSomething() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where endDate is greater than
        defaultRestaurationStagiaireFiltering("endDate.greaterThan=" + SMALLER_END_DATE, "endDate.greaterThan=" + DEFAULT_END_DATE);
    }

    @Test
    void getAllRestaurationStagiairesByStatusIsEqualToSomething() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where status equals to
        defaultRestaurationStagiaireFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    void getAllRestaurationStagiairesByStatusIsInShouldWork() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where status in
        defaultRestaurationStagiaireFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    void getAllRestaurationStagiairesByStatusIsNullOrNotNull() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where status is not null
        defaultRestaurationStagiaireFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    void getAllRestaurationStagiairesByCardNumberIsEqualToSomething() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where cardNumber equals to
        defaultRestaurationStagiaireFiltering("cardNumber.equals=" + DEFAULT_CARD_NUMBER, "cardNumber.equals=" + UPDATED_CARD_NUMBER);
    }

    @Test
    void getAllRestaurationStagiairesByCardNumberIsInShouldWork() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where cardNumber in
        defaultRestaurationStagiaireFiltering(
            "cardNumber.in=" + DEFAULT_CARD_NUMBER + "," + UPDATED_CARD_NUMBER,
            "cardNumber.in=" + UPDATED_CARD_NUMBER
        );
    }

    @Test
    void getAllRestaurationStagiairesByCardNumberIsNullOrNotNull() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where cardNumber is not null
        defaultRestaurationStagiaireFiltering("cardNumber.specified=true", "cardNumber.specified=false");
    }

    @Test
    void getAllRestaurationStagiairesByCardNumberContainsSomething() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where cardNumber contains
        defaultRestaurationStagiaireFiltering("cardNumber.contains=" + DEFAULT_CARD_NUMBER, "cardNumber.contains=" + UPDATED_CARD_NUMBER);
    }

    @Test
    void getAllRestaurationStagiairesByCardNumberNotContainsSomething() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        // Get all the restaurationStagiaireList where cardNumber does not contain
        defaultRestaurationStagiaireFiltering(
            "cardNumber.doesNotContain=" + UPDATED_CARD_NUMBER,
            "cardNumber.doesNotContain=" + DEFAULT_CARD_NUMBER
        );
    }

    @Test
    void getAllRestaurationStagiairesByCandidatIsEqualToSomething() {
        Candidat candidat = CandidatResourceIT.createEntity();
        candidatRepository.save(candidat).block();
        Long candidatId = candidat.getId();
        restaurationStagiaire.setCandidatId(candidatId);
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();
        // Get all the restaurationStagiaireList where candidat equals to candidatId
        defaultRestaurationStagiaireShouldBeFound("candidatId.equals=" + candidatId);

        // Get all the restaurationStagiaireList where candidat equals to (candidatId + 1)
        defaultRestaurationStagiaireShouldNotBeFound("candidatId.equals=" + (candidatId + 1));
    }

    private void defaultRestaurationStagiaireFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultRestaurationStagiaireShouldBeFound(shouldBeFound);
        defaultRestaurationStagiaireShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRestaurationStagiaireShouldBeFound(String filter) {
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
            .value(hasItem(restaurationStagiaire.getId().intValue()))
            .jsonPath("$.[*].startDate")
            .value(hasItem(DEFAULT_START_DATE.toString()))
            .jsonPath("$.[*].endDate")
            .value(hasItem(DEFAULT_END_DATE.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.booleanValue()))
            .jsonPath("$.[*].cardNumber")
            .value(hasItem(DEFAULT_CARD_NUMBER));

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
    private void defaultRestaurationStagiaireShouldNotBeFound(String filter) {
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
    void getNonExistingRestaurationStagiaire() {
        // Get the restaurationStagiaire
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingRestaurationStagiaire() throws Exception {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the restaurationStagiaire
        RestaurationStagiaire updatedRestaurationStagiaire = restaurationStagiaireRepository
            .findById(restaurationStagiaire.getId())
            .block();
        updatedRestaurationStagiaire
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .cardNumber(UPDATED_CARD_NUMBER);
        RestaurationStagiaireDTO restaurationStagiaireDTO = restaurationStagiaireMapper.toDto(updatedRestaurationStagiaire);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, restaurationStagiaireDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurationStagiaireDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RestaurationStagiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRestaurationStagiaireToMatchAllProperties(updatedRestaurationStagiaire);
    }

    @Test
    void putNonExistingRestaurationStagiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurationStagiaire.setId(longCount.incrementAndGet());

        // Create the RestaurationStagiaire
        RestaurationStagiaireDTO restaurationStagiaireDTO = restaurationStagiaireMapper.toDto(restaurationStagiaire);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, restaurationStagiaireDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurationStagiaireDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RestaurationStagiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRestaurationStagiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurationStagiaire.setId(longCount.incrementAndGet());

        // Create the RestaurationStagiaire
        RestaurationStagiaireDTO restaurationStagiaireDTO = restaurationStagiaireMapper.toDto(restaurationStagiaire);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurationStagiaireDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RestaurationStagiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRestaurationStagiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurationStagiaire.setId(longCount.incrementAndGet());

        // Create the RestaurationStagiaire
        RestaurationStagiaireDTO restaurationStagiaireDTO = restaurationStagiaireMapper.toDto(restaurationStagiaire);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(restaurationStagiaireDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RestaurationStagiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRestaurationStagiaireWithPatch() throws Exception {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the restaurationStagiaire using partial update
        RestaurationStagiaire partialUpdatedRestaurationStagiaire = new RestaurationStagiaire();
        partialUpdatedRestaurationStagiaire.setId(restaurationStagiaire.getId());

        partialUpdatedRestaurationStagiaire.startDate(UPDATED_START_DATE).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRestaurationStagiaire.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRestaurationStagiaire))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RestaurationStagiaire in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRestaurationStagiaireUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRestaurationStagiaire, restaurationStagiaire),
            getPersistedRestaurationStagiaire(restaurationStagiaire)
        );
    }

    @Test
    void fullUpdateRestaurationStagiaireWithPatch() throws Exception {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the restaurationStagiaire using partial update
        RestaurationStagiaire partialUpdatedRestaurationStagiaire = new RestaurationStagiaire();
        partialUpdatedRestaurationStagiaire.setId(restaurationStagiaire.getId());

        partialUpdatedRestaurationStagiaire
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .cardNumber(UPDATED_CARD_NUMBER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRestaurationStagiaire.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedRestaurationStagiaire))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RestaurationStagiaire in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRestaurationStagiaireUpdatableFieldsEquals(
            partialUpdatedRestaurationStagiaire,
            getPersistedRestaurationStagiaire(partialUpdatedRestaurationStagiaire)
        );
    }

    @Test
    void patchNonExistingRestaurationStagiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurationStagiaire.setId(longCount.incrementAndGet());

        // Create the RestaurationStagiaire
        RestaurationStagiaireDTO restaurationStagiaireDTO = restaurationStagiaireMapper.toDto(restaurationStagiaire);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, restaurationStagiaireDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(restaurationStagiaireDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RestaurationStagiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRestaurationStagiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurationStagiaire.setId(longCount.incrementAndGet());

        // Create the RestaurationStagiaire
        RestaurationStagiaireDTO restaurationStagiaireDTO = restaurationStagiaireMapper.toDto(restaurationStagiaire);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(restaurationStagiaireDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RestaurationStagiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRestaurationStagiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurationStagiaire.setId(longCount.incrementAndGet());

        // Create the RestaurationStagiaire
        RestaurationStagiaireDTO restaurationStagiaireDTO = restaurationStagiaireMapper.toDto(restaurationStagiaire);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(restaurationStagiaireDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RestaurationStagiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRestaurationStagiaire() {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the restaurationStagiaire
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, restaurationStagiaire.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return restaurationStagiaireRepository.count().block();
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

    protected RestaurationStagiaire getPersistedRestaurationStagiaire(RestaurationStagiaire restaurationStagiaire) {
        return restaurationStagiaireRepository.findById(restaurationStagiaire.getId()).block();
    }

    protected void assertPersistedRestaurationStagiaireToMatchAllProperties(RestaurationStagiaire expectedRestaurationStagiaire) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRestaurationStagiaireAllPropertiesEquals(expectedRestaurationStagiaire, getPersistedRestaurationStagiaire(expectedRestaurationStagiaire));
        assertRestaurationStagiaireUpdatableFieldsEquals(
            expectedRestaurationStagiaire,
            getPersistedRestaurationStagiaire(expectedRestaurationStagiaire)
        );
    }

    protected void assertPersistedRestaurationStagiaireToMatchUpdatableProperties(RestaurationStagiaire expectedRestaurationStagiaire) {
        // Test fails because reactive api returns an empty object instead of null
        // assertRestaurationStagiaireAllUpdatablePropertiesEquals(expectedRestaurationStagiaire, getPersistedRestaurationStagiaire(expectedRestaurationStagiaire));
        assertRestaurationStagiaireUpdatableFieldsEquals(
            expectedRestaurationStagiaire,
            getPersistedRestaurationStagiaire(expectedRestaurationStagiaire)
        );
    }
}
