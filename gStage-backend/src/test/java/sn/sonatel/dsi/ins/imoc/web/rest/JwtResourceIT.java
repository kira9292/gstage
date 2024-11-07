package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.sonatel.dsi.ins.imoc.domain.JwtAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.Jwt;
import sn.sonatel.dsi.ins.imoc.repository.JwtRepository;

/**
 * Integration tests for the {@link JwtResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class JwtResourceIT {

    private static final Boolean DEFAULT_DESACTIVE = false;
    private static final Boolean UPDATED_DESACTIVE = true;

    private static final Boolean DEFAULT_EXPIRE = false;
    private static final Boolean UPDATED_EXPIRE = true;

    private static final String DEFAULT_VALEUR = "AAAAAAAAAA";
    private static final String UPDATED_VALEUR = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/jwts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private JwtRepository jwtRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restJwtMockMvc;

    private Jwt jwt;

    private Jwt insertedJwt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Jwt createEntity() {
        return new Jwt().desactive(DEFAULT_DESACTIVE).expire(DEFAULT_EXPIRE).valeur(DEFAULT_VALEUR);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Jwt createUpdatedEntity() {
        return new Jwt().desactive(UPDATED_DESACTIVE).expire(UPDATED_EXPIRE).valeur(UPDATED_VALEUR);
    }

    @BeforeEach
    public void initTest() {
        jwt = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedJwt != null) {
            jwtRepository.delete(insertedJwt);
            insertedJwt = null;
        }
    }

    @Test
    @Transactional
    void createJwt() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Jwt
        var returnedJwt = om.readValue(
            restJwtMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(jwt)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Jwt.class
        );

        // Validate the Jwt in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertJwtUpdatableFieldsEquals(returnedJwt, getPersistedJwt(returnedJwt));

        insertedJwt = returnedJwt;
    }

    @Test
    @Transactional
    void createJwtWithExistingId() throws Exception {
        // Create the Jwt with an existing ID
        jwt.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restJwtMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(jwt)))
            .andExpect(status().isBadRequest());

        // Validate the Jwt in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllJwts() throws Exception {
        // Initialize the database
        insertedJwt = jwtRepository.saveAndFlush(jwt);

        // Get all the jwtList
        restJwtMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jwt.getId().intValue())))
            .andExpect(jsonPath("$.[*].desactive").value(hasItem(DEFAULT_DESACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].expire").value(hasItem(DEFAULT_EXPIRE.booleanValue())))
            .andExpect(jsonPath("$.[*].valeur").value(hasItem(DEFAULT_VALEUR)));
    }

    @Test
    @Transactional
    void getJwt() throws Exception {
        // Initialize the database
        insertedJwt = jwtRepository.saveAndFlush(jwt);

        // Get the jwt
        restJwtMockMvc
            .perform(get(ENTITY_API_URL_ID, jwt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(jwt.getId().intValue()))
            .andExpect(jsonPath("$.desactive").value(DEFAULT_DESACTIVE.booleanValue()))
            .andExpect(jsonPath("$.expire").value(DEFAULT_EXPIRE.booleanValue()))
            .andExpect(jsonPath("$.valeur").value(DEFAULT_VALEUR));
    }

    @Test
    @Transactional
    void getNonExistingJwt() throws Exception {
        // Get the jwt
        restJwtMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingJwt() throws Exception {
        // Initialize the database
        insertedJwt = jwtRepository.saveAndFlush(jwt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the jwt
        Jwt updatedJwt = jwtRepository.findById(jwt.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedJwt are not directly saved in db
        em.detach(updatedJwt);
        updatedJwt.desactive(UPDATED_DESACTIVE).expire(UPDATED_EXPIRE).valeur(UPDATED_VALEUR);

        restJwtMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedJwt.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(updatedJwt))
            )
            .andExpect(status().isOk());

        // Validate the Jwt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedJwtToMatchAllProperties(updatedJwt);
    }

    @Test
    @Transactional
    void putNonExistingJwt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        jwt.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJwtMockMvc
            .perform(put(ENTITY_API_URL_ID, jwt.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(jwt)))
            .andExpect(status().isBadRequest());

        // Validate the Jwt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchJwt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        jwt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJwtMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(jwt))
            )
            .andExpect(status().isBadRequest());

        // Validate the Jwt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamJwt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        jwt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJwtMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(jwt)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Jwt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateJwtWithPatch() throws Exception {
        // Initialize the database
        insertedJwt = jwtRepository.saveAndFlush(jwt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the jwt using partial update
        Jwt partialUpdatedJwt = new Jwt();
        partialUpdatedJwt.setId(jwt.getId());

        partialUpdatedJwt.desactive(UPDATED_DESACTIVE).expire(UPDATED_EXPIRE).valeur(UPDATED_VALEUR);

        restJwtMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJwt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedJwt))
            )
            .andExpect(status().isOk());

        // Validate the Jwt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJwtUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedJwt, jwt), getPersistedJwt(jwt));
    }

    @Test
    @Transactional
    void fullUpdateJwtWithPatch() throws Exception {
        // Initialize the database
        insertedJwt = jwtRepository.saveAndFlush(jwt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the jwt using partial update
        Jwt partialUpdatedJwt = new Jwt();
        partialUpdatedJwt.setId(jwt.getId());

        partialUpdatedJwt.desactive(UPDATED_DESACTIVE).expire(UPDATED_EXPIRE).valeur(UPDATED_VALEUR);

        restJwtMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedJwt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedJwt))
            )
            .andExpect(status().isOk());

        // Validate the Jwt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJwtUpdatableFieldsEquals(partialUpdatedJwt, getPersistedJwt(partialUpdatedJwt));
    }

    @Test
    @Transactional
    void patchNonExistingJwt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        jwt.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJwtMockMvc
            .perform(patch(ENTITY_API_URL_ID, jwt.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(jwt)))
            .andExpect(status().isBadRequest());

        // Validate the Jwt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchJwt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        jwt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJwtMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(jwt))
            )
            .andExpect(status().isBadRequest());

        // Validate the Jwt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamJwt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        jwt.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restJwtMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(jwt)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Jwt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteJwt() throws Exception {
        // Initialize the database
        insertedJwt = jwtRepository.saveAndFlush(jwt);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the jwt
        restJwtMockMvc.perform(delete(ENTITY_API_URL_ID, jwt.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return jwtRepository.count();
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

    protected Jwt getPersistedJwt(Jwt jwt) {
        return jwtRepository.findById(jwt.getId()).orElseThrow();
    }

    protected void assertPersistedJwtToMatchAllProperties(Jwt expectedJwt) {
        assertJwtAllPropertiesEquals(expectedJwt, getPersistedJwt(expectedJwt));
    }

    protected void assertPersistedJwtToMatchUpdatableProperties(Jwt expectedJwt) {
        assertJwtAllUpdatablePropertiesEquals(expectedJwt, getPersistedJwt(expectedJwt));
    }
}
