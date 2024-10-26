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
import sn.sonatel.dsi.ins.imoc.domain.criteria.CandidatCriteria;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.service.CandidatService;
import sn.sonatel.dsi.ins.imoc.service.dto.CandidatDTO;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.Candidat}.
 */
@RestController
@RequestMapping("/api/candidats")
public class CandidatResource {

    private static final Logger LOG = LoggerFactory.getLogger(CandidatResource.class);

    private static final String ENTITY_NAME = "gStageCandidat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CandidatService candidatService;

    private final CandidatRepository candidatRepository;

    public CandidatResource(CandidatService candidatService, CandidatRepository candidatRepository) {
        this.candidatService = candidatService;
        this.candidatRepository = candidatRepository;
    }

    /**
     * {@code POST  /candidats} : Create a new candidat.
     *
     * @param candidatDTO the candidatDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new candidatDTO, or with status {@code 400 (Bad Request)} if the candidat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<CandidatDTO>> createCandidat(@Valid @RequestBody CandidatDTO candidatDTO) throws URISyntaxException {
        LOG.debug("REST request to save Candidat : {}", candidatDTO);
        if (candidatDTO.getId() != null) {
            throw new BadRequestAlertException("A new candidat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return candidatService
            .save(candidatDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/candidats/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /candidats/:id} : Updates an existing candidat.
     *
     * @param id the id of the candidatDTO to save.
     * @param candidatDTO the candidatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated candidatDTO,
     * or with status {@code 400 (Bad Request)} if the candidatDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the candidatDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<CandidatDTO>> updateCandidat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CandidatDTO candidatDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Candidat : {}, {}", id, candidatDTO);
        if (candidatDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, candidatDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return candidatRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return candidatService
                    .update(candidatDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /candidats/:id} : Partial updates given fields of an existing candidat, field will ignore if it is null
     *
     * @param id the id of the candidatDTO to save.
     * @param candidatDTO the candidatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated candidatDTO,
     * or with status {@code 400 (Bad Request)} if the candidatDTO is not valid,
     * or with status {@code 404 (Not Found)} if the candidatDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the candidatDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<CandidatDTO>> partialUpdateCandidat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CandidatDTO candidatDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Candidat partially : {}, {}", id, candidatDTO);
        if (candidatDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, candidatDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return candidatRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<CandidatDTO> result = candidatService.partialUpdate(candidatDTO);

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
     * {@code GET  /candidats} : get all the candidats.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of candidats in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<CandidatDTO>>> getAllCandidats(
        CandidatCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Candidats by criteria: {}", criteria);
        return candidatService
            .countByCriteria(criteria)
            .zipWith(candidatService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /candidats/count} : count all the candidats.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countCandidats(CandidatCriteria criteria) {
        LOG.debug("REST request to count Candidats by criteria: {}", criteria);
        return candidatService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /candidats/:id} : get the "id" candidat.
     *
     * @param id the id of the candidatDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the candidatDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<CandidatDTO>> getCandidat(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Candidat : {}", id);
        Mono<CandidatDTO> candidatDTO = candidatService.findOne(id);
        return ResponseUtil.wrapOrNotFound(candidatDTO);
    }

    /**
     * {@code DELETE  /candidats/:id} : delete the "id" candidat.
     *
     * @param id the id of the candidatDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCandidat(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Candidat : {}", id);
        return candidatService
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
