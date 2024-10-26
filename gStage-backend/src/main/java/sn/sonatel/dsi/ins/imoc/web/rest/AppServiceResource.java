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
import sn.sonatel.dsi.ins.imoc.domain.criteria.AppServiceCriteria;
import sn.sonatel.dsi.ins.imoc.repository.AppServiceRepository;
import sn.sonatel.dsi.ins.imoc.service.AppServiceService;
import sn.sonatel.dsi.ins.imoc.service.dto.AppServiceDTO;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.AppService}.
 */
@RestController
@RequestMapping("/api/app-services")
public class AppServiceResource {

    private static final Logger LOG = LoggerFactory.getLogger(AppServiceResource.class);

    private static final String ENTITY_NAME = "gStageAppService";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppServiceService appServiceService;

    private final AppServiceRepository appServiceRepository;

    public AppServiceResource(AppServiceService appServiceService, AppServiceRepository appServiceRepository) {
        this.appServiceService = appServiceService;
        this.appServiceRepository = appServiceRepository;
    }

    /**
     * {@code POST  /app-services} : Create a new appService.
     *
     * @param appServiceDTO the appServiceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appServiceDTO, or with status {@code 400 (Bad Request)} if the appService has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AppServiceDTO>> createAppService(@Valid @RequestBody AppServiceDTO appServiceDTO) throws URISyntaxException {
        LOG.debug("REST request to save AppService : {}", appServiceDTO);
        if (appServiceDTO.getId() != null) {
            throw new BadRequestAlertException("A new appService cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return appServiceService
            .save(appServiceDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/app-services/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /app-services/:id} : Updates an existing appService.
     *
     * @param id the id of the appServiceDTO to save.
     * @param appServiceDTO the appServiceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appServiceDTO,
     * or with status {@code 400 (Bad Request)} if the appServiceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appServiceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AppServiceDTO>> updateAppService(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AppServiceDTO appServiceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AppService : {}, {}", id, appServiceDTO);
        if (appServiceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appServiceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return appServiceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return appServiceService
                    .update(appServiceDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /app-services/:id} : Partial updates given fields of an existing appService, field will ignore if it is null
     *
     * @param id the id of the appServiceDTO to save.
     * @param appServiceDTO the appServiceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appServiceDTO,
     * or with status {@code 400 (Bad Request)} if the appServiceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the appServiceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the appServiceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AppServiceDTO>> partialUpdateAppService(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AppServiceDTO appServiceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AppService partially : {}, {}", id, appServiceDTO);
        if (appServiceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appServiceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return appServiceRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<AppServiceDTO> result = appServiceService.partialUpdate(appServiceDTO);

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
     * {@code GET  /app-services} : get all the appServices.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appServices in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<AppServiceDTO>>> getAllAppServices(
        AppServiceCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get AppServices by criteria: {}", criteria);
        return appServiceService
            .countByCriteria(criteria)
            .zipWith(appServiceService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /app-services/count} : count all the appServices.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countAppServices(AppServiceCriteria criteria) {
        LOG.debug("REST request to count AppServices by criteria: {}", criteria);
        return appServiceService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /app-services/:id} : get the "id" appService.
     *
     * @param id the id of the appServiceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appServiceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AppServiceDTO>> getAppService(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AppService : {}", id);
        Mono<AppServiceDTO> appServiceDTO = appServiceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(appServiceDTO);
    }

    /**
     * {@code DELETE  /app-services/:id} : delete the "id" appService.
     *
     * @param id the id of the appServiceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAppService(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AppService : {}", id);
        return appServiceService
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
