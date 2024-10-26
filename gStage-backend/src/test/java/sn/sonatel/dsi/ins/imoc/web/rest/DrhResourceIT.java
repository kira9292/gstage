package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static sn.sonatel.dsi.ins.imoc.domain.DrhAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.Drh;
import sn.sonatel.dsi.ins.imoc.repository.DrhRepository;
import sn.sonatel.dsi.ins.imoc.repository.EntityManager;
import sn.sonatel.dsi.ins.imoc.service.dto.DrhDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.DrhMapper;

/**
 * Integration tests for the {@link DrhResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DrhResourceIT {

    private static final String DEFAULT_PHONE = "508648369";
    private static final String UPDATED_PHONE = "556645433";

    private static final String ENTITY_API_URL = "/api/drhs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DrhRepository drhRepository;

    @Autowired
    private DrhMapper drhMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Drh drh;

    private Drh insertedDrh;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Drh createEntity() {
        return new Drh().phone(DEFAULT_PHONE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Drh createUpdatedEntity() {
        return new Drh().phone(UPDATED_PHONE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Drh.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        drh = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedDrh != null) {
            drhRepository.delete(insertedDrh).block();
            insertedDrh = null;
        }
        deleteEntities(em);
    }

    @Test
    void createDrh() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Drh
        DrhDTO drhDTO = drhMapper.toDto(drh);
        var returnedDrhDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(drhDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DrhDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Drh in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDrh = drhMapper.toEntity(returnedDrhDTO);
        assertDrhUpdatableFieldsEquals(returnedDrh, getPersistedDrh(returnedDrh));

        insertedDrh = returnedDrh;
    }

    @Test
    void createDrhWithExistingId() throws Exception {
        // Create the Drh with an existing ID
        drh.setId(1L);
        DrhDTO drhDTO = drhMapper.toDto(drh);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(drhDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Drh in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllDrhs() {
        // Initialize the database
        insertedDrh = drhRepository.save(drh).block();

        // Get all the drhList
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
            .value(hasItem(drh.getId().intValue()))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE));
    }

    @Test
    void getDrh() {
        // Initialize the database
        insertedDrh = drhRepository.save(drh).block();

        // Get the drh
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, drh.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(drh.getId().intValue()))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE));
    }

    @Test
    void getDrhsByIdFiltering() {
        // Initialize the database
        insertedDrh = drhRepository.save(drh).block();

        Long id = drh.getId();

        defaultDrhFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDrhFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDrhFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllDrhsByPhoneIsEqualToSomething() {
        // Initialize the database
        insertedDrh = drhRepository.save(drh).block();

        // Get all the drhList where phone equals to
        defaultDrhFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    void getAllDrhsByPhoneIsInShouldWork() {
        // Initialize the database
        insertedDrh = drhRepository.save(drh).block();

        // Get all the drhList where phone in
        defaultDrhFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    void getAllDrhsByPhoneIsNullOrNotNull() {
        // Initialize the database
        insertedDrh = drhRepository.save(drh).block();

        // Get all the drhList where phone is not null
        defaultDrhFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    void getAllDrhsByPhoneContainsSomething() {
        // Initialize the database
        insertedDrh = drhRepository.save(drh).block();

        // Get all the drhList where phone contains
        defaultDrhFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    void getAllDrhsByPhoneNotContainsSomething() {
        // Initialize the database
        insertedDrh = drhRepository.save(drh).block();

        // Get all the drhList where phone does not contain
        defaultDrhFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    private void defaultDrhFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultDrhShouldBeFound(shouldBeFound);
        defaultDrhShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDrhShouldBeFound(String filter) {
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
            .value(hasItem(drh.getId().intValue()))
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
    private void defaultDrhShouldNotBeFound(String filter) {
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
    void getNonExistingDrh() {
        // Get the drh
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDrh() throws Exception {
        // Initialize the database
        insertedDrh = drhRepository.save(drh).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the drh
        Drh updatedDrh = drhRepository.findById(drh.getId()).block();
        updatedDrh.phone(UPDATED_PHONE);
        DrhDTO drhDTO = drhMapper.toDto(updatedDrh);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, drhDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(drhDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Drh in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDrhToMatchAllProperties(updatedDrh);
    }

    @Test
    void putNonExistingDrh() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        drh.setId(longCount.incrementAndGet());

        // Create the Drh
        DrhDTO drhDTO = drhMapper.toDto(drh);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, drhDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(drhDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Drh in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDrh() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        drh.setId(longCount.incrementAndGet());

        // Create the Drh
        DrhDTO drhDTO = drhMapper.toDto(drh);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(drhDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Drh in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDrh() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        drh.setId(longCount.incrementAndGet());

        // Create the Drh
        DrhDTO drhDTO = drhMapper.toDto(drh);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(drhDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Drh in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDrhWithPatch() throws Exception {
        // Initialize the database
        insertedDrh = drhRepository.save(drh).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the drh using partial update
        Drh partialUpdatedDrh = new Drh();
        partialUpdatedDrh.setId(drh.getId());

        partialUpdatedDrh.phone(UPDATED_PHONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDrh.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDrh))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Drh in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDrhUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedDrh, drh), getPersistedDrh(drh));
    }

    @Test
    void fullUpdateDrhWithPatch() throws Exception {
        // Initialize the database
        insertedDrh = drhRepository.save(drh).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the drh using partial update
        Drh partialUpdatedDrh = new Drh();
        partialUpdatedDrh.setId(drh.getId());

        partialUpdatedDrh.phone(UPDATED_PHONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDrh.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDrh))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Drh in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDrhUpdatableFieldsEquals(partialUpdatedDrh, getPersistedDrh(partialUpdatedDrh));
    }

    @Test
    void patchNonExistingDrh() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        drh.setId(longCount.incrementAndGet());

        // Create the Drh
        DrhDTO drhDTO = drhMapper.toDto(drh);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, drhDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(drhDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Drh in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDrh() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        drh.setId(longCount.incrementAndGet());

        // Create the Drh
        DrhDTO drhDTO = drhMapper.toDto(drh);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(drhDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Drh in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDrh() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        drh.setId(longCount.incrementAndGet());

        // Create the Drh
        DrhDTO drhDTO = drhMapper.toDto(drh);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(drhDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Drh in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDrh() {
        // Initialize the database
        insertedDrh = drhRepository.save(drh).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the drh
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, drh.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return drhRepository.count().block();
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

    protected Drh getPersistedDrh(Drh drh) {
        return drhRepository.findById(drh.getId()).block();
    }

    protected void assertPersistedDrhToMatchAllProperties(Drh expectedDrh) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDrhAllPropertiesEquals(expectedDrh, getPersistedDrh(expectedDrh));
        assertDrhUpdatableFieldsEquals(expectedDrh, getPersistedDrh(expectedDrh));
    }

    protected void assertPersistedDrhToMatchUpdatableProperties(Drh expectedDrh) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDrhAllUpdatablePropertiesEquals(expectedDrh, getPersistedDrh(expectedDrh));
        assertDrhUpdatableFieldsEquals(expectedDrh, getPersistedDrh(expectedDrh));
    }
}
