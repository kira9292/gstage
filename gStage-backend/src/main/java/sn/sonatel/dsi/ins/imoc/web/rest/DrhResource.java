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
import sn.sonatel.dsi.ins.imoc.domain.criteria.DrhCriteria;
import sn.sonatel.dsi.ins.imoc.repository.DrhRepository;
import sn.sonatel.dsi.ins.imoc.service.DrhService;
import sn.sonatel.dsi.ins.imoc.service.dto.DrhDTO;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.Drh}.
 */
@RestController
@RequestMapping("/api/drhs")
public class DrhResource {

    private static final Logger LOG = LoggerFactory.getLogger(DrhResource.class);

    private static final String ENTITY_NAME = "gStageDrh";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DrhService drhService;

    private final DrhRepository drhRepository;

    public DrhResource(DrhService drhService, DrhRepository drhRepository) {
        this.drhService = drhService;
        this.drhRepository = drhRepository;
    }

    /**
     * {@code POST  /drhs} : Create a new drh.
     *
     * @param drhDTO the drhDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new drhDTO, or with status {@code 400 (Bad Request)} if the drh has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<DrhDTO>> createDrh(@Valid @RequestBody DrhDTO drhDTO) throws URISyntaxException {
        LOG.debug("REST request to save Drh : {}", drhDTO);
        if (drhDTO.getId() != null) {
            throw new BadRequestAlertException("A new drh cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return drhService
            .save(drhDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/drhs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /drhs/:id} : Updates an existing drh.
     *
     * @param id the id of the drhDTO to save.
     * @param drhDTO the drhDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated drhDTO,
     * or with status {@code 400 (Bad Request)} if the drhDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the drhDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<DrhDTO>> updateDrh(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DrhDTO drhDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Drh : {}, {}", id, drhDTO);
        if (drhDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, drhDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return drhRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return drhService
                    .update(drhDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /drhs/:id} : Partial updates given fields of an existing drh, field will ignore if it is null
     *
     * @param id the id of the drhDTO to save.
     * @param drhDTO the drhDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated drhDTO,
     * or with status {@code 400 (Bad Request)} if the drhDTO is not valid,
     * or with status {@code 404 (Not Found)} if the drhDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the drhDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<DrhDTO>> partialUpdateDrh(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DrhDTO drhDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Drh partially : {}, {}", id, drhDTO);
        if (drhDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, drhDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return drhRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<DrhDTO> result = drhService.partialUpdate(drhDTO);

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
     * {@code GET  /drhs} : get all the drhs.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of drhs in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<DrhDTO>>> getAllDrhs(
        DrhCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Drhs by criteria: {}", criteria);
        return drhService
            .countByCriteria(criteria)
            .zipWith(drhService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /drhs/count} : count all the drhs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countDrhs(DrhCriteria criteria) {
        LOG.debug("REST request to count Drhs by criteria: {}", criteria);
        return drhService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /drhs/:id} : get the "id" drh.
     *
     * @param id the id of the drhDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the drhDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<DrhDTO>> getDrh(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Drh : {}", id);
        Mono<DrhDTO> drhDTO = drhService.findOne(id);
        return ResponseUtil.wrapOrNotFound(drhDTO);
    }

    /**
     * {@code DELETE  /drhs/:id} : delete the "id" drh.
     *
     * @param id the id of the drhDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDrh(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Drh : {}", id);
        return drhService
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
