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
import sn.sonatel.dsi.ins.imoc.domain.criteria.DemandeStageCriteria;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.service.DemandeStageService;
import sn.sonatel.dsi.ins.imoc.service.dto.DemandeStageDTO;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.DemandeStage}.
 */
@RestController
@RequestMapping("/api/demande-stages")
public class DemandeStageResource {

    private static final Logger LOG = LoggerFactory.getLogger(DemandeStageResource.class);

    private static final String ENTITY_NAME = "gStageDemandeStage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DemandeStageService demandeStageService;

    private final DemandeStageRepository demandeStageRepository;

    public DemandeStageResource(DemandeStageService demandeStageService, DemandeStageRepository demandeStageRepository) {
        this.demandeStageService = demandeStageService;
        this.demandeStageRepository = demandeStageRepository;
    }

    /**
     * {@code POST  /demande-stages} : Create a new demandeStage.
     *
     * @param demandeStageDTO the demandeStageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new demandeStageDTO, or with status {@code 400 (Bad Request)} if the demandeStage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<DemandeStageDTO>> createDemandeStage(@Valid @RequestBody DemandeStageDTO demandeStageDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save DemandeStage : {}", demandeStageDTO);
        if (demandeStageDTO.getId() != null) {
            throw new BadRequestAlertException("A new demandeStage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return demandeStageService
            .save(demandeStageDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/demande-stages/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /demande-stages/:id} : Updates an existing demandeStage.
     *
     * @param id the id of the demandeStageDTO to save.
     * @param demandeStageDTO the demandeStageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated demandeStageDTO,
     * or with status {@code 400 (Bad Request)} if the demandeStageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the demandeStageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<DemandeStageDTO>> updateDemandeStage(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DemandeStageDTO demandeStageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DemandeStage : {}, {}", id, demandeStageDTO);
        if (demandeStageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, demandeStageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return demandeStageRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return demandeStageService
                    .update(demandeStageDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /demande-stages/:id} : Partial updates given fields of an existing demandeStage, field will ignore if it is null
     *
     * @param id the id of the demandeStageDTO to save.
     * @param demandeStageDTO the demandeStageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated demandeStageDTO,
     * or with status {@code 400 (Bad Request)} if the demandeStageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the demandeStageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the demandeStageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<DemandeStageDTO>> partialUpdateDemandeStage(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DemandeStageDTO demandeStageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DemandeStage partially : {}, {}", id, demandeStageDTO);
        if (demandeStageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, demandeStageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return demandeStageRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<DemandeStageDTO> result = demandeStageService.partialUpdate(demandeStageDTO);

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
     * {@code GET  /demande-stages} : get all the demandeStages.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of demandeStages in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<DemandeStageDTO>>> getAllDemandeStages(
        DemandeStageCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get DemandeStages by criteria: {}", criteria);
        return demandeStageService
            .countByCriteria(criteria)
            .zipWith(demandeStageService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /demande-stages/count} : count all the demandeStages.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countDemandeStages(DemandeStageCriteria criteria) {
        LOG.debug("REST request to count DemandeStages by criteria: {}", criteria);
        return demandeStageService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /demande-stages/:id} : get the "id" demandeStage.
     *
     * @param id the id of the demandeStageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the demandeStageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<DemandeStageDTO>> getDemandeStage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DemandeStage : {}", id);
        Mono<DemandeStageDTO> demandeStageDTO = demandeStageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(demandeStageDTO);
    }

    /**
     * {@code DELETE  /demande-stages/:id} : delete the "id" demandeStage.
     *
     * @param id the id of the demandeStageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDemandeStage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DemandeStage : {}", id);
        return demandeStageService
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
