package sn.sonatel.dsi.ins.imoc.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.sonatel.dsi.ins.imoc.domain.Contrat;
import sn.sonatel.dsi.ins.imoc.repository.ContratRepository;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.Contrat}.
 */
@RestController
@RequestMapping("/api/contrats")
@Transactional
public class ContratResource {

    private static final Logger LOG = LoggerFactory.getLogger(ContratResource.class);

    private static final String ENTITY_NAME = "backendContrat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ContratRepository contratRepository;

    public ContratResource(ContratRepository contratRepository) {
        this.contratRepository = contratRepository;
    }

    /**
     * {@code POST  /contrats} : Create a new contrat.
     *
     * @param contrat the contrat to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new contrat, or with status {@code 400 (Bad Request)} if the contrat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Contrat> createContrat(@Valid @RequestBody Contrat contrat) throws URISyntaxException {
        LOG.debug("REST request to save Contrat : {}", contrat);
        if (contrat.getId() != null) {
            throw new BadRequestAlertException("A new contrat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        contrat = contratRepository.save(contrat);
        return ResponseEntity.created(new URI("/api/contrats/" + contrat.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, contrat.getId().toString()))
            .body(contrat);
    }

    /**
     * {@code PUT  /contrats/:id} : Updates an existing contrat.
     *
     * @param id the id of the contrat to save.
     * @param contrat the contrat to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contrat,
     * or with status {@code 400 (Bad Request)} if the contrat is not valid,
     * or with status {@code 500 (Internal Server Error)} if the contrat couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Contrat> updateContrat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Contrat contrat
    ) throws URISyntaxException {
        LOG.debug("REST request to update Contrat : {}, {}", id, contrat);
        if (contrat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contrat.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contratRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        contrat = contratRepository.save(contrat);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, contrat.getId().toString()))
            .body(contrat);
    }

    /**
     * {@code PATCH  /contrats/:id} : Partial updates given fields of an existing contrat, field will ignore if it is null
     *
     * @param id the id of the contrat to save.
     * @param contrat the contrat to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated contrat,
     * or with status {@code 400 (Bad Request)} if the contrat is not valid,
     * or with status {@code 404 (Not Found)} if the contrat is not found,
     * or with status {@code 500 (Internal Server Error)} if the contrat couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Contrat> partialUpdateContrat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Contrat contrat
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Contrat partially : {}, {}", id, contrat);
        if (contrat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, contrat.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!contratRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Contrat> result = contratRepository
            .findById(contrat.getId())
            .map(existingContrat -> {
                if (contrat.getReference() != null) {
                    existingContrat.setReference(contrat.getReference());
                }
                if (contrat.getStartDate() != null) {
                    existingContrat.setStartDate(contrat.getStartDate());
                }
                if (contrat.getEndDate() != null) {
                    existingContrat.setEndDate(contrat.getEndDate());
                }
                if (contrat.getCompensation() != null) {
                    existingContrat.setCompensation(contrat.getCompensation());
                }
                if (contrat.getStatus() != null) {
                    existingContrat.setStatus(contrat.getStatus());
                }
                if (contrat.getAssignmentSite() != null) {
                    existingContrat.setAssignmentSite(contrat.getAssignmentSite());
                }
                if (contrat.getSignatureDate() != null) {
                    existingContrat.setSignatureDate(contrat.getSignatureDate());
                }
                if (contrat.getComments() != null) {
                    existingContrat.setComments(contrat.getComments());
                }
                if (contrat.getDocs() != null) {
                    existingContrat.setDocs(contrat.getDocs());
                }
                if (contrat.getDocsContentType() != null) {
                    existingContrat.setDocsContentType(contrat.getDocsContentType());
                }

                return existingContrat;
            })
            .map(contratRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, contrat.getId().toString())
        );
    }

    /**
     * {@code GET  /contrats} : get all the contrats.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of contrats in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Contrat>> getAllContrats(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Contrats");
        Page<Contrat> page = contratRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /contrats/:id} : get the "id" contrat.
     *
     * @param id the id of the contrat to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the contrat, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Contrat> getContrat(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Contrat : {}", id);
        Optional<Contrat> contrat = contratRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(contrat);
    }

    /**
     * {@code DELETE  /contrats/:id} : delete the "id" contrat.
     *
     * @param id the id of the contrat to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContrat(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Contrat : {}", id);
        contratRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
