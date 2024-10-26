package sn.sonatel.dsi.ins.imoc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.AssistantGWTECriteria;
import sn.sonatel.dsi.ins.imoc.repository.AssistantGWTERepository;
import sn.sonatel.dsi.ins.imoc.service.dto.AssistantGWTEDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.AssistantGWTEMapper;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.AssistantGWTE}.
 */
@Service
@Transactional
public class AssistantGWTEService {

    private static final Logger LOG = LoggerFactory.getLogger(AssistantGWTEService.class);

    private final AssistantGWTERepository assistantGWTERepository;

    private final AssistantGWTEMapper assistantGWTEMapper;

    public AssistantGWTEService(AssistantGWTERepository assistantGWTERepository, AssistantGWTEMapper assistantGWTEMapper) {
        this.assistantGWTERepository = assistantGWTERepository;
        this.assistantGWTEMapper = assistantGWTEMapper;
    }

    /**
     * Save a assistantGWTE.
     *
     * @param assistantGWTEDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AssistantGWTEDTO> save(AssistantGWTEDTO assistantGWTEDTO) {
        LOG.debug("Request to save AssistantGWTE : {}", assistantGWTEDTO);
        return assistantGWTERepository.save(assistantGWTEMapper.toEntity(assistantGWTEDTO)).map(assistantGWTEMapper::toDto);
    }

    /**
     * Update a assistantGWTE.
     *
     * @param assistantGWTEDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AssistantGWTEDTO> update(AssistantGWTEDTO assistantGWTEDTO) {
        LOG.debug("Request to update AssistantGWTE : {}", assistantGWTEDTO);
        return assistantGWTERepository.save(assistantGWTEMapper.toEntity(assistantGWTEDTO)).map(assistantGWTEMapper::toDto);
    }

    /**
     * Partially update a assistantGWTE.
     *
     * @param assistantGWTEDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<AssistantGWTEDTO> partialUpdate(AssistantGWTEDTO assistantGWTEDTO) {
        LOG.debug("Request to partially update AssistantGWTE : {}", assistantGWTEDTO);

        return assistantGWTERepository
            .findById(assistantGWTEDTO.getId())
            .map(existingAssistantGWTE -> {
                assistantGWTEMapper.partialUpdate(existingAssistantGWTE, assistantGWTEDTO);

                return existingAssistantGWTE;
            })
            .flatMap(assistantGWTERepository::save)
            .map(assistantGWTEMapper::toDto);
    }

    /**
     * Find assistantGWTES by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AssistantGWTEDTO> findByCriteria(AssistantGWTECriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all AssistantGWTES by Criteria");
        return assistantGWTERepository.findByCriteria(criteria, pageable).map(assistantGWTEMapper::toDto);
    }

    /**
     * Find the count of assistantGWTES by criteria.
     * @param criteria filtering criteria
     * @return the count of assistantGWTES
     */
    public Mono<Long> countByCriteria(AssistantGWTECriteria criteria) {
        LOG.debug("Request to get the count of all AssistantGWTES by Criteria");
        return assistantGWTERepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of assistantGWTES available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return assistantGWTERepository.count();
    }

    /**
     * Get one assistantGWTE by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<AssistantGWTEDTO> findOne(Long id) {
        LOG.debug("Request to get AssistantGWTE : {}", id);
        return assistantGWTERepository.findById(id).map(assistantGWTEMapper::toDto);
    }

    /**
     * Delete the assistantGWTE by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete AssistantGWTE : {}", id);
        return assistantGWTERepository.deleteById(id);
    }
}
