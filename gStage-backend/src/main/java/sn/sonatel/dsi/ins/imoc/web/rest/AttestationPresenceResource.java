package sn.sonatel.dsi.ins.imoc.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.AttestationPresenceCriteria;
import sn.sonatel.dsi.ins.imoc.repository.AttestationPresenceRepository;
import sn.sonatel.dsi.ins.imoc.service.AttestationPresenceService;
import sn.sonatel.dsi.ins.imoc.service.dto.AttestationPresenceDTO;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.AttestationPresence}.
 */
@RestController
@RequestMapping("/api/attestation-presences")
public class AttestationPresenceResource {

    private static final Logger LOG = LoggerFactory.getLogger(AttestationPresenceResource.class);

    private static final String ENTITY_NAME = "gStageAttestationPresence";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AttestationPresenceService attestationPresenceService;

    private final AttestationPresenceRepository attestationPresenceRepository;

    public AttestationPresenceResource(
        AttestationPresenceService attestationPresenceService,
        AttestationPresenceRepository attestationPresenceRepository
    ) {
        this.attestationPresenceService = attestationPresenceService;
        this.attestationPresenceRepository = attestationPresenceRepository;
    }

    /**
     * {@code POST  /attestation-presences} : Create a new attestationPresence.
     *
     * @param attestationPresenceDTO the attestationPresenceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new attestationPresenceDTO, or with status {@code 400 (Bad Request)} if the attestationPresence has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AttestationPresenceDTO>> createAttestationPresence(
        @Valid @RequestBody AttestationPresenceDTO attestationPresenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save AttestationPresence : {}", attestationPresenceDTO);
        if (attestationPresenceDTO.getId() != null) {
            throw new BadRequestAlertException("A new attestationPresence cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return attestationPresenceService
            .save(attestationPresenceDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/attestation-presences/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /attestation-presences/:id} : Updates an existing attestationPresence.
     *
     * @param id the id of the attestationPresenceDTO to save.
     * @param attestationPresenceDTO the attestationPresenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attestationPresenceDTO,
     * or with status {@code 400 (Bad Request)} if the attestationPresenceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the attestationPresenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AttestationPresenceDTO>> updateAttestationPresence(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AttestationPresenceDTO attestationPresenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AttestationPresence : {}, {}", id, attestationPresenceDTO);
        if (attestationPresenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, attestationPresenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return attestationPresenceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return attestationPresenceService
                    .update(attestationPresenceDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /attestation-presences/:id} : Partial updates given fields of an existing attestationPresence, field will ignore if it is null
     *
     * @param id the id of the attestationPresenceDTO to save.
     * @param attestationPresenceDTO the attestationPresenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attestationPresenceDTO,
     * or with status {@code 400 (Bad Request)} if the attestationPresenceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the attestationPresenceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the attestationPresenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AttestationPresenceDTO>> partialUpdateAttestationPresence(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AttestationPresenceDTO attestationPresenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AttestationPresence partially : {}, {}", id, attestationPresenceDTO);
        if (attestationPresenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, attestationPresenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return attestationPresenceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<AttestationPresenceDTO> result = attestationPresenceService.partialUpdate(attestationPresenceDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /attestation-presences} : get all the attestationPresences.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of attestationPresences in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<AttestationPresenceDTO>>> getAllAttestationPresences(
        AttestationPresenceCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get AttestationPresences by criteria: {}", criteria);
        return attestationPresenceService
            .countByCriteria(criteria)
            .zipWith(attestationPresenceService.findByCriteria(criteria, pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /attestation-presences/count} : count all the attestationPresences.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countAttestationPresences(AttestationPresenceCriteria criteria) {
        LOG.debug("REST request to count AttestationPresences by criteria: {}", criteria);
        return attestationPresenceService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /attestation-presences/:id} : get the "id" attestationPresence.
     *
     * @param id the id of the attestationPresenceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the attestationPresenceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AttestationPresenceDTO>> getAttestationPresence(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AttestationPresence : {}", id);
        Mono<AttestationPresenceDTO> attestationPresenceDTO = attestationPresenceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(attestationPresenceDTO);
    }

    /**
     * {@code DELETE  /attestation-presences/:id} : delete the "id" attestationPresence.
     *
     * @param id the id of the attestationPresenceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAttestationPresence(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AttestationPresence : {}", id);
        return attestationPresenceService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
