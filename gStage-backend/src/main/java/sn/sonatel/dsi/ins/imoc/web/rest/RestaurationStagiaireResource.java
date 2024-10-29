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
import sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaire;
import sn.sonatel.dsi.ins.imoc.repository.RestaurationStagiaireRepository;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaire}.
 */
@RestController
@RequestMapping("/api/restauration-stagiaires")
@Transactional
public class RestaurationStagiaireResource {

    private static final Logger LOG = LoggerFactory.getLogger(RestaurationStagiaireResource.class);

    private static final String ENTITY_NAME = "backendRestaurationStagiaire";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RestaurationStagiaireRepository restaurationStagiaireRepository;

    public RestaurationStagiaireResource(RestaurationStagiaireRepository restaurationStagiaireRepository) {
        this.restaurationStagiaireRepository = restaurationStagiaireRepository;
    }

    /**
     * {@code POST  /restauration-stagiaires} : Create a new restaurationStagiaire.
     *
     * @param restaurationStagiaire the restaurationStagiaire to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new restaurationStagiaire, or with status {@code 400 (Bad Request)} if the restaurationStagiaire has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RestaurationStagiaire> createRestaurationStagiaire(
        @Valid @RequestBody RestaurationStagiaire restaurationStagiaire
    ) throws URISyntaxException {
        LOG.debug("REST request to save RestaurationStagiaire : {}", restaurationStagiaire);
        if (restaurationStagiaire.getId() != null) {
            throw new BadRequestAlertException("A new restaurationStagiaire cannot already have an ID", ENTITY_NAME, "idexists");
        }
        restaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire);
        return ResponseEntity.created(new URI("/api/restauration-stagiaires/" + restaurationStagiaire.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, restaurationStagiaire.getId().toString()))
            .body(restaurationStagiaire);
    }

    /**
     * {@code PUT  /restauration-stagiaires/:id} : Updates an existing restaurationStagiaire.
     *
     * @param id the id of the restaurationStagiaire to save.
     * @param restaurationStagiaire the restaurationStagiaire to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurationStagiaire,
     * or with status {@code 400 (Bad Request)} if the restaurationStagiaire is not valid,
     * or with status {@code 500 (Internal Server Error)} if the restaurationStagiaire couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestaurationStagiaire> updateRestaurationStagiaire(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RestaurationStagiaire restaurationStagiaire
    ) throws URISyntaxException {
        LOG.debug("REST request to update RestaurationStagiaire : {}, {}", id, restaurationStagiaire);
        if (restaurationStagiaire.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, restaurationStagiaire.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!restaurationStagiaireRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        restaurationStagiaire = restaurationStagiaireRepository.save(restaurationStagiaire);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, restaurationStagiaire.getId().toString()))
            .body(restaurationStagiaire);
    }

    /**
     * {@code PATCH  /restauration-stagiaires/:id} : Partial updates given fields of an existing restaurationStagiaire, field will ignore if it is null
     *
     * @param id the id of the restaurationStagiaire to save.
     * @param restaurationStagiaire the restaurationStagiaire to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurationStagiaire,
     * or with status {@code 400 (Bad Request)} if the restaurationStagiaire is not valid,
     * or with status {@code 404 (Not Found)} if the restaurationStagiaire is not found,
     * or with status {@code 500 (Internal Server Error)} if the restaurationStagiaire couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RestaurationStagiaire> partialUpdateRestaurationStagiaire(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RestaurationStagiaire restaurationStagiaire
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RestaurationStagiaire partially : {}, {}", id, restaurationStagiaire);
        if (restaurationStagiaire.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, restaurationStagiaire.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!restaurationStagiaireRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RestaurationStagiaire> result = restaurationStagiaireRepository
            .findById(restaurationStagiaire.getId())
            .map(existingRestaurationStagiaire -> {
                if (restaurationStagiaire.getStartDate() != null) {
                    existingRestaurationStagiaire.setStartDate(restaurationStagiaire.getStartDate());
                }
                if (restaurationStagiaire.getEndDate() != null) {
                    existingRestaurationStagiaire.setEndDate(restaurationStagiaire.getEndDate());
                }
                if (restaurationStagiaire.getStatus() != null) {
                    existingRestaurationStagiaire.setStatus(restaurationStagiaire.getStatus());
                }
                if (restaurationStagiaire.getCardNumber() != null) {
                    existingRestaurationStagiaire.setCardNumber(restaurationStagiaire.getCardNumber());
                }

                return existingRestaurationStagiaire;
            })
            .map(restaurationStagiaireRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, restaurationStagiaire.getId().toString())
        );
    }

    /**
     * {@code GET  /restauration-stagiaires} : get all the restaurationStagiaires.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of restaurationStagiaires in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RestaurationStagiaire>> getAllRestaurationStagiaires(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of RestaurationStagiaires");
        Page<RestaurationStagiaire> page = restaurationStagiaireRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /restauration-stagiaires/:id} : get the "id" restaurationStagiaire.
     *
     * @param id the id of the restaurationStagiaire to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the restaurationStagiaire, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RestaurationStagiaire> getRestaurationStagiaire(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RestaurationStagiaire : {}", id);
        Optional<RestaurationStagiaire> restaurationStagiaire = restaurationStagiaireRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(restaurationStagiaire);
    }

    /**
     * {@code DELETE  /restauration-stagiaires/:id} : delete the "id" restaurationStagiaire.
     *
     * @param id the id of the restaurationStagiaire to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurationStagiaire(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RestaurationStagiaire : {}", id);
        restaurationStagiaireRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
