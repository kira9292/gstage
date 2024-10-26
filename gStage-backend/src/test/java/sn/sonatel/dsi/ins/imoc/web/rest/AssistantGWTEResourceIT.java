package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static sn.sonatel.dsi.ins.imoc.domain.AssistantGWTEAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.AssistantGWTE;
import sn.sonatel.dsi.ins.imoc.repository.AssistantGWTERepository;
import sn.sonatel.dsi.ins.imoc.repository.EntityManager;
import sn.sonatel.dsi.ins.imoc.service.dto.AssistantGWTEDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.AssistantGWTEMapper;

/**
 * Integration tests for the {@link AssistantGWTEResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AssistantGWTEResourceIT {

    private static final String DEFAULT_PHONE = "284801120";
    private static final String UPDATED_PHONE = "147814514";

    private static final String ENTITY_API_URL = "/api/assistant-gwtes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssistantGWTERepository assistantGWTERepository;

    @Autowired
    private AssistantGWTEMapper assistantGWTEMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private AssistantGWTE assistantGWTE;

    private AssistantGWTE insertedAssistantGWTE;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssistantGWTE createEntity() {
        return new AssistantGWTE().phone(DEFAULT_PHONE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssistantGWTE createUpdatedEntity() {
        return new AssistantGWTE().phone(UPDATED_PHONE);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(AssistantGWTE.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        assistantGWTE = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAssistantGWTE != null) {
            assistantGWTERepository.delete(insertedAssistantGWTE).block();
            insertedAssistantGWTE = null;
        }
        deleteEntities(em);
    }

    @Test
    void createAssistantGWTE() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AssistantGWTE
        AssistantGWTEDTO assistantGWTEDTO = assistantGWTEMapper.toDto(assistantGWTE);
        var returnedAssistantGWTEDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(assistantGWTEDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AssistantGWTEDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AssistantGWTE in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAssistantGWTE = assistantGWTEMapper.toEntity(returnedAssistantGWTEDTO);
        assertAssistantGWTEUpdatableFieldsEquals(returnedAssistantGWTE, getPersistedAssistantGWTE(returnedAssistantGWTE));

        insertedAssistantGWTE = returnedAssistantGWTE;
    }

    @Test
    void createAssistantGWTEWithExistingId() throws Exception {
        // Create the AssistantGWTE with an existing ID
        assistantGWTE.setId(1L);
        AssistantGWTEDTO assistantGWTEDTO = assistantGWTEMapper.toDto(assistantGWTE);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(assistantGWTEDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AssistantGWTE in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllAssistantGWTES() {
        // Initialize the database
        insertedAssistantGWTE = assistantGWTERepository.save(assistantGWTE).block();

        // Get all the assistantGWTEList
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
            .value(hasItem(assistantGWTE.getId().intValue()))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE));
    }

    @Test
    void getAssistantGWTE() {
        // Initialize the database
        insertedAssistantGWTE = assistantGWTERepository.save(assistantGWTE).block();

        // Get the assistantGWTE
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, assistantGWTE.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(assistantGWTE.getId().intValue()))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE));
    }

    @Test
    void getAssistantGWTESByIdFiltering() {
        // Initialize the database
        insertedAssistantGWTE = assistantGWTERepository.save(assistantGWTE).block();

        Long id = assistantGWTE.getId();

        defaultAssistantGWTEFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAssistantGWTEFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAssistantGWTEFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllAssistantGWTESByPhoneIsEqualToSomething() {
        // Initialize the database
        insertedAssistantGWTE = assistantGWTERepository.save(assistantGWTE).block();

        // Get all the assistantGWTEList where phone equals to
        defaultAssistantGWTEFiltering("phone.equals=" + DEFAULT_PHONE, "phone.equals=" + UPDATED_PHONE);
    }

    @Test
    void getAllAssistantGWTESByPhoneIsInShouldWork() {
        // Initialize the database
        insertedAssistantGWTE = assistantGWTERepository.save(assistantGWTE).block();

        // Get all the assistantGWTEList where phone in
        defaultAssistantGWTEFiltering("phone.in=" + DEFAULT_PHONE + "," + UPDATED_PHONE, "phone.in=" + UPDATED_PHONE);
    }

    @Test
    void getAllAssistantGWTESByPhoneIsNullOrNotNull() {
        // Initialize the database
        insertedAssistantGWTE = assistantGWTERepository.save(assistantGWTE).block();

        // Get all the assistantGWTEList where phone is not null
        defaultAssistantGWTEFiltering("phone.specified=true", "phone.specified=false");
    }

    @Test
    void getAllAssistantGWTESByPhoneContainsSomething() {
        // Initialize the database
        insertedAssistantGWTE = assistantGWTERepository.save(assistantGWTE).block();

        // Get all the assistantGWTEList where phone contains
        defaultAssistantGWTEFiltering("phone.contains=" + DEFAULT_PHONE, "phone.contains=" + UPDATED_PHONE);
    }

    @Test
    void getAllAssistantGWTESByPhoneNotContainsSomething() {
        // Initialize the database
        insertedAssistantGWTE = assistantGWTERepository.save(assistantGWTE).block();

        // Get all the assistantGWTEList where phone does not contain
        defaultAssistantGWTEFiltering("phone.doesNotContain=" + UPDATED_PHONE, "phone.doesNotContain=" + DEFAULT_PHONE);
    }

    private void defaultAssistantGWTEFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultAssistantGWTEShouldBeFound(shouldBeFound);
        defaultAssistantGWTEShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAssistantGWTEShouldBeFound(String filter) {
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
            .value(hasItem(assistantGWTE.getId().intValue()))
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
    private void defaultAssistantGWTEShouldNotBeFound(String filter) {
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
    void getNonExistingAssistantGWTE() {
        // Get the assistantGWTE
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAssistantGWTE() throws Exception {
        // Initialize the database
        insertedAssistantGWTE = assistantGWTERepository.save(assistantGWTE).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assistantGWTE
        AssistantGWTE updatedAssistantGWTE = assistantGWTERepository.findById(assistantGWTE.getId()).block();
        updatedAssistantGWTE.phone(UPDATED_PHONE);
        AssistantGWTEDTO assistantGWTEDTO = assistantGWTEMapper.toDto(updatedAssistantGWTE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, assistantGWTEDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(assistantGWTEDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AssistantGWTE in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAssistantGWTEToMatchAllProperties(updatedAssistantGWTE);
    }

    @Test
    void putNonExistingAssistantGWTE() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assistantGWTE.setId(longCount.incrementAndGet());

        // Create the AssistantGWTE
        AssistantGWTEDTO assistantGWTEDTO = assistantGWTEMapper.toDto(assistantGWTE);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, assistantGWTEDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(assistantGWTEDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AssistantGWTE in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAssistantGWTE() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assistantGWTE.setId(longCount.incrementAndGet());

        // Create the AssistantGWTE
        AssistantGWTEDTO assistantGWTEDTO = assistantGWTEMapper.toDto(assistantGWTE);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(assistantGWTEDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AssistantGWTE in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAssistantGWTE() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assistantGWTE.setId(longCount.incrementAndGet());

        // Create the AssistantGWTE
        AssistantGWTEDTO assistantGWTEDTO = assistantGWTEMapper.toDto(assistantGWTE);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(assistantGWTEDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AssistantGWTE in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAssistantGWTEWithPatch() throws Exception {
        // Initialize the database
        insertedAssistantGWTE = assistantGWTERepository.save(assistantGWTE).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assistantGWTE using partial update
        AssistantGWTE partialUpdatedAssistantGWTE = new AssistantGWTE();
        partialUpdatedAssistantGWTE.setId(assistantGWTE.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAssistantGWTE.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAssistantGWTE))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AssistantGWTE in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssistantGWTEUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAssistantGWTE, assistantGWTE),
            getPersistedAssistantGWTE(assistantGWTE)
        );
    }

    @Test
    void fullUpdateAssistantGWTEWithPatch() throws Exception {
        // Initialize the database
        insertedAssistantGWTE = assistantGWTERepository.save(assistantGWTE).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assistantGWTE using partial update
        AssistantGWTE partialUpdatedAssistantGWTE = new AssistantGWTE();
        partialUpdatedAssistantGWTE.setId(assistantGWTE.getId());

        partialUpdatedAssistantGWTE.phone(UPDATED_PHONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAssistantGWTE.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAssistantGWTE))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AssistantGWTE in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssistantGWTEUpdatableFieldsEquals(partialUpdatedAssistantGWTE, getPersistedAssistantGWTE(partialUpdatedAssistantGWTE));
    }

    @Test
    void patchNonExistingAssistantGWTE() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assistantGWTE.setId(longCount.incrementAndGet());

        // Create the AssistantGWTE
        AssistantGWTEDTO assistantGWTEDTO = assistantGWTEMapper.toDto(assistantGWTE);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, assistantGWTEDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(assistantGWTEDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AssistantGWTE in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAssistantGWTE() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assistantGWTE.setId(longCount.incrementAndGet());

        // Create the AssistantGWTE
        AssistantGWTEDTO assistantGWTEDTO = assistantGWTEMapper.toDto(assistantGWTE);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(assistantGWTEDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AssistantGWTE in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAssistantGWTE() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assistantGWTE.setId(longCount.incrementAndGet());

        // Create the AssistantGWTE
        AssistantGWTEDTO assistantGWTEDTO = assistantGWTEMapper.toDto(assistantGWTE);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(assistantGWTEDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AssistantGWTE in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAssistantGWTE() {
        // Initialize the database
        insertedAssistantGWTE = assistantGWTERepository.save(assistantGWTE).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the assistantGWTE
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, assistantGWTE.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return assistantGWTERepository.count().block();
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

    protected AssistantGWTE getPersistedAssistantGWTE(AssistantGWTE assistantGWTE) {
        return assistantGWTERepository.findById(assistantGWTE.getId()).block();
    }

    protected void assertPersistedAssistantGWTEToMatchAllProperties(AssistantGWTE expectedAssistantGWTE) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAssistantGWTEAllPropertiesEquals(expectedAssistantGWTE, getPersistedAssistantGWTE(expectedAssistantGWTE));
        assertAssistantGWTEUpdatableFieldsEquals(expectedAssistantGWTE, getPersistedAssistantGWTE(expectedAssistantGWTE));
    }

    protected void assertPersistedAssistantGWTEToMatchUpdatableProperties(AssistantGWTE expectedAssistantGWTE) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAssistantGWTEAllUpdatablePropertiesEquals(expectedAssistantGWTE, getPersistedAssistantGWTE(expectedAssistantGWTE));
        assertAssistantGWTEUpdatableFieldsEquals(expectedAssistantGWTE, getPersistedAssistantGWTE(expectedAssistantGWTE));
    }
}
