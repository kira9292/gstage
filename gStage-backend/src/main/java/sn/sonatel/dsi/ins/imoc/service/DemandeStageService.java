package sn.sonatel.dsi.ins.imoc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.DemandeStageCriteria;
import sn.sonatel.dsi.ins.imoc.repository.DemandeStageRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.DemandeStageDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.DemandeStageMapper;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.DemandeStage}.
 */
@Service
@Transactional
public class DemandeStageService {

    private static final Logger LOG = LoggerFactory.getLogger(DemandeStageService.class);

    private final DemandeStageRepository demandeStageRepository;

    private final DemandeStageMapper demandeStageMapper;

    public DemandeStageService(DemandeStageRepository demandeStageRepository, DemandeStageMapper demandeStageMapper) {
        this.demandeStageRepository = demandeStageRepository;
        this.demandeStageMapper = demandeStageMapper;
    }

    /**
     * Save a demandeStage.
     *
     * @param demandeStageDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<DemandeStageDTO> save(DemandeStageDTO demandeStageDTO) {
        LOG.debug("Request to save DemandeStage : {}", demandeStageDTO);
        return demandeStageRepository.save(demandeStageMapper.toEntity(demandeStageDTO)).map(demandeStageMapper::toDto);
    }

    /**
     * Update a demandeStage.
     *
     * @param demandeStageDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<DemandeStageDTO> update(DemandeStageDTO demandeStageDTO) {
        LOG.debug("Request to update DemandeStage : {}", demandeStageDTO);
        return demandeStageRepository.save(demandeStageMapper.toEntity(demandeStageDTO)).map(demandeStageMapper::toDto);
    }

    /**
     * Partially update a demandeStage.
     *
     * @param demandeStageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<DemandeStageDTO> partialUpdate(DemandeStageDTO demandeStageDTO) {
        LOG.debug("Request to partially update DemandeStage : {}", demandeStageDTO);

        return demandeStageRepository
            .findById(demandeStageDTO.getId())
            .map(existingDemandeStage -> {
                demandeStageMapper.partialUpdate(existingDemandeStage, demandeStageDTO);

                return existingDemandeStage;
            })
            .flatMap(demandeStageRepository::save)
            .map(demandeStageMapper::toDto);
    }

    /**
     * Find demandeStages by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<DemandeStageDTO> findByCriteria(DemandeStageCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all DemandeStages by Criteria");
        return demandeStageRepository.findByCriteria(criteria, pageable).map(demandeStageMapper::toDto);
    }

    /**
     * Find the count of demandeStages by criteria.
     * @param criteria filtering criteria
     * @return the count of demandeStages
     */
    public Mono<Long> countByCriteria(DemandeStageCriteria criteria) {
        LOG.debug("Request to get the count of all DemandeStages by Criteria");
        return demandeStageRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of demandeStages available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return demandeStageRepository.count();
    }

    /**
     * Get one demandeStage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<DemandeStageDTO> findOne(Long id) {
        LOG.debug("Request to get DemandeStage : {}", id);
        return demandeStageRepository.findById(id).map(demandeStageMapper::toDto);
    }

    /**
     * Delete the demandeStage by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete DemandeStage : {}", id);
        return demandeStageRepository.deleteById(id);
    }
}
