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
import sn.sonatel.dsi.ins.imoc.domain.EtatPaiement;
import sn.sonatel.dsi.ins.imoc.repository.EtatPaiementRepository;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.EtatPaiement}.
 */
@RestController
@RequestMapping("/api/etat-paiements")
@Transactional
public class EtatPaiementResource {

    private static final Logger LOG = LoggerFactory.getLogger(EtatPaiementResource.class);

    private static final String ENTITY_NAME = "backendEtatPaiement";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EtatPaiementRepository etatPaiementRepository;

    public EtatPaiementResource(EtatPaiementRepository etatPaiementRepository) {
        this.etatPaiementRepository = etatPaiementRepository;
    }

    /**
     * {@code POST  /etat-paiements} : Create a new etatPaiement.
     *
     * @param etatPaiement the etatPaiement to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new etatPaiement, or with status {@code 400 (Bad Request)} if the etatPaiement has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EtatPaiement> createEtatPaiement(@Valid @RequestBody EtatPaiement etatPaiement) throws URISyntaxException {
        LOG.debug("REST request to save EtatPaiement : {}", etatPaiement);
        if (etatPaiement.getId() != null) {
            throw new BadRequestAlertException("A new etatPaiement cannot already have an ID", ENTITY_NAME, "idexists");
        }
        etatPaiement = etatPaiementRepository.save(etatPaiement);
        return ResponseEntity.created(new URI("/api/etat-paiements/" + etatPaiement.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, etatPaiement.getId().toString()))
            .body(etatPaiement);
    }

    /**
     * {@code PUT  /etat-paiements/:id} : Updates an existing etatPaiement.
     *
     * @param id the id of the etatPaiement to save.
     * @param etatPaiement the etatPaiement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etatPaiement,
     * or with status {@code 400 (Bad Request)} if the etatPaiement is not valid,
     * or with status {@code 500 (Internal Server Error)} if the etatPaiement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EtatPaiement> updateEtatPaiement(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EtatPaiement etatPaiement
    ) throws URISyntaxException {
        LOG.debug("REST request to update EtatPaiement : {}, {}", id, etatPaiement);
        if (etatPaiement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, etatPaiement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!etatPaiementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        etatPaiement = etatPaiementRepository.save(etatPaiement);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, etatPaiement.getId().toString()))
            .body(etatPaiement);
    }

    /**
     * {@code PATCH  /etat-paiements/:id} : Partial updates given fields of an existing etatPaiement, field will ignore if it is null
     *
     * @param id the id of the etatPaiement to save.
     * @param etatPaiement the etatPaiement to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etatPaiement,
     * or with status {@code 400 (Bad Request)} if the etatPaiement is not valid,
     * or with status {@code 404 (Not Found)} if the etatPaiement is not found,
     * or with status {@code 500 (Internal Server Error)} if the etatPaiement couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EtatPaiement> partialUpdateEtatPaiement(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EtatPaiement etatPaiement
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EtatPaiement partially : {}, {}", id, etatPaiement);
        if (etatPaiement.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, etatPaiement.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!etatPaiementRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EtatPaiement> result = etatPaiementRepository
            .findById(etatPaiement.getId())
            .map(existingEtatPaiement -> {
                if (etatPaiement.getPaymentNumber() != null) {
                    existingEtatPaiement.setPaymentNumber(etatPaiement.getPaymentNumber());
                }
                if (etatPaiement.getPaymentDate() != null) {
                    existingEtatPaiement.setPaymentDate(etatPaiement.getPaymentDate());
                }
                if (etatPaiement.getAmount() != null) {
                    existingEtatPaiement.setAmount(etatPaiement.getAmount());
                }
                if (etatPaiement.getActCode() != null) {
                    existingEtatPaiement.setActCode(etatPaiement.getActCode());
                }
                if (etatPaiement.getPaymentPhone() != null) {
                    existingEtatPaiement.setPaymentPhone(etatPaiement.getPaymentPhone());
                }
                if (etatPaiement.getStatus() != null) {
                    existingEtatPaiement.setStatus(etatPaiement.getStatus());
                }
                if (etatPaiement.getProcessingDate() != null) {
                    existingEtatPaiement.setProcessingDate(etatPaiement.getProcessingDate());
                }
                if (etatPaiement.getComments() != null) {
                    existingEtatPaiement.setComments(etatPaiement.getComments());
                }

                return existingEtatPaiement;
            })
            .map(etatPaiementRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, etatPaiement.getId().toString())
        );
    }

    /**
     * {@code GET  /etat-paiements} : get all the etatPaiements.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of etatPaiements in body.
     */
    @GetMapping("")
    public ResponseEntity<List<EtatPaiement>> getAllEtatPaiements(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of EtatPaiements");
        Page<EtatPaiement> page = etatPaiementRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /etat-paiements/:id} : get the "id" etatPaiement.
     *
     * @param id the id of the etatPaiement to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the etatPaiement, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EtatPaiement> getEtatPaiement(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EtatPaiement : {}", id);
        Optional<EtatPaiement> etatPaiement = etatPaiementRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(etatPaiement);
    }

    /**
     * {@code DELETE  /etat-paiements/:id} : delete the "id" etatPaiement.
     *
     * @param id the id of the etatPaiement to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtatPaiement(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EtatPaiement : {}", id);
        etatPaiementRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
