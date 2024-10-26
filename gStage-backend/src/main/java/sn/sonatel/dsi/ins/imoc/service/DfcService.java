package sn.sonatel.dsi.ins.imoc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.DfcCriteria;
import sn.sonatel.dsi.ins.imoc.repository.DfcRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.DfcDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.DfcMapper;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.Dfc}.
 */
@Service
@Transactional
public class DfcService {

    private static final Logger LOG = LoggerFactory.getLogger(DfcService.class);

    private final DfcRepository dfcRepository;

    private final DfcMapper dfcMapper;

    public DfcService(DfcRepository dfcRepository, DfcMapper dfcMapper) {
        this.dfcRepository = dfcRepository;
        this.dfcMapper = dfcMapper;
    }

    /**
     * Save a dfc.
     *
     * @param dfcDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<DfcDTO> save(DfcDTO dfcDTO) {
        LOG.debug("Request to save Dfc : {}", dfcDTO);
        return dfcRepository.save(dfcMapper.toEntity(dfcDTO)).map(dfcMapper::toDto);
    }

    /**
     * Find dfcs by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<DfcDTO> findByCriteria(DfcCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Dfcs by Criteria");
        return dfcRepository.findByCriteria(criteria, pageable).map(dfcMapper::toDto);
    }

    /**
     * Find the count of dfcs by criteria.
     * @param criteria filtering criteria
     * @return the count of dfcs
     */
    public Mono<Long> countByCriteria(DfcCriteria criteria) {
        LOG.debug("Request to get the count of all Dfcs by Criteria");
        return dfcRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of dfcs available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return dfcRepository.count();
    }

    /**
     * Get one dfc by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<DfcDTO> findOne(Long id) {
        LOG.debug("Request to get Dfc : {}", id);
        return dfcRepository.findById(id).map(dfcMapper::toDto);
    }

    /**
     * Delete the dfc by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Dfc : {}", id);
        return dfcRepository.deleteById(id);
    }
}
