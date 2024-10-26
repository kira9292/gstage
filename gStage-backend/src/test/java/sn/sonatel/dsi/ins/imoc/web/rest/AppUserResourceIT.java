package sn.sonatel.dsi.ins.imoc.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static sn.sonatel.dsi.ins.imoc.domain.AppUserAsserts.*;
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
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.repository.AppUserRepository;
import sn.sonatel.dsi.ins.imoc.repository.EntityManager;
import sn.sonatel.dsi.ins.imoc.service.dto.AppUserDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.AppUserMapper;

/**
 * Integration tests for the {@link AppUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AppUserResourceIT {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = ".Ay@}y.A";
    private static final String UPDATED_EMAIL = "O*LG{@s.Q6BX";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/app-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private AppUser appUser;

    private AppUser insertedAppUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createEntity() {
        return new AppUser()
            .username(DEFAULT_USERNAME)
            .email(DEFAULT_EMAIL)
            .password(DEFAULT_PASSWORD)
            .name(DEFAULT_NAME)
            .firstName(DEFAULT_FIRST_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createUpdatedEntity() {
        return new AppUser()
            .username(UPDATED_USERNAME)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .name(UPDATED_NAME)
            .firstName(UPDATED_FIRST_NAME);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(AppUser.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        appUser = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAppUser != null) {
            appUserRepository.delete(insertedAppUser).block();
            insertedAppUser = null;
        }
        deleteEntities(em);
    }

    @Test
    void createAppUser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);
        var returnedAppUserDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(AppUserDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the AppUser in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAppUser = appUserMapper.toEntity(returnedAppUserDTO);
        assertAppUserUpdatableFieldsEquals(returnedAppUser, getPersistedAppUser(returnedAppUser));

        insertedAppUser = returnedAppUser;
    }

    @Test
    void createAppUserWithExistingId() throws Exception {
        // Create the AppUser with an existing ID
        appUser.setId(1L);
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkUsernameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appUser.setUsername(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appUser.setEmail(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPasswordIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appUser.setPassword(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appUser.setName(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        appUser.setFirstName(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllAppUsers() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList
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
            .value(hasItem(appUser.getId().intValue()))
            .jsonPath("$.[*].username")
            .value(hasItem(DEFAULT_USERNAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].password")
            .value(hasItem(DEFAULT_PASSWORD))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME));
    }

    @Test
    void getAppUser() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get the appUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, appUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(appUser.getId().intValue()))
            .jsonPath("$.username")
            .value(is(DEFAULT_USERNAME))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.password")
            .value(is(DEFAULT_PASSWORD))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME));
    }

    @Test
    void getAppUsersByIdFiltering() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        Long id = appUser.getId();

        defaultAppUserFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAppUserFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAppUserFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllAppUsersByUsernameIsEqualToSomething() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where username equals to
        defaultAppUserFiltering("username.equals=" + DEFAULT_USERNAME, "username.equals=" + UPDATED_USERNAME);
    }

    @Test
    void getAllAppUsersByUsernameIsInShouldWork() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where username in
        defaultAppUserFiltering("username.in=" + DEFAULT_USERNAME + "," + UPDATED_USERNAME, "username.in=" + UPDATED_USERNAME);
    }

    @Test
    void getAllAppUsersByUsernameIsNullOrNotNull() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where username is not null
        defaultAppUserFiltering("username.specified=true", "username.specified=false");
    }

    @Test
    void getAllAppUsersByUsernameContainsSomething() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where username contains
        defaultAppUserFiltering("username.contains=" + DEFAULT_USERNAME, "username.contains=" + UPDATED_USERNAME);
    }

    @Test
    void getAllAppUsersByUsernameNotContainsSomething() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where username does not contain
        defaultAppUserFiltering("username.doesNotContain=" + UPDATED_USERNAME, "username.doesNotContain=" + DEFAULT_USERNAME);
    }

    @Test
    void getAllAppUsersByEmailIsEqualToSomething() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where email equals to
        defaultAppUserFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    void getAllAppUsersByEmailIsInShouldWork() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where email in
        defaultAppUserFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    void getAllAppUsersByEmailIsNullOrNotNull() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where email is not null
        defaultAppUserFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    void getAllAppUsersByEmailContainsSomething() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where email contains
        defaultAppUserFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    void getAllAppUsersByEmailNotContainsSomething() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where email does not contain
        defaultAppUserFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    void getAllAppUsersByPasswordIsEqualToSomething() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where password equals to
        defaultAppUserFiltering("password.equals=" + DEFAULT_PASSWORD, "password.equals=" + UPDATED_PASSWORD);
    }

    @Test
    void getAllAppUsersByPasswordIsInShouldWork() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where password in
        defaultAppUserFiltering("password.in=" + DEFAULT_PASSWORD + "," + UPDATED_PASSWORD, "password.in=" + UPDATED_PASSWORD);
    }

    @Test
    void getAllAppUsersByPasswordIsNullOrNotNull() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where password is not null
        defaultAppUserFiltering("password.specified=true", "password.specified=false");
    }

    @Test
    void getAllAppUsersByPasswordContainsSomething() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where password contains
        defaultAppUserFiltering("password.contains=" + DEFAULT_PASSWORD, "password.contains=" + UPDATED_PASSWORD);
    }

    @Test
    void getAllAppUsersByPasswordNotContainsSomething() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where password does not contain
        defaultAppUserFiltering("password.doesNotContain=" + UPDATED_PASSWORD, "password.doesNotContain=" + DEFAULT_PASSWORD);
    }

    @Test
    void getAllAppUsersByNameIsEqualToSomething() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where name equals to
        defaultAppUserFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllAppUsersByNameIsInShouldWork() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where name in
        defaultAppUserFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllAppUsersByNameIsNullOrNotNull() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where name is not null
        defaultAppUserFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllAppUsersByNameContainsSomething() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where name contains
        defaultAppUserFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllAppUsersByNameNotContainsSomething() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where name does not contain
        defaultAppUserFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllAppUsersByFirstNameIsEqualToSomething() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where firstName equals to
        defaultAppUserFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllAppUsersByFirstNameIsInShouldWork() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where firstName in
        defaultAppUserFiltering("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME, "firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllAppUsersByFirstNameIsNullOrNotNull() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where firstName is not null
        defaultAppUserFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    void getAllAppUsersByFirstNameContainsSomething() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where firstName contains
        defaultAppUserFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllAppUsersByFirstNameNotContainsSomething() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        // Get all the appUserList where firstName does not contain
        defaultAppUserFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    private void defaultAppUserFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultAppUserShouldBeFound(shouldBeFound);
        defaultAppUserShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAppUserShouldBeFound(String filter) {
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
            .value(hasItem(appUser.getId().intValue()))
            .jsonPath("$.[*].username")
            .value(hasItem(DEFAULT_USERNAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].password")
            .value(hasItem(DEFAULT_PASSWORD))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME));

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
    private void defaultAppUserShouldNotBeFound(String filter) {
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
    void getNonExistingAppUser() {
        // Get the appUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAppUser() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appUser
        AppUser updatedAppUser = appUserRepository.findById(appUser.getId()).block();
        updatedAppUser
            .username(UPDATED_USERNAME)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .name(UPDATED_NAME)
            .firstName(UPDATED_FIRST_NAME);
        AppUserDTO appUserDTO = appUserMapper.toDto(updatedAppUser);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appUserDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAppUserToMatchAllProperties(updatedAppUser);
    }

    @Test
    void putNonExistingAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appUserDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAppUserWithPatch() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appUser using partial update
        AppUser partialUpdatedAppUser = new AppUser();
        partialUpdatedAppUser.setId(appUser.getId());

        partialUpdatedAppUser.email(UPDATED_EMAIL).name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAppUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppUserUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAppUser, appUser), getPersistedAppUser(appUser));
    }

    @Test
    void fullUpdateAppUserWithPatch() throws Exception {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the appUser using partial update
        AppUser partialUpdatedAppUser = new AppUser();
        partialUpdatedAppUser.setId(appUser.getId());

        partialUpdatedAppUser
            .username(UPDATED_USERNAME)
            .email(UPDATED_EMAIL)
            .password(UPDATED_PASSWORD)
            .name(UPDATED_NAME)
            .firstName(UPDATED_FIRST_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedAppUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAppUserUpdatableFieldsEquals(partialUpdatedAppUser, getPersistedAppUser(partialUpdatedAppUser));
    }

    @Test
    void patchNonExistingAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, appUserDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAppUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        appUser.setId(longCount.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(appUserDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAppUser() {
        // Initialize the database
        insertedAppUser = appUserRepository.save(appUser).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the appUser
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, appUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return appUserRepository.count().block();
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

    protected AppUser getPersistedAppUser(AppUser appUser) {
        return appUserRepository.findById(appUser.getId()).block();
    }

    protected void assertPersistedAppUserToMatchAllProperties(AppUser expectedAppUser) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAppUserAllPropertiesEquals(expectedAppUser, getPersistedAppUser(expectedAppUser));
        assertAppUserUpdatableFieldsEquals(expectedAppUser, getPersistedAppUser(expectedAppUser));
    }

    protected void assertPersistedAppUserToMatchUpdatableProperties(AppUser expectedAppUser) {
        // Test fails because reactive api returns an empty object instead of null
        // assertAppUserAllUpdatablePropertiesEquals(expectedAppUser, getPersistedAppUser(expectedAppUser));
        assertAppUserUpdatableFieldsEquals(expectedAppUser, getPersistedAppUser(expectedAppUser));
    }
}
