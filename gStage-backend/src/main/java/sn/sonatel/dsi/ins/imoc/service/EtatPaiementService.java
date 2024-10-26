package sn.sonatel.dsi.ins.imoc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.EtatPaiementCriteria;
import sn.sonatel.dsi.ins.imoc.repository.EtatPaiementRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.EtatPaiementDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.EtatPaiementMapper;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.EtatPaiement}.
 */
@Service
@Transactional
public class EtatPaiementService {

    private static final Logger LOG = LoggerFactory.getLogger(EtatPaiementService.class);

    private final EtatPaiementRepository etatPaiementRepository;

    private final EtatPaiementMapper etatPaiementMapper;

    public EtatPaiementService(EtatPaiementRepository etatPaiementRepository, EtatPaiementMapper etatPaiementMapper) {
        this.etatPaiementRepository = etatPaiementRepository;
        this.etatPaiementMapper = etatPaiementMapper;
    }

    /**
     * Save a etatPaiement.
     *
     * @param etatPaiementDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<EtatPaiementDTO> save(EtatPaiementDTO etatPaiementDTO) {
        LOG.debug("Request to save EtatPaiement : {}", etatPaiementDTO);
        return etatPaiementRepository.save(etatPaiementMapper.toEntity(etatPaiementDTO)).map(etatPaiementMapper::toDto);
    }

    /**
     * Update a etatPaiement.
     *
     * @param etatPaiementDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<EtatPaiementDTO> update(EtatPaiementDTO etatPaiementDTO) {
        LOG.debug("Request to update EtatPaiement : {}", etatPaiementDTO);
        return etatPaiementRepository.save(etatPaiementMapper.toEntity(etatPaiementDTO)).map(etatPaiementMapper::toDto);
    }

    /**
     * Partially update a etatPaiement.
     *
     * @param etatPaiementDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<EtatPaiementDTO> partialUpdate(EtatPaiementDTO etatPaiementDTO) {
        LOG.debug("Request to partially update EtatPaiement : {}", etatPaiementDTO);

        return etatPaiementRepository
            .findById(etatPaiementDTO.getId())
            .map(existingEtatPaiement -> {
                etatPaiementMapper.partialUpdate(existingEtatPaiement, etatPaiementDTO);

                return existingEtatPaiement;
            })
            .flatMap(etatPaiementRepository::save)
            .map(etatPaiementMapper::toDto);
    }

    /**
     * Find etatPaiements by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<EtatPaiementDTO> findByCriteria(EtatPaiementCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all EtatPaiements by Criteria");
        return etatPaiementRepository.findByCriteria(criteria, pageable).map(etatPaiementMapper::toDto);
    }

    /**
     * Find the count of etatPaiements by criteria.
     * @param criteria filtering criteria
     * @return the count of etatPaiements
     */
    public Mono<Long> countByCriteria(EtatPaiementCriteria criteria) {
        LOG.debug("Request to get the count of all EtatPaiements by Criteria");
        return etatPaiementRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of etatPaiements available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return etatPaiementRepository.count();
    }

    /**
     * Get one etatPaiement by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<EtatPaiementDTO> findOne(Long id) {
        LOG.debug("Request to get EtatPaiement : {}", id);
        return etatPaiementRepository.findById(id).map(etatPaiementMapper::toDto);
    }

    /**
     * Delete the etatPaiement by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete EtatPaiement : {}", id);
        return etatPaiementRepository.deleteById(id);
    }
}
