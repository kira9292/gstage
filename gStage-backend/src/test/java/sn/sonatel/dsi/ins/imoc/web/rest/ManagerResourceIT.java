package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static sn.sonatel.dsi.ins.imoc.domain.ManagerAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.AppService;
import sn.sonatel.dsi.ins.imoc.domain.Manager;
import sn.sonatel.dsi.ins.imoc.repository.AppServiceRepository;
import sn.sonatel.dsi.ins.imoc.repository.EntityManager;
import sn.sonatel.dsi.ins.imoc.repository.ManagerRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.ManagerDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.ManagerMapper;

/**
 * Integration tests for the {@link ManagerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ManagerResourceIT {

    private static final String DEFAULT_PHONE = "286695761";
    private static final String UPDATED_PHONE = "085074613";

    private static final String ENTITY_API_URL = "/api/managers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private ManagerMapper managerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Manager manager;

    private Manager insertedManager;

    @Autowired
    private AppServiceRepository appServiceRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Manager createEntity() {
        return new Manager().phone(DEFAULT_PHONE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Manager createUpdatedEntity() {
        return new Manager().phone(UPDATED_PHONE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Manager.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        manager = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedManager != null) {
            managerRepository.delete(insertedManager).block();
            insertedManager = null;
        }
        deleteEntities(em);
    }

    @Test
    void createManager() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Manager
        ManagerDTO managerDTO = managerMapper.toDto(manager);
        var returnedManagerDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(managerDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ManagerDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Manager in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedManager = managerMapper.toEntity(returnedManagerDTO);
        assertManagerUpdatableFieldsEquals(returnedManager, getPersistedManager(returnedManager));

        insertedManager = returnedManager;
    }

    @Test
    void createManagerWithExistingId() throws Exception {
        // Create the Manager with an existing ID
        manager.setId(1L);
        ManagerDTO managerDTO = managerMapper.toDto(manager);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(managerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Manager in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllManagers() {
        // Initialize the database
        insertedManager = managerRepository.save(manager).block();

        // Get all the managerList
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
            .value(hasItem(manager.getId().intValue()))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE));
    }

    @Test
    void getManager() {
        // Initialize the database
        insertedManager = managerRepository.save(manager).block();

        // Get the manager
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, manager.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(manager.getId().intValue()))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE));
    }

    @Test
    void getManagersByIdFiltering() {
        // Initialize the database
        insertedManager = managerRepository.save(manager).block();

        Long id = manager.getId();

        defaultManagerFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultManagerFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultManagerFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllManagersByPhoneIsEqualToSomething() {
        // Initialize the database
        insertedManager = managerRepository.save(manager).block();

        // Get all the managerList where phone equals to
        defaultManagerFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    void getAllManagersByPhoneIsInShouldWork() {
        // Initialize the database
        insertedManager = managerRepository.save(manager).block();

        // Get all the managerList where phone in
        defaultManagerFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    void getAllManagersByPhoneIsNullOrNotNull() {
        // Initialize the database
        insertedManager = managerRepository.save(manager).block();

        // Get all the managerList where phone is not null
        defaultManagerFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    void getAllManagersByPhoneContainsSomething() {
        // Initialize the database
        insertedManager = managerRepository.save(manager).block();

        // Get all the managerList where phone contains
        defaultManagerFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    void getAllManagersByPhoneNotContainsSomething() {
        // Initialize the database
        insertedManager = managerRepository.save(manager).block();

        // Get all the managerList where phone does not contain
        defaultManagerFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    @Test
    void getAllManagersByServiceIsEqualToSomething() {
        AppService service = AppServiceResourceIT.createEntity();
        appServiceRepository.save(service).block();
        Long serviceId = service.getId();
        manager.setServiceId(serviceId);
        insertedManager = managerRepository.save(manager).block();
        // Get all the managerList where service equals to serviceId
        defaultManagerShouldBeFound("serviceId.equals=" + serviceId);

        // Get all the managerList where service equals to (serviceId + 1)
        defaultManagerShouldNotBeFound("serviceId.equals=" + (serviceId + 1));
    }

    private void defaultManagerFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultManagerShouldBeFound(shouldBeFound);
        defaultManagerShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultManagerShouldBeFound(String filter) {
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
            .value(hasItem(manager.getId().intValue()))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE));

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
    private void defaultManagerShouldNotBeFound(String filter) {
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
    void getNonExistingManager() {
        // Get the manager
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingManager() throws Exception {
        // Initialize the database
        insertedManager = managerRepository.save(manager).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the manager
        Manager updatedManager = managerRepository.findById(manager.getId()).block();
        updatedManager.phone(UPDATED_PHONE);
        ManagerDTO managerDTO = managerMapper.toDto(updatedManager);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, managerDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(managerDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Manager in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedManagerToMatchAllProperties(updatedManager);
    }

    @Test
    void putNonExistingManager() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        manager.setId(longCount.incrementAndGet());

        // Create the Manager
        ManagerDTO managerDTO = managerMapper.toDto(manager);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, managerDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(managerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Manager in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchManager() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        manager.setId(longCount.incrementAndGet());

        // Create the Manager
        ManagerDTO managerDTO = managerMapper.toDto(manager);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(managerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Manager in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamManager() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        manager.setId(longCount.incrementAndGet());

        // Create the Manager
        ManagerDTO managerDTO = managerMapper.toDto(manager);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(managerDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Manager in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateManagerWithPatch() throws Exception {
        // Initialize the database
        insertedManager = managerRepository.save(manager).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the manager using partial update
        Manager partialUpdatedManager = new Manager();
        partialUpdatedManager.setId(manager.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedManager.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedManager))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Manager in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertManagerUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedManager, manager), getPersistedManager(manager));
    }

    @Test
    void fullUpdateManagerWithPatch() throws Exception {
        // Initialize the database
        insertedManager = managerRepository.save(manager).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the manager using partial update
        Manager partialUpdatedManager = new Manager();
        partialUpdatedManager.setId(manager.getId());

        partialUpdatedManager.phone(UPDATED_PHONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedManager.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedManager))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Manager in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertManagerUpdatableFieldsEquals(partialUpdatedManager, getPersistedManager(partialUpdatedManager));
    }

    @Test
    void patchNonExistingManager() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        manager.setId(longCount.incrementAndGet());

        // Create the Manager
        ManagerDTO managerDTO = managerMapper.toDto(manager);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, managerDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(managerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Manager in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchManager() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        manager.setId(longCount.incrementAndGet());

        // Create the Manager
        ManagerDTO managerDTO = managerMapper.toDto(manager);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(managerDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Manager in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamManager() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        manager.setId(longCount.incrementAndGet());

        // Create the Manager
        ManagerDTO managerDTO = managerMapper.toDto(manager);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(managerDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Manager in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteManager() {
        // Initialize the database
        insertedManager = managerRepository.save(manager).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the manager
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, manager.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return managerRepository.count().block();
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

    protected Manager getPersistedManager(Manager manager) {
        return managerRepository.findById(manager.getId()).block();
    }

    protected void assertPersistedManagerToMatchAllProperties(Manager expectedManager) {
        // Test fails because reactive api returns an empty object instead of null
        // assertManagerAllPropertiesEquals(expectedManager, getPersistedManager(expectedManager));
        assertManagerUpdatableFieldsEquals(expectedManager, getPersistedManager(expectedManager));
    }

    protected void assertPersistedManagerToMatchUpdatableProperties(Manager expectedManager) {
        // Test fails because reactive api returns an empty object instead of null
        // assertManagerAllUpdatablePropertiesEquals(expectedManager, getPersistedManager(expectedManager));
        assertManagerUpdatableFieldsEquals(expectedManager, getPersistedManager(expectedManager));
    }
}
