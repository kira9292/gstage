package sn.sonatel.dsi.ins.imoc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.CandidatCriteria;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.CandidatDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.CandidatMapper;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.Candidat}.
 */
@Service
@Transactional
public class CandidatService {

    private static final Logger LOG = LoggerFactory.getLogger(CandidatService.class);

    private final CandidatRepository candidatRepository;

    private final CandidatMapper candidatMapper;

    public CandidatService(CandidatRepository candidatRepository, CandidatMapper candidatMapper) {
        this.candidatRepository = candidatRepository;
        this.candidatMapper = candidatMapper;
    }

    /**
     * Save a candidat.
     *
     * @param candidatDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CandidatDTO> save(CandidatDTO candidatDTO) {
        LOG.debug("Request to save Candidat : {}", candidatDTO);
        return candidatRepository.save(candidatMapper.toEntity(candidatDTO)).map(candidatMapper::toDto);
    }

    /**
     * Update a candidat.
     *
     * @param candidatDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<CandidatDTO> update(CandidatDTO candidatDTO) {
        LOG.debug("Request to update Candidat : {}", candidatDTO);
        return candidatRepository.save(candidatMapper.toEntity(candidatDTO)).map(candidatMapper::toDto);
    }

    /**
     * Partially update a candidat.
     *
     * @param candidatDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<CandidatDTO> partialUpdate(CandidatDTO candidatDTO) {
        LOG.debug("Request to partially update Candidat : {}", candidatDTO);

        return candidatRepository
            .findById(candidatDTO.getId())
            .map(existingCandidat -> {
                candidatMapper.partialUpdate(existingCandidat, candidatDTO);

                return existingCandidat;
            })
            .flatMap(candidatRepository::save)
            .map(candidatMapper::toDto);
    }

    /**
     * Find candidats by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CandidatDTO> findByCriteria(CandidatCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Candidats by Criteria");
        return candidatRepository.findByCriteria(criteria, pageable).map(candidatMapper::toDto);
    }

    /**
     * Find the count of candidats by criteria.
     * @param criteria filtering criteria
     * @return the count of candidats
     */
    public Mono<Long> countByCriteria(CandidatCriteria criteria) {
        LOG.debug("Request to get the count of all Candidats by Criteria");
        return candidatRepository.countByCriteria(criteria);
    }

    /**
     *  Get all the candidats where DemandeStage is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<CandidatDTO> findAllWhereDemandeStageIsNull() {
        LOG.debug("Request to get all candidats where DemandeStage is null");
        return candidatRepository.findAllWhereDemandeStageIsNull().map(candidatMapper::toDto);
    }

    /**
     * Returns the number of candidats available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return candidatRepository.count();
    }

    /**
     * Get one candidat by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<CandidatDTO> findOne(Long id) {
        LOG.debug("Request to get Candidat : {}", id);
        return candidatRepository.findById(id).map(candidatMapper::toDto);
    }

    /**
     * Delete the candidat by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Candidat : {}", id);
        return candidatRepository.deleteById(id);
    }
}
