package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaireAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaire;
import sn.sonatel.dsi.ins.imoc.repository.RestaurationStagiaireRepository;

/**
 * Integration tests for the {@link RestaurationStagiaireResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RestaurationStagiaireResourceIT {

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

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
    private EntityManager em;

    @Autowired
    private MockMvc restRestaurationStagiaireMockMvc;

    private RestaurationStagiaire restaurationStagiaire;

    private RestaurationStagiaire insertedRestaurationStagiaire;

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

    @BeforeEach
    public void initTest() {
        restaurationStagiaire = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedRestaurationStagiaire != null) {
            restaurationStagiaireRepository.delete(insertedRestaurationStagiaire);
            insertedRestaurationStagiaire = null;
        }
    }

    @Test
    @Transactional
    void createRestaurationStagiaire() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RestaurationStagiaire
        var returnedRestaurationStagiaire = om.readValue(
            restRestaurationStagiaireMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurationStagiaire)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RestaurationStagiaire.class
        );

        // Validate the RestaurationStagiaire in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRestaurationStagiaireUpdatableFieldsEquals(
            returnedRestaurationStagiaire,
            getPersistedRestaurationStagiaire(returnedRestaurationStagiaire)
        );

        insertedRestaurationStagiaire = returnedRestaurationStagiaire;
    }

    @Test
    @Transactional
    void createRestaurationStagiaireWithExistingId() throws Exception {
        // Create the RestaurationStagiaire with an existing ID
        restaurationStagiaire.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRestaurationStagiaireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurationStagiaire)))
            .andExpect(status().isBadRequest());

        // Validate the RestaurationStagiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurationStagiaire.setStartDate(null);

        // Create the RestaurationStagiaire, which fails.

        restRestaurationStagiaireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurationStagiaire)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurationStagiaire.setEndDate(null);

        // Create the RestaurationStagiaire, which fails.

        restRestaurationStagiaireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurationStagiaire)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        restaurationStagiaire.setStatus(null);

        // Create the RestaurationStagiaire, which fails.

        restRestaurationStagiaireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurationStagiaire)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRestaurationStagiaires() throws Exception {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.saveAndFlush(restaurationStagiaire);

        // Get all the restaurationStagiaireList
        restRestaurationStagiaireMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(restaurationStagiaire.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].cardNumber").value(hasItem(DEFAULT_CARD_NUMBER)));
    }

    @Test
    @Transactional
    void getRestaurationStagiaire() throws Exception {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.saveAndFlush(restaurationStagiaire);

        // Get the restaurationStagiaire
        restRestaurationStagiaireMockMvc
            .perform(get(ENTITY_API_URL_ID, restaurationStagiaire.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(restaurationStagiaire.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.booleanValue()))
            .andExpect(jsonPath("$.cardNumber").value(DEFAULT_CARD_NUMBER));
    }

    @Test
    @Transactional
    void getNonExistingRestaurationStagiaire() throws Exception {
        // Get the restaurationStagiaire
        restRestaurationStagiaireMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRestaurationStagiaire() throws Exception {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.saveAndFlush(restaurationStagiaire);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the restaurationStagiaire
        RestaurationStagiaire updatedRestaurationStagiaire = restaurationStagiaireRepository
            .findById(restaurationStagiaire.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedRestaurationStagiaire are not directly saved in db
        em.detach(updatedRestaurationStagiaire);
        updatedRestaurationStagiaire
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .cardNumber(UPDATED_CARD_NUMBER);

        restRestaurationStagiaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRestaurationStagiaire.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedRestaurationStagiaire))
            )
            .andExpect(status().isOk());

        // Validate the RestaurationStagiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRestaurationStagiaireToMatchAllProperties(updatedRestaurationStagiaire);
    }

    @Test
    @Transactional
    void putNonExistingRestaurationStagiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurationStagiaire.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRestaurationStagiaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, restaurationStagiaire.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(restaurationStagiaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the RestaurationStagiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRestaurationStagiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurationStagiaire.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurationStagiaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(restaurationStagiaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the RestaurationStagiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRestaurationStagiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurationStagiaire.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurationStagiaireMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(restaurationStagiaire)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RestaurationStagiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRestaurationStagiaireWithPatch() throws Exception {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.saveAndFlush(restaurationStagiaire);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the restaurationStagiaire using partial update
        RestaurationStagiaire partialUpdatedRestaurationStagiaire = new RestaurationStagiaire();
        partialUpdatedRestaurationStagiaire.setId(restaurationStagiaire.getId());

        partialUpdatedRestaurationStagiaire.endDate(UPDATED_END_DATE);

        restRestaurationStagiaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRestaurationStagiaire.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRestaurationStagiaire))
            )
            .andExpect(status().isOk());

        // Validate the RestaurationStagiaire in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRestaurationStagiaireUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRestaurationStagiaire, restaurationStagiaire),
            getPersistedRestaurationStagiaire(restaurationStagiaire)
        );
    }

    @Test
    @Transactional
    void fullUpdateRestaurationStagiaireWithPatch() throws Exception {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.saveAndFlush(restaurationStagiaire);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the restaurationStagiaire using partial update
        RestaurationStagiaire partialUpdatedRestaurationStagiaire = new RestaurationStagiaire();
        partialUpdatedRestaurationStagiaire.setId(restaurationStagiaire.getId());

        partialUpdatedRestaurationStagiaire
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .status(UPDATED_STATUS)
            .cardNumber(UPDATED_CARD_NUMBER);

        restRestaurationStagiaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRestaurationStagiaire.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRestaurationStagiaire))
            )
            .andExpect(status().isOk());

        // Validate the RestaurationStagiaire in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRestaurationStagiaireUpdatableFieldsEquals(
            partialUpdatedRestaurationStagiaire,
            getPersistedRestaurationStagiaire(partialUpdatedRestaurationStagiaire)
        );
    }

    @Test
    @Transactional
    void patchNonExistingRestaurationStagiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurationStagiaire.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRestaurationStagiaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, restaurationStagiaire.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(restaurationStagiaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the RestaurationStagiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRestaurationStagiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurationStagiaire.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurationStagiaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(restaurationStagiaire))
            )
            .andExpect(status().isBadRequest());

        // Validate the RestaurationStagiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRestaurationStagiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        restaurationStagiaire.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRestaurationStagiaireMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(restaurationStagiaire)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RestaurationStagiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRestaurationStagiaire() throws Exception {
        // Initialize the database
        insertedRestaurationStagiaire = restaurationStagiaireRepository.saveAndFlush(restaurationStagiaire);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the restaurationStagiaire
        restRestaurationStagiaireMockMvc
            .perform(delete(ENTITY_API_URL_ID, restaurationStagiaire.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return restaurationStagiaireRepository.count();
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
        return restaurationStagiaireRepository.findById(restaurationStagiaire.getId()).orElseThrow();
    }

    protected void assertPersistedRestaurationStagiaireToMatchAllProperties(RestaurationStagiaire expectedRestaurationStagiaire) {
        assertRestaurationStagiaireAllPropertiesEquals(
            expectedRestaurationStagiaire,
            getPersistedRestaurationStagiaire(expectedRestaurationStagiaire)
        );
    }

    protected void assertPersistedRestaurationStagiaireToMatchUpdatableProperties(RestaurationStagiaire expectedRestaurationStagiaire) {
        assertRestaurationStagiaireAllUpdatablePropertiesEquals(
            expectedRestaurationStagiaire,
            getPersistedRestaurationStagiaire(expectedRestaurationStagiaire)
        );
    }
}
