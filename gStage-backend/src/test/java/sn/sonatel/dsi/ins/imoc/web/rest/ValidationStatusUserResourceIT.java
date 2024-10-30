package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationStatusUserAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatusUser;
import sn.sonatel.dsi.ins.imoc.repository.ValidationStatusUserRepository;

/**
 * Integration tests for the {@link ValidationStatusUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ValidationStatusUserResourceIT {

    private static final Instant DEFAULT_CREATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_EXPIRE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ACTIVATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTIVATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/validation-status-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ValidationStatusUserRepository validationStatusUserRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restValidationStatusUserMockMvc;

    private ValidationStatusUser validationStatusUser;

    private ValidationStatusUser insertedValidationStatusUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ValidationStatusUser createEntity() {
        return new ValidationStatusUser()
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
    public static ValidationStatusUser createUpdatedEntity() {
        return new ValidationStatusUser()
            .creation(UPDATED_CREATION)
            .expire(UPDATED_EXPIRE)
            .activation(UPDATED_ACTIVATION)
            .code(UPDATED_CODE);
    }

    @BeforeEach
    public void initTest() {
        validationStatusUser = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedValidationStatusUser != null) {
            validationStatusUserRepository.delete(insertedValidationStatusUser);
            insertedValidationStatusUser = null;
        }
    }

    @Test
    @Transactional
    void createValidationStatusUser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ValidationStatusUser
        var returnedValidationStatusUser = om.readValue(
            restValidationStatusUserMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(validationStatusUser)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ValidationStatusUser.class
        );

        // Validate the ValidationStatusUser in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertValidationStatusUserUpdatableFieldsEquals(
            returnedValidationStatusUser,
            getPersistedValidationStatusUser(returnedValidationStatusUser)
        );

        insertedValidationStatusUser = returnedValidationStatusUser;
    }

    @Test
    @Transactional
    void createValidationStatusUserWithExistingId() throws Exception {
        // Create the ValidationStatusUser with an existing ID
        validationStatusUser.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restValidationStatusUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(validationStatusUser)))
            .andExpect(status().isBadRequest());

        // Validate the ValidationStatusUser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllValidationStatusUsers() throws Exception {
        // Initialize the database
        insertedValidationStatusUser = validationStatusUserRepository.saveAndFlush(validationStatusUser);

        // Get all the validationStatusUserList
        restValidationStatusUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(validationStatusUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].creation").value(hasItem(DEFAULT_CREATION.toString())))
            .andExpect(jsonPath("$.[*].expire").value(hasItem(DEFAULT_EXPIRE.toString())))
            .andExpect(jsonPath("$.[*].activation").value(hasItem(DEFAULT_ACTIVATION.toString())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)));
    }

    @Test
    @Transactional
    void getValidationStatusUser() throws Exception {
        // Initialize the database
        insertedValidationStatusUser = validationStatusUserRepository.saveAndFlush(validationStatusUser);

        // Get the validationStatusUser
        restValidationStatusUserMockMvc
            .perform(get(ENTITY_API_URL_ID, validationStatusUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(validationStatusUser.getId().intValue()))
            .andExpect(jsonPath("$.creation").value(DEFAULT_CREATION.toString()))
            .andExpect(jsonPath("$.expire").value(DEFAULT_EXPIRE.toString()))
            .andExpect(jsonPath("$.activation").value(DEFAULT_ACTIVATION.toString()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE));
    }

    @Test
    @Transactional
    void getNonExistingValidationStatusUser() throws Exception {
        // Get the validationStatusUser
        restValidationStatusUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingValidationStatusUser() throws Exception {
        // Initialize the database
        insertedValidationStatusUser = validationStatusUserRepository.saveAndFlush(validationStatusUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the validationStatusUser
        ValidationStatusUser updatedValidationStatusUser = validationStatusUserRepository
            .findById(validationStatusUser.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedValidationStatusUser are not directly saved in db
        em.detach(updatedValidationStatusUser);
        updatedValidationStatusUser.creation(UPDATED_CREATION).expire(UPDATED_EXPIRE).activation(UPDATED_ACTIVATION).code(UPDATED_CODE);

        restValidationStatusUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedValidationStatusUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedValidationStatusUser))
            )
            .andExpect(status().isOk());

        // Validate the ValidationStatusUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedValidationStatusUserToMatchAllProperties(updatedValidationStatusUser);
    }

    @Test
    @Transactional
    void putNonExistingValidationStatusUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validationStatusUser.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restValidationStatusUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, validationStatusUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(validationStatusUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the ValidationStatusUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchValidationStatusUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validationStatusUser.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restValidationStatusUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(validationStatusUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the ValidationStatusUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamValidationStatusUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validationStatusUser.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restValidationStatusUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(validationStatusUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ValidationStatusUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateValidationStatusUserWithPatch() throws Exception {
        // Initialize the database
        insertedValidationStatusUser = validationStatusUserRepository.saveAndFlush(validationStatusUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the validationStatusUser using partial update
        ValidationStatusUser partialUpdatedValidationStatusUser = new ValidationStatusUser();
        partialUpdatedValidationStatusUser.setId(validationStatusUser.getId());

        restValidationStatusUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedValidationStatusUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedValidationStatusUser))
            )
            .andExpect(status().isOk());

        // Validate the ValidationStatusUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertValidationStatusUserUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedValidationStatusUser, validationStatusUser),
            getPersistedValidationStatusUser(validationStatusUser)
        );
    }

    @Test
    @Transactional
    void fullUpdateValidationStatusUserWithPatch() throws Exception {
        // Initialize the database
        insertedValidationStatusUser = validationStatusUserRepository.saveAndFlush(validationStatusUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the validationStatusUser using partial update
        ValidationStatusUser partialUpdatedValidationStatusUser = new ValidationStatusUser();
        partialUpdatedValidationStatusUser.setId(validationStatusUser.getId());

        partialUpdatedValidationStatusUser
            .creation(UPDATED_CREATION)
            .expire(UPDATED_EXPIRE)
            .activation(UPDATED_ACTIVATION)
            .code(UPDATED_CODE);

        restValidationStatusUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedValidationStatusUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedValidationStatusUser))
            )
            .andExpect(status().isOk());

        // Validate the ValidationStatusUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertValidationStatusUserUpdatableFieldsEquals(
            partialUpdatedValidationStatusUser,
            getPersistedValidationStatusUser(partialUpdatedValidationStatusUser)
        );
    }

    @Test
    @Transactional
    void patchNonExistingValidationStatusUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validationStatusUser.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restValidationStatusUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, validationStatusUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(validationStatusUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the ValidationStatusUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchValidationStatusUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validationStatusUser.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restValidationStatusUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(validationStatusUser))
            )
            .andExpect(status().isBadRequest());

        // Validate the ValidationStatusUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamValidationStatusUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        validationStatusUser.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restValidationStatusUserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(validationStatusUser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ValidationStatusUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteValidationStatusUser() throws Exception {
        // Initialize the database
        insertedValidationStatusUser = validationStatusUserRepository.saveAndFlush(validationStatusUser);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the validationStatusUser
        restValidationStatusUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, validationStatusUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return validationStatusUserRepository.count();
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

    protected ValidationStatusUser getPersistedValidationStatusUser(ValidationStatusUser validationStatusUser) {
        return validationStatusUserRepository.findById(validationStatusUser.getId()).orElseThrow();
    }

    protected void assertPersistedValidationStatusUserToMatchAllProperties(ValidationStatusUser expectedValidationStatusUser) {
        assertValidationStatusUserAllPropertiesEquals(
            expectedValidationStatusUser,
            getPersistedValidationStatusUser(expectedValidationStatusUser)
        );
    }

    protected void assertPersistedValidationStatusUserToMatchUpdatableProperties(ValidationStatusUser expectedValidationStatusUser) {
        assertValidationStatusUserAllUpdatablePropertiesEquals(
            expectedValidationStatusUser,
            getPersistedValidationStatusUser(expectedValidationStatusUser)
        );
    }
}
