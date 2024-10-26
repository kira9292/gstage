package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static sn.sonatel.dsi.ins.imoc.domain.DepartementAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.Departement;
import sn.sonatel.dsi.ins.imoc.repository.DepartementRepository;
import sn.sonatel.dsi.ins.imoc.repository.EntityManager;
import sn.sonatel.dsi.ins.imoc.service.dto.DepartementDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.DepartementMapper;

/**
 * Integration tests for the {@link DepartementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DepartementResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/departements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DepartementRepository departementRepository;

    @Autowired
    private DepartementMapper departementMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Departement departement;

    private Departement insertedDepartement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Departement createEntity() {
        return new Departement().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Departement createUpdatedEntity() {
        return new Departement().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Departement.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        departement = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedDepartement != null) {
            departementRepository.delete(insertedDepartement).block();
            insertedDepartement = null;
        }
        deleteEntities(em);
    }

    @Test
    void createDepartement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Departement
        DepartementDTO departementDTO = departementMapper.toDto(departement);
        var returnedDepartementDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departementDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DepartementDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Departement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDepartement = departementMapper.toEntity(returnedDepartementDTO);
        assertDepartementUpdatableFieldsEquals(returnedDepartement, getPersistedDepartement(returnedDepartement));

        insertedDepartement = returnedDepartement;
    }

    @Test
    void createDepartementWithExistingId() throws Exception {
        // Create the Departement with an existing ID
        departement.setId(1L);
        DepartementDTO departementDTO = departementMapper.toDto(departement);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Departement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        departement.setName(null);

        // Create the Departement, which fails.
        DepartementDTO departementDTO = departementMapper.toDto(departement);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllDepartements() {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        // Get all the departementList
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
            .value(hasItem(departement.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getDepartement() {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        // Get the departement
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, departement.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(departement.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getDepartementsByIdFiltering() {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        Long id = departement.getId();

        defaultDepartementFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDepartementFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDepartementFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllDepartementsByNameIsEqualToSomething() {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        // Get all the departementList where name equals to
        defaultDepartementFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllDepartementsByNameIsInShouldWork() {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        // Get all the departementList where name in
        defaultDepartementFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllDepartementsByNameIsNullOrNotNull() {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        // Get all the departementList where name is not null
        defaultDepartementFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllDepartementsByNameContainsSomething() {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        // Get all the departementList where name contains
        defaultDepartementFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllDepartementsByNameNotContainsSomething() {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        // Get all the departementList where name does not contain
        defaultDepartementFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllDepartementsByDescriptionIsEqualToSomething() {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        // Get all the departementList where description equals to
        defaultDepartementFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllDepartementsByDescriptionIsInShouldWork() {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        // Get all the departementList where description in
        defaultDepartementFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    void getAllDepartementsByDescriptionIsNullOrNotNull() {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        // Get all the departementList where description is not null
        defaultDepartementFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    void getAllDepartementsByDescriptionContainsSomething() {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        // Get all the departementList where description contains
        defaultDepartementFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllDepartementsByDescriptionNotContainsSomething() {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        // Get all the departementList where description does not contain
        defaultDepartementFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    private void defaultDepartementFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultDepartementShouldBeFound(shouldBeFound);
        defaultDepartementShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDepartementShouldBeFound(String filter) {
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
            .value(hasItem(departement.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));

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
    private void defaultDepartementShouldNotBeFound(String filter) {
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
    void getNonExistingDepartement() {
        // Get the departement
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDepartement() throws Exception {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the departement
        Departement updatedDepartement = departementRepository.findById(departement.getId()).block();
        updatedDepartement.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        DepartementDTO departementDTO = departementMapper.toDto(updatedDepartement);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, departementDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departementDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Departement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDepartementToMatchAllProperties(updatedDepartement);
    }

    @Test
    void putNonExistingDepartement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        departement.setId(longCount.incrementAndGet());

        // Create the Departement
        DepartementDTO departementDTO = departementMapper.toDto(departement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, departementDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Departement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDepartement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        departement.setId(longCount.incrementAndGet());

        // Create the Departement
        DepartementDTO departementDTO = departementMapper.toDto(departement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Departement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDepartement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        departement.setId(longCount.incrementAndGet());

        // Create the Departement
        DepartementDTO departementDTO = departementMapper.toDto(departement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departementDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Departement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDepartementWithPatch() throws Exception {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the departement using partial update
        Departement partialUpdatedDepartement = new Departement();
        partialUpdatedDepartement.setId(departement.getId());

        partialUpdatedDepartement.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDepartement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDepartement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Departement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDepartementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDepartement, departement),
            getPersistedDepartement(departement)
        );
    }

    @Test
    void fullUpdateDepartementWithPatch() throws Exception {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the departement using partial update
        Departement partialUpdatedDepartement = new Departement();
        partialUpdatedDepartement.setId(departement.getId());

        partialUpdatedDepartement.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDepartement.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDepartement))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Departement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDepartementUpdatableFieldsEquals(partialUpdatedDepartement, getPersistedDepartement(partialUpdatedDepartement));
    }

    @Test
    void patchNonExistingDepartement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        departement.setId(longCount.incrementAndGet());

        // Create the Departement
        DepartementDTO departementDTO = departementMapper.toDto(departement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, departementDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(departementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Departement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDepartement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        departement.setId(longCount.incrementAndGet());

        // Create the Departement
        DepartementDTO departementDTO = departementMapper.toDto(departement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(departementDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Departement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDepartement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        departement.setId(longCount.incrementAndGet());

        // Create the Departement
        DepartementDTO departementDTO = departementMapper.toDto(departement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(departementDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Departement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDepartement() {
        // Initialize the database
        insertedDepartement = departementRepository.save(departement).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the departement
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, departement.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return departementRepository.count().block();
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

    protected Departement getPersistedDepartement(Departement departement) {
        return departementRepository.findById(departement.getId()).block();
    }

    protected void assertPersistedDepartementToMatchAllProperties(Departement expectedDepartement) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDepartementAllPropertiesEquals(expectedDepartement, getPersistedDepartement(expectedDepartement));
        assertDepartementUpdatableFieldsEquals(expectedDepartement, getPersistedDepartement(expectedDepartement));
    }

    protected void assertPersistedDepartementToMatchUpdatableProperties(Departement expectedDepartement) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDepartementAllUpdatablePropertiesEquals(expectedDepartement, getPersistedDepartement(expectedDepartement));
        assertDepartementUpdatableFieldsEquals(expectedDepartement, getPersistedDepartement(expectedDepartement));
    }
}
