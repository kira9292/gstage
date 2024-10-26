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
import sn.sonatel.dsi.ins.imoc.domain.criteria.ValidationCriteria;
import sn.sonatel.dsi.ins.imoc.repository.ValidationRepository;
import sn.sonatel.dsi.ins.imoc.service.ValidationService;
import sn.sonatel.dsi.ins.imoc.service.dto.ValidationDTO;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.Validation}.
 */
@RestController
@RequestMapping("/api/validations")
public class ValidationResource {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationResource.class);

    private static final String ENTITY_NAME = "gStageValidation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ValidationService validationService;

    private final ValidationRepository validationRepository;

    public ValidationResource(ValidationService validationService, ValidationRepository validationRepository) {
        this.validationService = validationService;
        this.validationRepository = validationRepository;
    }

    /**
     * {@code POST  /validations} : Create a new validation.
     *
     * @param validationDTO the validationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new validationDTO, or with status {@code 400 (Bad Request)} if the validation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ValidationDTO>> createValidation(@Valid @RequestBody ValidationDTO validationDTO) throws URISyntaxException {
        LOG.debug("REST request to save Validation : {}", validationDTO);
        if (validationDTO.getId() != null) {
            throw new BadRequestAlertException("A new validation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return validationService
            .save(validationDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/validations/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /validations/:id} : Updates an existing validation.
     *
     * @param id the id of the validationDTO to save.
     * @param validationDTO the validationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated validationDTO,
     * or with status {@code 400 (Bad Request)} if the validationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the validationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ValidationDTO>> updateValidation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ValidationDTO validationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Validation : {}, {}", id, validationDTO);
        if (validationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, validationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return validationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return validationService
                    .update(validationDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /validations/:id} : Partial updates given fields of an existing validation, field will ignore if it is null
     *
     * @param id the id of the validationDTO to save.
     * @param validationDTO the validationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated validationDTO,
     * or with status {@code 400 (Bad Request)} if the validationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the validationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the validationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ValidationDTO>> partialUpdateValidation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ValidationDTO validationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Validation partially : {}, {}", id, validationDTO);
        if (validationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, validationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return validationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ValidationDTO> result = validationService.partialUpdate(validationDTO);

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
     * {@code GET  /validations} : get all the validations.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of validations in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ValidationDTO>>> getAllValidations(
        ValidationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Validations by criteria: {}", criteria);
        return validationService
            .countByCriteria(criteria)
            .zipWith(validationService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /validations/count} : count all the validations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countValidations(ValidationCriteria criteria) {
        LOG.debug("REST request to count Validations by criteria: {}", criteria);
        return validationService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /validations/:id} : get the "id" validation.
     *
     * @param id the id of the validationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the validationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ValidationDTO>> getValidation(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Validation : {}", id);
        Mono<ValidationDTO> validationDTO = validationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(validationDTO);
    }

    /**
     * {@code DELETE  /validations/:id} : delete the "id" validation.
     *
     * @param id the id of the validationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteValidation(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Validation : {}", id);
        return validationService
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
