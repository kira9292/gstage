package sn.sonatel.dsi.ins.imoc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.DepartementCriteria;
import sn.sonatel.dsi.ins.imoc.repository.DepartementRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.DepartementDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.DepartementMapper;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.Departement}.
 */
@Service
@Transactional
public class DepartementService {

    private static final Logger LOG = LoggerFactory.getLogger(DepartementService.class);

    private final DepartementRepository departementRepository;

    private final DepartementMapper departementMapper;

    public DepartementService(DepartementRepository departementRepository, DepartementMapper departementMapper) {
        this.departementRepository = departementRepository;
        this.departementMapper = departementMapper;
    }

    /**
     * Save a departement.
     *
     * @param departementDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<DepartementDTO> save(DepartementDTO departementDTO) {
        LOG.debug("Request to save Departement : {}", departementDTO);
        return departementRepository.save(departementMapper.toEntity(departementDTO)).map(departementMapper::toDto);
    }

    /**
     * Update a departement.
     *
     * @param departementDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<DepartementDTO> update(DepartementDTO departementDTO) {
        LOG.debug("Request to update Departement : {}", departementDTO);
        return departementRepository.save(departementMapper.toEntity(departementDTO)).map(departementMapper::toDto);
    }

    /**
     * Partially update a departement.
     *
     * @param departementDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<DepartementDTO> partialUpdate(DepartementDTO departementDTO) {
        LOG.debug("Request to partially update Departement : {}", departementDTO);

        return departementRepository
            .findById(departementDTO.getId())
            .map(existingDepartement -> {
                departementMapper.partialUpdate(existingDepartement, departementDTO);

                return existingDepartement;
            })
            .flatMap(departementRepository::save)
            .map(departementMapper::toDto);
    }

    /**
     * Find departements by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<DepartementDTO> findByCriteria(DepartementCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Departements by Criteria");
        return departementRepository.findByCriteria(criteria, pageable).map(departementMapper::toDto);
    }

    /**
     * Find the count of departements by criteria.
     * @param criteria filtering criteria
     * @return the count of departements
     */
    public Mono<Long> countByCriteria(DepartementCriteria criteria) {
        LOG.debug("Request to get the count of all Departements by Criteria");
        return departementRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of departements available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return departementRepository.count();
    }

    /**
     * Get one departement by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<DepartementDTO> findOne(Long id) {
        LOG.debug("Request to get Departement : {}", id);
        return departementRepository.findById(id).map(departementMapper::toDto);
    }

    /**
     * Delete the departement by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Departement : {}", id);
        return departementRepository.deleteById(id);
    }
}
