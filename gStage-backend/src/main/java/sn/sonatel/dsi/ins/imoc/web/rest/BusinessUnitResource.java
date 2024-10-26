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
import sn.sonatel.dsi.ins.imoc.domain.criteria.BusinessUnitCriteria;
import sn.sonatel.dsi.ins.imoc.repository.BusinessUnitRepository;
import sn.sonatel.dsi.ins.imoc.service.BusinessUnitService;
import sn.sonatel.dsi.ins.imoc.service.dto.BusinessUnitDTO;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.BusinessUnit}.
 */
@RestController
@RequestMapping("/api/business-units")
public class BusinessUnitResource {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessUnitResource.class);

    private static final String ENTITY_NAME = "gStageBusinessUnit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BusinessUnitService businessUnitService;

    private final BusinessUnitRepository businessUnitRepository;

    public BusinessUnitResource(BusinessUnitService businessUnitService, BusinessUnitRepository businessUnitRepository) {
        this.businessUnitService = businessUnitService;
        this.businessUnitRepository = businessUnitRepository;
    }

    /**
     * {@code POST  /business-units} : Create a new businessUnit.
     *
     * @param businessUnitDTO the businessUnitDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new businessUnitDTO, or with status {@code 400 (Bad Request)} if the businessUnit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<BusinessUnitDTO>> createBusinessUnit(@Valid @RequestBody BusinessUnitDTO businessUnitDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save BusinessUnit : {}", businessUnitDTO);
        if (businessUnitDTO.getId() != null) {
            throw new BadRequestAlertException("A new businessUnit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return businessUnitService
            .save(businessUnitDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/business-units/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /business-units/:id} : Updates an existing businessUnit.
     *
     * @param id the id of the businessUnitDTO to save.
     * @param businessUnitDTO the businessUnitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessUnitDTO,
     * or with status {@code 400 (Bad Request)} if the businessUnitDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the businessUnitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<BusinessUnitDTO>> updateBusinessUnit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BusinessUnitDTO businessUnitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update BusinessUnit : {}, {}", id, businessUnitDTO);
        if (businessUnitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, businessUnitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return businessUnitRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return businessUnitService
                    .update(businessUnitDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /business-units/:id} : Partial updates given fields of an existing businessUnit, field will ignore if it is null
     *
     * @param id the id of the businessUnitDTO to save.
     * @param businessUnitDTO the businessUnitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated businessUnitDTO,
     * or with status {@code 400 (Bad Request)} if the businessUnitDTO is not valid,
     * or with status {@code 404 (Not Found)} if the businessUnitDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the businessUnitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<BusinessUnitDTO>> partialUpdateBusinessUnit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BusinessUnitDTO businessUnitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BusinessUnit partially : {}, {}", id, businessUnitDTO);
        if (businessUnitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, businessUnitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return businessUnitRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<BusinessUnitDTO> result = businessUnitService.partialUpdate(businessUnitDTO);

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
     * {@code GET  /business-units} : get all the businessUnits.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of businessUnits in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<BusinessUnitDTO>>> getAllBusinessUnits(
        BusinessUnitCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get BusinessUnits by criteria: {}", criteria);
        return businessUnitService
            .countByCriteria(criteria)
            .zipWith(businessUnitService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /business-units/count} : count all the businessUnits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countBusinessUnits(BusinessUnitCriteria criteria) {
        LOG.debug("REST request to count BusinessUnits by criteria: {}", criteria);
        return businessUnitService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /business-units/:id} : get the "id" businessUnit.
     *
     * @param id the id of the businessUnitDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the businessUnitDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<BusinessUnitDTO>> getBusinessUnit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BusinessUnit : {}", id);
        Mono<BusinessUnitDTO> businessUnitDTO = businessUnitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(businessUnitDTO);
    }

    /**
     * {@code DELETE  /business-units/:id} : delete the "id" businessUnit.
     *
     * @param id the id of the businessUnitDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteBusinessUnit(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BusinessUnit : {}", id);
        return businessUnitService
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
