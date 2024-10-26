package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static sn.sonatel.dsi.ins.imoc.domain.DfcAsserts.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import sn.sonatel.dsi.ins.imoc.IntegrationTest;
import sn.sonatel.dsi.ins.imoc.domain.Dfc;
import sn.sonatel.dsi.ins.imoc.repository.DfcRepository;
import sn.sonatel.dsi.ins.imoc.repository.EntityManager;
import sn.sonatel.dsi.ins.imoc.service.dto.DfcDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.DfcMapper;

/**
 * Integration tests for the {@link DfcResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DfcResourceIT {

    private static final String ENTITY_API_URL = "/api/dfcs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DfcRepository dfcRepository;

    @Autowired
    private DfcMapper dfcMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Dfc dfc;

    private Dfc insertedDfc;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dfc createEntity() {
        return new Dfc();
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Dfc createUpdatedEntity() {
        return new Dfc();
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Dfc.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        dfc = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedDfc != null) {
            dfcRepository.delete(insertedDfc).block();
            insertedDfc = null;
        }
        deleteEntities(em);
    }

    @Test
    void createDfc() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Dfc
        DfcDTO dfcDTO = dfcMapper.toDto(dfc);
        var returnedDfcDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dfcDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(DfcDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Dfc in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDfc = dfcMapper.toEntity(returnedDfcDTO);
        assertDfcUpdatableFieldsEquals(returnedDfc, getPersistedDfc(returnedDfc));

        insertedDfc = returnedDfc;
    }

    @Test
    void createDfcWithExistingId() throws Exception {
        // Create the Dfc with an existing ID
        dfc.setId(1L);
        DfcDTO dfcDTO = dfcMapper.toDto(dfc);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(dfcDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Dfc in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void getAllDfcs() {
        // Initialize the database
        insertedDfc = dfcRepository.save(dfc).block();

        // Get all the dfcList
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
            .value(hasItem(dfc.getId().intValue()));
    }

    @Test
    void getDfc() {
        // Initialize the database
        insertedDfc = dfcRepository.save(dfc).block();

        // Get the dfc
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, dfc.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(dfc.getId().intValue()));
    }

    @Test
    void getDfcsByIdFiltering() {
        // Initialize the database
        insertedDfc = dfcRepository.save(dfc).block();

        Long id = dfc.getId();

        defaultDfcFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDfcFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDfcFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    private void defaultDfcFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultDfcShouldBeFound(shouldBeFound);
        defaultDfcShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDfcShouldBeFound(String filter) {
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
            .value(hasItem(dfc.getId().intValue()));

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
    private void defaultDfcShouldNotBeFound(String filter) {
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
    void getNonExistingDfc() {
        // Get the dfc
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void deleteDfc() {
        // Initialize the database
        insertedDfc = dfcRepository.save(dfc).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the dfc
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, dfc.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return dfcRepository.count().block();
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

    protected Dfc getPersistedDfc(Dfc dfc) {
        return dfcRepository.findById(dfc.getId()).block();
    }

    protected void assertPersistedDfcToMatchAllProperties(Dfc expectedDfc) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDfcAllPropertiesEquals(expectedDfc, getPersistedDfc(expectedDfc));
        assertDfcUpdatableFieldsEquals(expectedDfc, getPersistedDfc(expectedDfc));
    }

    protected void assertPersistedDfcToMatchUpdatableProperties(Dfc expectedDfc) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDfcAllUpdatablePropertiesEquals(expectedDfc, getPersistedDfc(expectedDfc));
        assertDfcUpdatableFieldsEquals(expectedDfc, getPersistedDfc(expectedDfc));
    }
}
