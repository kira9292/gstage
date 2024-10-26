package sn.sonatel.dsi.ins.imoc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.RestaurationStagiaireCriteria;
import sn.sonatel.dsi.ins.imoc.repository.RestaurationStagiaireRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.RestaurationStagiaireDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.RestaurationStagiaireMapper;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaire}.
 */
@Service
@Transactional
public class RestaurationStagiaireService {

    private static final Logger LOG = LoggerFactory.getLogger(RestaurationStagiaireService.class);

    private final RestaurationStagiaireRepository restaurationStagiaireRepository;

    private final RestaurationStagiaireMapper restaurationStagiaireMapper;

    public RestaurationStagiaireService(
        RestaurationStagiaireRepository restaurationStagiaireRepository,
        RestaurationStagiaireMapper restaurationStagiaireMapper
    ) {
        this.restaurationStagiaireRepository = restaurationStagiaireRepository;
        this.restaurationStagiaireMapper = restaurationStagiaireMapper;
    }

    /**
     * Save a restaurationStagiaire.
     *
     * @param restaurationStagiaireDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RestaurationStagiaireDTO> save(RestaurationStagiaireDTO restaurationStagiaireDTO) {
        LOG.debug("Request to save RestaurationStagiaire : {}", restaurationStagiaireDTO);
        return restaurationStagiaireRepository
            .save(restaurationStagiaireMapper.toEntity(restaurationStagiaireDTO))
            .map(restaurationStagiaireMapper::toDto);
    }

    /**
     * Update a restaurationStagiaire.
     *
     * @param restaurationStagiaireDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<RestaurationStagiaireDTO> update(RestaurationStagiaireDTO restaurationStagiaireDTO) {
        LOG.debug("Request to update RestaurationStagiaire : {}", restaurationStagiaireDTO);
        return restaurationStagiaireRepository
            .save(restaurationStagiaireMapper.toEntity(restaurationStagiaireDTO))
            .map(restaurationStagiaireMapper::toDto);
    }

    /**
     * Partially update a restaurationStagiaire.
     *
     * @param restaurationStagiaireDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<RestaurationStagiaireDTO> partialUpdate(RestaurationStagiaireDTO restaurationStagiaireDTO) {
        LOG.debug("Request to partially update RestaurationStagiaire : {}", restaurationStagiaireDTO);

        return restaurationStagiaireRepository
            .findById(restaurationStagiaireDTO.getId())
            .map(existingRestaurationStagiaire -> {
                restaurationStagiaireMapper.partialUpdate(existingRestaurationStagiaire, restaurationStagiaireDTO);

                return existingRestaurationStagiaire;
            })
            .flatMap(restaurationStagiaireRepository::save)
            .map(restaurationStagiaireMapper::toDto);
    }

    /**
     * Find restaurationStagiaires by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<RestaurationStagiaireDTO> findByCriteria(RestaurationStagiaireCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all RestaurationStagiaires by Criteria");
        return restaurationStagiaireRepository.findByCriteria(criteria, pageable).map(restaurationStagiaireMapper::toDto);
    }

    /**
     * Find the count of restaurationStagiaires by criteria.
     * @param criteria filtering criteria
     * @return the count of restaurationStagiaires
     */
    public Mono<Long> countByCriteria(RestaurationStagiaireCriteria criteria) {
        LOG.debug("Request to get the count of all RestaurationStagiaires by Criteria");
        return restaurationStagiaireRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of restaurationStagiaires available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return restaurationStagiaireRepository.count();
    }

    /**
     * Get one restaurationStagiaire by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<RestaurationStagiaireDTO> findOne(Long id) {
        LOG.debug("Request to get RestaurationStagiaire : {}", id);
        return restaurationStagiaireRepository.findById(id).map(restaurationStagiaireMapper::toDto);
    }

    /**
     * Delete the restaurationStagiaire by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete RestaurationStagiaire : {}", id);
        return restaurationStagiaireRepository.deleteById(id);
    }
}
