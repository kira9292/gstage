package sn.sonatel.dsi.ins.imoc.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatusUser;
import sn.sonatel.dsi.ins.imoc.repository.ValidationStatusUserRepository;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.ValidationStatusUser}.
 */
@RestController
@RequestMapping("/api/validation-status-users")
@Transactional
public class ValidationStatusUserResource {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationStatusUserResource.class);

    private static final String ENTITY_NAME = "backendValidationStatusUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ValidationStatusUserRepository validationStatusUserRepository;

    public ValidationStatusUserResource(ValidationStatusUserRepository validationStatusUserRepository) {
        this.validationStatusUserRepository = validationStatusUserRepository;
    }

    /**
     * {@code POST  /validation-status-users} : Create a new validationStatusUser.
     *
     * @param validationStatusUser the validationStatusUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new validationStatusUser, or with status {@code 400 (Bad Request)} if the validationStatusUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ValidationStatusUser> createValidationStatusUser(@RequestBody ValidationStatusUser validationStatusUser)
        throws URISyntaxException {
        LOG.debug("REST request to save ValidationStatusUser : {}", validationStatusUser);
        if (validationStatusUser.getId() != null) {
            throw new BadRequestAlertException("A new validationStatusUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        validationStatusUser = validationStatusUserRepository.save(validationStatusUser);
        return ResponseEntity.created(new URI("/api/validation-status-users/" + validationStatusUser.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, validationStatusUser.getId().toString()))
            .body(validationStatusUser);
    }

    /**
     * {@code PUT  /validation-status-users/:id} : Updates an existing validationStatusUser.
     *
     * @param id the id of the validationStatusUser to save.
     * @param validationStatusUser the validationStatusUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated validationStatusUser,
     * or with status {@code 400 (Bad Request)} if the validationStatusUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the validationStatusUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ValidationStatusUser> updateValidationStatusUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ValidationStatusUser validationStatusUser
    ) throws URISyntaxException {
        LOG.debug("REST request to update ValidationStatusUser : {}, {}", id, validationStatusUser);
        if (validationStatusUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, validationStatusUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!validationStatusUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        validationStatusUser = validationStatusUserRepository.save(validationStatusUser);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, validationStatusUser.getId().toString()))
            .body(validationStatusUser);
    }

    /**
     * {@code PATCH  /validation-status-users/:id} : Partial updates given fields of an existing validationStatusUser, field will ignore if it is null
     *
     * @param id the id of the validationStatusUser to save.
     * @param validationStatusUser the validationStatusUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated validationStatusUser,
     * or with status {@code 400 (Bad Request)} if the validationStatusUser is not valid,
     * or with status {@code 404 (Not Found)} if the validationStatusUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the validationStatusUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ValidationStatusUser> partialUpdateValidationStatusUser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ValidationStatusUser validationStatusUser
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ValidationStatusUser partially : {}, {}", id, validationStatusUser);
        if (validationStatusUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, validationStatusUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!validationStatusUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ValidationStatusUser> result = validationStatusUserRepository
            .findById(validationStatusUser.getId())
            .map(existingValidationStatusUser -> {
                if (validationStatusUser.getCreation() != null) {
                    existingValidationStatusUser.setCreation(validationStatusUser.getCreation());
                }
                if (validationStatusUser.getExpire() != null) {
                    existingValidationStatusUser.setExpire(validationStatusUser.getExpire());
                }
                if (validationStatusUser.getActivation() != null) {
                    existingValidationStatusUser.setActivation(validationStatusUser.getActivation());
                }
                if (validationStatusUser.getCode() != null) {
                    existingValidationStatusUser.setCode(validationStatusUser.getCode());
                }

                return existingValidationStatusUser;
            })
            .map(validationStatusUserRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, validationStatusUser.getId().toString())
        );
    }

    /**
     * {@code GET  /validation-status-users} : get all the validationStatusUsers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of validationStatusUsers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ValidationStatusUser>> getAllValidationStatusUsers(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ValidationStatusUsers");
        Page<ValidationStatusUser> page = validationStatusUserRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /validation-status-users/:id} : get the "id" validationStatusUser.
     *
     * @param id the id of the validationStatusUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the validationStatusUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ValidationStatusUser> getValidationStatusUser(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ValidationStatusUser : {}", id);
        Optional<ValidationStatusUser> validationStatusUser = validationStatusUserRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(validationStatusUser);
    }

    /**
     * {@code DELETE  /validation-status-users/:id} : delete the "id" validationStatusUser.
     *
     * @param id the id of the validationStatusUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteValidationStatusUser(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ValidationStatusUser : {}", id);
        validationStatusUserRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
