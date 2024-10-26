package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static sn.sonatel.dsi.ins.imoc.domain.AppServiceAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.BusinessUnit;
import sn.sonatel.dsi.ins.imoc.domain.Departement;
import sn.sonatel.dsi.ins.imoc.repository.AppServiceRepository;
import sn.sonatel.dsi.ins.imoc.repository.BusinessUnitRepository;
import sn.sonatel.dsi.ins.imoc.repository.DepartementRepository;
import sn.sonatel.dsi.ins.imoc.repository.EntityManager;
import sn.sonatel.dsi.ins.imoc.service.dto.AppServiceDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.AppServiceMapper;

/**
 * Integration tests for the {@link AppServiceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AppServiceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/app-services";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AppServiceRepository appServiceRepository;

    @Autowired
    private AppServiceMapper appServiceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private AppService appService;

    private AppService insertedAppService;

    @Autowired
    private BusinessUnitRepository businessUnitRepository;

    @Autowired
    private DepartementRepository departementRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppService createEntity() {
        return new AppService().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppService createUpdatedEntity() {
        return new AppService().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(AppService.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        appService = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAppService != null) {
            appServiceRepository.delete(insertedAppService).block();
            insertedAppService = null;
        }
        deleteEntities(em);
    }

    @Test
    void createAppService() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AppService
        AppServiceDTO appServiceDTO = appServiceMapper.toDto(appService);
        var returnedAppServiceDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appServiceDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AppServiceDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AppService in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAppService = appServiceMapper.toEntity(returnedAppServiceDTO);
        assertAppServiceUpdatableFieldsEquals(returnedAppService, getPersistedAppService(returnedAppService));

        insertedAppService = returnedAppService;
    }

    @Test
    void createAppServiceWithExistingId() throws Exception {
        // Create the AppService with an existing ID
        appService.setId(1L);
        AppServiceDTO appServiceDTO = appServiceMapper.toDto(appService);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appServiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppService in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appService.setName(null);

        // Create the AppService, which fails.
        AppServiceDTO appServiceDTO = appServiceMapper.toDto(appService);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appServiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAppServices() {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        // Get all the appServiceList
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
            .value(hasItem(appService.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getAppService() {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        // Get the appService
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, appService.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(appService.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getAppServicesByIdFiltering() {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        Long id = appService.getId();

        defaultAppServiceFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAppServiceFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAppServiceFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllAppServicesByNameIsEqualToSomething() {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        // Get all the appServiceList where name equals to
        defaultAppServiceFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllAppServicesByNameIsInShouldWork() {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        // Get all the appServiceList where name in
        defaultAppServiceFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllAppServicesByNameIsNullOrNotNull() {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        // Get all the appServiceList where name is not null
        defaultAppServiceFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllAppServicesByNameContainsSomething() {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        // Get all the appServiceList where name contains
        defaultAppServiceFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllAppServicesByNameNotContainsSomething() {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        // Get all the appServiceList where name does not contain
        defaultAppServiceFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllAppServicesByDescriptionIsEqualToSomething() {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        // Get all the appServiceList where description equals to
        defaultAppServiceFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllAppServicesByDescriptionIsInShouldWork() {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        // Get all the appServiceList where description in
        defaultAppServiceFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    void getAllAppServicesByDescriptionIsNullOrNotNull() {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        // Get all the appServiceList where description is not null
        defaultAppServiceFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    void getAllAppServicesByDescriptionContainsSomething() {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        // Get all the appServiceList where description contains
        defaultAppServiceFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllAppServicesByDescriptionNotContainsSomething() {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        // Get all the appServiceList where description does not contain
        defaultAppServiceFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    void getAllAppServicesByBusinessUnitIsEqualToSomething() {
        BusinessUnit businessUnit = BusinessUnitResourceIT.createEntity();
        businessUnitRepository.save(businessUnit).block();
        Long businessUnitId = businessUnit.getId();
        appService.setBusinessUnitId(businessUnitId);
        insertedAppService = appServiceRepository.save(appService).block();
        // Get all the appServiceList where businessUnit equals to businessUnitId
        defaultAppServiceShouldBeFound("businessUnitId.equals=" + businessUnitId);

        // Get all the appServiceList where businessUnit equals to (businessUnitId + 1)
        defaultAppServiceShouldNotBeFound("businessUnitId.equals=" + (businessUnitId + 1));
    }

    @Test
    void getAllAppServicesByDepartemenIsEqualToSomething() {
        Departement departemen = DepartementResourceIT.createEntity();
        departementRepository.save(departemen).block();
        Long departemenId = departemen.getId();
        appService.setDepartemenId(departemenId);
        insertedAppService = appServiceRepository.save(appService).block();
        // Get all the appServiceList where departemen equals to departemenId
        defaultAppServiceShouldBeFound("departemenId.equals=" + departemenId);

        // Get all the appServiceList where departemen equals to (departemenId + 1)
        defaultAppServiceShouldNotBeFound("departemenId.equals=" + (departemenId + 1));
    }

    private void defaultAppServiceFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultAppServiceShouldBeFound(shouldBeFound);
        defaultAppServiceShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAppServiceShouldBeFound(String filter) {
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
            .value(hasItem(appService.getId().intValue()))
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
    private void defaultAppServiceShouldNotBeFound(String filter) {
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
    void getNonExistingAppService() {
        // Get the appService
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAppService() throws Exception {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appService
        AppService updatedAppService = appServiceRepository.findById(appService.getId()).block();
        updatedAppService.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        AppServiceDTO appServiceDTO = appServiceMapper.toDto(updatedAppService);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appServiceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appServiceDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAppServiceToMatchAllProperties(updatedAppService);
    }

    @Test
    void putNonExistingAppService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appService.setId(longCount.incrementAndGet());

        // Create the AppService
        AppServiceDTO appServiceDTO = appServiceMapper.toDto(appService);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appServiceDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appServiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAppService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appService.setId(longCount.incrementAndGet());

        // Create the AppService
        AppServiceDTO appServiceDTO = appServiceMapper.toDto(appService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appServiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAppService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appService.setId(longCount.incrementAndGet());

        // Create the AppService
        AppServiceDTO appServiceDTO = appServiceMapper.toDto(appService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appServiceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAppServiceWithPatch() throws Exception {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appService using partial update
        AppService partialUpdatedAppService = new AppService();
        partialUpdatedAppService.setId(appService.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppService.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAppService))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppService in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppServiceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAppService, appService),
            getPersistedAppService(appService)
        );
    }

    @Test
    void fullUpdateAppServiceWithPatch() throws Exception {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appService using partial update
        AppService partialUpdatedAppService = new AppService();
        partialUpdatedAppService.setId(appService.getId());

        partialUpdatedAppService.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppService.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAppService))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppService in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppServiceUpdatableFieldsEquals(partialUpdatedAppService, getPersistedAppService(partialUpdatedAppService));
    }

    @Test
    void patchNonExistingAppService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appService.setId(longCount.incrementAndGet());

        // Create the AppService
        AppServiceDTO appServiceDTO = appServiceMapper.toDto(appService);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, appServiceDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(appServiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAppService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appService.setId(longCount.incrementAndGet());

        // Create the AppService
        AppServiceDTO appServiceDTO = appServiceMapper.toDto(appService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(appServiceDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAppService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appService.setId(longCount.incrementAndGet());

        // Create the AppService
        AppServiceDTO appServiceDTO = appServiceMapper.toDto(appService);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(appServiceDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppService in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAppService() {
        // Initialize the database
        insertedAppService = appServiceRepository.save(appService).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the appService
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, appService.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return appServiceRepository.count().block();
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

    protected AppService getPersistedAppService(AppService appService) {
        return appServiceRepository.findById(appService.getId()).block();
    }

    protected void assertPersistedAppServiceToMatchAllProperties(AppService expectedAppService) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAppServiceAllPropertiesEquals(expectedAppService, getPersistedAppService(expectedAppService));
        assertAppServiceUpdatableFieldsEquals(expectedAppService, getPersistedAppService(expectedAppService));
    }

    protected void assertPersistedAppServiceToMatchUpdatableProperties(AppService expectedAppService) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAppServiceAllUpdatablePropertiesEquals(expectedAppService, getPersistedAppService(expectedAppService));
        assertAppServiceUpdatableFieldsEquals(expectedAppService, getPersistedAppService(expectedAppService));
    }
}
