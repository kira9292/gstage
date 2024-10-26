package sn.sonatel.dsi.ins.imoc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.BusinessUnitCriteria;
import sn.sonatel.dsi.ins.imoc.repository.BusinessUnitRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.BusinessUnitDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.BusinessUnitMapper;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.BusinessUnit}.
 */
@Service
@Transactional
public class BusinessUnitService {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessUnitService.class);

    private final BusinessUnitRepository businessUnitRepository;

    private final BusinessUnitMapper businessUnitMapper;

    public BusinessUnitService(BusinessUnitRepository businessUnitRepository, BusinessUnitMapper businessUnitMapper) {
        this.businessUnitRepository = businessUnitRepository;
        this.businessUnitMapper = businessUnitMapper;
    }

    /**
     * Save a businessUnit.
     *
     * @param businessUnitDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BusinessUnitDTO> save(BusinessUnitDTO businessUnitDTO) {
        LOG.debug("Request to save BusinessUnit : {}", businessUnitDTO);
        return businessUnitRepository.save(businessUnitMapper.toEntity(businessUnitDTO)).map(businessUnitMapper::toDto);
    }

    /**
     * Update a businessUnit.
     *
     * @param businessUnitDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BusinessUnitDTO> update(BusinessUnitDTO businessUnitDTO) {
        LOG.debug("Request to update BusinessUnit : {}", businessUnitDTO);
        return businessUnitRepository.save(businessUnitMapper.toEntity(businessUnitDTO)).map(businessUnitMapper::toDto);
    }

    /**
     * Partially update a businessUnit.
     *
     * @param businessUnitDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<BusinessUnitDTO> partialUpdate(BusinessUnitDTO businessUnitDTO) {
        LOG.debug("Request to partially update BusinessUnit : {}", businessUnitDTO);

        return businessUnitRepository
            .findById(businessUnitDTO.getId())
            .map(existingBusinessUnit -> {
                businessUnitMapper.partialUpdate(existingBusinessUnit, businessUnitDTO);

                return existingBusinessUnit;
            })
            .flatMap(businessUnitRepository::save)
            .map(businessUnitMapper::toDto);
    }

    /**
     * Find businessUnits by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BusinessUnitDTO> findByCriteria(BusinessUnitCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all BusinessUnits by Criteria");
        return businessUnitRepository.findByCriteria(criteria, pageable).map(businessUnitMapper::toDto);
    }

    /**
     * Find the count of businessUnits by criteria.
     * @param criteria filtering criteria
     * @return the count of businessUnits
     */
    public Mono<Long> countByCriteria(BusinessUnitCriteria criteria) {
        LOG.debug("Request to get the count of all BusinessUnits by Criteria");
        return businessUnitRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of businessUnits available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return businessUnitRepository.count();
    }

    /**
     * Get one businessUnit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<BusinessUnitDTO> findOne(Long id) {
        LOG.debug("Request to get BusinessUnit : {}", id);
        return businessUnitRepository.findById(id).map(businessUnitMapper::toDto);
    }

    /**
     * Delete the businessUnit by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete BusinessUnit : {}", id);
        return businessUnitRepository.deleteById(id);
    }
}
