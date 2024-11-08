package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidatAsserts.*;
import static sn.sonatel.dsi.ins.imoc.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidat;
import sn.sonatel.dsi.ins.imoc.repository.ValidationStatuscandidatRepository;

/**
 * Integration tests for the {@link ValidationStatuscandidatResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ValidationStatuscandidatResourceIT {

    private static final Instant DEFAULT_CREATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EXPIRE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ACTIVATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTIVATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/validation-statuscandidats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ValidationStatuscandidatRepository validationStatuscandidatRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restValidationStatuscandidatMockMvc;

    private ValidationStatuscandidat validationStatuscandidat;

    private ValidationStatuscandidat insertedValidationStatuscandidat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ValidationStatuscandidat createEntity() {
        return new ValidationStatuscandidat()
            .creation(DEFAULT_CREATION)
            .expire(DEFAULT_EXPIRE)
            .activation(DEFAULT_ACTIVATION)
            .code(DEFAULT_CODE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ValidationStatuscandidat createUpdatedEntity() {
        return new ValidationStatuscandidat()
            .creation(UPDATED_CREATION)
            .expire(UPDATED_EXPIRE)
            .activation(UPDATED_ACTIVATION)
            .code(UPDATED_CODE);
    }

    @BeforeEach
    public void initTest() {
        validationStatuscandidat = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedValidationStatuscandidat != null) {
            validationStatuscandidatRepository.delete(insertedValidationStatuscandidat);
            insertedValidationStatuscandidat = null;
        }
    }

    @Test
    @Transactional
    void createValidationStatuscandidat() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ValidationStatuscandidat
        var returnedValidationStatuscandidat = om.readValue(
            restValidationStatuscandidatMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(validationStatuscandidat))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ValidationStatuscandidat.class
        );

        // Validate the ValidationStatuscandidat in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertValidationStatuscandidatUpdatableFieldsEquals(
            returnedValidationStatuscandidat,
            getPersistedValidationStatuscandidat(returnedValidationStatuscandidat)
        );

        insertedValidationStatuscandidat = returnedValidationStatuscandidat;
    }

    @Test
    @Transactional
    void createValidationStatuscandidatWithExistingId() throws Exception {
        // Create the ValidationStatuscandidat with an existing ID
        validationStatuscandidat.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restValidationStatuscandidatMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(validationStatuscandidat)))
            .andExpect(status().isBadRequest());

        // Validate the ValidationStatuscandidat in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllValidationStatuscandidats() throws Exception {
        // Initialize the database
        insertedValidationStatuscandidat = validationStatuscandidatRepository.saveAndFlush(validationStatuscandidat);

        // Get all the validationStatuscandidatList
        restValidationStatuscandidatMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(validationStatuscandidat.getId().intValue())))
            .andExpect(jsonPath("$.[*].creation").value(hasItem(DEFAULT_CREATION.toString())))
            .andExpect(jsonPath("$.[*].expire").value(hasItem(DEFAULT_EXPIRE.toString())))
            .andExpect(jsonPath("$.[*].activation").value(hasItem(DEFAULT_ACTIVATION.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)));
    }

    @Test
    @Transactional
    void getValidationStatuscandidat() throws Exception {
        // Initialize the database
        insertedValidationStatuscandidat = validationStatuscandidatRepository.saveAndFlush(validationStatuscandidat);

        // Get the validationStatuscandidat
        restValidationStatuscandidatMockMvc
            .perform(get(ENTITY_API_URL_ID, validationStatuscandidat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(validationStatuscandidat.getId().intValue()))
            .andExpect(jsonPath("$.creation").value(DEFAULT_CREATION.toString()))
            .andExpect(jsonPath("$.expire").value(DEFAULT_EXPIRE.toString()))
            .andExpect(jsonPath("$.activation").value(DEFAULT_ACTIVATION.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE));
    }

    @Test
    @Transactional
    void getNonExistingValidationStatuscandidat() throws Exception {
        // Get the validationStatuscandidat
        restValidationStatuscandidatMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingValidationStatuscandidat() throws Exception {
        // Initialize the database
        insertedValidationStatuscandidat = validationStatuscandidatRepository.saveAndFlush(validationStatuscandidat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the validationStatuscandidat
        ValidationStatuscandidat updatedValidationStatuscandidat = validationStatuscandidatRepository
            .findById(validationStatuscandidat.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedValidationStatuscandidat are not directly saved in db
        em.detach(updatedValidationStatuscandidat);
        updatedValidationStatuscandidat.creation(UPDATED_CREATION).expire(UPDATED_EXPIRE).activation(UPDATED_ACTIVATION).code(UPDATED_CODE);

        restValidationStatuscandidatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedValidationStatuscandidat.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedValidationStatuscandidat))
            )
            .andExpect(status().isOk());

        // Validate the ValidationStatuscandidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedValidationStatuscandidatToMatchAllProperties(updatedValidationStatuscandidat);
    }

    @Test
    @Transactional
    void putNonExistingValidationStatuscandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validationStatuscandidat.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restValidationStatuscandidatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, validationStatuscandidat.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(validationStatuscandidat))
            )
            .andExpect(status().isBadRequest());

        // Validate the ValidationStatuscandidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchValidationStatuscandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validationStatuscandidat.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restValidationStatuscandidatMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(validationStatuscandidat))
            )
            .andExpect(status().isBadRequest());

        // Validate the ValidationStatuscandidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamValidationStatuscandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validationStatuscandidat.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restValidationStatuscandidatMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(validationStatuscandidat)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ValidationStatuscandidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateValidationStatuscandidatWithPatch() throws Exception {
        // Initialize the database
        insertedValidationStatuscandidat = validationStatuscandidatRepository.saveAndFlush(validationStatuscandidat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the validationStatuscandidat using partial update
        ValidationStatuscandidat partialUpdatedValidationStatuscandidat = new ValidationStatuscandidat();
        partialUpdatedValidationStatuscandidat.setId(validationStatuscandidat.getId());

        partialUpdatedValidationStatuscandidat
            .creation(UPDATED_CREATION)
            .expire(UPDATED_EXPIRE)
            .activation(UPDATED_ACTIVATION)
            .code(UPDATED_CODE);

        restValidationStatuscandidatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedValidationStatuscandidat.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedValidationStatuscandidat))
            )
            .andExpect(status().isOk());

        // Validate the ValidationStatuscandidat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertValidationStatuscandidatUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedValidationStatuscandidat, validationStatuscandidat),
            getPersistedValidationStatuscandidat(validationStatuscandidat)
        );
    }

    @Test
    @Transactional
    void fullUpdateValidationStatuscandidatWithPatch() throws Exception {
        // Initialize the database
        insertedValidationStatuscandidat = validationStatuscandidatRepository.saveAndFlush(validationStatuscandidat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the validationStatuscandidat using partial update
        ValidationStatuscandidat partialUpdatedValidationStatuscandidat = new ValidationStatuscandidat();
        partialUpdatedValidationStatuscandidat.setId(validationStatuscandidat.getId());

        partialUpdatedValidationStatuscandidat
            .creation(UPDATED_CREATION)
            .expire(UPDATED_EXPIRE)
            .activation(UPDATED_ACTIVATION)
            .code(UPDATED_CODE);

        restValidationStatuscandidatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedValidationStatuscandidat.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedValidationStatuscandidat))
            )
            .andExpect(status().isOk());

        // Validate the ValidationStatuscandidat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertValidationStatuscandidatUpdatableFieldsEquals(
            partialUpdatedValidationStatuscandidat,
            getPersistedValidationStatuscandidat(partialUpdatedValidationStatuscandidat)
        );
    }

    @Test
    @Transactional
    void patchNonExistingValidationStatuscandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validationStatuscandidat.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restValidationStatuscandidatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, validationStatuscandidat.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(validationStatuscandidat))
            )
            .andExpect(status().isBadRequest());

        // Validate the ValidationStatuscandidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchValidationStatuscandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validationStatuscandidat.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restValidationStatuscandidatMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(validationStatuscandidat))
            )
            .andExpect(status().isBadRequest());

        // Validate the ValidationStatuscandidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamValidationStatuscandidat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validationStatuscandidat.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restValidationStatuscandidatMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(validationStatuscandidat))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ValidationStatuscandidat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteValidationStatuscandidat() throws Exception {
        // Initialize the database
        insertedValidationStatuscandidat = validationStatuscandidatRepository.saveAndFlush(validationStatuscandidat);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the validationStatuscandidat
        restValidationStatuscandidatMockMvc
            .perform(delete(ENTITY_API_URL_ID, validationStatuscandidat.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return validationStatuscandidatRepository.count();
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

    protected ValidationStatuscandidat getPersistedValidationStatuscandidat(ValidationStatuscandidat validationStatuscandidat) {
        return validationStatuscandidatRepository.findById(validationStatuscandidat.getId()).orElseThrow();
    }

    protected void assertPersistedValidationStatuscandidatToMatchAllProperties(ValidationStatuscandidat expectedValidationStatuscandidat) {
        assertValidationStatuscandidatAllPropertiesEquals(
            expectedValidationStatuscandidat,
            getPersistedValidationStatuscandidat(expectedValidationStatuscandidat)
        );
    }

    protected void assertPersistedValidationStatuscandidatToMatchUpdatableProperties(
        ValidationStatuscandidat expectedValidationStatuscandidat
    ) {
        assertValidationStatuscandidatAllUpdatablePropertiesEquals(
            expectedValidationStatuscandidat,
            getPersistedValidationStatuscandidat(expectedValidationStatuscandidat)
        );
    }
}
