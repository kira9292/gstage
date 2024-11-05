package sn.sonatel.dsi.ins.imoc.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage;
import sn.sonatel.dsi.ins.imoc.repository.AttestationFinStageRepository;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage}.
 */
@RestController
@RequestMapping("/api/attestation-fin-stages")
@Transactional
public class AttestationFinStageResource {

    private static final Logger LOG = LoggerFactory.getLogger(AttestationFinStageResource.class);

    private static final String ENTITY_NAME = "backendAttestationFinStage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AttestationFinStageRepository attestationFinStageRepository;

    public AttestationFinStageResource(AttestationFinStageRepository attestationFinStageRepository) {
        this.attestationFinStageRepository = attestationFinStageRepository;
    }

    /**
     * {@code POST  /attestation-fin-stages} : Create a new attestationFinStage.
     *
     * @param attestationFinStage the attestationFinStage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new attestationFinStage, or with status {@code 400 (Bad Request)} if the attestationFinStage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AttestationFinStage> createAttestationFinStage(@Valid @RequestBody AttestationFinStage attestationFinStage)
        throws URISyntaxException {
        LOG.debug("REST request to save AttestationFinStage : {}", attestationFinStage);
        if (attestationFinStage.getId() != null) {
            throw new BadRequestAlertException("A new attestationFinStage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        attestationFinStage = attestationFinStageRepository.save(attestationFinStage);
        return ResponseEntity.created(new URI("/api/attestation-fin-stages/" + attestationFinStage.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, attestationFinStage.getId().toString()))
            .body(attestationFinStage);
    }

    /**
     * {@code PUT  /attestation-fin-stages/:id} : Updates an existing attestationFinStage.
     *
     * @param id the id of the attestationFinStage to save.
     * @param attestationFinStage the attestationFinStage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attestationFinStage,
     * or with status {@code 400 (Bad Request)} if the attestationFinStage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the attestationFinStage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AttestationFinStage> updateAttestationFinStage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AttestationFinStage attestationFinStage
    ) throws URISyntaxException {
        LOG.debug("REST request to update AttestationFinStage : {}, {}", id, attestationFinStage);
        if (attestationFinStage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, attestationFinStage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!attestationFinStageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        attestationFinStage = attestationFinStageRepository.save(attestationFinStage);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, attestationFinStage.getId().toString()))
            .body(attestationFinStage);
    }

    /**
     * {@code PATCH  /attestation-fin-stages/:id} : Partial updates given fields of an existing attestationFinStage, field will ignore if it is null
     *
     * @param id the id of the attestationFinStage to save.
     * @param attestationFinStage the attestationFinStage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attestationFinStage,
     * or with status {@code 400 (Bad Request)} if the attestationFinStage is not valid,
     * or with status {@code 404 (Not Found)} if the attestationFinStage is not found,
     * or with status {@code 500 (Internal Server Error)} if the attestationFinStage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AttestationFinStage> partialUpdateAttestationFinStage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AttestationFinStage attestationFinStage
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AttestationFinStage partially : {}, {}", id, attestationFinStage);
        if (attestationFinStage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, attestationFinStage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!attestationFinStageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AttestationFinStage> result = attestationFinStageRepository
            .findById(attestationFinStage.getId())
            .map(existingAttestationFinStage -> {
                if (attestationFinStage.getReference() != null) {
                    existingAttestationFinStage.setReference(attestationFinStage.getReference());
                }
                if (attestationFinStage.getIssueDate() != null) {
                    existingAttestationFinStage.setIssueDate(attestationFinStage.getIssueDate());
                }
                if (attestationFinStage.getSignatureDate() != null) {
                    existingAttestationFinStage.setSignatureDate(attestationFinStage.getSignatureDate());
                }
                if (attestationFinStage.getComments() != null) {
                    existingAttestationFinStage.setComments(attestationFinStage.getComments());
                }

                return existingAttestationFinStage;
            })
            .map(attestationFinStageRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, attestationFinStage.getId().toString())
        );
    }

    /**
     * {@code GET  /attestation-fin-stages} : get all the attestationFinStages.
     *
     * @param pageable the pagination information.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of attestationFinStages in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AttestationFinStage>> getAllAttestationFinStages(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "filter", required = false) String filter
    ) {
        if ("appuser-is-null".equals(filter)) {
            LOG.debug("REST request to get all AttestationFinStages where appuser is null");
            return new ResponseEntity<>(
                StreamSupport.stream(attestationFinStageRepository.findAll().spliterator(), false)
                    .filter(attestationFinStage -> attestationFinStage.getAppuser() == null)
                    .toList(),
                HttpStatus.OK
            );
        }
        LOG.debug("REST request to get a page of AttestationFinStages");
        Page<AttestationFinStage> page = attestationFinStageRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /attestation-fin-stages/:id} : get the "id" attestationFinStage.
     *
     * @param id the id of the attestationFinStage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the attestationFinStage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AttestationFinStage> getAttestationFinStage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AttestationFinStage : {}", id);
        Optional<AttestationFinStage> attestationFinStage = attestationFinStageRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(attestationFinStage);
    }

    /**
     * {@code DELETE  /attestation-fin-stages/:id} : delete the "id" attestationFinStage.
     *
     * @param id the id of the attestationFinStage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttestationFinStage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AttestationFinStage : {}", id);
        attestationFinStageRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
