package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.sonatel.dsi.ins.imoc.domain.AttestationPresenceAsserts.*;
import static sn.sonatel.dsi.ins.imoc.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
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
import sn.sonatel.dsi.ins.imoc.domain.AttestationPresence;
import sn.sonatel.dsi.ins.imoc.repository.AttestationPresenceRepository;

/**
 * Integration tests for the {@link AttestationPresenceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AttestationPresenceResourceIT {

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_SIGNATURE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SIGNATURE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final byte[] DEFAULT_DOCS = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_DOCS = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_DOCS_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_DOCS_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/attestation-presences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AttestationPresenceRepository attestationPresenceRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAttestationPresenceMockMvc;

    private AttestationPresence attestationPresence;

    private AttestationPresence insertedAttestationPresence;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AttestationPresence createEntity() {
        return new AttestationPresence()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .signatureDate(DEFAULT_SIGNATURE_DATE)
            .status(DEFAULT_STATUS)
            .comments(DEFAULT_COMMENTS)
            .docs(DEFAULT_DOCS)
            .docsContentType(DEFAULT_DOCS_CONTENT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AttestationPresence createUpdatedEntity() {
        return new AttestationPresence()
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .status(UPDATED_STATUS)
            .comments(UPDATED_COMMENTS)
            .docs(UPDATED_DOCS)
            .docsContentType(UPDATED_DOCS_CONTENT_TYPE);
    }

    @BeforeEach
    public void initTest() {
        attestationPresence = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAttestationPresence != null) {
            attestationPresenceRepository.delete(insertedAttestationPresence);
            insertedAttestationPresence = null;
        }
    }

    @Test
    @Transactional
    void createAttestationPresence() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AttestationPresence
        var returnedAttestationPresence = om.readValue(
            restAttestationPresenceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attestationPresence)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AttestationPresence.class
        );

        // Validate the AttestationPresence in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertAttestationPresenceUpdatableFieldsEquals(
            returnedAttestationPresence,
            getPersistedAttestationPresence(returnedAttestationPresence)
        );

        insertedAttestationPresence = returnedAttestationPresence;
    }

    @Test
    @Transactional
    void createAttestationPresenceWithExistingId() throws Exception {
        // Create the AttestationPresence with an existing ID
        attestationPresence.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAttestationPresenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attestationPresence)))
            .andExpect(status().isBadRequest());

        // Validate the AttestationPresence in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        attestationPresence.setStartDate(null);

        // Create the AttestationPresence, which fails.

        restAttestationPresenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attestationPresence)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        attestationPresence.setEndDate(null);

        // Create the AttestationPresence, which fails.

        restAttestationPresenceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attestationPresence)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAttestationPresences() throws Exception {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.saveAndFlush(attestationPresence);

        // Get all the attestationPresenceList
        restAttestationPresenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(attestationPresence.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].signatureDate").value(hasItem(DEFAULT_SIGNATURE_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.booleanValue())))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)))
            .andExpect(jsonPath("$.[*].docsContentType").value(hasItem(DEFAULT_DOCS_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].docs").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_DOCS))));
    }

    @Test
    @Transactional
    void getAttestationPresence() throws Exception {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.saveAndFlush(attestationPresence);

        // Get the attestationPresence
        restAttestationPresenceMockMvc
            .perform(get(ENTITY_API_URL_ID, attestationPresence.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(attestationPresence.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.signatureDate").value(DEFAULT_SIGNATURE_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.booleanValue()))
            .andExpect(jsonPath("$.comments").value(DEFAULT_COMMENTS))
            .andExpect(jsonPath("$.docsContentType").value(DEFAULT_DOCS_CONTENT_TYPE))
            .andExpect(jsonPath("$.docs").value(Base64.getEncoder().encodeToString(DEFAULT_DOCS)));
    }

    @Test
    @Transactional
    void getNonExistingAttestationPresence() throws Exception {
        // Get the attestationPresence
        restAttestationPresenceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAttestationPresence() throws Exception {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.saveAndFlush(attestationPresence);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attestationPresence
        AttestationPresence updatedAttestationPresence = attestationPresenceRepository.findById(attestationPresence.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAttestationPresence are not directly saved in db
        em.detach(updatedAttestationPresence);
        updatedAttestationPresence
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .status(UPDATED_STATUS)
            .comments(UPDATED_COMMENTS)
            .docs(UPDATED_DOCS)
            .docsContentType(UPDATED_DOCS_CONTENT_TYPE);

        restAttestationPresenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAttestationPresence.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedAttestationPresence))
            )
            .andExpect(status().isOk());

        // Validate the AttestationPresence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAttestationPresenceToMatchAllProperties(updatedAttestationPresence);
    }

    @Test
    @Transactional
    void putNonExistingAttestationPresence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationPresence.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttestationPresenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, attestationPresence.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(attestationPresence))
            )
            .andExpect(status().isBadRequest());

        // Validate the AttestationPresence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAttestationPresence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationPresence.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttestationPresenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(attestationPresence))
            )
            .andExpect(status().isBadRequest());

        // Validate the AttestationPresence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAttestationPresence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationPresence.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttestationPresenceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(attestationPresence)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AttestationPresence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAttestationPresenceWithPatch() throws Exception {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.saveAndFlush(attestationPresence);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attestationPresence using partial update
        AttestationPresence partialUpdatedAttestationPresence = new AttestationPresence();
        partialUpdatedAttestationPresence.setId(attestationPresence.getId());

        partialUpdatedAttestationPresence.startDate(UPDATED_START_DATE).comments(UPDATED_COMMENTS);

        restAttestationPresenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAttestationPresence.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAttestationPresence))
            )
            .andExpect(status().isOk());

        // Validate the AttestationPresence in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAttestationPresenceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAttestationPresence, attestationPresence),
            getPersistedAttestationPresence(attestationPresence)
        );
    }

    @Test
    @Transactional
    void fullUpdateAttestationPresenceWithPatch() throws Exception {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.saveAndFlush(attestationPresence);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the attestationPresence using partial update
        AttestationPresence partialUpdatedAttestationPresence = new AttestationPresence();
        partialUpdatedAttestationPresence.setId(attestationPresence.getId());

        partialUpdatedAttestationPresence
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .status(UPDATED_STATUS)
            .comments(UPDATED_COMMENTS)
            .docs(UPDATED_DOCS)
            .docsContentType(UPDATED_DOCS_CONTENT_TYPE);

        restAttestationPresenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAttestationPresence.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAttestationPresence))
            )
            .andExpect(status().isOk());

        // Validate the AttestationPresence in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAttestationPresenceUpdatableFieldsEquals(
            partialUpdatedAttestationPresence,
            getPersistedAttestationPresence(partialUpdatedAttestationPresence)
        );
    }

    @Test
    @Transactional
    void patchNonExistingAttestationPresence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationPresence.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAttestationPresenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, attestationPresence.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(attestationPresence))
            )
            .andExpect(status().isBadRequest());

        // Validate the AttestationPresence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAttestationPresence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationPresence.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttestationPresenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(attestationPresence))
            )
            .andExpect(status().isBadRequest());

        // Validate the AttestationPresence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAttestationPresence() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        attestationPresence.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAttestationPresenceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(attestationPresence)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AttestationPresence in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAttestationPresence() throws Exception {
        // Initialize the database
        insertedAttestationPresence = attestationPresenceRepository.saveAndFlush(attestationPresence);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the attestationPresence
        restAttestationPresenceMockMvc
            .perform(delete(ENTITY_API_URL_ID, attestationPresence.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return attestationPresenceRepository.count();
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

    protected AttestationPresence getPersistedAttestationPresence(AttestationPresence attestationPresence) {
        return attestationPresenceRepository.findById(attestationPresence.getId()).orElseThrow();
    }

    protected void assertPersistedAttestationPresenceToMatchAllProperties(AttestationPresence expectedAttestationPresence) {
        assertAttestationPresenceAllPropertiesEquals(
            expectedAttestationPresence,
            getPersistedAttestationPresence(expectedAttestationPresence)
        );
    }

    protected void assertPersistedAttestationPresenceToMatchUpdatableProperties(AttestationPresence expectedAttestationPresence) {
        assertAttestationPresenceAllUpdatablePropertiesEquals(
            expectedAttestationPresence,
            getPersistedAttestationPresence(expectedAttestationPresence)
        );
    }
}
