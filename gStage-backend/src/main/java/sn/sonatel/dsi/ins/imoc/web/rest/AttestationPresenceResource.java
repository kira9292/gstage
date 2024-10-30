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
import sn.sonatel.dsi.ins.imoc.domain.AttestationPresence;
import sn.sonatel.dsi.ins.imoc.repository.AttestationPresenceRepository;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.AttestationPresence}.
 */
@RestController
@RequestMapping("/api/attestation-presences")
@Transactional
public class AttestationPresenceResource {

    private static final Logger LOG = LoggerFactory.getLogger(AttestationPresenceResource.class);

    private static final String ENTITY_NAME = "backendAttestationPresence";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AttestationPresenceRepository attestationPresenceRepository;

    public AttestationPresenceResource(AttestationPresenceRepository attestationPresenceRepository) {
        this.attestationPresenceRepository = attestationPresenceRepository;
    }

    /**
     * {@code POST  /attestation-presences} : Create a new attestationPresence.
     *
     * @param attestationPresence the attestationPresence to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new attestationPresence, or with status {@code 400 (Bad Request)} if the attestationPresence has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AttestationPresence> createAttestationPresence(@Valid @RequestBody AttestationPresence attestationPresence)
        throws URISyntaxException {
        LOG.debug("REST request to save AttestationPresence : {}", attestationPresence);
        if (attestationPresence.getId() != null) {
            throw new BadRequestAlertException("A new attestationPresence cannot already have an ID", ENTITY_NAME, "idexists");
        }
        attestationPresence = attestationPresenceRepository.save(attestationPresence);
        return ResponseEntity.created(new URI("/api/attestation-presences/" + attestationPresence.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, attestationPresence.getId().toString()))
            .body(attestationPresence);
    }

    /**
     * {@code PUT  /attestation-presences/:id} : Updates an existing attestationPresence.
     *
     * @param id the id of the attestationPresence to save.
     * @param attestationPresence the attestationPresence to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attestationPresence,
     * or with status {@code 400 (Bad Request)} if the attestationPresence is not valid,
     * or with status {@code 500 (Internal Server Error)} if the attestationPresence couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AttestationPresence> updateAttestationPresence(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AttestationPresence attestationPresence
    ) throws URISyntaxException {
        LOG.debug("REST request to update AttestationPresence : {}, {}", id, attestationPresence);
        if (attestationPresence.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, attestationPresence.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!attestationPresenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        attestationPresence = attestationPresenceRepository.save(attestationPresence);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, attestationPresence.getId().toString()))
            .body(attestationPresence);
    }

    /**
     * {@code PATCH  /attestation-presences/:id} : Partial updates given fields of an existing attestationPresence, field will ignore if it is null
     *
     * @param id the id of the attestationPresence to save.
     * @param attestationPresence the attestationPresence to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attestationPresence,
     * or with status {@code 400 (Bad Request)} if the attestationPresence is not valid,
     * or with status {@code 404 (Not Found)} if the attestationPresence is not found,
     * or with status {@code 500 (Internal Server Error)} if the attestationPresence couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AttestationPresence> partialUpdateAttestationPresence(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AttestationPresence attestationPresence
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AttestationPresence partially : {}, {}", id, attestationPresence);
        if (attestationPresence.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, attestationPresence.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!attestationPresenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AttestationPresence> result = attestationPresenceRepository
            .findById(attestationPresence.getId())
            .map(existingAttestationPresence -> {
                if (attestationPresence.getReference() != null) {
                    existingAttestationPresence.setReference(attestationPresence.getReference());
                }
                if (attestationPresence.getStartDate() != null) {
                    existingAttestationPresence.setStartDate(attestationPresence.getStartDate());
                }
                if (attestationPresence.getEndDate() != null) {
                    existingAttestationPresence.setEndDate(attestationPresence.getEndDate());
                }
                if (attestationPresence.getSignatureDate() != null) {
                    existingAttestationPresence.setSignatureDate(attestationPresence.getSignatureDate());
                }
                if (attestationPresence.getStatus() != null) {
                    existingAttestationPresence.setStatus(attestationPresence.getStatus());
                }
                if (attestationPresence.getComments() != null) {
                    existingAttestationPresence.setComments(attestationPresence.getComments());
                }

                return existingAttestationPresence;
            })
            .map(attestationPresenceRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, attestationPresence.getId().toString())
        );
    }

    /**
     * {@code GET  /attestation-presences} : get all the attestationPresences.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of attestationPresences in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AttestationPresence>> getAllAttestationPresences(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of AttestationPresences");
        Page<AttestationPresence> page = attestationPresenceRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /attestation-presences/:id} : get the "id" attestationPresence.
     *
     * @param id the id of the attestationPresence to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the attestationPresence, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AttestationPresence> getAttestationPresence(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AttestationPresence : {}", id);
        Optional<AttestationPresence> attestationPresence = attestationPresenceRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(attestationPresence);
    }

    /**
     * {@code DELETE  /attestation-presences/:id} : delete the "id" attestationPresence.
     *
     * @param id the id of the attestationPresence to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttestationPresence(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AttestationPresence : {}", id);
        attestationPresenceRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
