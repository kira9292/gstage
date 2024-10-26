package sn.sonatel.dsi.ins.imoc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.AttestationPresenceCriteria;
import sn.sonatel.dsi.ins.imoc.repository.AttestationPresenceRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.AttestationPresenceDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.AttestationPresenceMapper;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.AttestationPresence}.
 */
@Service
@Transactional
public class AttestationPresenceService {

    private static final Logger LOG = LoggerFactory.getLogger(AttestationPresenceService.class);

    private final AttestationPresenceRepository attestationPresenceRepository;

    private final AttestationPresenceMapper attestationPresenceMapper;

    public AttestationPresenceService(
        AttestationPresenceRepository attestationPresenceRepository,
        AttestationPresenceMapper attestationPresenceMapper
    ) {
        this.attestationPresenceRepository = attestationPresenceRepository;
        this.attestationPresenceMapper = attestationPresenceMapper;
    }

    /**
     * Save a attestationPresence.
     *
     * @param attestationPresenceDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AttestationPresenceDTO> save(AttestationPresenceDTO attestationPresenceDTO) {
        LOG.debug("Request to save AttestationPresence : {}", attestationPresenceDTO);
        return attestationPresenceRepository
            .save(attestationPresenceMapper.toEntity(attestationPresenceDTO))
            .map(attestationPresenceMapper::toDto);
    }

    /**
     * Update a attestationPresence.
     *
     * @param attestationPresenceDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AttestationPresenceDTO> update(AttestationPresenceDTO attestationPresenceDTO) {
        LOG.debug("Request to update AttestationPresence : {}", attestationPresenceDTO);
        return attestationPresenceRepository
            .save(attestationPresenceMapper.toEntity(attestationPresenceDTO))
            .map(attestationPresenceMapper::toDto);
    }

    /**
     * Partially update a attestationPresence.
     *
     * @param attestationPresenceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<AttestationPresenceDTO> partialUpdate(AttestationPresenceDTO attestationPresenceDTO) {
        LOG.debug("Request to partially update AttestationPresence : {}", attestationPresenceDTO);

        return attestationPresenceRepository
            .findById(attestationPresenceDTO.getId())
            .map(existingAttestationPresence -> {
                attestationPresenceMapper.partialUpdate(existingAttestationPresence, attestationPresenceDTO);

                return existingAttestationPresence;
            })
            .flatMap(attestationPresenceRepository::save)
            .map(attestationPresenceMapper::toDto);
    }

    /**
     * Find attestationPresences by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AttestationPresenceDTO> findByCriteria(AttestationPresenceCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all AttestationPresences by Criteria");
        return attestationPresenceRepository.findByCriteria(criteria, pageable).map(attestationPresenceMapper::toDto);
    }

    /**
     * Find the count of attestationPresences by criteria.
     * @param criteria filtering criteria
     * @return the count of attestationPresences
     */
    public Mono<Long> countByCriteria(AttestationPresenceCriteria criteria) {
        LOG.debug("Request to get the count of all AttestationPresences by Criteria");
        return attestationPresenceRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of attestationPresences available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return attestationPresenceRepository.count();
    }

    /**
     * Get one attestationPresence by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<AttestationPresenceDTO> findOne(Long id) {
        LOG.debug("Request to get AttestationPresence : {}", id);
        return attestationPresenceRepository.findById(id).map(attestationPresenceMapper::toDto);
    }

    /**
     * Delete the attestationPresence by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete AttestationPresence : {}", id);
        return attestationPresenceRepository.deleteById(id);
    }
}
