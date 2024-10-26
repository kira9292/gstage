package sn.sonatel.dsi.ins.imoc.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
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
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.DfcCriteria;
import sn.sonatel.dsi.ins.imoc.repository.DfcRepository;
import sn.sonatel.dsi.ins.imoc.service.DfcService;
import sn.sonatel.dsi.ins.imoc.service.dto.DfcDTO;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.Dfc}.
 */
@RestController
@RequestMapping("/api/dfcs")
public class DfcResource {

    private static final Logger LOG = LoggerFactory.getLogger(DfcResource.class);

    private static final String ENTITY_NAME = "gStageDfc";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DfcService dfcService;

    private final DfcRepository dfcRepository;

    public DfcResource(DfcService dfcService, DfcRepository dfcRepository) {
        this.dfcService = dfcService;
        this.dfcRepository = dfcRepository;
    }

    /**
     * {@code POST  /dfcs} : Create a new dfc.
     *
     * @param dfcDTO the dfcDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new dfcDTO, or with status {@code 400 (Bad Request)} if the dfc has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<DfcDTO>> createDfc(@RequestBody DfcDTO dfcDTO) throws URISyntaxException {
        LOG.debug("REST request to save Dfc : {}", dfcDTO);
        if (dfcDTO.getId() != null) {
            throw new BadRequestAlertException("A new dfc cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return dfcService
            .save(dfcDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/dfcs/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code GET  /dfcs} : get all the dfcs.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of dfcs in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<DfcDTO>>> getAllDfcs(
        DfcCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Dfcs by criteria: {}", criteria);
        return dfcService
            .countByCriteria(criteria)
            .zipWith(dfcService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /dfcs/count} : count all the dfcs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countDfcs(DfcCriteria criteria) {
        LOG.debug("REST request to count Dfcs by criteria: {}", criteria);
        return dfcService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /dfcs/:id} : get the "id" dfc.
     *
     * @param id the id of the dfcDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the dfcDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<DfcDTO>> getDfc(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Dfc : {}", id);
        Mono<DfcDTO> dfcDTO = dfcService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dfcDTO);
    }

    /**
     * {@code DELETE  /dfcs/:id} : delete the "id" dfc.
     *
     * @param id the id of the dfcDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteDfc(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Dfc : {}", id);
        return dfcService
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
