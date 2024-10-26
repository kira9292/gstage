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
import sn.sonatel.dsi.ins.imoc.domain.criteria.ManagerCriteria;
import sn.sonatel.dsi.ins.imoc.repository.ManagerRepository;
import sn.sonatel.dsi.ins.imoc.service.ManagerService;
import sn.sonatel.dsi.ins.imoc.service.dto.ManagerDTO;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.Manager}.
 */
@RestController
@RequestMapping("/api/managers")
public class ManagerResource {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerResource.class);

    private static final String ENTITY_NAME = "gStageManager";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ManagerService managerService;

    private final ManagerRepository managerRepository;

    public ManagerResource(ManagerService managerService, ManagerRepository managerRepository) {
        this.managerService = managerService;
        this.managerRepository = managerRepository;
    }

    /**
     * {@code POST  /managers} : Create a new manager.
     *
     * @param managerDTO the managerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new managerDTO, or with status {@code 400 (Bad Request)} if the manager has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ManagerDTO>> createManager(@Valid @RequestBody ManagerDTO managerDTO) throws URISyntaxException {
        LOG.debug("REST request to save Manager : {}", managerDTO);
        if (managerDTO.getId() != null) {
            throw new BadRequestAlertException("A new manager cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return managerService
            .save(managerDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/managers/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /managers/:id} : Updates an existing manager.
     *
     * @param id the id of the managerDTO to save.
     * @param managerDTO the managerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated managerDTO,
     * or with status {@code 400 (Bad Request)} if the managerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the managerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ManagerDTO>> updateManager(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ManagerDTO managerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Manager : {}, {}", id, managerDTO);
        if (managerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, managerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return managerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return managerService
                    .update(managerDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /managers/:id} : Partial updates given fields of an existing manager, field will ignore if it is null
     *
     * @param id the id of the managerDTO to save.
     * @param managerDTO the managerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated managerDTO,
     * or with status {@code 400 (Bad Request)} if the managerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the managerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the managerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ManagerDTO>> partialUpdateManager(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ManagerDTO managerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Manager partially : {}, {}", id, managerDTO);
        if (managerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, managerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return managerRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ManagerDTO> result = managerService.partialUpdate(managerDTO);

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
     * {@code GET  /managers} : get all the managers.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of managers in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ManagerDTO>>> getAllManagers(
        ManagerCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Managers by criteria: {}", criteria);
        return managerService
            .countByCriteria(criteria)
            .zipWith(managerService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /managers/count} : count all the managers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countManagers(ManagerCriteria criteria) {
        LOG.debug("REST request to count Managers by criteria: {}", criteria);
        return managerService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /managers/:id} : get the "id" manager.
     *
     * @param id the id of the managerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the managerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ManagerDTO>> getManager(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Manager : {}", id);
        Mono<ManagerDTO> managerDTO = managerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(managerDTO);
    }

    /**
     * {@code DELETE  /managers/:id} : delete the "id" manager.
     *
     * @param id the id of the managerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteManager(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Manager : {}", id);
        return managerService
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
