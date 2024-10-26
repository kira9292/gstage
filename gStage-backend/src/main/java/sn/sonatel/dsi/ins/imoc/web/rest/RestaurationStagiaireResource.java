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
import sn.sonatel.dsi.ins.imoc.domain.criteria.RestaurationStagiaireCriteria;
import sn.sonatel.dsi.ins.imoc.repository.RestaurationStagiaireRepository;
import sn.sonatel.dsi.ins.imoc.service.RestaurationStagiaireService;
import sn.sonatel.dsi.ins.imoc.service.dto.RestaurationStagiaireDTO;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaire}.
 */
@RestController
@RequestMapping("/api/restauration-stagiaires")
public class RestaurationStagiaireResource {

    private static final Logger LOG = LoggerFactory.getLogger(RestaurationStagiaireResource.class);

    private static final String ENTITY_NAME = "gStageRestaurationStagiaire";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RestaurationStagiaireService restaurationStagiaireService;

    private final RestaurationStagiaireRepository restaurationStagiaireRepository;

    public RestaurationStagiaireResource(
        RestaurationStagiaireService restaurationStagiaireService,
        RestaurationStagiaireRepository restaurationStagiaireRepository
    ) {
        this.restaurationStagiaireService = restaurationStagiaireService;
        this.restaurationStagiaireRepository = restaurationStagiaireRepository;
    }

    /**
     * {@code POST  /restauration-stagiaires} : Create a new restaurationStagiaire.
     *
     * @param restaurationStagiaireDTO the restaurationStagiaireDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new restaurationStagiaireDTO, or with status {@code 400 (Bad Request)} if the restaurationStagiaire has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<RestaurationStagiaireDTO>> createRestaurationStagiaire(
        @Valid @RequestBody RestaurationStagiaireDTO restaurationStagiaireDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save RestaurationStagiaire : {}", restaurationStagiaireDTO);
        if (restaurationStagiaireDTO.getId() != null) {
            throw new BadRequestAlertException("A new restaurationStagiaire cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return restaurationStagiaireService
            .save(restaurationStagiaireDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/restauration-stagiaires/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /restauration-stagiaires/:id} : Updates an existing restaurationStagiaire.
     *
     * @param id the id of the restaurationStagiaireDTO to save.
     * @param restaurationStagiaireDTO the restaurationStagiaireDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurationStagiaireDTO,
     * or with status {@code 400 (Bad Request)} if the restaurationStagiaireDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the restaurationStagiaireDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<RestaurationStagiaireDTO>> updateRestaurationStagiaire(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RestaurationStagiaireDTO restaurationStagiaireDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RestaurationStagiaire : {}, {}", id, restaurationStagiaireDTO);
        if (restaurationStagiaireDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, restaurationStagiaireDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return restaurationStagiaireRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return restaurationStagiaireService
                    .update(restaurationStagiaireDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /restauration-stagiaires/:id} : Partial updates given fields of an existing restaurationStagiaire, field will ignore if it is null
     *
     * @param id the id of the restaurationStagiaireDTO to save.
     * @param restaurationStagiaireDTO the restaurationStagiaireDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurationStagiaireDTO,
     * or with status {@code 400 (Bad Request)} if the restaurationStagiaireDTO is not valid,
     * or with status {@code 404 (Not Found)} if the restaurationStagiaireDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the restaurationStagiaireDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RestaurationStagiaireDTO>> partialUpdateRestaurationStagiaire(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RestaurationStagiaireDTO restaurationStagiaireDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RestaurationStagiaire partially : {}, {}", id, restaurationStagiaireDTO);
        if (restaurationStagiaireDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, restaurationStagiaireDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return restaurationStagiaireRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<RestaurationStagiaireDTO> result = restaurationStagiaireService.partialUpdate(restaurationStagiaireDTO);

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
     * {@code GET  /restauration-stagiaires} : get all the restaurationStagiaires.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of restaurationStagiaires in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<RestaurationStagiaireDTO>>> getAllRestaurationStagiaires(
        RestaurationStagiaireCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get RestaurationStagiaires by criteria: {}", criteria);
        return restaurationStagiaireService
            .countByCriteria(criteria)
            .zipWith(restaurationStagiaireService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /restauration-stagiaires/count} : count all the restaurationStagiaires.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countRestaurationStagiaires(RestaurationStagiaireCriteria criteria) {
        LOG.debug("REST request to count RestaurationStagiaires by criteria: {}", criteria);
        return restaurationStagiaireService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /restauration-stagiaires/:id} : get the "id" restaurationStagiaire.
     *
     * @param id the id of the restaurationStagiaireDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the restaurationStagiaireDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<RestaurationStagiaireDTO>> getRestaurationStagiaire(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RestaurationStagiaire : {}", id);
        Mono<RestaurationStagiaireDTO> restaurationStagiaireDTO = restaurationStagiaireService.findOne(id);
        return ResponseUtil.wrapOrNotFound(restaurationStagiaireDTO);
    }

    /**
     * {@code DELETE  /restauration-stagiaires/:id} : delete the "id" restaurationStagiaire.
     *
     * @param id the id of the restaurationStagiaireDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteRestaurationStagiaire(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RestaurationStagiaire : {}", id);
        return restaurationStagiaireService
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
