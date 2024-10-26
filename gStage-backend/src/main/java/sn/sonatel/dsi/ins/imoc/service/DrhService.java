package sn.sonatel.dsi.ins.imoc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.DrhCriteria;
import sn.sonatel.dsi.ins.imoc.repository.DrhRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.DrhDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.DrhMapper;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.Drh}.
 */
@Service
@Transactional
public class DrhService {

    private static final Logger LOG = LoggerFactory.getLogger(DrhService.class);

    private final DrhRepository drhRepository;

    private final DrhMapper drhMapper;

    public DrhService(DrhRepository drhRepository, DrhMapper drhMapper) {
        this.drhRepository = drhRepository;
        this.drhMapper = drhMapper;
    }

    /**
     * Save a drh.
     *
     * @param drhDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<DrhDTO> save(DrhDTO drhDTO) {
        LOG.debug("Request to save Drh : {}", drhDTO);
        return drhRepository.save(drhMapper.toEntity(drhDTO)).map(drhMapper::toDto);
    }

    /**
     * Update a drh.
     *
     * @param drhDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<DrhDTO> update(DrhDTO drhDTO) {
        LOG.debug("Request to update Drh : {}", drhDTO);
        return drhRepository.save(drhMapper.toEntity(drhDTO)).map(drhMapper::toDto);
    }

    /**
     * Partially update a drh.
     *
     * @param drhDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<DrhDTO> partialUpdate(DrhDTO drhDTO) {
        LOG.debug("Request to partially update Drh : {}", drhDTO);

        return drhRepository
            .findById(drhDTO.getId())
            .map(existingDrh -> {
                drhMapper.partialUpdate(existingDrh, drhDTO);

                return existingDrh;
            })
            .flatMap(drhRepository::save)
            .map(drhMapper::toDto);
    }

    /**
     * Find drhs by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<DrhDTO> findByCriteria(DrhCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Drhs by Criteria");
        return drhRepository.findByCriteria(criteria, pageable).map(drhMapper::toDto);
    }

    /**
     * Find the count of drhs by criteria.
     * @param criteria filtering criteria
     * @return the count of drhs
     */
    public Mono<Long> countByCriteria(DrhCriteria criteria) {
        LOG.debug("Request to get the count of all Drhs by Criteria");
        return drhRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of drhs available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return drhRepository.count();
    }

    /**
     * Get one drh by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<DrhDTO> findOne(Long id) {
        LOG.debug("Request to get Drh : {}", id);
        return drhRepository.findById(id).map(drhMapper::toDto);
    }

    /**
     * Delete the drh by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Drh : {}", id);
        return drhRepository.deleteById(id);
    }
}
