package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.Validation;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ValidationStatus;
import sn.sonatel.dsi.ins.imoc.repository.ValidationRepository;

/**
 * Integration tests for the {@link ValidationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ValidationResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_VALIDATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_VALIDATION_DATE = LocalDate.now(ZoneId.systemDefault());

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
    private EntityManager em;

    @Autowired
    private MockMvc restValidationMockMvc;

    private Validation validation;

    private Validation insertedValidation;

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

    @BeforeEach
    public void initTest() {
        validation = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedValidation != null) {
            validationRepository.delete(insertedValidation);
            insertedValidation = null;
        }
    }

    @Test
    @Transactional
    void createValidation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Validation
        var returnedValidation = om.readValue(
            restValidationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(validation)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Validation.class
        );

        // Validate the Validation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertValidationUpdatableFieldsEquals(returnedValidation, getPersistedValidation(returnedValidation));

        insertedValidation = returnedValidation;
    }

    @Test
    @Transactional
    void createValidationWithExistingId() throws Exception {
        // Create the Validation with an existing ID
        validation.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restValidationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(validation)))
            .andExpect(status().isBadRequest());

        // Validate the Validation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        validation.setReference(null);

        // Create the Validation, which fails.

        restValidationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(validation)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValidationDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        validation.setValidationDate(null);

        // Create the Validation, which fails.

        restValidationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(validation)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        validation.setStatus(null);

        // Create the Validation, which fails.

        restValidationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(validation)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkValidatedByIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        validation.setValidatedBy(null);

        // Create the Validation, which fails.

        restValidationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(validation)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllValidations() throws Exception {
        // Initialize the database
        insertedValidation = validationRepository.saveAndFlush(validation);

        // Get all the validationList
        restValidationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(validation.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].validationDate").value(hasItem(DEFAULT_VALIDATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)))
            .andExpect(jsonPath("$.[*].validatedBy").value(hasItem(DEFAULT_VALIDATED_BY)));
    }

    @Test
    @Transactional
    void getValidation() throws Exception {
        // Initialize the database
        insertedValidation = validationRepository.saveAndFlush(validation);

        // Get the validation
        restValidationMockMvc
            .perform(get(ENTITY_API_URL_ID, validation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(validation.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.validationDate").value(DEFAULT_VALIDATION_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.comments").value(DEFAULT_COMMENTS))
            .andExpect(jsonPath("$.validatedBy").value(DEFAULT_VALIDATED_BY));
    }

    @Test
    @Transactional
    void getNonExistingValidation() throws Exception {
        // Get the validation
        restValidationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingValidation() throws Exception {
        // Initialize the database
        insertedValidation = validationRepository.saveAndFlush(validation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the validation
        Validation updatedValidation = validationRepository.findById(validation.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedValidation are not directly saved in db
        em.detach(updatedValidation);
        updatedValidation
            .reference(UPDATED_REFERENCE)
            .validationDate(UPDATED_VALIDATION_DATE)
            .status(UPDATED_STATUS)
            .comments(UPDATED_COMMENTS)
            .validatedBy(UPDATED_VALIDATED_BY);

        restValidationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedValidation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedValidation))
            )
            .andExpect(status().isOk());

        // Validate the Validation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedValidationToMatchAllProperties(updatedValidation);
    }

    @Test
    @Transactional
    void putNonExistingValidation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validation.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restValidationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, validation.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(validation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Validation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchValidation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validation.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restValidationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(validation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Validation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamValidation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validation.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restValidationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(validation)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Validation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateValidationWithPatch() throws Exception {
        // Initialize the database
        insertedValidation = validationRepository.saveAndFlush(validation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the validation using partial update
        Validation partialUpdatedValidation = new Validation();
        partialUpdatedValidation.setId(validation.getId());

        partialUpdatedValidation.reference(UPDATED_REFERENCE).validationDate(UPDATED_VALIDATION_DATE).comments(UPDATED_COMMENTS);

        restValidationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedValidation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedValidation))
            )
            .andExpect(status().isOk());

        // Validate the Validation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertValidationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedValidation, validation),
            getPersistedValidation(validation)
        );
    }

    @Test
    @Transactional
    void fullUpdateValidationWithPatch() throws Exception {
        // Initialize the database
        insertedValidation = validationRepository.saveAndFlush(validation);

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

        restValidationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedValidation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedValidation))
            )
            .andExpect(status().isOk());

        // Validate the Validation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertValidationUpdatableFieldsEquals(partialUpdatedValidation, getPersistedValidation(partialUpdatedValidation));
    }

    @Test
    @Transactional
    void patchNonExistingValidation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validation.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restValidationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, validation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(validation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Validation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchValidation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validation.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restValidationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(validation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Validation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamValidation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validation.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restValidationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(validation)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Validation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteValidation() throws Exception {
        // Initialize the database
        insertedValidation = validationRepository.saveAndFlush(validation);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the validation
        restValidationMockMvc
            .perform(delete(ENTITY_API_URL_ID, validation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return validationRepository.count();
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
        return validationRepository.findById(validation.getId()).orElseThrow();
    }

    protected void assertPersistedValidationToMatchAllProperties(Validation expectedValidation) {
        assertValidationAllPropertiesEquals(expectedValidation, getPersistedValidation(expectedValidation));
    }

    protected void assertPersistedValidationToMatchUpdatableProperties(Validation expectedValidation) {
        assertValidationAllUpdatablePropertiesEquals(expectedValidation, getPersistedValidation(expectedValidation));
    }
}
