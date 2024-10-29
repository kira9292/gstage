package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.sonatel.dsi.ins.imoc.domain.ServiceAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.Service;
import sn.sonatel.dsi.ins.imoc.repository.ServiceRepository;

/**
 * Integration tests for the {@link ServiceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ServiceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/services";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restServiceMockMvc;

    private Service service;

    private Service insertedService;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Service createEntity() {
        return new Service().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Service createUpdatedEntity() {
        return new Service().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    public void initTest() {
        service = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedService != null) {
            serviceRepository.delete(insertedService);
            insertedService = null;
        }
    }

    @Test
    @Transactional
    void createService() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Service
        var returnedService = om.readValue(
            restServiceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(service)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Service.class
        );

        // Validate the Service in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertServiceUpdatableFieldsEquals(returnedService, getPersistedService(returnedService));

        insertedService = returnedService;
    }

    @Test
    @Transactional
    void createServiceWithExistingId() throws Exception {
        // Create the Service with an existing ID
        service.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(service)))
            .andExpect(status().isBadRequest());

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        service.setName(null);

        // Create the Service, which fails.

        restServiceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(service)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllServices() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get all the serviceList
        restServiceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(service.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getService() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        // Get the service
        restServiceMockMvc
            .perform(get(ENTITY_API_URL_ID, service.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(service.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingService() throws Exception {
        // Get the service
        restServiceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingService() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the service
        Service updatedService = serviceRepository.findById(service.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedService are not directly saved in db
        em.detach(updatedService);
        updatedService.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedService.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedService))
            )
            .andExpect(status().isOk());

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedServiceToMatchAllProperties(updatedService);
    }

    @Test
    @Transactional
    void putNonExistingService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        service.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceMockMvc
            .perform(put(ENTITY_API_URL_ID, service.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(service)))
            .andExpect(status().isBadRequest());

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        service.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(service))
            )
            .andExpect(status().isBadRequest());

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        service.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(service)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateServiceWithPatch() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the service using partial update
        Service partialUpdatedService = new Service();
        partialUpdatedService.setId(service.getId());

        restServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedService.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedService))
            )
            .andExpect(status().isOk());

        // Validate the Service in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedService, service), getPersistedService(service));
    }

    @Test
    @Transactional
    void fullUpdateServiceWithPatch() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the service using partial update
        Service partialUpdatedService = new Service();
        partialUpdatedService.setId(service.getId());

        partialUpdatedService.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedService.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedService))
            )
            .andExpect(status().isOk());

        // Validate the Service in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertServiceUpdatableFieldsEquals(partialUpdatedService, getPersistedService(partialUpdatedService));
    }

    @Test
    @Transactional
    void patchNonExistingService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        service.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, service.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(service))
            )
            .andExpect(status().isBadRequest());

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        service.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(service))
            )
            .andExpect(status().isBadRequest());

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamService() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        service.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restServiceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(service)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Service in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteService() throws Exception {
        // Initialize the database
        insertedService = serviceRepository.saveAndFlush(service);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the service
        restServiceMockMvc
            .perform(delete(ENTITY_API_URL_ID, service.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return serviceRepository.count();
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

    protected Service getPersistedService(Service service) {
        return serviceRepository.findById(service.getId()).orElseThrow();
    }

    protected void assertPersistedServiceToMatchAllProperties(Service expectedService) {
        assertServiceAllPropertiesEquals(expectedService, getPersistedService(expectedService));
    }

    protected void assertPersistedServiceToMatchUpdatableProperties(Service expectedService) {
        assertServiceAllUpdatablePropertiesEquals(expectedService, getPersistedService(expectedService));
    }
}
