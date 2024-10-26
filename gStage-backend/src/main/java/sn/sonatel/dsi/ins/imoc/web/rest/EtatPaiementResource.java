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
import sn.sonatel.dsi.ins.imoc.domain.criteria.EtatPaiementCriteria;
import sn.sonatel.dsi.ins.imoc.repository.EtatPaiementRepository;
import sn.sonatel.dsi.ins.imoc.service.EtatPaiementService;
import sn.sonatel.dsi.ins.imoc.service.dto.EtatPaiementDTO;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.EtatPaiement}.
 */
@RestController
@RequestMapping("/api/etat-paiements")
public class EtatPaiementResource {

    private static final Logger LOG = LoggerFactory.getLogger(EtatPaiementResource.class);

    private static final String ENTITY_NAME = "gStageEtatPaiement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EtatPaiementService etatPaiementService;

    private final EtatPaiementRepository etatPaiementRepository;

    public EtatPaiementResource(EtatPaiementService etatPaiementService, EtatPaiementRepository etatPaiementRepository) {
        this.etatPaiementService = etatPaiementService;
        this.etatPaiementRepository = etatPaiementRepository;
    }

    /**
     * {@code POST  /etat-paiements} : Create a new etatPaiement.
     *
     * @param etatPaiementDTO the etatPaiementDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new etatPaiementDTO, or with status {@code 400 (Bad Request)} if the etatPaiement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<EtatPaiementDTO>> createEtatPaiement(@Valid @RequestBody EtatPaiementDTO etatPaiementDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save EtatPaiement : {}", etatPaiementDTO);
        if (etatPaiementDTO.getId() != null) {
            throw new BadRequestAlertException("A new etatPaiement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return etatPaiementService
            .save(etatPaiementDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/etat-paiements/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /etat-paiements/:id} : Updates an existing etatPaiement.
     *
     * @param id the id of the etatPaiementDTO to save.
     * @param etatPaiementDTO the etatPaiementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etatPaiementDTO,
     * or with status {@code 400 (Bad Request)} if the etatPaiementDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the etatPaiementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<EtatPaiementDTO>> updateEtatPaiement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EtatPaiementDTO etatPaiementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EtatPaiement : {}, {}", id, etatPaiementDTO);
        if (etatPaiementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, etatPaiementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return etatPaiementRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return etatPaiementService
                    .update(etatPaiementDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /etat-paiements/:id} : Partial updates given fields of an existing etatPaiement, field will ignore if it is null
     *
     * @param id the id of the etatPaiementDTO to save.
     * @param etatPaiementDTO the etatPaiementDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etatPaiementDTO,
     * or with status {@code 400 (Bad Request)} if the etatPaiementDTO is not valid,
     * or with status {@code 404 (Not Found)} if the etatPaiementDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the etatPaiementDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<EtatPaiementDTO>> partialUpdateEtatPaiement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EtatPaiementDTO etatPaiementDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EtatPaiement partially : {}, {}", id, etatPaiementDTO);
        if (etatPaiementDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, etatPaiementDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return etatPaiementRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<EtatPaiementDTO> result = etatPaiementService.partialUpdate(etatPaiementDTO);

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
     * {@code GET  /etat-paiements} : get all the etatPaiements.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of etatPaiements in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<EtatPaiementDTO>>> getAllEtatPaiements(
        EtatPaiementCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get EtatPaiements by criteria: {}", criteria);
        return etatPaiementService
            .countByCriteria(criteria)
            .zipWith(etatPaiementService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /etat-paiements/count} : count all the etatPaiements.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countEtatPaiements(EtatPaiementCriteria criteria) {
        LOG.debug("REST request to count EtatPaiements by criteria: {}", criteria);
        return etatPaiementService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /etat-paiements/:id} : get the "id" etatPaiement.
     *
     * @param id the id of the etatPaiementDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the etatPaiementDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<EtatPaiementDTO>> getEtatPaiement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EtatPaiement : {}", id);
        Mono<EtatPaiementDTO> etatPaiementDTO = etatPaiementService.findOne(id);
        return ResponseUtil.wrapOrNotFound(etatPaiementDTO);
    }

    /**
     * {@code DELETE  /etat-paiements/:id} : delete the "id" etatPaiement.
     *
     * @param id the id of the etatPaiementDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEtatPaiement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EtatPaiement : {}", id);
        return etatPaiementService
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
