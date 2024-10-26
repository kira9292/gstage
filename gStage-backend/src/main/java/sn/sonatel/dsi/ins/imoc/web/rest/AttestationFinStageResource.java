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
import sn.sonatel.dsi.ins.imoc.domain.criteria.AttestationFinStageCriteria;
import sn.sonatel.dsi.ins.imoc.repository.AttestationFinStageRepository;
import sn.sonatel.dsi.ins.imoc.service.AttestationFinStageService;
import sn.sonatel.dsi.ins.imoc.service.dto.AttestationFinStageDTO;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage}.
 */
@RestController
@RequestMapping("/api/attestation-fin-stages")
public class AttestationFinStageResource {

    private static final Logger LOG = LoggerFactory.getLogger(AttestationFinStageResource.class);

    private static final String ENTITY_NAME = "gStageAttestationFinStage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AttestationFinStageService attestationFinStageService;

    private final AttestationFinStageRepository attestationFinStageRepository;

    public AttestationFinStageResource(
        AttestationFinStageService attestationFinStageService,
        AttestationFinStageRepository attestationFinStageRepository
    ) {
        this.attestationFinStageService = attestationFinStageService;
        this.attestationFinStageRepository = attestationFinStageRepository;
    }

    /**
     * {@code POST  /attestation-fin-stages} : Create a new attestationFinStage.
     *
     * @param attestationFinStageDTO the attestationFinStageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new attestationFinStageDTO, or with status {@code 400 (Bad Request)} if the attestationFinStage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AttestationFinStageDTO>> createAttestationFinStage(
        @Valid @RequestBody AttestationFinStageDTO attestationFinStageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save AttestationFinStage : {}", attestationFinStageDTO);
        if (attestationFinStageDTO.getId() != null) {
            throw new BadRequestAlertException("A new attestationFinStage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return attestationFinStageService
            .save(attestationFinStageDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/attestation-fin-stages/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /attestation-fin-stages/:id} : Updates an existing attestationFinStage.
     *
     * @param id the id of the attestationFinStageDTO to save.
     * @param attestationFinStageDTO the attestationFinStageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attestationFinStageDTO,
     * or with status {@code 400 (Bad Request)} if the attestationFinStageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the attestationFinStageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AttestationFinStageDTO>> updateAttestationFinStage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AttestationFinStageDTO attestationFinStageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AttestationFinStage : {}, {}", id, attestationFinStageDTO);
        if (attestationFinStageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, attestationFinStageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return attestationFinStageRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return attestationFinStageService
                    .update(attestationFinStageDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /attestation-fin-stages/:id} : Partial updates given fields of an existing attestationFinStage, field will ignore if it is null
     *
     * @param id the id of the attestationFinStageDTO to save.
     * @param attestationFinStageDTO the attestationFinStageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated attestationFinStageDTO,
     * or with status {@code 400 (Bad Request)} if the attestationFinStageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the attestationFinStageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the attestationFinStageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AttestationFinStageDTO>> partialUpdateAttestationFinStage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AttestationFinStageDTO attestationFinStageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AttestationFinStage partially : {}, {}", id, attestationFinStageDTO);
        if (attestationFinStageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, attestationFinStageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return attestationFinStageRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<AttestationFinStageDTO> result = attestationFinStageService.partialUpdate(attestationFinStageDTO);

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
     * {@code GET  /attestation-fin-stages} : get all the attestationFinStages.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of attestationFinStages in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<AttestationFinStageDTO>>> getAllAttestationFinStages(
        AttestationFinStageCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get AttestationFinStages by criteria: {}", criteria);
        return attestationFinStageService
            .countByCriteria(criteria)
            .zipWith(attestationFinStageService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /attestation-fin-stages/count} : count all the attestationFinStages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countAttestationFinStages(AttestationFinStageCriteria criteria) {
        LOG.debug("REST request to count AttestationFinStages by criteria: {}", criteria);
        return attestationFinStageService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /attestation-fin-stages/:id} : get the "id" attestationFinStage.
     *
     * @param id the id of the attestationFinStageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the attestationFinStageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AttestationFinStageDTO>> getAttestationFinStage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AttestationFinStage : {}", id);
        Mono<AttestationFinStageDTO> attestationFinStageDTO = attestationFinStageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(attestationFinStageDTO);
    }

    /**
     * {@code DELETE  /attestation-fin-stages/:id} : delete the "id" attestationFinStage.
     *
     * @param id the id of the attestationFinStageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAttestationFinStage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AttestationFinStage : {}", id);
        return attestationFinStageService
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
