package sn.sonatel.dsi.ins.imoc.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import sn.sonatel.dsi.ins.imoc.domain.Validation;
import sn.sonatel.dsi.ins.imoc.repository.ValidationRepository;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.Validation}.
 */
@RestController
@RequestMapping("/api/validations")
@Transactional
public class ValidationResource {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationResource.class);

    private static final String ENTITY_NAME = "backendValidation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ValidationRepository validationRepository;

    public ValidationResource(ValidationRepository validationRepository) {
        this.validationRepository = validationRepository;
    }

    /**
     * {@code POST  /validations} : Create a new validation.
     *
     * @param validation the validation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new validation, or with status {@code 400 (Bad Request)} if the validation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Validation> createValidation(@Valid @RequestBody Validation validation) throws URISyntaxException {
        LOG.debug("REST request to save Validation : {}", validation);
        if (validation.getId() != null) {
            throw new BadRequestAlertException("A new validation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        validation = validationRepository.save(validation);
        return ResponseEntity.created(new URI("/api/validations/" + validation.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, validation.getId().toString()))
            .body(validation);
    }

    /**
     * {@code PUT  /validations/:id} : Updates an existing validation.
     *
     * @param id the id of the validation to save.
     * @param validation the validation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated validation,
     * or with status {@code 400 (Bad Request)} if the validation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the validation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Validation> updateValidation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Validation validation
    ) throws URISyntaxException {
        LOG.debug("REST request to update Validation : {}, {}", id, validation);
        if (validation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, validation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!validationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        validation = validationRepository.save(validation);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, validation.getId().toString()))
            .body(validation);
    }

    /**
     * {@code PATCH  /validations/:id} : Partial updates given fields of an existing validation, field will ignore if it is null
     *
     * @param id the id of the validation to save.
     * @param validation the validation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated validation,
     * or with status {@code 400 (Bad Request)} if the validation is not valid,
     * or with status {@code 404 (Not Found)} if the validation is not found,
     * or with status {@code 500 (Internal Server Error)} if the validation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Validation> partialUpdateValidation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Validation validation
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Validation partially : {}, {}", id, validation);
        if (validation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, validation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!validationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Validation> result = validationRepository
            .findById(validation.getId())
            .map(existingValidation -> {
                if (validation.getReference() != null) {
                    existingValidation.setReference(validation.getReference());
                }
                if (validation.getValidationDate() != null) {
                    existingValidation.setValidationDate(validation.getValidationDate());
                }
                if (validation.getStatus() != null) {
                    existingValidation.setStatus(validation.getStatus());
                }
                if (validation.getComments() != null) {
                    existingValidation.setComments(validation.getComments());
                }
                if (validation.getValidatedBy() != null) {
                    existingValidation.setValidatedBy(validation.getValidatedBy());
                }

                return existingValidation;
            })
            .map(validationRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, validation.getId().toString())
        );
    }

    /**
     * {@code GET  /validations} : get all the validations.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of validations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Validation>> getAllValidations(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Validations");
        Page<Validation> page = validationRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /validations/:id} : get the "id" validation.
     *
     * @param id the id of the validation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the validation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Validation> getValidation(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Validation : {}", id);
        Optional<Validation> validation = validationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(validation);
    }

    /**
     * {@code DELETE  /validations/:id} : delete the "id" validation.
     *
     * @param id the id of the validation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteValidation(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Validation : {}", id);
        validationRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
