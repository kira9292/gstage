package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.sonatel.dsi.ins.imoc.domain.ContratAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.Contrat;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ContractStatus;
import sn.sonatel.dsi.ins.imoc.repository.ContratRepository;

/**
 * Integration tests for the {@link ContratResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ContratResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Double DEFAULT_COMPENSATION = 0D;
    private static final Double UPDATED_COMPENSATION = 1D;

    private static final ContractStatus DEFAULT_STATUS = ContractStatus.EN_PREPARATION;
    private static final ContractStatus UPDATED_STATUS = ContractStatus.EN_SIGNATURE;

    private static final String DEFAULT_ASSIGNMENT_SITE = "AAAAAAAAAA";
    private static final String UPDATED_ASSIGNMENT_SITE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_SIGNATURE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_SIGNATURE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_COMMENTS = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/contrats";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restContratMockMvc;

    private Contrat contrat;

    private Contrat insertedContrat;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Contrat createEntity() {
        return new Contrat()
            .reference(DEFAULT_REFERENCE)
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .compensation(DEFAULT_COMPENSATION)
            .status(DEFAULT_STATUS)
            .assignmentSite(DEFAULT_ASSIGNMENT_SITE)
            .signatureDate(DEFAULT_SIGNATURE_DATE)
            .comments(DEFAULT_COMMENTS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Contrat createUpdatedEntity() {
        return new Contrat()
            .reference(UPDATED_REFERENCE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .compensation(UPDATED_COMPENSATION)
            .status(UPDATED_STATUS)
            .assignmentSite(UPDATED_ASSIGNMENT_SITE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .comments(UPDATED_COMMENTS);
    }

    @BeforeEach
    public void initTest() {
        contrat = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedContrat != null) {
            contratRepository.delete(insertedContrat);
            insertedContrat = null;
        }
    }

    @Test
    @Transactional
    void createContrat() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Contrat
        var returnedContrat = om.readValue(
            restContratMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contrat)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Contrat.class
        );

        // Validate the Contrat in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertContratUpdatableFieldsEquals(returnedContrat, getPersistedContrat(returnedContrat));

        insertedContrat = returnedContrat;
    }

    @Test
    @Transactional
    void createContratWithExistingId() throws Exception {
        // Create the Contrat with an existing ID
        contrat.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restContratMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contrat)))
            .andExpect(status().isBadRequest());

        // Validate the Contrat in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contrat.setReference(null);

        // Create the Contrat, which fails.

        restContratMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contrat)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contrat.setStartDate(null);

        // Create the Contrat, which fails.

        restContratMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contrat)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contrat.setEndDate(null);

        // Create the Contrat, which fails.

        restContratMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contrat)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCompensationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contrat.setCompensation(null);

        // Create the Contrat, which fails.

        restContratMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contrat)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contrat.setStatus(null);

        // Create the Contrat, which fails.

        restContratMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contrat)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAssignmentSiteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        contrat.setAssignmentSite(null);

        // Create the Contrat, which fails.

        restContratMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contrat)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllContrats() throws Exception {
        // Initialize the database
        insertedContrat = contratRepository.saveAndFlush(contrat);

        // Get all the contratList
        restContratMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(contrat.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].compensation").value(hasItem(DEFAULT_COMPENSATION.doubleValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].assignmentSite").value(hasItem(DEFAULT_ASSIGNMENT_SITE)))
            .andExpect(jsonPath("$.[*].signatureDate").value(hasItem(DEFAULT_SIGNATURE_DATE.toString())))
            .andExpect(jsonPath("$.[*].comments").value(hasItem(DEFAULT_COMMENTS)));
    }

    @Test
    @Transactional
    void getContrat() throws Exception {
        // Initialize the database
        insertedContrat = contratRepository.saveAndFlush(contrat);

        // Get the contrat
        restContratMockMvc
            .perform(get(ENTITY_API_URL_ID, contrat.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(contrat.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.compensation").value(DEFAULT_COMPENSATION.doubleValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.assignmentSite").value(DEFAULT_ASSIGNMENT_SITE))
            .andExpect(jsonPath("$.signatureDate").value(DEFAULT_SIGNATURE_DATE.toString()))
            .andExpect(jsonPath("$.comments").value(DEFAULT_COMMENTS));
    }

    @Test
    @Transactional
    void getNonExistingContrat() throws Exception {
        // Get the contrat
        restContratMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingContrat() throws Exception {
        // Initialize the database
        insertedContrat = contratRepository.saveAndFlush(contrat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contrat
        Contrat updatedContrat = contratRepository.findById(contrat.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedContrat are not directly saved in db
        em.detach(updatedContrat);
        updatedContrat
            .reference(UPDATED_REFERENCE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .compensation(UPDATED_COMPENSATION)
            .status(UPDATED_STATUS)
            .assignmentSite(UPDATED_ASSIGNMENT_SITE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .comments(UPDATED_COMMENTS);

        restContratMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedContrat.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedContrat))
            )
            .andExpect(status().isOk());

        // Validate the Contrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedContratToMatchAllProperties(updatedContrat);
    }

    @Test
    @Transactional
    void putNonExistingContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contrat.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContratMockMvc
            .perform(put(ENTITY_API_URL_ID, contrat.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contrat)))
            .andExpect(status().isBadRequest());

        // Validate the Contrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contrat.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContratMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(contrat))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contrat.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContratMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(contrat)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Contrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateContratWithPatch() throws Exception {
        // Initialize the database
        insertedContrat = contratRepository.saveAndFlush(contrat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contrat using partial update
        Contrat partialUpdatedContrat = new Contrat();
        partialUpdatedContrat.setId(contrat.getId());

        partialUpdatedContrat
            .reference(UPDATED_REFERENCE)
            .endDate(UPDATED_END_DATE)
            .compensation(UPDATED_COMPENSATION)
            .assignmentSite(UPDATED_ASSIGNMENT_SITE)
            .comments(UPDATED_COMMENTS);

        restContratMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContrat.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContrat))
            )
            .andExpect(status().isOk());

        // Validate the Contrat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContratUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedContrat, contrat), getPersistedContrat(contrat));
    }

    @Test
    @Transactional
    void fullUpdateContratWithPatch() throws Exception {
        // Initialize the database
        insertedContrat = contratRepository.saveAndFlush(contrat);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the contrat using partial update
        Contrat partialUpdatedContrat = new Contrat();
        partialUpdatedContrat.setId(contrat.getId());

        partialUpdatedContrat
            .reference(UPDATED_REFERENCE)
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .compensation(UPDATED_COMPENSATION)
            .status(UPDATED_STATUS)
            .assignmentSite(UPDATED_ASSIGNMENT_SITE)
            .signatureDate(UPDATED_SIGNATURE_DATE)
            .comments(UPDATED_COMMENTS);

        restContratMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedContrat.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedContrat))
            )
            .andExpect(status().isOk());

        // Validate the Contrat in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertContratUpdatableFieldsEquals(partialUpdatedContrat, getPersistedContrat(partialUpdatedContrat));
    }

    @Test
    @Transactional
    void patchNonExistingContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contrat.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restContratMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, contrat.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(contrat))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contrat.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContratMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(contrat))
            )
            .andExpect(status().isBadRequest());

        // Validate the Contrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamContrat() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        contrat.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restContratMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(contrat)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Contrat in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteContrat() throws Exception {
        // Initialize the database
        insertedContrat = contratRepository.saveAndFlush(contrat);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the contrat
        restContratMockMvc
            .perform(delete(ENTITY_API_URL_ID, contrat.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return contratRepository.count();
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

    protected Contrat getPersistedContrat(Contrat contrat) {
        return contratRepository.findById(contrat.getId()).orElseThrow();
    }

    protected void assertPersistedContratToMatchAllProperties(Contrat expectedContrat) {
        assertContratAllPropertiesEquals(expectedContrat, getPersistedContrat(expectedContrat));
    }

    protected void assertPersistedContratToMatchUpdatableProperties(Contrat expectedContrat) {
        assertContratAllUpdatablePropertiesEquals(expectedContrat, getPersistedContrat(expectedContrat));
    }
}
