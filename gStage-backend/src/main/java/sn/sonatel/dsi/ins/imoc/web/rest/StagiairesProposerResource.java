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
import sn.sonatel.dsi.ins.imoc.domain.StagiairesProposer;
import sn.sonatel.dsi.ins.imoc.repository.StagiairesProposerRepository;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.StagiairesProposer}.
 */
@RestController
@RequestMapping("/api/stagiaires-proposers")
@Transactional
public class StagiairesProposerResource {

    private static final Logger LOG = LoggerFactory.getLogger(StagiairesProposerResource.class);

    private static final String ENTITY_NAME = "backendStagiairesProposer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StagiairesProposerRepository stagiairesProposerRepository;

    public StagiairesProposerResource(StagiairesProposerRepository stagiairesProposerRepository) {
        this.stagiairesProposerRepository = stagiairesProposerRepository;
    }

    /**
     * {@code POST  /stagiaires-proposers} : Create a new stagiairesProposer.
     *
     * @param stagiairesProposer the stagiairesProposer to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stagiairesProposer, or with status {@code 400 (Bad Request)} if the stagiairesProposer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StagiairesProposer> createStagiairesProposer(@RequestBody StagiairesProposer stagiairesProposer)
        throws URISyntaxException {
        LOG.debug("REST request to save StagiairesProposer : {}", stagiairesProposer);
        if (stagiairesProposer.getId() != null) {
            throw new BadRequestAlertException("A new stagiairesProposer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        stagiairesProposer = stagiairesProposerRepository.save(stagiairesProposer);
        return ResponseEntity.created(new URI("/api/stagiaires-proposers/" + stagiairesProposer.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, stagiairesProposer.getId().toString()))
            .body(stagiairesProposer);
    }

    /**
     * {@code PUT  /stagiaires-proposers/:id} : Updates an existing stagiairesProposer.
     *
     * @param id the id of the stagiairesProposer to save.
     * @param stagiairesProposer the stagiairesProposer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stagiairesProposer,
     * or with status {@code 400 (Bad Request)} if the stagiairesProposer is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stagiairesProposer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StagiairesProposer> updateStagiairesProposer(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StagiairesProposer stagiairesProposer
    ) throws URISyntaxException {
        LOG.debug("REST request to update StagiairesProposer : {}, {}", id, stagiairesProposer);
        if (stagiairesProposer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stagiairesProposer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stagiairesProposerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        stagiairesProposer = stagiairesProposerRepository.save(stagiairesProposer);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, stagiairesProposer.getId().toString()))
            .body(stagiairesProposer);
    }

    /**
     * {@code PATCH  /stagiaires-proposers/:id} : Partial updates given fields of an existing stagiairesProposer, field will ignore if it is null
     *
     * @param id the id of the stagiairesProposer to save.
     * @param stagiairesProposer the stagiairesProposer to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stagiairesProposer,
     * or with status {@code 400 (Bad Request)} if the stagiairesProposer is not valid,
     * or with status {@code 404 (Not Found)} if the stagiairesProposer is not found,
     * or with status {@code 500 (Internal Server Error)} if the stagiairesProposer couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StagiairesProposer> partialUpdateStagiairesProposer(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody StagiairesProposer stagiairesProposer
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StagiairesProposer partially : {}, {}", id, stagiairesProposer);
        if (stagiairesProposer.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stagiairesProposer.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stagiairesProposerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StagiairesProposer> result = stagiairesProposerRepository
            .findById(stagiairesProposer.getId())
            .map(existingStagiairesProposer -> {
                if (stagiairesProposer.getDemandeur() != null) {
                    existingStagiairesProposer.setDemandeur(stagiairesProposer.getDemandeur());
                }
                if (stagiairesProposer.getDirection() != null) {
                    existingStagiairesProposer.setDirection(stagiairesProposer.getDirection());
                }
                if (stagiairesProposer.getNbreStagiaire() != null) {
                    existingStagiairesProposer.setNbreStagiaire(stagiairesProposer.getNbreStagiaire());
                }
                if (stagiairesProposer.getProfilFormation() != null) {
                    existingStagiairesProposer.setProfilFormation(stagiairesProposer.getProfilFormation());
                }
                if (stagiairesProposer.getStagiaieSousRecomandation() != null) {
                    existingStagiairesProposer.setStagiaieSousRecomandation(stagiairesProposer.getStagiaieSousRecomandation());
                }
                if (stagiairesProposer.getCommentaire() != null) {
                    existingStagiairesProposer.setCommentaire(stagiairesProposer.getCommentaire());
                }
                if (stagiairesProposer.getMotif() != null) {
                    existingStagiairesProposer.setMotif(stagiairesProposer.getMotif());
                }
                if (stagiairesProposer.getTraitement() != null) {
                    existingStagiairesProposer.setTraitement(stagiairesProposer.getTraitement());
                }

                return existingStagiairesProposer;
            })
            .map(stagiairesProposerRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, stagiairesProposer.getId().toString())
        );
    }

    /**
     * {@code GET  /stagiaires-proposers} : get all the stagiairesProposers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stagiairesProposers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StagiairesProposer>> getAllStagiairesProposers(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of StagiairesProposers");
        Page<StagiairesProposer> page = stagiairesProposerRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stagiaires-proposers/:id} : get the "id" stagiairesProposer.
     *
     * @param id the id of the stagiairesProposer to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stagiairesProposer, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StagiairesProposer> getStagiairesProposer(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StagiairesProposer : {}", id);
        Optional<StagiairesProposer> stagiairesProposer = stagiairesProposerRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(stagiairesProposer);
    }

    /**
     * {@code DELETE  /stagiaires-proposers/:id} : delete the "id" stagiairesProposer.
     *
     * @param id the id of the stagiairesProposer to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStagiairesProposer(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StagiairesProposer : {}", id);
        stagiairesProposerRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
