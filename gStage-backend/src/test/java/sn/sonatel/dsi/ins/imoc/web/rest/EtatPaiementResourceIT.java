package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static sn.sonatel.dsi.ins.imoc.domain.EtatPaiementAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.Contrat;
import sn.sonatel.dsi.ins.imoc.domain.Dfc;
import sn.sonatel.dsi.ins.imoc.domain.EtatPaiement;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.PaymentStatus;
import sn.sonatel.dsi.ins.imoc.repository.AssistantGWTERepository;
import sn.sonatel.dsi.ins.imoc.repository.ContratRepository;
import sn.sonatel.dsi.ins.imoc.repository.DfcRepository;
import sn.sonatel.dsi.ins.imoc.repository.EntityManager;
import sn.sonatel.dsi.ins.imoc.repository.EtatPaiementRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.EtatPaiementDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.EtatPaiementMapper;

/**
 * Integration tests for the {@link EtatPaiementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EtatPaiementResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final String DEFAULT_PAYMENT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_NUMBER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_PAYMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PAYMENT_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_PAYMENT_DATE = LocalDate.ofEpochDay(-1L);

    private static final Double DEFAULT_AMOUNT = 0D;
    private static final Double UPDATED_AMOUNT = 1D;
    private static final Double SMALLER_AMOUNT = 0D - 1D;

    private static final String DEFAULT_ACT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ACT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_PAYMENT_PHONE = "873509076";
    private static final String UPDATED_PAYMENT_PHONE = "419336913";

    private static final PaymentStatus DEFAULT_STATUS = PaymentStatus.EN_ATTENTE;
    private static final PaymentStatus UPDATED_STATUS = PaymentStatus.EN_COURS;

    private static final LocalDate DEFAULT_PROCESSING_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PROCESSING_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_PROCESSING_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/etat-paiements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EtatPaiementRepository etatPaiementRepository;

    @Autowired
    private EtatPaiementMapper etatPaiementMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private EtatPaiement etatPaiement;

    private EtatPaiement insertedEtatPaiement;

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private DfcRepository dfcRepository;

    @Autowired
    private AssistantGWTERepository assistantGWTERepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EtatPaiement createEntity() {
        return new EtatPaiement()
            .reference(DEFAULT_REFERENCE)
            .paymentNumber(DEFAULT_PAYMENT_NUMBER)
            .paymentDate(DEFAULT_PAYMENT_DATE)
            .amount(DEFAULT_AMOUNT)
            .actCode(DEFAULT_ACT_CODE)
            .paymentPhone(DEFAULT_PAYMENT_PHONE)
            .status(DEFAULT_STATUS)
            .processingDate(DEFAULT_PROCESSING_DATE)
            .comments(DEFAULT_COMMENTS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EtatPaiement createUpdatedEntity() {
        return new EtatPaiement()
            .reference(UPDATED_REFERENCE)
            .paymentNumber(UPDATED_PAYMENT_NUMBER)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .amount(UPDATED_AMOUNT)
            .actCode(UPDATED_ACT_CODE)
            .paymentPhone(UPDATED_PAYMENT_PHONE)
            .status(UPDATED_STATUS)
            .processingDate(UPDATED_PROCESSING_DATE)
            .comments(UPDATED_COMMENTS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(EtatPaiement.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        etatPaiement = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEtatPaiement != null) {
            etatPaiementRepository.delete(insertedEtatPaiement).block();
            insertedEtatPaiement = null;
        }
        deleteEntities(em);
    }

    @Test
    void createEtatPaiement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EtatPaiement
        EtatPaiementDTO etatPaiementDTO = etatPaiementMapper.toDto(etatPaiement);
        var returnedEtatPaiementDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etatPaiementDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(EtatPaiementDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the EtatPaiement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEtatPaiement = etatPaiementMapper.toEntity(returnedEtatPaiementDTO);
        assertEtatPaiementUpdatableFieldsEquals(returnedEtatPaiement, getPersistedEtatPaiement(returnedEtatPaiement));

        insertedEtatPaiement = returnedEtatPaiement;
    }

    @Test
    void createEtatPaiementWithExistingId() throws Exception {
        // Create the EtatPaiement with an existing ID
        etatPaiement.setId(1L);
        EtatPaiementDTO etatPaiementDTO = etatPaiementMapper.toDto(etatPaiement);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etatPaiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EtatPaiement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etatPaiement.setReference(null);

        // Create the EtatPaiement, which fails.
        EtatPaiementDTO etatPaiementDTO = etatPaiementMapper.toDto(etatPaiement);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etatPaiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPaymentNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etatPaiement.setPaymentNumber(null);

        // Create the EtatPaiement, which fails.
        EtatPaiementDTO etatPaiementDTO = etatPaiementMapper.toDto(etatPaiement);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etatPaiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPaymentDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etatPaiement.setPaymentDate(null);

        // Create the EtatPaiement, which fails.
        EtatPaiementDTO etatPaiementDTO = etatPaiementMapper.toDto(etatPaiement);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etatPaiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etatPaiement.setAmount(null);

        // Create the EtatPaiement, which fails.
        EtatPaiementDTO etatPaiementDTO = etatPaiementMapper.toDto(etatPaiement);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etatPaiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etatPaiement.setActCode(null);

        // Create the EtatPaiement, which fails.
        EtatPaiementDTO etatPaiementDTO = etatPaiementMapper.toDto(etatPaiement);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etatPaiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPaymentPhoneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etatPaiement.setPaymentPhone(null);

        // Create the EtatPaiement, which fails.
        EtatPaiementDTO etatPaiementDTO = etatPaiementMapper.toDto(etatPaiement);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etatPaiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etatPaiement.setStatus(null);

        // Create the EtatPaiement, which fails.
        EtatPaiementDTO etatPaiementDTO = etatPaiementMapper.toDto(etatPaiement);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etatPaiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllEtatPaiements() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList
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
            .value(hasItem(etatPaiement.getId().intValue()))
            .jsonPath("$.[*].reference")
            .value(hasItem(DEFAULT_REFERENCE))
            .jsonPath("$.[*].paymentNumber")
            .value(hasItem(DEFAULT_PAYMENT_NUMBER))
            .jsonPath("$.[*].paymentDate")
            .value(hasItem(DEFAULT_PAYMENT_DATE.toString()))
            .jsonPath("$.[*].amount")
            .value(hasItem(DEFAULT_AMOUNT.doubleValue()))
            .jsonPath("$.[*].actCode")
            .value(hasItem(DEFAULT_ACT_CODE))
            .jsonPath("$.[*].paymentPhone")
            .value(hasItem(DEFAULT_PAYMENT_PHONE))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].processingDate")
            .value(hasItem(DEFAULT_PROCESSING_DATE.toString()))
            .jsonPath("$.[*].comments")
            .value(hasItem(DEFAULT_COMMENTS));
    }

    @Test
    void getEtatPaiement() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get the etatPaiement
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, etatPaiement.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(etatPaiement.getId().intValue()))
            .jsonPath("$.reference")
            .value(is(DEFAULT_REFERENCE))
            .jsonPath("$.paymentNumber")
            .value(is(DEFAULT_PAYMENT_NUMBER))
            .jsonPath("$.paymentDate")
            .value(is(DEFAULT_PAYMENT_DATE.toString()))
            .jsonPath("$.amount")
            .value(is(DEFAULT_AMOUNT.doubleValue()))
            .jsonPath("$.actCode")
            .value(is(DEFAULT_ACT_CODE))
            .jsonPath("$.paymentPhone")
            .value(is(DEFAULT_PAYMENT_PHONE))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.processingDate")
            .value(is(DEFAULT_PROCESSING_DATE.toString()))
            .jsonPath("$.comments")
            .value(is(DEFAULT_COMMENTS));
    }

    @Test
    void getEtatPaiementsByIdFiltering() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        Long id = etatPaiement.getId();

        defaultEtatPaiementFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEtatPaiementFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEtatPaiementFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllEtatPaiementsByReferenceIsEqualToSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where reference equals to
        defaultEtatPaiementFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    void getAllEtatPaiementsByReferenceIsInShouldWork() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where reference in
        defaultEtatPaiementFiltering("reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE, "reference.in=" + UPDATED_REFERENCE);
    }

    @Test
    void getAllEtatPaiementsByReferenceIsNullOrNotNull() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where reference is not null
        defaultEtatPaiementFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    void getAllEtatPaiementsByReferenceContainsSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where reference contains
        defaultEtatPaiementFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    void getAllEtatPaiementsByReferenceNotContainsSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where reference does not contain
        defaultEtatPaiementFiltering("reference.doesNotContain=" + UPDATED_REFERENCE, "reference.doesNotContain=" + DEFAULT_REFERENCE);
    }

    @Test
    void getAllEtatPaiementsByPaymentNumberIsEqualToSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentNumber equals to
        defaultEtatPaiementFiltering("paymentNumber.equals=" + DEFAULT_PAYMENT_NUMBER, "paymentNumber.equals=" + UPDATED_PAYMENT_NUMBER);
    }

    @Test
    void getAllEtatPaiementsByPaymentNumberIsInShouldWork() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentNumber in
        defaultEtatPaiementFiltering(
            "paymentNumber.in=" + DEFAULT_PAYMENT_NUMBER + "," + UPDATED_PAYMENT_NUMBER,
            "paymentNumber.in=" + UPDATED_PAYMENT_NUMBER
        );
    }

    @Test
    void getAllEtatPaiementsByPaymentNumberIsNullOrNotNull() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentNumber is not null
        defaultEtatPaiementFiltering("paymentNumber.specified=true", "paymentNumber.specified=false");
    }

    @Test
    void getAllEtatPaiementsByPaymentNumberContainsSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentNumber contains
        defaultEtatPaiementFiltering(
            "paymentNumber.contains=" + DEFAULT_PAYMENT_NUMBER,
            "paymentNumber.contains=" + UPDATED_PAYMENT_NUMBER
        );
    }

    @Test
    void getAllEtatPaiementsByPaymentNumberNotContainsSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentNumber does not contain
        defaultEtatPaiementFiltering(
            "paymentNumber.doesNotContain=" + UPDATED_PAYMENT_NUMBER,
            "paymentNumber.doesNotContain=" + DEFAULT_PAYMENT_NUMBER
        );
    }

    @Test
    void getAllEtatPaiementsByPaymentDateIsEqualToSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentDate equals to
        defaultEtatPaiementFiltering("paymentDate.equals=" + DEFAULT_PAYMENT_DATE, "paymentDate.equals=" + UPDATED_PAYMENT_DATE);
    }

    @Test
    void getAllEtatPaiementsByPaymentDateIsInShouldWork() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentDate in
        defaultEtatPaiementFiltering(
            "paymentDate.in=" + DEFAULT_PAYMENT_DATE + "," + UPDATED_PAYMENT_DATE,
            "paymentDate.in=" + UPDATED_PAYMENT_DATE
        );
    }

    @Test
    void getAllEtatPaiementsByPaymentDateIsNullOrNotNull() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentDate is not null
        defaultEtatPaiementFiltering("paymentDate.specified=true", "paymentDate.specified=false");
    }

    @Test
    void getAllEtatPaiementsByPaymentDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentDate is greater than or equal to
        defaultEtatPaiementFiltering(
            "paymentDate.greaterThanOrEqual=" + DEFAULT_PAYMENT_DATE,
            "paymentDate.greaterThanOrEqual=" + UPDATED_PAYMENT_DATE
        );
    }

    @Test
    void getAllEtatPaiementsByPaymentDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentDate is less than or equal to
        defaultEtatPaiementFiltering(
            "paymentDate.lessThanOrEqual=" + DEFAULT_PAYMENT_DATE,
            "paymentDate.lessThanOrEqual=" + SMALLER_PAYMENT_DATE
        );
    }

    @Test
    void getAllEtatPaiementsByPaymentDateIsLessThanSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentDate is less than
        defaultEtatPaiementFiltering("paymentDate.lessThan=" + UPDATED_PAYMENT_DATE, "paymentDate.lessThan=" + DEFAULT_PAYMENT_DATE);
    }

    @Test
    void getAllEtatPaiementsByPaymentDateIsGreaterThanSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentDate is greater than
        defaultEtatPaiementFiltering("paymentDate.greaterThan=" + SMALLER_PAYMENT_DATE, "paymentDate.greaterThan=" + DEFAULT_PAYMENT_DATE);
    }

    @Test
    void getAllEtatPaiementsByAmountIsEqualToSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where amount equals to
        defaultEtatPaiementFiltering("amount.equals=" + DEFAULT_AMOUNT, "amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    void getAllEtatPaiementsByAmountIsInShouldWork() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where amount in
        defaultEtatPaiementFiltering("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT, "amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    void getAllEtatPaiementsByAmountIsNullOrNotNull() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where amount is not null
        defaultEtatPaiementFiltering("amount.specified=true", "amount.specified=false");
    }

    @Test
    void getAllEtatPaiementsByAmountIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where amount is greater than or equal to
        defaultEtatPaiementFiltering("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT, "amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    void getAllEtatPaiementsByAmountIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where amount is less than or equal to
        defaultEtatPaiementFiltering("amount.lessThanOrEqual=" + DEFAULT_AMOUNT, "amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    void getAllEtatPaiementsByAmountIsLessThanSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where amount is less than
        defaultEtatPaiementFiltering("amount.lessThan=" + UPDATED_AMOUNT, "amount.lessThan=" + DEFAULT_AMOUNT);
    }

    @Test
    void getAllEtatPaiementsByAmountIsGreaterThanSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where amount is greater than
        defaultEtatPaiementFiltering("amount.greaterThan=" + SMALLER_AMOUNT, "amount.greaterThan=" + DEFAULT_AMOUNT);
    }

    @Test
    void getAllEtatPaiementsByActCodeIsEqualToSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where actCode equals to
        defaultEtatPaiementFiltering("actCode.equals=" + DEFAULT_ACT_CODE, "actCode.equals=" + UPDATED_ACT_CODE);
    }

    @Test
    void getAllEtatPaiementsByActCodeIsInShouldWork() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where actCode in
        defaultEtatPaiementFiltering("actCode.in=" + DEFAULT_ACT_CODE + "," + UPDATED_ACT_CODE, "actCode.in=" + UPDATED_ACT_CODE);
    }

    @Test
    void getAllEtatPaiementsByActCodeIsNullOrNotNull() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where actCode is not null
        defaultEtatPaiementFiltering("actCode.specified=true", "actCode.specified=false");
    }

    @Test
    void getAllEtatPaiementsByActCodeContainsSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where actCode contains
        defaultEtatPaiementFiltering("actCode.contains=" + DEFAULT_ACT_CODE, "actCode.contains=" + UPDATED_ACT_CODE);
    }

    @Test
    void getAllEtatPaiementsByActCodeNotContainsSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where actCode does not contain
        defaultEtatPaiementFiltering("actCode.doesNotContain=" + UPDATED_ACT_CODE, "actCode.doesNotContain=" + DEFAULT_ACT_CODE);
    }

    @Test
    void getAllEtatPaiementsByPaymentPhoneIsEqualToSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentPhone equals to
        defaultEtatPaiementFiltering("paymentPhone.equals=" + DEFAULT_PAYMENT_PHONE, "paymentPhone.equals=" + UPDATED_PAYMENT_PHONE);
    }

    @Test
    void getAllEtatPaiementsByPaymentPhoneIsInShouldWork() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentPhone in
        defaultEtatPaiementFiltering(
            "paymentPhone.in=" + DEFAULT_PAYMENT_PHONE + "," + UPDATED_PAYMENT_PHONE,
            "paymentPhone.in=" + UPDATED_PAYMENT_PHONE
        );
    }

    @Test
    void getAllEtatPaiementsByPaymentPhoneIsNullOrNotNull() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentPhone is not null
        defaultEtatPaiementFiltering("paymentPhone.specified=true", "paymentPhone.specified=false");
    }

    @Test
    void getAllEtatPaiementsByPaymentPhoneContainsSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentPhone contains
        defaultEtatPaiementFiltering("paymentPhone.contains=" + DEFAULT_PAYMENT_PHONE, "paymentPhone.contains=" + UPDATED_PAYMENT_PHONE);
    }

    @Test
    void getAllEtatPaiementsByPaymentPhoneNotContainsSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where paymentPhone does not contain
        defaultEtatPaiementFiltering(
            "paymentPhone.doesNotContain=" + UPDATED_PAYMENT_PHONE,
            "paymentPhone.doesNotContain=" + DEFAULT_PAYMENT_PHONE
        );
    }

    @Test
    void getAllEtatPaiementsByStatusIsEqualToSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where status equals to
        defaultEtatPaiementFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    void getAllEtatPaiementsByStatusIsInShouldWork() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where status in
        defaultEtatPaiementFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    void getAllEtatPaiementsByStatusIsNullOrNotNull() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where status is not null
        defaultEtatPaiementFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    void getAllEtatPaiementsByProcessingDateIsEqualToSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where processingDate equals to
        defaultEtatPaiementFiltering(
            "processingDate.equals=" + DEFAULT_PROCESSING_DATE,
            "processingDate.equals=" + UPDATED_PROCESSING_DATE
        );
    }

    @Test
    void getAllEtatPaiementsByProcessingDateIsInShouldWork() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where processingDate in
        defaultEtatPaiementFiltering(
            "processingDate.in=" + DEFAULT_PROCESSING_DATE + "," + UPDATED_PROCESSING_DATE,
            "processingDate.in=" + UPDATED_PROCESSING_DATE
        );
    }

    @Test
    void getAllEtatPaiementsByProcessingDateIsNullOrNotNull() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where processingDate is not null
        defaultEtatPaiementFiltering("processingDate.specified=true", "processingDate.specified=false");
    }

    @Test
    void getAllEtatPaiementsByProcessingDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where processingDate is greater than or equal to
        defaultEtatPaiementFiltering(
            "processingDate.greaterThanOrEqual=" + DEFAULT_PROCESSING_DATE,
            "processingDate.greaterThanOrEqual=" + UPDATED_PROCESSING_DATE
        );
    }

    @Test
    void getAllEtatPaiementsByProcessingDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where processingDate is less than or equal to
        defaultEtatPaiementFiltering(
            "processingDate.lessThanOrEqual=" + DEFAULT_PROCESSING_DATE,
            "processingDate.lessThanOrEqual=" + SMALLER_PROCESSING_DATE
        );
    }

    @Test
    void getAllEtatPaiementsByProcessingDateIsLessThanSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where processingDate is less than
        defaultEtatPaiementFiltering(
            "processingDate.lessThan=" + UPDATED_PROCESSING_DATE,
            "processingDate.lessThan=" + DEFAULT_PROCESSING_DATE
        );
    }

    @Test
    void getAllEtatPaiementsByProcessingDateIsGreaterThanSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where processingDate is greater than
        defaultEtatPaiementFiltering(
            "processingDate.greaterThan=" + SMALLER_PROCESSING_DATE,
            "processingDate.greaterThan=" + DEFAULT_PROCESSING_DATE
        );
    }

    @Test
    void getAllEtatPaiementsByCommentsIsEqualToSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where comments equals to
        defaultEtatPaiementFiltering("comments.equals=" + DEFAULT_COMMENTS, "comments.equals=" + UPDATED_COMMENTS);
    }

    @Test
    void getAllEtatPaiementsByCommentsIsInShouldWork() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where comments in
        defaultEtatPaiementFiltering("comments.in=" + DEFAULT_COMMENTS + "," + UPDATED_COMMENTS, "comments.in=" + UPDATED_COMMENTS);
    }

    @Test
    void getAllEtatPaiementsByCommentsIsNullOrNotNull() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where comments is not null
        defaultEtatPaiementFiltering("comments.specified=true", "comments.specified=false");
    }

    @Test
    void getAllEtatPaiementsByCommentsContainsSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where comments contains
        defaultEtatPaiementFiltering("comments.contains=" + DEFAULT_COMMENTS, "comments.contains=" + UPDATED_COMMENTS);
    }

    @Test
    void getAllEtatPaiementsByCommentsNotContainsSomething() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        // Get all the etatPaiementList where comments does not contain
        defaultEtatPaiementFiltering("comments.doesNotContain=" + UPDATED_COMMENTS, "comments.doesNotContain=" + DEFAULT_COMMENTS);
    }

    @Test
    void getAllEtatPaiementsByContratIsEqualToSomething() {
        Contrat contrat = ContratResourceIT.createEntity();
        contratRepository.save(contrat).block();
        Long contratId = contrat.getId();
        etatPaiement.setContratId(contratId);
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();
        // Get all the etatPaiementList where contrat equals to contratId
        defaultEtatPaiementShouldBeFound("contratId.equals=" + contratId);

        // Get all the etatPaiementList where contrat equals to (contratId + 1)
        defaultEtatPaiementShouldNotBeFound("contratId.equals=" + (contratId + 1));
    }

    @Test
    void getAllEtatPaiementsByDfcIsEqualToSomething() {
        Dfc dfc = DfcResourceIT.createEntity();
        dfcRepository.save(dfc).block();
        Long dfcId = dfc.getId();
        etatPaiement.setDfcId(dfcId);
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();
        // Get all the etatPaiementList where dfc equals to dfcId
        defaultEtatPaiementShouldBeFound("dfcId.equals=" + dfcId);

        // Get all the etatPaiementList where dfc equals to (dfcId + 1)
        defaultEtatPaiementShouldNotBeFound("dfcId.equals=" + (dfcId + 1));
    }

    @Test
    void getAllEtatPaiementsByAssistantGWTECreatorIsEqualToSomething() {
        AssistantGWTE assistantGWTECreator = AssistantGWTEResourceIT.createEntity();
        assistantGWTERepository.save(assistantGWTECreator).block();
        Long assistantGWTECreatorId = assistantGWTECreator.getId();
        etatPaiement.setAssistantGWTECreatorId(assistantGWTECreatorId);
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();
        // Get all the etatPaiementList where assistantGWTECreator equals to assistantGWTECreatorId
        defaultEtatPaiementShouldBeFound("assistantGWTECreatorId.equals=" + assistantGWTECreatorId);

        // Get all the etatPaiementList where assistantGWTECreator equals to (assistantGWTECreatorId + 1)
        defaultEtatPaiementShouldNotBeFound("assistantGWTECreatorId.equals=" + (assistantGWTECreatorId + 1));
    }

    private void defaultEtatPaiementFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultEtatPaiementShouldBeFound(shouldBeFound);
        defaultEtatPaiementShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEtatPaiementShouldBeFound(String filter) {
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
            .value(hasItem(etatPaiement.getId().intValue()))
            .jsonPath("$.[*].reference")
            .value(hasItem(DEFAULT_REFERENCE))
            .jsonPath("$.[*].paymentNumber")
            .value(hasItem(DEFAULT_PAYMENT_NUMBER))
            .jsonPath("$.[*].paymentDate")
            .value(hasItem(DEFAULT_PAYMENT_DATE.toString()))
            .jsonPath("$.[*].amount")
            .value(hasItem(DEFAULT_AMOUNT.doubleValue()))
            .jsonPath("$.[*].actCode")
            .value(hasItem(DEFAULT_ACT_CODE))
            .jsonPath("$.[*].paymentPhone")
            .value(hasItem(DEFAULT_PAYMENT_PHONE))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].processingDate")
            .value(hasItem(DEFAULT_PROCESSING_DATE.toString()))
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
    private void defaultEtatPaiementShouldNotBeFound(String filter) {
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
    void getNonExistingEtatPaiement() {
        // Get the etatPaiement
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEtatPaiement() throws Exception {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etatPaiement
        EtatPaiement updatedEtatPaiement = etatPaiementRepository.findById(etatPaiement.getId()).block();
        updatedEtatPaiement
            .reference(UPDATED_REFERENCE)
            .paymentNumber(UPDATED_PAYMENT_NUMBER)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .amount(UPDATED_AMOUNT)
            .actCode(UPDATED_ACT_CODE)
            .paymentPhone(UPDATED_PAYMENT_PHONE)
            .status(UPDATED_STATUS)
            .processingDate(UPDATED_PROCESSING_DATE)
            .comments(UPDATED_COMMENTS);
        EtatPaiementDTO etatPaiementDTO = etatPaiementMapper.toDto(updatedEtatPaiement);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, etatPaiementDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etatPaiementDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EtatPaiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEtatPaiementToMatchAllProperties(updatedEtatPaiement);
    }

    @Test
    void putNonExistingEtatPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etatPaiement.setId(longCount.incrementAndGet());

        // Create the EtatPaiement
        EtatPaiementDTO etatPaiementDTO = etatPaiementMapper.toDto(etatPaiement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, etatPaiementDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etatPaiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EtatPaiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEtatPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etatPaiement.setId(longCount.incrementAndGet());

        // Create the EtatPaiement
        EtatPaiementDTO etatPaiementDTO = etatPaiementMapper.toDto(etatPaiement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etatPaiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EtatPaiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEtatPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etatPaiement.setId(longCount.incrementAndGet());

        // Create the EtatPaiement
        EtatPaiementDTO etatPaiementDTO = etatPaiementMapper.toDto(etatPaiement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(etatPaiementDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EtatPaiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEtatPaiementWithPatch() throws Exception {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etatPaiement using partial update
        EtatPaiement partialUpdatedEtatPaiement = new EtatPaiement();
        partialUpdatedEtatPaiement.setId(etatPaiement.getId());

        partialUpdatedEtatPaiement
            .paymentNumber(UPDATED_PAYMENT_NUMBER)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .amount(UPDATED_AMOUNT)
            .actCode(UPDATED_ACT_CODE)
            .paymentPhone(UPDATED_PAYMENT_PHONE)
            .comments(UPDATED_COMMENTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEtatPaiement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEtatPaiement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EtatPaiement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEtatPaiementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEtatPaiement, etatPaiement),
            getPersistedEtatPaiement(etatPaiement)
        );
    }

    @Test
    void fullUpdateEtatPaiementWithPatch() throws Exception {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etatPaiement using partial update
        EtatPaiement partialUpdatedEtatPaiement = new EtatPaiement();
        partialUpdatedEtatPaiement.setId(etatPaiement.getId());

        partialUpdatedEtatPaiement
            .reference(UPDATED_REFERENCE)
            .paymentNumber(UPDATED_PAYMENT_NUMBER)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .amount(UPDATED_AMOUNT)
            .actCode(UPDATED_ACT_CODE)
            .paymentPhone(UPDATED_PAYMENT_PHONE)
            .status(UPDATED_STATUS)
            .processingDate(UPDATED_PROCESSING_DATE)
            .comments(UPDATED_COMMENTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEtatPaiement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEtatPaiement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EtatPaiement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEtatPaiementUpdatableFieldsEquals(partialUpdatedEtatPaiement, getPersistedEtatPaiement(partialUpdatedEtatPaiement));
    }

    @Test
    void patchNonExistingEtatPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etatPaiement.setId(longCount.incrementAndGet());

        // Create the EtatPaiement
        EtatPaiementDTO etatPaiementDTO = etatPaiementMapper.toDto(etatPaiement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, etatPaiementDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(etatPaiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EtatPaiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEtatPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etatPaiement.setId(longCount.incrementAndGet());

        // Create the EtatPaiement
        EtatPaiementDTO etatPaiementDTO = etatPaiementMapper.toDto(etatPaiement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(etatPaiementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EtatPaiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEtatPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etatPaiement.setId(longCount.incrementAndGet());

        // Create the EtatPaiement
        EtatPaiementDTO etatPaiementDTO = etatPaiementMapper.toDto(etatPaiement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(etatPaiementDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EtatPaiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEtatPaiement() {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.save(etatPaiement).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the etatPaiement
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, etatPaiement.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return etatPaiementRepository.count().block();
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

    protected EtatPaiement getPersistedEtatPaiement(EtatPaiement etatPaiement) {
        return etatPaiementRepository.findById(etatPaiement.getId()).block();
    }

    protected void assertPersistedEtatPaiementToMatchAllProperties(EtatPaiement expectedEtatPaiement) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEtatPaiementAllPropertiesEquals(expectedEtatPaiement, getPersistedEtatPaiement(expectedEtatPaiement));
        assertEtatPaiementUpdatableFieldsEquals(expectedEtatPaiement, getPersistedEtatPaiement(expectedEtatPaiement));
    }

    protected void assertPersistedEtatPaiementToMatchUpdatableProperties(EtatPaiement expectedEtatPaiement) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEtatPaiementAllUpdatablePropertiesEquals(expectedEtatPaiement, getPersistedEtatPaiement(expectedEtatPaiement));
        assertEtatPaiementUpdatableFieldsEquals(expectedEtatPaiement, getPersistedEtatPaiement(expectedEtatPaiement));
    }
}
