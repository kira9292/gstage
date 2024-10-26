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
import sn.sonatel.dsi.ins.imoc.domain.criteria.ContratCriteria;
import sn.sonatel.dsi.ins.imoc.repository.ContratRepository;
import sn.sonatel.dsi.ins.imoc.service.ContratService;
import sn.sonatel.dsi.ins.imoc.service.dto.ContratDTO;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.Contrat}.
 */
@RestController
@RequestMapping("/api/contrats")
public class ContratResource {

    private static final Logger LOG = LoggerFactory.getLogger(ContratResource.class);

    private static final String ENTITY_NAME = "gStageContrat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ContratService contratService;

    private final ContratRepository contratRepository;

    public ContratResource(ContratService contratService, ContratRepository contratRepository) {
        this.contratService = contratService;
        this.contratRepository = contratRepository;
    }

    /**
     * {@code POST  /contrats} : Create a new contrat.
     *
     * @param contratDTO the contratDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new contratDTO, or with status {@code 400 (Bad Request)} if the contrat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ContratDTO>> createContrat(@Valid @RequestBody ContratDTO contratDTO) throws URISyntaxException {
        LOG.debug("REST request to save Contrat : {}", contratDTO);
        if (contratDTO.getId() != null) {
            throw new BadRequestAlertException("A new contrat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return contratService
            .save(contratDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/contrats/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /contrats/:id} : Updates an existing contrat.
     *
     * @param id the id of the contratDTO to save.
     * @param contratDTO the contratDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contratDTO,
     * or with status {@code 400 (Bad Request)} if the contratDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contratDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ContratDTO>> updateContrat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ContratDTO contratDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Contrat : {}, {}", id, contratDTO);
        if (contratDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contratDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return contratRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return contratService
                    .update(contratDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /contrats/:id} : Partial updates given fields of an existing contrat, field will ignore if it is null
     *
     * @param id the id of the contratDTO to save.
     * @param contratDTO the contratDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contratDTO,
     * or with status {@code 400 (Bad Request)} if the contratDTO is not valid,
     * or with status {@code 404 (Not Found)} if the contratDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the contratDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ContratDTO>> partialUpdateContrat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ContratDTO contratDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Contrat partially : {}, {}", id, contratDTO);
        if (contratDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contratDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return contratRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ContratDTO> result = contratService.partialUpdate(contratDTO);

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
     * {@code GET  /contrats} : get all the contrats.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of contrats in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ContratDTO>>> getAllContrats(
        ContratCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Contrats by criteria: {}", criteria);
        return contratService
            .countByCriteria(criteria)
            .zipWith(contratService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /contrats/count} : count all the contrats.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countContrats(ContratCriteria criteria) {
        LOG.debug("REST request to count Contrats by criteria: {}", criteria);
        return contratService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /contrats/:id} : get the "id" contrat.
     *
     * @param id the id of the contratDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the contratDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ContratDTO>> getContrat(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Contrat : {}", id);
        Mono<ContratDTO> contratDTO = contratService.findOne(id);
        return ResponseUtil.wrapOrNotFound(contratDTO);
    }

    /**
     * {@code DELETE  /contrats/:id} : delete the "id" contrat.
     *
     * @param id the id of the contratDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteContrat(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Contrat : {}", id);
        return contratService
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
