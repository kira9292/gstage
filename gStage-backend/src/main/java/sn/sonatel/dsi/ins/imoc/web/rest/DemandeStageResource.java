package sn.sonatel.dsi.ins.imoc.web.rest;

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
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.DemandeStage}.
 */
@RestController
@RequestMapping("/api/demande-stages")
@Transactional
public class DemandeStageResource {

    private static final Logger LOG = LoggerFactory.getLogger(DemandeStageResource.class);

    private static final String ENTITY_NAME = "backendDemandeStage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DemandeStageRepository demandeStageRepository;

    public DemandeStageResource(DemandeStageRepository demandeStageRepository) {
        this.demandeStageRepository = demandeStageRepository;
    }

    /**
     * {@code POST  /demande-stages} : Create a new demandeStage.
     *
     * @param demandeStage the demandeStage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new demandeStage, or with status {@code 400 (Bad Request)} if the demandeStage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DemandeStage> createDemandeStage(@RequestBody DemandeStage demandeStage) throws URISyntaxException {
        LOG.debug("REST request to save DemandeStage : {}", demandeStage);
        if (demandeStage.getId() != null) {
            throw new BadRequestAlertException("A new demandeStage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        demandeStage = demandeStageRepository.save(demandeStage);
        return ResponseEntity.created(new URI("/api/demande-stages/" + demandeStage.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, demandeStage.getId().toString()))
            .body(demandeStage);
    }

    /**
     * {@code PUT  /demande-stages/:id} : Updates an existing demandeStage.
     *
     * @param id the id of the demandeStage to save.
     * @param demandeStage the demandeStage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated demandeStage,
     * or with status {@code 400 (Bad Request)} if the demandeStage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the demandeStage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DemandeStage> updateDemandeStage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DemandeStage demandeStage
    ) throws URISyntaxException {
        LOG.debug("REST request to update DemandeStage : {}, {}", id, demandeStage);
        if (demandeStage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, demandeStage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!demandeStageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        demandeStage = demandeStageRepository.save(demandeStage);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, demandeStage.getId().toString()))
            .body(demandeStage);
    }

    /**
     * {@code PATCH  /demande-stages/:id} : Partial updates given fields of an existing demandeStage, field will ignore if it is null
     *
     * @param id the id of the demandeStage to save.
     * @param demandeStage the demandeStage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated demandeStage,
     * or with status {@code 400 (Bad Request)} if the demandeStage is not valid,
     * or with status {@code 404 (Not Found)} if the demandeStage is not found,
     * or with status {@code 500 (Internal Server Error)} if the demandeStage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DemandeStage> partialUpdateDemandeStage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DemandeStage demandeStage
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DemandeStage partially : {}, {}", id, demandeStage);
        if (demandeStage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, demandeStage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!demandeStageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DemandeStage> result = demandeStageRepository
            .findById(demandeStage.getId())
            .map(existingDemandeStage -> {
                if (demandeStage.getCreationDate() != null) {
                    existingDemandeStage.setCreationDate(demandeStage.getCreationDate());
                }
                if (demandeStage.getInternshipType() != null) {
                    existingDemandeStage.setInternshipType(demandeStage.getInternshipType());
                }
                if (demandeStage.getStartDate() != null) {
                    existingDemandeStage.setStartDate(demandeStage.getStartDate());
                }
                if (demandeStage.getEndDate() != null) {
                    existingDemandeStage.setEndDate(demandeStage.getEndDate());
                }
                if (demandeStage.getCv() != null) {
                    existingDemandeStage.setCv(demandeStage.getCv());
                }
                if (demandeStage.getCvContentType() != null) {
                    existingDemandeStage.setCvContentType(demandeStage.getCvContentType());
                }
                if (demandeStage.getCoverLetter() != null) {
                    existingDemandeStage.setCoverLetter(demandeStage.getCoverLetter());
                }
                if (demandeStage.getCoverLetterContentType() != null) {
                    existingDemandeStage.setCoverLetterContentType(demandeStage.getCoverLetterContentType());
                }
                if (demandeStage.getStatus() != null) {
                    existingDemandeStage.setStatus(demandeStage.getStatus());
                }
                if (demandeStage.getValidated() != null) {
                    existingDemandeStage.setValidated(demandeStage.getValidated());
                }
                if (demandeStage.getFormation() != null) {
                    existingDemandeStage.setFormation(demandeStage.getFormation());
                }

                return existingDemandeStage;
            })
            .map(demandeStageRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, demandeStage.getId().toString())
        );
    }

    /**
     * {@code GET  /demande-stages} : get all the demandeStages.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of demandeStages in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DemandeStage>> getAllDemandeStages(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of DemandeStages");
        Page<DemandeStage> page = demandeStageRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /demande-stages/:id} : get the "id" demandeStage.
     *
     * @param id the id of the demandeStage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the demandeStage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DemandeStage> getDemandeStage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DemandeStage : {}", id);
        Optional<DemandeStage> demandeStage = demandeStageRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(demandeStage);
    }

    /**
     * {@code DELETE  /demande-stages/:id} : delete the "id" demandeStage.
     *
     * @param id the id of the demandeStage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDemandeStage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DemandeStage : {}", id);
        demandeStageRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
