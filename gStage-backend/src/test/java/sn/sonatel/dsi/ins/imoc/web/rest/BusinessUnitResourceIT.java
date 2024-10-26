package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static sn.sonatel.dsi.ins.imoc.domain.BusinessUnitAsserts.*;
import static sn.sonatel.dsi.ins.imoc.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import sn.sonatel.dsi.ins.imoc.IntegrationTest;
import sn.sonatel.dsi.ins.imoc.domain.BusinessUnit;
import sn.sonatel.dsi.ins.imoc.repository.BusinessUnitRepository;
import sn.sonatel.dsi.ins.imoc.repository.EntityManager;
import sn.sonatel.dsi.ins.imoc.service.dto.BusinessUnitDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.BusinessUnitMapper;

/**
 * Integration tests for the {@link BusinessUnitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
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
    private BusinessUnitMapper businessUnitMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

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

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(BusinessUnit.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        businessUnit = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedBusinessUnit != null) {
            businessUnitRepository.delete(insertedBusinessUnit).block();
            insertedBusinessUnit = null;
        }
        deleteEntities(em);
    }

    @Test
    void createBusinessUnit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BusinessUnit
        BusinessUnitDTO businessUnitDTO = businessUnitMapper.toDto(businessUnit);
        var returnedBusinessUnitDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(businessUnitDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(BusinessUnitDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the BusinessUnit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBusinessUnit = businessUnitMapper.toEntity(returnedBusinessUnitDTO);
        assertBusinessUnitUpdatableFieldsEquals(returnedBusinessUnit, getPersistedBusinessUnit(returnedBusinessUnit));

        insertedBusinessUnit = returnedBusinessUnit;
    }

    @Test
    void createBusinessUnitWithExistingId() throws Exception {
        // Create the BusinessUnit with an existing ID
        businessUnit.setId(1L);
        BusinessUnitDTO businessUnitDTO = businessUnitMapper.toDto(businessUnit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(businessUnitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BusinessUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        businessUnit.setName(null);

        // Create the BusinessUnit, which fails.
        BusinessUnitDTO businessUnitDTO = businessUnitMapper.toDto(businessUnit);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(businessUnitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        businessUnit.setCode(null);

        // Create the BusinessUnit, which fails.
        BusinessUnitDTO businessUnitDTO = businessUnitMapper.toDto(businessUnit);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(businessUnitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllBusinessUnits() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get all the businessUnitList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(businessUnit.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE));
    }

    @Test
    void getBusinessUnit() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get the businessUnit
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, businessUnit.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(businessUnit.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE));
    }

    @Test
    void getBusinessUnitsByIdFiltering() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        Long id = businessUnit.getId();

        defaultBusinessUnitFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultBusinessUnitFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultBusinessUnitFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllBusinessUnitsByNameIsEqualToSomething() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get all the businessUnitList where name equals to
        defaultBusinessUnitFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllBusinessUnitsByNameIsInShouldWork() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get all the businessUnitList where name in
        defaultBusinessUnitFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllBusinessUnitsByNameIsNullOrNotNull() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get all the businessUnitList where name is not null
        defaultBusinessUnitFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllBusinessUnitsByNameContainsSomething() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get all the businessUnitList where name contains
        defaultBusinessUnitFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllBusinessUnitsByNameNotContainsSomething() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get all the businessUnitList where name does not contain
        defaultBusinessUnitFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllBusinessUnitsByDescriptionIsEqualToSomething() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get all the businessUnitList where description equals to
        defaultBusinessUnitFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllBusinessUnitsByDescriptionIsInShouldWork() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get all the businessUnitList where description in
        defaultBusinessUnitFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    void getAllBusinessUnitsByDescriptionIsNullOrNotNull() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get all the businessUnitList where description is not null
        defaultBusinessUnitFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    void getAllBusinessUnitsByDescriptionContainsSomething() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get all the businessUnitList where description contains
        defaultBusinessUnitFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllBusinessUnitsByDescriptionNotContainsSomething() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get all the businessUnitList where description does not contain
        defaultBusinessUnitFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    void getAllBusinessUnitsByCodeIsEqualToSomething() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get all the businessUnitList where code equals to
        defaultBusinessUnitFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    void getAllBusinessUnitsByCodeIsInShouldWork() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get all the businessUnitList where code in
        defaultBusinessUnitFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    void getAllBusinessUnitsByCodeIsNullOrNotNull() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get all the businessUnitList where code is not null
        defaultBusinessUnitFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    void getAllBusinessUnitsByCodeContainsSomething() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get all the businessUnitList where code contains
        defaultBusinessUnitFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    void getAllBusinessUnitsByCodeNotContainsSomething() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        // Get all the businessUnitList where code does not contain
        defaultBusinessUnitFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    private void defaultBusinessUnitFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultBusinessUnitShouldBeFound(shouldBeFound);
        defaultBusinessUnitShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultBusinessUnitShouldBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(businessUnit.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE));

        // Check, that the count call also returns 1
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(1));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultBusinessUnitShouldNotBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .isArray()
            .jsonPath("$")
            .isEmpty();

        // Check, that the count call also returns 0
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(0));
    }

    @Test
    void getNonExistingBusinessUnit() {
        // Get the businessUnit
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingBusinessUnit() throws Exception {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the businessUnit
        BusinessUnit updatedBusinessUnit = businessUnitRepository.findById(businessUnit.getId()).block();
        updatedBusinessUnit.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).code(UPDATED_CODE);
        BusinessUnitDTO businessUnitDTO = businessUnitMapper.toDto(updatedBusinessUnit);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, businessUnitDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(businessUnitDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BusinessUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBusinessUnitToMatchAllProperties(updatedBusinessUnit);
    }

    @Test
    void putNonExistingBusinessUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUnit.setId(longCount.incrementAndGet());

        // Create the BusinessUnit
        BusinessUnitDTO businessUnitDTO = businessUnitMapper.toDto(businessUnit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, businessUnitDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(businessUnitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BusinessUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchBusinessUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUnit.setId(longCount.incrementAndGet());

        // Create the BusinessUnit
        BusinessUnitDTO businessUnitDTO = businessUnitMapper.toDto(businessUnit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(businessUnitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BusinessUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamBusinessUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUnit.setId(longCount.incrementAndGet());

        // Create the BusinessUnit
        BusinessUnitDTO businessUnitDTO = businessUnitMapper.toDto(businessUnit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(businessUnitDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BusinessUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBusinessUnitWithPatch() throws Exception {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the businessUnit using partial update
        BusinessUnit partialUpdatedBusinessUnit = new BusinessUnit();
        partialUpdatedBusinessUnit.setId(businessUnit.getId());

        partialUpdatedBusinessUnit.name(UPDATED_NAME).code(UPDATED_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBusinessUnit.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBusinessUnit))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BusinessUnit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBusinessUnitUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBusinessUnit, businessUnit),
            getPersistedBusinessUnit(businessUnit)
        );
    }

    @Test
    void fullUpdateBusinessUnitWithPatch() throws Exception {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the businessUnit using partial update
        BusinessUnit partialUpdatedBusinessUnit = new BusinessUnit();
        partialUpdatedBusinessUnit.setId(businessUnit.getId());

        partialUpdatedBusinessUnit.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).code(UPDATED_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBusinessUnit.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedBusinessUnit))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the BusinessUnit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBusinessUnitUpdatableFieldsEquals(partialUpdatedBusinessUnit, getPersistedBusinessUnit(partialUpdatedBusinessUnit));
    }

    @Test
    void patchNonExistingBusinessUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUnit.setId(longCount.incrementAndGet());

        // Create the BusinessUnit
        BusinessUnitDTO businessUnitDTO = businessUnitMapper.toDto(businessUnit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, businessUnitDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(businessUnitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BusinessUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchBusinessUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUnit.setId(longCount.incrementAndGet());

        // Create the BusinessUnit
        BusinessUnitDTO businessUnitDTO = businessUnitMapper.toDto(businessUnit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(businessUnitDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the BusinessUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamBusinessUnit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        businessUnit.setId(longCount.incrementAndGet());

        // Create the BusinessUnit
        BusinessUnitDTO businessUnitDTO = businessUnitMapper.toDto(businessUnit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(businessUnitDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the BusinessUnit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteBusinessUnit() {
        // Initialize the database
        insertedBusinessUnit = businessUnitRepository.save(businessUnit).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the businessUnit
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, businessUnit.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return businessUnitRepository.count().block();
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
        return businessUnitRepository.findById(businessUnit.getId()).block();
    }

    protected void assertPersistedBusinessUnitToMatchAllProperties(BusinessUnit expectedBusinessUnit) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBusinessUnitAllPropertiesEquals(expectedBusinessUnit, getPersistedBusinessUnit(expectedBusinessUnit));
        assertBusinessUnitUpdatableFieldsEquals(expectedBusinessUnit, getPersistedBusinessUnit(expectedBusinessUnit));
    }

    protected void assertPersistedBusinessUnitToMatchUpdatableProperties(BusinessUnit expectedBusinessUnit) {
        // Test fails because reactive api returns an empty object instead of null
        // assertBusinessUnitAllUpdatablePropertiesEquals(expectedBusinessUnit, getPersistedBusinessUnit(expectedBusinessUnit));
        assertBusinessUnitUpdatableFieldsEquals(expectedBusinessUnit, getPersistedBusinessUnit(expectedBusinessUnit));
    }
}
