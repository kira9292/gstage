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
import sn.sonatel.dsi.ins.imoc.domain.criteria.AssistantGWTECriteria;
import sn.sonatel.dsi.ins.imoc.repository.AssistantGWTERepository;
import sn.sonatel.dsi.ins.imoc.service.AssistantGWTEService;
import sn.sonatel.dsi.ins.imoc.service.dto.AssistantGWTEDTO;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.AssistantGWTE}.
 */
@RestController
@RequestMapping("/api/assistant-gwtes")
public class AssistantGWTEResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssistantGWTEResource.class);

    private static final String ENTITY_NAME = "gStageAssistantGwte";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AssistantGWTEService assistantGWTEService;

    private final AssistantGWTERepository assistantGWTERepository;

    public AssistantGWTEResource(AssistantGWTEService assistantGWTEService, AssistantGWTERepository assistantGWTERepository) {
        this.assistantGWTEService = assistantGWTEService;
        this.assistantGWTERepository = assistantGWTERepository;
    }

    /**
     * {@code POST  /assistant-gwtes} : Create a new assistantGWTE.
     *
     * @param assistantGWTEDTO the assistantGWTEDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assistantGWTEDTO, or with status {@code 400 (Bad Request)} if the assistantGWTE has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<AssistantGWTEDTO>> createAssistantGWTE(@Valid @RequestBody AssistantGWTEDTO assistantGWTEDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AssistantGWTE : {}", assistantGWTEDTO);
        if (assistantGWTEDTO.getId() != null) {
            throw new BadRequestAlertException("A new assistantGWTE cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return assistantGWTEService
            .save(assistantGWTEDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/assistant-gwtes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /assistant-gwtes/:id} : Updates an existing assistantGWTE.
     *
     * @param id the id of the assistantGWTEDTO to save.
     * @param assistantGWTEDTO the assistantGWTEDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assistantGWTEDTO,
     * or with status {@code 400 (Bad Request)} if the assistantGWTEDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assistantGWTEDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<AssistantGWTEDTO>> updateAssistantGWTE(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AssistantGWTEDTO assistantGWTEDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AssistantGWTE : {}, {}", id, assistantGWTEDTO);
        if (assistantGWTEDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assistantGWTEDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return assistantGWTERepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return assistantGWTEService
                    .update(assistantGWTEDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /assistant-gwtes/:id} : Partial updates given fields of an existing assistantGWTE, field will ignore if it is null
     *
     * @param id the id of the assistantGWTEDTO to save.
     * @param assistantGWTEDTO the assistantGWTEDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assistantGWTEDTO,
     * or with status {@code 400 (Bad Request)} if the assistantGWTEDTO is not valid,
     * or with status {@code 404 (Not Found)} if the assistantGWTEDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the assistantGWTEDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AssistantGWTEDTO>> partialUpdateAssistantGWTE(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AssistantGWTEDTO assistantGWTEDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AssistantGWTE partially : {}, {}", id, assistantGWTEDTO);
        if (assistantGWTEDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assistantGWTEDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return assistantGWTERepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<AssistantGWTEDTO> result = assistantGWTEService.partialUpdate(assistantGWTEDTO);

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
     * {@code GET  /assistant-gwtes} : get all the assistantGWTES.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of assistantGWTES in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<AssistantGWTEDTO>>> getAllAssistantGWTES(
        AssistantGWTECriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get AssistantGWTES by criteria: {}", criteria);
        return assistantGWTEService
            .countByCriteria(criteria)
            .zipWith(assistantGWTEService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /assistant-gwtes/count} : count all the assistantGWTES.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countAssistantGWTES(AssistantGWTECriteria criteria) {
        LOG.debug("REST request to count AssistantGWTES by criteria: {}", criteria);
        return assistantGWTEService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /assistant-gwtes/:id} : get the "id" assistantGWTE.
     *
     * @param id the id of the assistantGWTEDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assistantGWTEDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AssistantGWTEDTO>> getAssistantGWTE(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AssistantGWTE : {}", id);
        Mono<AssistantGWTEDTO> assistantGWTEDTO = assistantGWTEService.findOne(id);
        return ResponseUtil.wrapOrNotFound(assistantGWTEDTO);
    }

    /**
     * {@code DELETE  /assistant-gwtes/:id} : delete the "id" assistantGWTE.
     *
     * @param id the id of the assistantGWTEDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAssistantGWTE(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AssistantGWTE : {}", id);
        return assistantGWTEService
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
