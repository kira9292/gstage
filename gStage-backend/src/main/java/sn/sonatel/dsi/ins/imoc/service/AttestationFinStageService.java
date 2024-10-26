package sn.sonatel.dsi.ins.imoc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.AttestationFinStageCriteria;
import sn.sonatel.dsi.ins.imoc.repository.AttestationFinStageRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.AttestationFinStageDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.AttestationFinStageMapper;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage}.
 */
@Service
@Transactional
public class AttestationFinStageService {

    private static final Logger LOG = LoggerFactory.getLogger(AttestationFinStageService.class);

    private final AttestationFinStageRepository attestationFinStageRepository;

    private final AttestationFinStageMapper attestationFinStageMapper;

    public AttestationFinStageService(
        AttestationFinStageRepository attestationFinStageRepository,
        AttestationFinStageMapper attestationFinStageMapper
    ) {
        this.attestationFinStageRepository = attestationFinStageRepository;
        this.attestationFinStageMapper = attestationFinStageMapper;
    }

    /**
     * Save a attestationFinStage.
     *
     * @param attestationFinStageDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AttestationFinStageDTO> save(AttestationFinStageDTO attestationFinStageDTO) {
        LOG.debug("Request to save AttestationFinStage : {}", attestationFinStageDTO);
        return attestationFinStageRepository
            .save(attestationFinStageMapper.toEntity(attestationFinStageDTO))
            .map(attestationFinStageMapper::toDto);
    }

    /**
     * Update a attestationFinStage.
     *
     * @param attestationFinStageDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AttestationFinStageDTO> update(AttestationFinStageDTO attestationFinStageDTO) {
        LOG.debug("Request to update AttestationFinStage : {}", attestationFinStageDTO);
        return attestationFinStageRepository
            .save(attestationFinStageMapper.toEntity(attestationFinStageDTO))
            .map(attestationFinStageMapper::toDto);
    }

    /**
     * Partially update a attestationFinStage.
     *
     * @param attestationFinStageDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<AttestationFinStageDTO> partialUpdate(AttestationFinStageDTO attestationFinStageDTO) {
        LOG.debug("Request to partially update AttestationFinStage : {}", attestationFinStageDTO);

        return attestationFinStageRepository
            .findById(attestationFinStageDTO.getId())
            .map(existingAttestationFinStage -> {
                attestationFinStageMapper.partialUpdate(existingAttestationFinStage, attestationFinStageDTO);

                return existingAttestationFinStage;
            })
            .flatMap(attestationFinStageRepository::save)
            .map(attestationFinStageMapper::toDto);
    }

    /**
     * Find attestationFinStages by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AttestationFinStageDTO> findByCriteria(AttestationFinStageCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all AttestationFinStages by Criteria");
        return attestationFinStageRepository.findByCriteria(criteria, pageable).map(attestationFinStageMapper::toDto);
    }

    /**
     * Find the count of attestationFinStages by criteria.
     * @param criteria filtering criteria
     * @return the count of attestationFinStages
     */
    public Mono<Long> countByCriteria(AttestationFinStageCriteria criteria) {
        LOG.debug("Request to get the count of all AttestationFinStages by Criteria");
        return attestationFinStageRepository.countByCriteria(criteria);
    }

    /**
     *  Get all the attestationFinStages where Contrat is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AttestationFinStageDTO> findAllWhereContratIsNull() {
        LOG.debug("Request to get all attestationFinStages where Contrat is null");
        return attestationFinStageRepository.findAllWhereContratIsNull().map(attestationFinStageMapper::toDto);
    }

    /**
     * Returns the number of attestationFinStages available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return attestationFinStageRepository.count();
    }

    /**
     * Get one attestationFinStage by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<AttestationFinStageDTO> findOne(Long id) {
        LOG.debug("Request to get AttestationFinStage : {}", id);
        return attestationFinStageRepository.findById(id).map(attestationFinStageMapper::toDto);
    }

    /**
     * Delete the attestationFinStage by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete AttestationFinStage : {}", id);
        return attestationFinStageRepository.deleteById(id);
    }
}
