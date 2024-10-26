package sn.sonatel.dsi.ins.imoc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.ContratCriteria;
import sn.sonatel.dsi.ins.imoc.repository.ContratRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.ContratDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.ContratMapper;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.Contrat}.
 */
@Service
@Transactional
public class ContratService {

    private static final Logger LOG = LoggerFactory.getLogger(ContratService.class);

    private final ContratRepository contratRepository;

    private final ContratMapper contratMapper;

    public ContratService(ContratRepository contratRepository, ContratMapper contratMapper) {
        this.contratRepository = contratRepository;
        this.contratMapper = contratMapper;
    }

    /**
     * Save a contrat.
     *
     * @param contratDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ContratDTO> save(ContratDTO contratDTO) {
        LOG.debug("Request to save Contrat : {}", contratDTO);
        return contratRepository.save(contratMapper.toEntity(contratDTO)).map(contratMapper::toDto);
    }

    /**
     * Update a contrat.
     *
     * @param contratDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ContratDTO> update(ContratDTO contratDTO) {
        LOG.debug("Request to update Contrat : {}", contratDTO);
        return contratRepository.save(contratMapper.toEntity(contratDTO)).map(contratMapper::toDto);
    }

    /**
     * Partially update a contrat.
     *
     * @param contratDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ContratDTO> partialUpdate(ContratDTO contratDTO) {
        LOG.debug("Request to partially update Contrat : {}", contratDTO);

        return contratRepository
            .findById(contratDTO.getId())
            .map(existingContrat -> {
                contratMapper.partialUpdate(existingContrat, contratDTO);

                return existingContrat;
            })
            .flatMap(contratRepository::save)
            .map(contratMapper::toDto);
    }

    /**
     * Find contrats by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ContratDTO> findByCriteria(ContratCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Contrats by Criteria");
        return contratRepository.findByCriteria(criteria, pageable).map(contratMapper::toDto);
    }

    /**
     * Find the count of contrats by criteria.
     * @param criteria filtering criteria
     * @return the count of contrats
     */
    public Mono<Long> countByCriteria(ContratCriteria criteria) {
        LOG.debug("Request to get the count of all Contrats by Criteria");
        return contratRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of contrats available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return contratRepository.count();
    }

    /**
     * Get one contrat by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ContratDTO> findOne(Long id) {
        LOG.debug("Request to get Contrat : {}", id);
        return contratRepository.findById(id).map(contratMapper::toDto);
    }

    /**
     * Delete the contrat by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Contrat : {}", id);
        return contratRepository.deleteById(id);
    }
}
