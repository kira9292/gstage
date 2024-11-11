package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.sonatel.dsi.ins.imoc.domain.DemandeStageAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipType;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;

/**
 * Integration tests for the {@link DemandeStageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DemandeStageResourceIT {

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final InternshipType DEFAULT_INTERNSHIP_TYPE = InternshipType.ACADEMIQUE;
    private static final InternshipType UPDATED_INTERNSHIP_TYPE = InternshipType.PROFESSIONNEL;

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final byte[] DEFAULT_CV = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_CV = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_CV_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_CV_CONTENT_TYPE = "image/png";

    private static final byte[] DEFAULT_COVER_LETTER = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_COVER_LETTER = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_COVER_LETTER_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_COVER_LETTER_CONTENT_TYPE = "image/png";

    private static final InternshipStatus DEFAULT_STATUS = InternshipStatus.EN_ATTENTE;
    private static final InternshipStatus UPDATED_STATUS = InternshipStatus.ACCEPTE;

    private static final Boolean DEFAULT_VALIDATED = false;
    private static final Boolean UPDATED_VALIDATED = true;

    private static final String ENTITY_API_URL = "/api/demande-stages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DemandeStageRepository demandeStageRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDemandeStageMockMvc;

    private DemandeStage demandeStage;

    private DemandeStage insertedDemandeStage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DemandeStage createEntity() {
        return new DemandeStage()
            .creationDate(DEFAULT_CREATION_DATE)
            .internshipType(DEFAULT_INTERNSHIP_TYPE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .cv(DEFAULT_CV)
            .cvContentType(DEFAULT_CV_CONTENT_TYPE)
            .coverLetter(DEFAULT_COVER_LETTER)
            .coverLetterContentType(DEFAULT_COVER_LETTER_CONTENT_TYPE)
            .status(DEFAULT_STATUS)
            .validated(DEFAULT_VALIDATED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DemandeStage createUpdatedEntity() {
        return new DemandeStage()
            .creationDate(UPDATED_CREATION_DATE)
            .internshipType(UPDATED_INTERNSHIP_TYPE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .cv(UPDATED_CV)
            .cvContentType(UPDATED_CV_CONTENT_TYPE)
            .coverLetter(UPDATED_COVER_LETTER)
            .coverLetterContentType(UPDATED_COVER_LETTER_CONTENT_TYPE)
            .status(UPDATED_STATUS)
            .validated(UPDATED_VALIDATED);
    }

    @BeforeEach
    public void initTest() {
        demandeStage = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedDemandeStage != null) {
            demandeStageRepository.delete(insertedDemandeStage);
            insertedDemandeStage = null;
        }
    }

    @Test
    @Transactional
    void createDemandeStage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DemandeStage
        var returnedDemandeStage = om.readValue(
            restDemandeStageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeStage)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DemandeStage.class
        );

        // Validate the DemandeStage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDemandeStageUpdatableFieldsEquals(returnedDemandeStage, getPersistedDemandeStage(returnedDemandeStage));

        insertedDemandeStage = returnedDemandeStage;
    }

    @Test
    @Transactional
    void createDemandeStageWithExistingId() throws Exception {
        // Create the DemandeStage with an existing ID
        demandeStage.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDemandeStageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeStage)))
            .andExpect(status().isBadRequest());

        // Validate the DemandeStage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDemandeStages() throws Exception {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.saveAndFlush(demandeStage);

        // Get all the demandeStageList
        restDemandeStageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(demandeStage.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].internshipType").value(hasItem(DEFAULT_INTERNSHIP_TYPE.toString())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].cvContentType").value(hasItem(DEFAULT_CV_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].cv").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_CV))))
            .andExpect(jsonPath("$.[*].coverLetterContentType").value(hasItem(DEFAULT_COVER_LETTER_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].coverLetter").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_COVER_LETTER))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].validated").value(hasItem(DEFAULT_VALIDATED.booleanValue())));
    }

    @Test
    @Transactional
    void getDemandeStage() throws Exception {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.saveAndFlush(demandeStage);

        // Get the demandeStage
        restDemandeStageMockMvc
            .perform(get(ENTITY_API_URL_ID, demandeStage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(demandeStage.getId().intValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.internshipType").value(DEFAULT_INTERNSHIP_TYPE.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.cvContentType").value(DEFAULT_CV_CONTENT_TYPE))
            .andExpect(jsonPath("$.cv").value(Base64.getEncoder().encodeToString(DEFAULT_CV)))
            .andExpect(jsonPath("$.coverLetterContentType").value(DEFAULT_COVER_LETTER_CONTENT_TYPE))
            .andExpect(jsonPath("$.coverLetter").value(Base64.getEncoder().encodeToString(DEFAULT_COVER_LETTER)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.validated").value(DEFAULT_VALIDATED.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingDemandeStage() throws Exception {
        // Get the demandeStage
        restDemandeStageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDemandeStage() throws Exception {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.saveAndFlush(demandeStage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the demandeStage
        DemandeStage updatedDemandeStage = demandeStageRepository.findById(demandeStage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDemandeStage are not directly saved in db
        em.detach(updatedDemandeStage);
        updatedDemandeStage
            .creationDate(UPDATED_CREATION_DATE)
            .internshipType(UPDATED_INTERNSHIP_TYPE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .cv(UPDATED_CV)
            .cvContentType(UPDATED_CV_CONTENT_TYPE)
            .coverLetter(UPDATED_COVER_LETTER)
            .coverLetterContentType(UPDATED_COVER_LETTER_CONTENT_TYPE)
            .status(UPDATED_STATUS)
            .validated(UPDATED_VALIDATED);

        restDemandeStageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDemandeStage.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedDemandeStage))
            )
            .andExpect(status().isOk());

        // Validate the DemandeStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDemandeStageToMatchAllProperties(updatedDemandeStage);
    }

    @Test
    @Transactional
    void putNonExistingDemandeStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandeStage.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDemandeStageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, demandeStage.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(demandeStage))
            )
            .andExpect(status().isBadRequest());

        // Validate the DemandeStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDemandeStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandeStage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeStageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(demandeStage))
            )
            .andExpect(status().isBadRequest());

        // Validate the DemandeStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDemandeStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandeStage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeStageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandeStage)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DemandeStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDemandeStageWithPatch() throws Exception {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.saveAndFlush(demandeStage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the demandeStage using partial update
        DemandeStage partialUpdatedDemandeStage = new DemandeStage();
        partialUpdatedDemandeStage.setId(demandeStage.getId());

        partialUpdatedDemandeStage
            .internshipType(UPDATED_INTERNSHIP_TYPE)
            .endDate(UPDATED_END_DATE)
            .coverLetter(UPDATED_COVER_LETTER)
            .coverLetterContentType(UPDATED_COVER_LETTER_CONTENT_TYPE);

        restDemandeStageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDemandeStage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDemandeStage))
            )
            .andExpect(status().isOk());

        // Validate the DemandeStage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDemandeStageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDemandeStage, demandeStage),
            getPersistedDemandeStage(demandeStage)
        );
    }

    @Test
    @Transactional
    void fullUpdateDemandeStageWithPatch() throws Exception {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.saveAndFlush(demandeStage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the demandeStage using partial update
        DemandeStage partialUpdatedDemandeStage = new DemandeStage();
        partialUpdatedDemandeStage.setId(demandeStage.getId());

        partialUpdatedDemandeStage
            .creationDate(UPDATED_CREATION_DATE)
            .internshipType(UPDATED_INTERNSHIP_TYPE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .cv(UPDATED_CV)
            .cvContentType(UPDATED_CV_CONTENT_TYPE)
            .coverLetter(UPDATED_COVER_LETTER)
            .coverLetterContentType(UPDATED_COVER_LETTER_CONTENT_TYPE)
            .status(UPDATED_STATUS)
            .validated(UPDATED_VALIDATED);

        restDemandeStageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDemandeStage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDemandeStage))
            )
            .andExpect(status().isOk());

        // Validate the DemandeStage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDemandeStageUpdatableFieldsEquals(partialUpdatedDemandeStage, getPersistedDemandeStage(partialUpdatedDemandeStage));
    }

    @Test
    @Transactional
    void patchNonExistingDemandeStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandeStage.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDemandeStageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, demandeStage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(demandeStage))
            )
            .andExpect(status().isBadRequest());

        // Validate the DemandeStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDemandeStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandeStage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeStageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(demandeStage))
            )
            .andExpect(status().isBadRequest());

        // Validate the DemandeStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDemandeStage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandeStage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandeStageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(demandeStage)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DemandeStage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDemandeStage() throws Exception {
        // Initialize the database
        insertedDemandeStage = demandeStageRepository.saveAndFlush(demandeStage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the demandeStage
        restDemandeStageMockMvc
            .perform(delete(ENTITY_API_URL_ID, demandeStage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return demandeStageRepository.count();
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

    protected DemandeStage getPersistedDemandeStage(DemandeStage demandeStage) {
        return demandeStageRepository.findById(demandeStage.getId()).orElseThrow();
    }

    protected void assertPersistedDemandeStageToMatchAllProperties(DemandeStage expectedDemandeStage) {
        assertDemandeStageAllPropertiesEquals(expectedDemandeStage, getPersistedDemandeStage(expectedDemandeStage));
    }

    protected void assertPersistedDemandeStageToMatchUpdatableProperties(DemandeStage expectedDemandeStage) {
        assertDemandeStageAllUpdatablePropertiesEquals(expectedDemandeStage, getPersistedDemandeStage(expectedDemandeStage));
    }
}
