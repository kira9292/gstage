package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.sonatel.dsi.ins.imoc.domain.EtatPaiementAsserts.*;
import static sn.sonatel.dsi.ins.imoc.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sn.sonatel.dsi.ins.imoc.IntegrationTest;
import sn.sonatel.dsi.ins.imoc.domain.EtatPaiement;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.PaymentStatus;
import sn.sonatel.dsi.ins.imoc.repository.EtatPaiementRepository;

/**
 * Integration tests for the {@link EtatPaiementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EtatPaiementResourceIT {

    private static final String DEFAULT_PAYMENT_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_NUMBER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_PAYMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PAYMENT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_AMOUNT = 0D;
    private static final Double UPDATED_AMOUNT = 1D;

    private static final String DEFAULT_ACT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ACT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_PAYMENT_PHONE = "798257954";
    private static final String UPDATED_PAYMENT_PHONE = "083592855";

    private static final PaymentStatus DEFAULT_STATUS = PaymentStatus.EN_ATTENTE;
    private static final PaymentStatus UPDATED_STATUS = PaymentStatus.EN_COURS;

    private static final LocalDate DEFAULT_PROCESSING_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PROCESSING_DATE = LocalDate.now(ZoneId.systemDefault());

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
    private EntityManager em;

    @Autowired
    private MockMvc restEtatPaiementMockMvc;

    private EtatPaiement etatPaiement;

    private EtatPaiement insertedEtatPaiement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EtatPaiement createEntity() {
        return new EtatPaiement()
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
            .paymentNumber(UPDATED_PAYMENT_NUMBER)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .amount(UPDATED_AMOUNT)
            .actCode(UPDATED_ACT_CODE)
            .paymentPhone(UPDATED_PAYMENT_PHONE)
            .status(UPDATED_STATUS)
            .processingDate(UPDATED_PROCESSING_DATE)
            .comments(UPDATED_COMMENTS);
    }

    @BeforeEach
    public void initTest() {
        etatPaiement = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEtatPaiement != null) {
            etatPaiementRepository.delete(insertedEtatPaiement);
            insertedEtatPaiement = null;
        }
    }

    @Test
    @Transactional
    void createEtatPaiement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EtatPaiement
        var returnedEtatPaiement = om.readValue(
            restEtatPaiementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etatPaiement)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EtatPaiement.class
        );

        // Validate the EtatPaiement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEtatPaiementUpdatableFieldsEquals(returnedEtatPaiement, getPersistedEtatPaiement(returnedEtatPaiement));

        insertedEtatPaiement = returnedEtatPaiement;
    }

    @Test
    @Transactional
    void createEtatPaiementWithExistingId() throws Exception {
        // Create the EtatPaiement with an existing ID
        etatPaiement.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEtatPaiementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etatPaiement)))
            .andExpect(status().isBadRequest());

        // Validate the EtatPaiement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPaymentDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etatPaiement.setPaymentDate(null);

        // Create the EtatPaiement, which fails.

        restEtatPaiementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etatPaiement)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etatPaiement.setAmount(null);

        // Create the EtatPaiement, which fails.

        restEtatPaiementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etatPaiement)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etatPaiement.setActCode(null);

        // Create the EtatPaiement, which fails.

        restEtatPaiementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etatPaiement)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPaymentPhoneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etatPaiement.setPaymentPhone(null);

        // Create the EtatPaiement, which fails.

        restEtatPaiementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etatPaiement)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etatPaiement.setStatus(null);

        // Create the EtatPaiement, which fails.

        restEtatPaiementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etatPaiement)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEtatPaiements() throws Exception {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.saveAndFlush(etatPaiement);

        // Get all the etatPaiementList
        restEtatPaiementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(etatPaiement.getId().intValue())))
            .andExpect(jsonPath("$.[*].paymentNumber").value(hasItem(DEFAULT_PAYMENT_NUMBER)))
            .andExpect(jsonPath("$.[*].paymentDate").value(hasItem(DEFAULT_PAYMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].actCode").value(hasItem(DEFAULT_ACT_CODE)))
            .andExpect(jsonPath("$.[*].paymentPhone").value(hasItem(DEFAULT_PAYMENT_PHONE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].processingDate").value(hasItem(DEFAULT_PROCESSING_DATE.toString())))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)));
    }

    @Test
    @Transactional
    void getEtatPaiement() throws Exception {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.saveAndFlush(etatPaiement);

        // Get the etatPaiement
        restEtatPaiementMockMvc
            .perform(get(ENTITY_API_URL_ID, etatPaiement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(etatPaiement.getId().intValue()))
            .andExpect(jsonPath("$.paymentNumber").value(DEFAULT_PAYMENT_NUMBER))
            .andExpect(jsonPath("$.paymentDate").value(DEFAULT_PAYMENT_DATE.toString()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.actCode").value(DEFAULT_ACT_CODE))
            .andExpect(jsonPath("$.paymentPhone").value(DEFAULT_PAYMENT_PHONE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.processingDate").value(DEFAULT_PROCESSING_DATE.toString()))
            .andExpect(jsonPath("$.comments").value(DEFAULT_COMMENTS));
    }

    @Test
    @Transactional
    void getNonExistingEtatPaiement() throws Exception {
        // Get the etatPaiement
        restEtatPaiementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEtatPaiement() throws Exception {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.saveAndFlush(etatPaiement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etatPaiement
        EtatPaiement updatedEtatPaiement = etatPaiementRepository.findById(etatPaiement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEtatPaiement are not directly saved in db
        em.detach(updatedEtatPaiement);
        updatedEtatPaiement
            .paymentNumber(UPDATED_PAYMENT_NUMBER)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .amount(UPDATED_AMOUNT)
            .actCode(UPDATED_ACT_CODE)
            .paymentPhone(UPDATED_PAYMENT_PHONE)
            .status(UPDATED_STATUS)
            .processingDate(UPDATED_PROCESSING_DATE)
            .comments(UPDATED_COMMENTS);

        restEtatPaiementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEtatPaiement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedEtatPaiement))
            )
            .andExpect(status().isOk());

        // Validate the EtatPaiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEtatPaiementToMatchAllProperties(updatedEtatPaiement);
    }

    @Test
    @Transactional
    void putNonExistingEtatPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etatPaiement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEtatPaiementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, etatPaiement.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(etatPaiement))
            )
            .andExpect(status().isBadRequest());

        // Validate the EtatPaiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEtatPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etatPaiement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtatPaiementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(etatPaiement))
            )
            .andExpect(status().isBadRequest());

        // Validate the EtatPaiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEtatPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etatPaiement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtatPaiementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etatPaiement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EtatPaiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEtatPaiementWithPatch() throws Exception {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.saveAndFlush(etatPaiement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etatPaiement using partial update
        EtatPaiement partialUpdatedEtatPaiement = new EtatPaiement();
        partialUpdatedEtatPaiement.setId(etatPaiement.getId());

        partialUpdatedEtatPaiement
            .paymentNumber(UPDATED_PAYMENT_NUMBER)
            .paymentDate(UPDATED_PAYMENT_DATE)
            .status(UPDATED_STATUS)
            .processingDate(UPDATED_PROCESSING_DATE)
            .comments(UPDATED_COMMENTS);

        restEtatPaiementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEtatPaiement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEtatPaiement))
            )
            .andExpect(status().isOk());

        // Validate the EtatPaiement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEtatPaiementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEtatPaiement, etatPaiement),
            getPersistedEtatPaiement(etatPaiement)
        );
    }

    @Test
    @Transactional
    void fullUpdateEtatPaiementWithPatch() throws Exception {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.saveAndFlush(etatPaiement);

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
            .status(UPDATED_STATUS)
            .processingDate(UPDATED_PROCESSING_DATE)
            .comments(UPDATED_COMMENTS);

        restEtatPaiementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEtatPaiement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEtatPaiement))
            )
            .andExpect(status().isOk());

        // Validate the EtatPaiement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEtatPaiementUpdatableFieldsEquals(partialUpdatedEtatPaiement, getPersistedEtatPaiement(partialUpdatedEtatPaiement));
    }

    @Test
    @Transactional
    void patchNonExistingEtatPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etatPaiement.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEtatPaiementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, etatPaiement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(etatPaiement))
            )
            .andExpect(status().isBadRequest());

        // Validate the EtatPaiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEtatPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etatPaiement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtatPaiementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(etatPaiement))
            )
            .andExpect(status().isBadRequest());

        // Validate the EtatPaiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEtatPaiement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etatPaiement.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtatPaiementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(etatPaiement)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EtatPaiement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEtatPaiement() throws Exception {
        // Initialize the database
        insertedEtatPaiement = etatPaiementRepository.saveAndFlush(etatPaiement);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the etatPaiement
        restEtatPaiementMockMvc
            .perform(delete(ENTITY_API_URL_ID, etatPaiement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return etatPaiementRepository.count();
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
        return etatPaiementRepository.findById(etatPaiement.getId()).orElseThrow();
    }

    protected void assertPersistedEtatPaiementToMatchAllProperties(EtatPaiement expectedEtatPaiement) {
        assertEtatPaiementAllPropertiesEquals(expectedEtatPaiement, getPersistedEtatPaiement(expectedEtatPaiement));
    }

    protected void assertPersistedEtatPaiementToMatchUpdatableProperties(EtatPaiement expectedEtatPaiement) {
        assertEtatPaiementAllUpdatablePropertiesEquals(expectedEtatPaiement, getPersistedEtatPaiement(expectedEtatPaiement));
    }
}
