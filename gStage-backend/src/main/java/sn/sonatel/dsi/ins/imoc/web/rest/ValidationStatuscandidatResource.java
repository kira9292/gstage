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
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidat;
import sn.sonatel.dsi.ins.imoc.repository.ValidationStatuscandidatRepository;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidat}.
 */
@RestController
@RequestMapping("/api/validation-statuscandidats")
@Transactional
public class ValidationStatuscandidatResource {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationStatuscandidatResource.class);

    private static final String ENTITY_NAME = "backendValidationStatuscandidat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ValidationStatuscandidatRepository validationStatuscandidatRepository;

    public ValidationStatuscandidatResource(ValidationStatuscandidatRepository validationStatuscandidatRepository) {
        this.validationStatuscandidatRepository = validationStatuscandidatRepository;
    }

    /**
     * {@code POST  /validation-statuscandidats} : Create a new validationStatuscandidat.
     *
     * @param validationStatuscandidat the validationStatuscandidat to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new validationStatuscandidat, or with status {@code 400 (Bad Request)} if the validationStatuscandidat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ValidationStatuscandidat> createValidationStatuscandidat(
        @RequestBody ValidationStatuscandidat validationStatuscandidat
    ) throws URISyntaxException {
        LOG.debug("REST request to save ValidationStatuscandidat : {}", validationStatuscandidat);
        if (validationStatuscandidat.getId() != null) {
            throw new BadRequestAlertException("A new validationStatuscandidat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        validationStatuscandidat = validationStatuscandidatRepository.save(validationStatuscandidat);
        return ResponseEntity.created(new URI("/api/validation-statuscandidats/" + validationStatuscandidat.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, validationStatuscandidat.getId().toString()))
            .body(validationStatuscandidat);
    }

    /**
     * {@code PUT  /validation-statuscandidats/:id} : Updates an existing validationStatuscandidat.
     *
     * @param id the id of the validationStatuscandidat to save.
     * @param validationStatuscandidat the validationStatuscandidat to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated validationStatuscandidat,
     * or with status {@code 400 (Bad Request)} if the validationStatuscandidat is not valid,
     * or with status {@code 500 (Internal Server Error)} if the validationStatuscandidat couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ValidationStatuscandidat> updateValidationStatuscandidat(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ValidationStatuscandidat validationStatuscandidat
    ) throws URISyntaxException {
        LOG.debug("REST request to update ValidationStatuscandidat : {}, {}", id, validationStatuscandidat);
        if (validationStatuscandidat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, validationStatuscandidat.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!validationStatuscandidatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        validationStatuscandidat = validationStatuscandidatRepository.save(validationStatuscandidat);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, validationStatuscandidat.getId().toString()))
            .body(validationStatuscandidat);
    }

    /**
     * {@code PATCH  /validation-statuscandidats/:id} : Partial updates given fields of an existing validationStatuscandidat, field will ignore if it is null
     *
     * @param id the id of the validationStatuscandidat to save.
     * @param validationStatuscandidat the validationStatuscandidat to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated validationStatuscandidat,
     * or with status {@code 400 (Bad Request)} if the validationStatuscandidat is not valid,
     * or with status {@code 404 (Not Found)} if the validationStatuscandidat is not found,
     * or with status {@code 500 (Internal Server Error)} if the validationStatuscandidat couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ValidationStatuscandidat> partialUpdateValidationStatuscandidat(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ValidationStatuscandidat validationStatuscandidat
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ValidationStatuscandidat partially : {}, {}", id, validationStatuscandidat);
        if (validationStatuscandidat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, validationStatuscandidat.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!validationStatuscandidatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ValidationStatuscandidat> result = validationStatuscandidatRepository
            .findById(validationStatuscandidat.getId())
            .map(existingValidationStatuscandidat -> {
                if (validationStatuscandidat.getCreation() != null) {
                    existingValidationStatuscandidat.setCreation(validationStatuscandidat.getCreation());
                }
                if (validationStatuscandidat.getExpire() != null) {
                    existingValidationStatuscandidat.setExpire(validationStatuscandidat.getExpire());
                }
                if (validationStatuscandidat.getActivation() != null) {
                    existingValidationStatuscandidat.setActivation(validationStatuscandidat.getActivation());
                }
                if (validationStatuscandidat.getCode() != null) {
                    existingValidationStatuscandidat.setCode(validationStatuscandidat.getCode());
                }

                return existingValidationStatuscandidat;
            })
            .map(validationStatuscandidatRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, validationStatuscandidat.getId().toString())
        );
    }

    /**
     * {@code GET  /validation-statuscandidats} : get all the validationStatuscandidats.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of validationStatuscandidats in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ValidationStatuscandidat>> getAllValidationStatuscandidats(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of ValidationStatuscandidats");
        Page<ValidationStatuscandidat> page = validationStatuscandidatRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /validation-statuscandidats/:id} : get the "id" validationStatuscandidat.
     *
     * @param id the id of the validationStatuscandidat to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the validationStatuscandidat, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ValidationStatuscandidat> getValidationStatuscandidat(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ValidationStatuscandidat : {}", id);
        Optional<ValidationStatuscandidat> validationStatuscandidat = validationStatuscandidatRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(validationStatuscandidat);
    }

    /**
     * {@code DELETE  /validation-statuscandidats/:id} : delete the "id" validationStatuscandidat.
     *
     * @param id the id of the validationStatuscandidat to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteValidationStatuscandidat(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ValidationStatuscandidat : {}", id);
        validationStatuscandidatRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
