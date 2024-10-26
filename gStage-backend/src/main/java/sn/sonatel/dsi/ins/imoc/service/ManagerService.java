package sn.sonatel.dsi.ins.imoc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.ManagerCriteria;
import sn.sonatel.dsi.ins.imoc.repository.ManagerRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.ManagerDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.ManagerMapper;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.Manager}.
 */
@Service
@Transactional
public class ManagerService {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerService.class);

    private final ManagerRepository managerRepository;

    private final ManagerMapper managerMapper;

    public ManagerService(ManagerRepository managerRepository, ManagerMapper managerMapper) {
        this.managerRepository = managerRepository;
        this.managerMapper = managerMapper;
    }

    /**
     * Save a manager.
     *
     * @param managerDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ManagerDTO> save(ManagerDTO managerDTO) {
        LOG.debug("Request to save Manager : {}", managerDTO);
        return managerRepository.save(managerMapper.toEntity(managerDTO)).map(managerMapper::toDto);
    }

    /**
     * Update a manager.
     *
     * @param managerDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ManagerDTO> update(ManagerDTO managerDTO) {
        LOG.debug("Request to update Manager : {}", managerDTO);
        return managerRepository.save(managerMapper.toEntity(managerDTO)).map(managerMapper::toDto);
    }

    /**
     * Partially update a manager.
     *
     * @param managerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ManagerDTO> partialUpdate(ManagerDTO managerDTO) {
        LOG.debug("Request to partially update Manager : {}", managerDTO);

        return managerRepository
            .findById(managerDTO.getId())
            .map(existingManager -> {
                managerMapper.partialUpdate(existingManager, managerDTO);

                return existingManager;
            })
            .flatMap(managerRepository::save)
            .map(managerMapper::toDto);
    }

    /**
     * Find managers by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ManagerDTO> findByCriteria(ManagerCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Managers by Criteria");
        return managerRepository.findByCriteria(criteria, pageable).map(managerMapper::toDto);
    }

    /**
     * Find the count of managers by criteria.
     * @param criteria filtering criteria
     * @return the count of managers
     */
    public Mono<Long> countByCriteria(ManagerCriteria criteria) {
        LOG.debug("Request to get the count of all Managers by Criteria");
        return managerRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of managers available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return managerRepository.count();
    }

    /**
     * Get one manager by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ManagerDTO> findOne(Long id) {
        LOG.debug("Request to get Manager : {}", id);
        return managerRepository.findById(id).map(managerMapper::toDto);
    }

    /**
     * Delete the manager by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Manager : {}", id);
        return managerRepository.deleteById(id);
    }
}
