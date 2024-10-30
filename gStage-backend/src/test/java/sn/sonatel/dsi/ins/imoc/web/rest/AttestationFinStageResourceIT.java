package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.sonatel.dsi.ins.imoc.domain.AttestationFinStageAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage;
import sn.sonatel.dsi.ins.imoc.repository.AttestationFinStageRepository;

/**
 * Integration tests for the {@link AttestationFinStageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AttestationFinStageResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_ISSUE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ISSUE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_SIGNATURE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SIGNATURE_DATE = LocalDate.now(ZoneId.systemDefault());

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
    private EntityManager em;

    @Autowired
    private MockMvc restAttestationFinStageMockMvc;

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

    @BeforeEach
    public void initTest() {
        attestationFinStage = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAttestationFinStage != null) {
            attestationFinStageRepository.delete(insertedAttestationFinStage);
            insertedAttestationFinStage = null;
        }
    }

    @Test
    @Transactional
    void createAttestationFinStage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AttestationFinStage
        var returnedAttestationFinStage = om.readValue(
            restAttestationFinStageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attestationFinStage)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AttestationFinStage.class
        );

        // Validate the AttestationFinStage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAttestationFinStageUpdatableFieldsEquals(
            returnedAttestationFinStage,
            getPersistedAttestationFinStage(returnedAttestationFinStage)
        );

        insertedAttestationFinStage = returnedAttestationFinStage;
    }

    @Test
    @Transactional
    void createAttestationFinStageWithExistingId() throws Exception {
        // Create the AttestationFinStage with an existing ID
        attestationFinStage.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAttestationFinStageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attestationFinStage)))
            .andExpect(status().isBadRequest());

        // Validate the AttestationFinStage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        attestationFinStage.setReference(null);

        // Create the AttestationFinStage, which fails.

        restAttestationFinStageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attestationFinStage)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIssueDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        attestationFinStage.setIssueDate(null);

        // Create the AttestationFinStage, which fails.

        restAttestationFinStageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attestationFinStage)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAttestationFinStages() throws Exception {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.saveAndFlush(attestationFinStage);

        // Get all the attestationFinStageList
        restAttestationFinStageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attestationFinStage.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].issueDate").value(hasItem(DEFAULT_ISSUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].signatureDate").value(hasItem(DEFAULT_SIGNATURE_DATE.toString())))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)));
    }

    @Test
    @Transactional
    void getAttestationFinStage() throws Exception {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.saveAndFlush(attestationFinStage);

        // Get the attestationFinStage
        restAttestationFinStageMockMvc
            .perform(get(ENTITY_API_URL_ID, attestationFinStage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(attestationFinStage.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.issueDate").value(DEFAULT_ISSUE_DATE.toString()))
            .andExpect(jsonPath("$.signatureDate").value(DEFAULT_SIGNATURE_DATE.toString()))
            .andExpect(jsonPath("$.comments").value(DEFAULT_COMMENTS));
    }

    @Test
    @Transactional
    void getNonExistingAttestationFinStage() throws Exception {
        // Get the attestationFinStage
        restAttestationFinStageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAttestationFinStage() throws Exception {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.saveAndFlush(attestationFinStage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attestationFinStage
        AttestationFinStage updatedAttestationFinStage = attestationFinStageRepository.findById(attestationFinStage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAttestationFinStage are not directly saved in db
        em.detach(updatedAttestationFinStage);
        updatedAttestationFinStage
            .reference(UPDATED_REFERENCE)
            .issueDate(UPDATED_ISSUE_DATE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .comments(UPDATED_COMMENTS);

        restAttestationFinStageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAttestationFinStage.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAttestationFinStage))
            )
            .andExpect(status().isOk());

        // Validate the AttestationFinStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAttestationFinStageToMatchAllProperties(updatedAttestationFinStage);
    }

    @Test
    @Transactional
    void putNonExistingAttestationFinStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationFinStage.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttestationFinStageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, attestationFinStage.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(attestationFinStage))
            )
            .andExpect(status().isBadRequest());

        // Validate the AttestationFinStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAttestationFinStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationFinStage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttestationFinStageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(attestationFinStage))
            )
            .andExpect(status().isBadRequest());

        // Validate the AttestationFinStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAttestationFinStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationFinStage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttestationFinStageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attestationFinStage)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AttestationFinStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAttestationFinStageWithPatch() throws Exception {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.saveAndFlush(attestationFinStage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attestationFinStage using partial update
        AttestationFinStage partialUpdatedAttestationFinStage = new AttestationFinStage();
        partialUpdatedAttestationFinStage.setId(attestationFinStage.getId());

        partialUpdatedAttestationFinStage.issueDate(UPDATED_ISSUE_DATE).signatureDate(UPDATED_SIGNATURE_DATE).comments(UPDATED_COMMENTS);

        restAttestationFinStageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAttestationFinStage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAttestationFinStage))
            )
            .andExpect(status().isOk());

        // Validate the AttestationFinStage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAttestationFinStageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAttestationFinStage, attestationFinStage),
            getPersistedAttestationFinStage(attestationFinStage)
        );
    }

    @Test
    @Transactional
    void fullUpdateAttestationFinStageWithPatch() throws Exception {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.saveAndFlush(attestationFinStage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attestationFinStage using partial update
        AttestationFinStage partialUpdatedAttestationFinStage = new AttestationFinStage();
        partialUpdatedAttestationFinStage.setId(attestationFinStage.getId());

        partialUpdatedAttestationFinStage
            .reference(UPDATED_REFERENCE)
            .issueDate(UPDATED_ISSUE_DATE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .comments(UPDATED_COMMENTS);

        restAttestationFinStageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAttestationFinStage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAttestationFinStage))
            )
            .andExpect(status().isOk());

        // Validate the AttestationFinStage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAttestationFinStageUpdatableFieldsEquals(
            partialUpdatedAttestationFinStage,
            getPersistedAttestationFinStage(partialUpdatedAttestationFinStage)
        );
    }

    @Test
    @Transactional
    void patchNonExistingAttestationFinStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationFinStage.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttestationFinStageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, attestationFinStage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(attestationFinStage))
            )
            .andExpect(status().isBadRequest());

        // Validate the AttestationFinStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAttestationFinStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationFinStage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttestationFinStageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(attestationFinStage))
            )
            .andExpect(status().isBadRequest());

        // Validate the AttestationFinStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAttestationFinStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationFinStage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttestationFinStageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(attestationFinStage)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AttestationFinStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAttestationFinStage() throws Exception {
        // Initialize the database
        insertedAttestationFinStage = attestationFinStageRepository.saveAndFlush(attestationFinStage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the attestationFinStage
        restAttestationFinStageMockMvc
            .perform(delete(ENTITY_API_URL_ID, attestationFinStage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return attestationFinStageRepository.count();
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
        return attestationFinStageRepository.findById(attestationFinStage.getId()).orElseThrow();
    }

    protected void assertPersistedAttestationFinStageToMatchAllProperties(AttestationFinStage expectedAttestationFinStage) {
        assertAttestationFinStageAllPropertiesEquals(
            expectedAttestationFinStage,
            getPersistedAttestationFinStage(expectedAttestationFinStage)
        );
    }

    protected void assertPersistedAttestationFinStageToMatchUpdatableProperties(AttestationFinStage expectedAttestationFinStage) {
        assertAttestationFinStageAllUpdatablePropertiesEquals(
            expectedAttestationFinStage,
            getPersistedAttestationFinStage(expectedAttestationFinStage)
        );
    }
}
