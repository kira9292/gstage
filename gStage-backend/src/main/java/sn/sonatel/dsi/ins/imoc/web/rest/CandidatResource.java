package sn.sonatel.dsi.ins.imoc.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.Candidat}.
 */
@RestController
@RequestMapping("/api/candidats")
@Transactional
public class CandidatResource {

    private static final Logger LOG = LoggerFactory.getLogger(CandidatResource.class);

    private static final String ENTITY_NAME = "backendCandidat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CandidatRepository candidatRepository;

    public CandidatResource(CandidatRepository candidatRepository) {
        this.candidatRepository = candidatRepository;
    }

    /**
     * {@code POST  /candidats} : Create a new candidat.
     *
     * @param candidat the candidat to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new candidat, or with status {@code 400 (Bad Request)} if the candidat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Candidat> createCandidat(@Valid @RequestBody Candidat candidat) throws URISyntaxException {
        LOG.debug("REST request to save Candidat : {}", candidat);
        if (candidat.getId() != null) {
            throw new BadRequestAlertException("A new candidat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        candidat = candidatRepository.save(candidat);
        return ResponseEntity.created(new URI("/api/candidats/" + candidat.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, candidat.getId().toString()))
            .body(candidat);
    }

    /**
     * {@code PUT  /candidats/:id} : Updates an existing candidat.
     *
     * @param id the id of the candidat to save.
     * @param candidat the candidat to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated candidat,
     * or with status {@code 400 (Bad Request)} if the candidat is not valid,
     * or with status {@code 500 (Internal Server Error)} if the candidat couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Candidat> updateCandidat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Candidat candidat
    ) throws URISyntaxException {
        LOG.debug("REST request to update Candidat : {}, {}", id, candidat);
        if (candidat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, candidat.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!candidatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        candidat = candidatRepository.save(candidat);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, candidat.getId().toString()))
            .body(candidat);
    }

    /**
     * {@code PATCH  /candidats/:id} : Partial updates given fields of an existing candidat, field will ignore if it is null
     *
     * @param id the id of the candidat to save.
     * @param candidat the candidat to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated candidat,
     * or with status {@code 400 (Bad Request)} if the candidat is not valid,
     * or with status {@code 404 (Not Found)} if the candidat is not found,
     * or with status {@code 500 (Internal Server Error)} if the candidat couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Candidat> partialUpdateCandidat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Candidat candidat
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Candidat partially : {}, {}", id, candidat);
        if (candidat.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, candidat.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!candidatRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Candidat> result = candidatRepository
            .findById(candidat.getId())
            .map(existingCandidat -> {
                if (candidat.getFirstName() != null) {
                    existingCandidat.setFirstName(candidat.getFirstName());
                }
                if (candidat.getLastName() != null) {
                    existingCandidat.setLastName(candidat.getLastName());
                }
                if (candidat.getBirthDate() != null) {
                    existingCandidat.setBirthDate(candidat.getBirthDate());
                }
                if (candidat.getNationality() != null) {
                    existingCandidat.setNationality(candidat.getNationality());
                }
                if (candidat.getBirthPlace() != null) {
                    existingCandidat.setBirthPlace(candidat.getBirthPlace());
                }
                if (candidat.getCni() != null) {
                    existingCandidat.setCni(candidat.getCni());
                }
                if (candidat.getAddress() != null) {
                    existingCandidat.setAddress(candidat.getAddress());
                }
                if (candidat.getEmail() != null) {
                    existingCandidat.setEmail(candidat.getEmail());
                }
                if (candidat.getPhone() != null) {
                    existingCandidat.setPhone(candidat.getPhone());
                }
                if (candidat.getEducationLevel() != null) {
                    existingCandidat.setEducationLevel(candidat.getEducationLevel());
                }
                if (candidat.getSchool() != null) {
                    existingCandidat.setSchool(candidat.getSchool());
                }

                return existingCandidat;
            })
            .map(candidatRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, candidat.getId().toString())
        );
    }

    /**
     * {@code GET  /candidats} : get all the candidats.
     *
     * @param pageable the pagination information.
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of candidats in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Candidat>> getAllCandidats(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "filter", required = false) String filter
    ) {
        if ("demandestage-is-null".equals(filter)) {
            LOG.debug("REST request to get all Candidats where demandeStage is null");
            return new ResponseEntity<>(
                StreamSupport.stream(candidatRepository.findAll().spliterator(), false)
                    .filter(candidat -> candidat.getDemandeStage() == null)
                    .toList(),
                HttpStatus.OK
            );
        }

        if ("validationstatuscandidat-is-null".equals(filter)) {
            LOG.debug("REST request to get all Candidats where validationStatuscandidat is null");
            return new ResponseEntity<>(
                StreamSupport.stream(candidatRepository.findAll().spliterator(), false)
                    .filter(candidat -> candidat.getValidationStatuscandidat() == null)
                    .toList(),
                HttpStatus.OK
            );
        }
        LOG.debug("REST request to get a page of Candidats");
        Page<Candidat> page = candidatRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /candidats/:id} : get the "id" candidat.
     *
     * @param id the id of the candidat to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the candidat, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Candidat> getCandidat(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Candidat : {}", id);
        Optional<Candidat> candidat = candidatRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(candidat);
    }

    /**
     * {@code DELETE  /candidats/:id} : delete the "id" candidat.
     *
     * @param id the id of the candidat to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidat(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Candidat : {}", id);
        candidatRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
