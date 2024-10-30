package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.sonatel.dsi.ins.imoc.domain.BusinessUnitAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.BusinessUnit;
import sn.sonatel.dsi.ins.imoc.repository.BusinessUnitRepository;

/**
 * Integration tests for the {@link BusinessUnitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BusinessUnitResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/business-units";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BusinessUnitRepository businessUnitRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBusinessUnitMockMvc;

    private BusinessUnit businessUnit;

    private BusinessUnit insertedBusinessUnit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BusinessUnit createEntity() {
        return new BusinessUnit().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).code(DEFAULT_CODE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BusinessUnit createUpdatedEntity() {
        return new BusinessUnit().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).code(UPDATED_CODE);
    }

    @BeforeEach
    public void initTest() {
        businessUnit = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedBusinessUnit != null) {
            businessUnitRepository.delete(insertedBusinessUnit);
            insertedBusinessUnit = null;
        }
    }

    @Test
    @Transactional
    void createBusinessUnit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BusinessUnit
        var returnedBusinessUnit = om.readValue(
            restBusinessUnitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessUnit)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BusinessUnit.class
        );

        // Validate the BusinessUnit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertBusinessUnitUpdatableFieldsEquals(returnedBusinessUnit, getPersistedBusinessUnit(returnedBusinessUnit));

        insertedBusinessUnit = returnedBusinessUnit;
    }

    @Test
    @Transactional
    void createBusinessUnitWithExistingId() throws Exception {
        // Create the BusinessUnit with an existing ID
        businessUnit.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBusinessUnitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessUnit)))
            .andExpect(status().isBadRequest());

        // Validate the BusinessUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        businessUnit.setName(null);

        // Create the BusinessUnit, which fails.

        restBusinessUnitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessUnit)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        businessUnit.setCode(null);

        // Create the BusinessUnit, which fails.

        restBusinessUnitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessUnit)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBusinessUnits() throws Exception {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.saveAndFlush(businessUnit);

        // Get all the businessUnitList
        restBusinessUnitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(businessUnit.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)));
    }

    @Test
    @Transactional
    void getBusinessUnit() throws Exception {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.saveAndFlush(businessUnit);

        // Get the businessUnit
        restBusinessUnitMockMvc
            .perform(get(ENTITY_API_URL_ID, businessUnit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(businessUnit.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE));
    }

    @Test
    @Transactional
    void getNonExistingBusinessUnit() throws Exception {
        // Get the businessUnit
        restBusinessUnitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBusinessUnit() throws Exception {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.saveAndFlush(businessUnit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the businessUnit
        BusinessUnit updatedBusinessUnit = businessUnitRepository.findById(businessUnit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBusinessUnit are not directly saved in db
        em.detach(updatedBusinessUnit);
        updatedBusinessUnit.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).code(UPDATED_CODE);

        restBusinessUnitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBusinessUnit.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedBusinessUnit))
            )
            .andExpect(status().isOk());

        // Validate the BusinessUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBusinessUnitToMatchAllProperties(updatedBusinessUnit);
    }

    @Test
    @Transactional
    void putNonExistingBusinessUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUnit.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBusinessUnitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, businessUnit.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(businessUnit))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBusinessUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUnit.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessUnitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(businessUnit))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBusinessUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUnit.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessUnitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(businessUnit)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BusinessUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBusinessUnitWithPatch() throws Exception {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.saveAndFlush(businessUnit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the businessUnit using partial update
        BusinessUnit partialUpdatedBusinessUnit = new BusinessUnit();
        partialUpdatedBusinessUnit.setId(businessUnit.getId());

        partialUpdatedBusinessUnit.description(UPDATED_DESCRIPTION);

        restBusinessUnitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBusinessUnit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBusinessUnit))
            )
            .andExpect(status().isOk());

        // Validate the BusinessUnit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBusinessUnitUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBusinessUnit, businessUnit),
            getPersistedBusinessUnit(businessUnit)
        );
    }

    @Test
    @Transactional
    void fullUpdateBusinessUnitWithPatch() throws Exception {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.saveAndFlush(businessUnit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the businessUnit using partial update
        BusinessUnit partialUpdatedBusinessUnit = new BusinessUnit();
        partialUpdatedBusinessUnit.setId(businessUnit.getId());

        partialUpdatedBusinessUnit.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).code(UPDATED_CODE);

        restBusinessUnitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBusinessUnit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBusinessUnit))
            )
            .andExpect(status().isOk());

        // Validate the BusinessUnit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBusinessUnitUpdatableFieldsEquals(partialUpdatedBusinessUnit, getPersistedBusinessUnit(partialUpdatedBusinessUnit));
    }

    @Test
    @Transactional
    void patchNonExistingBusinessUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUnit.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBusinessUnitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, businessUnit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(businessUnit))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBusinessUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUnit.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessUnitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(businessUnit))
            )
            .andExpect(status().isBadRequest());

        // Validate the BusinessUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBusinessUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUnit.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBusinessUnitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(businessUnit)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BusinessUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBusinessUnit() throws Exception {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.saveAndFlush(businessUnit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the businessUnit
        restBusinessUnitMockMvc
            .perform(delete(ENTITY_API_URL_ID, businessUnit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return businessUnitRepository.count();
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

    protected BusinessUnit getPersistedBusinessUnit(BusinessUnit businessUnit) {
        return businessUnitRepository.findById(businessUnit.getId()).orElseThrow();
    }

    protected void assertPersistedBusinessUnitToMatchAllProperties(BusinessUnit expectedBusinessUnit) {
        assertBusinessUnitAllPropertiesEquals(expectedBusinessUnit, getPersistedBusinessUnit(expectedBusinessUnit));
    }

    protected void assertPersistedBusinessUnitToMatchUpdatableProperties(BusinessUnit expectedBusinessUnit) {
        assertBusinessUnitAllUpdatablePropertiesEquals(expectedBusinessUnit, getPersistedBusinessUnit(expectedBusinessUnit));
    }
}
