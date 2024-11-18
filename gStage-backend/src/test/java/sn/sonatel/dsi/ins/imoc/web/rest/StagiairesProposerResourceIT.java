package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.sonatel.dsi.ins.imoc.domain.StagiairesProposerAsserts.*;
import static sn.sonatel.dsi.ins.imoc.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
import sn.sonatel.dsi.ins.imoc.domain.StagiairesProposer;
import sn.sonatel.dsi.ins.imoc.repository.StagiairesProposerRepository;

/**
 * Integration tests for the {@link StagiairesProposerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StagiairesProposerResourceIT {

    private static final String DEFAULT_DEMANDEUR = "AAAAAAAAAA";
    private static final String UPDATED_DEMANDEUR = "BBBBBBBBBB";

    private static final String DEFAULT_DIRECTION = "AAAAAAAAAA";
    private static final String UPDATED_DIRECTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_NBRE_STAGIAIRE = 1;
    private static final Integer UPDATED_NBRE_STAGIAIRE = 2;

    private static final String DEFAULT_PROFIL_FORMATION = "AAAAAAAAAA";
    private static final String UPDATED_PROFIL_FORMATION = "BBBBBBBBBB";

    private static final String DEFAULT_STAGIAIE_SOUS_RECOMANDATION = "AAAAAAAAAA";
    private static final String UPDATED_STAGIAIE_SOUS_RECOMANDATION = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENTAIRE = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTAIRE = "BBBBBBBBBB";

    private static final String DEFAULT_MOTIF = "AAAAAAAAAA";
    private static final String UPDATED_MOTIF = "BBBBBBBBBB";

    private static final String DEFAULT_TRAITEMENT = "AAAAAAAAAA";
    private static final String UPDATED_TRAITEMENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/stagiaires-proposers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StagiairesProposerRepository stagiairesProposerRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStagiairesProposerMockMvc;

    private StagiairesProposer stagiairesProposer;

    private StagiairesProposer insertedStagiairesProposer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StagiairesProposer createEntity() {
        return new StagiairesProposer()
            .demandeur(DEFAULT_DEMANDEUR)
            .direction(DEFAULT_DIRECTION)
            .nbreStagiaire(DEFAULT_NBRE_STAGIAIRE)
            .profilFormation(DEFAULT_PROFIL_FORMATION)
            .stagiaieSousRecomandation(DEFAULT_STAGIAIE_SOUS_RECOMANDATION)
            .commentaire(DEFAULT_COMMENTAIRE)
            .motif(DEFAULT_MOTIF)
            .traitement(DEFAULT_TRAITEMENT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StagiairesProposer createUpdatedEntity() {
        return new StagiairesProposer()
            .demandeur(UPDATED_DEMANDEUR)
            .direction(UPDATED_DIRECTION)
            .nbreStagiaire(UPDATED_NBRE_STAGIAIRE)
            .profilFormation(UPDATED_PROFIL_FORMATION)
            .stagiaieSousRecomandation(UPDATED_STAGIAIE_SOUS_RECOMANDATION)
            .commentaire(UPDATED_COMMENTAIRE)
            .motif(UPDATED_MOTIF)
            .traitement(UPDATED_TRAITEMENT);
    }

    @BeforeEach
    public void initTest() {
        stagiairesProposer = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedStagiairesProposer != null) {
            stagiairesProposerRepository.delete(insertedStagiairesProposer);
            insertedStagiairesProposer = null;
        }
    }

    @Test
    @Transactional
    void createStagiairesProposer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StagiairesProposer
        var returnedStagiairesProposer = om.readValue(
            restStagiairesProposerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stagiairesProposer)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StagiairesProposer.class
        );

        // Validate the StagiairesProposer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertStagiairesProposerUpdatableFieldsEquals(
            returnedStagiairesProposer,
            getPersistedStagiairesProposer(returnedStagiairesProposer)
        );

        insertedStagiairesProposer = returnedStagiairesProposer;
    }

    @Test
    @Transactional
    void createStagiairesProposerWithExistingId() throws Exception {
        // Create the StagiairesProposer with an existing ID
        stagiairesProposer.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStagiairesProposerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stagiairesProposer)))
            .andExpect(status().isBadRequest());

        // Validate the StagiairesProposer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllStagiairesProposers() throws Exception {
        // Initialize the database
        insertedStagiairesProposer = stagiairesProposerRepository.saveAndFlush(stagiairesProposer);

        // Get all the stagiairesProposerList
        restStagiairesProposerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stagiairesProposer.getId().intValue())))
            .andExpect(jsonPath("$.[*].demandeur").value(hasItem(DEFAULT_DEMANDEUR)))
            .andExpect(jsonPath("$.[*].direction").value(hasItem(DEFAULT_DIRECTION)))
            .andExpect(jsonPath("$.[*].nbreStagiaire").value(hasItem(DEFAULT_NBRE_STAGIAIRE)))
            .andExpect(jsonPath("$.[*].profilFormation").value(hasItem(DEFAULT_PROFIL_FORMATION)))
            .andExpect(jsonPath("$.[*].stagiaieSousRecomandation").value(hasItem(DEFAULT_STAGIAIE_SOUS_RECOMANDATION)))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)))
            .andExpect(jsonPath("$.[*].motif").value(hasItem(DEFAULT_MOTIF)))
            .andExpect(jsonPath("$.[*].traitement").value(hasItem(DEFAULT_TRAITEMENT)));
    }

    @Test
    @Transactional
    void getStagiairesProposer() throws Exception {
        // Initialize the database
        insertedStagiairesProposer = stagiairesProposerRepository.saveAndFlush(stagiairesProposer);

        // Get the stagiairesProposer
        restStagiairesProposerMockMvc
            .perform(get(ENTITY_API_URL_ID, stagiairesProposer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stagiairesProposer.getId().intValue()))
            .andExpect(jsonPath("$.demandeur").value(DEFAULT_DEMANDEUR))
            .andExpect(jsonPath("$.direction").value(DEFAULT_DIRECTION))
            .andExpect(jsonPath("$.nbreStagiaire").value(DEFAULT_NBRE_STAGIAIRE))
            .andExpect(jsonPath("$.profilFormation").value(DEFAULT_PROFIL_FORMATION))
            .andExpect(jsonPath("$.stagiaieSousRecomandation").value(DEFAULT_STAGIAIE_SOUS_RECOMANDATION))
            .andExpect(jsonPath("$.commentaire").value(DEFAULT_COMMENTAIRE))
            .andExpect(jsonPath("$.motif").value(DEFAULT_MOTIF))
            .andExpect(jsonPath("$.traitement").value(DEFAULT_TRAITEMENT));
    }

    @Test
    @Transactional
    void getNonExistingStagiairesProposer() throws Exception {
        // Get the stagiairesProposer
        restStagiairesProposerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStagiairesProposer() throws Exception {
        // Initialize the database
        insertedStagiairesProposer = stagiairesProposerRepository.saveAndFlush(stagiairesProposer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stagiairesProposer
        StagiairesProposer updatedStagiairesProposer = stagiairesProposerRepository.findById(stagiairesProposer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStagiairesProposer are not directly saved in db
        em.detach(updatedStagiairesProposer);
        updatedStagiairesProposer
            .demandeur(UPDATED_DEMANDEUR)
            .direction(UPDATED_DIRECTION)
            .nbreStagiaire(UPDATED_NBRE_STAGIAIRE)
            .profilFormation(UPDATED_PROFIL_FORMATION)
            .stagiaieSousRecomandation(UPDATED_STAGIAIE_SOUS_RECOMANDATION)
            .commentaire(UPDATED_COMMENTAIRE)
            .motif(UPDATED_MOTIF)
            .traitement(UPDATED_TRAITEMENT);

        restStagiairesProposerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStagiairesProposer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedStagiairesProposer))
            )
            .andExpect(status().isOk());

        // Validate the StagiairesProposer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStagiairesProposerToMatchAllProperties(updatedStagiairesProposer);
    }

    @Test
    @Transactional
    void putNonExistingStagiairesProposer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stagiairesProposer.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStagiairesProposerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stagiairesProposer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stagiairesProposer))
            )
            .andExpect(status().isBadRequest());

        // Validate the StagiairesProposer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStagiairesProposer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stagiairesProposer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStagiairesProposerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stagiairesProposer))
            )
            .andExpect(status().isBadRequest());

        // Validate the StagiairesProposer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStagiairesProposer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stagiairesProposer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStagiairesProposerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stagiairesProposer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StagiairesProposer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStagiairesProposerWithPatch() throws Exception {
        // Initialize the database
        insertedStagiairesProposer = stagiairesProposerRepository.saveAndFlush(stagiairesProposer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stagiairesProposer using partial update
        StagiairesProposer partialUpdatedStagiairesProposer = new StagiairesProposer();
        partialUpdatedStagiairesProposer.setId(stagiairesProposer.getId());

        partialUpdatedStagiairesProposer
            .demandeur(UPDATED_DEMANDEUR)
            .direction(UPDATED_DIRECTION)
            .profilFormation(UPDATED_PROFIL_FORMATION);

        restStagiairesProposerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStagiairesProposer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStagiairesProposer))
            )
            .andExpect(status().isOk());

        // Validate the StagiairesProposer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStagiairesProposerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStagiairesProposer, stagiairesProposer),
            getPersistedStagiairesProposer(stagiairesProposer)
        );
    }

    @Test
    @Transactional
    void fullUpdateStagiairesProposerWithPatch() throws Exception {
        // Initialize the database
        insertedStagiairesProposer = stagiairesProposerRepository.saveAndFlush(stagiairesProposer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stagiairesProposer using partial update
        StagiairesProposer partialUpdatedStagiairesProposer = new StagiairesProposer();
        partialUpdatedStagiairesProposer.setId(stagiairesProposer.getId());

        partialUpdatedStagiairesProposer
            .demandeur(UPDATED_DEMANDEUR)
            .direction(UPDATED_DIRECTION)
            .nbreStagiaire(UPDATED_NBRE_STAGIAIRE)
            .profilFormation(UPDATED_PROFIL_FORMATION)
            .stagiaieSousRecomandation(UPDATED_STAGIAIE_SOUS_RECOMANDATION)
            .commentaire(UPDATED_COMMENTAIRE)
            .motif(UPDATED_MOTIF)
            .traitement(UPDATED_TRAITEMENT);

        restStagiairesProposerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStagiairesProposer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStagiairesProposer))
            )
            .andExpect(status().isOk());

        // Validate the StagiairesProposer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStagiairesProposerUpdatableFieldsEquals(
            partialUpdatedStagiairesProposer,
            getPersistedStagiairesProposer(partialUpdatedStagiairesProposer)
        );
    }

    @Test
    @Transactional
    void patchNonExistingStagiairesProposer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stagiairesProposer.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStagiairesProposerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stagiairesProposer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stagiairesProposer))
            )
            .andExpect(status().isBadRequest());

        // Validate the StagiairesProposer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStagiairesProposer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stagiairesProposer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStagiairesProposerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stagiairesProposer))
            )
            .andExpect(status().isBadRequest());

        // Validate the StagiairesProposer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStagiairesProposer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stagiairesProposer.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStagiairesProposerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stagiairesProposer)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StagiairesProposer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStagiairesProposer() throws Exception {
        // Initialize the database
        insertedStagiairesProposer = stagiairesProposerRepository.saveAndFlush(stagiairesProposer);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the stagiairesProposer
        restStagiairesProposerMockMvc
            .perform(delete(ENTITY_API_URL_ID, stagiairesProposer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return stagiairesProposerRepository.count();
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

    protected StagiairesProposer getPersistedStagiairesProposer(StagiairesProposer stagiairesProposer) {
        return stagiairesProposerRepository.findById(stagiairesProposer.getId()).orElseThrow();
    }

    protected void assertPersistedStagiairesProposerToMatchAllProperties(StagiairesProposer expectedStagiairesProposer) {
        assertStagiairesProposerAllPropertiesEquals(expectedStagiairesProposer, getPersistedStagiairesProposer(expectedStagiairesProposer));
    }

    protected void assertPersistedStagiairesProposerToMatchUpdatableProperties(StagiairesProposer expectedStagiairesProposer) {
        assertStagiairesProposerAllUpdatablePropertiesEquals(
            expectedStagiairesProposer,
            getPersistedStagiairesProposer(expectedStagiairesProposer)
        );
    }
}
