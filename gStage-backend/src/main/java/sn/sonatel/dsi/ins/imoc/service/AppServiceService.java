package sn.sonatel.dsi.ins.imoc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.AppServiceCriteria;
import sn.sonatel.dsi.ins.imoc.repository.AppServiceRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.AppServiceDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.AppServiceMapper;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.AppService}.
 */
@Service
@Transactional
public class AppServiceService {

    private static final Logger LOG = LoggerFactory.getLogger(AppServiceService.class);

    private final AppServiceRepository appServiceRepository;

    private final AppServiceMapper appServiceMapper;

    public AppServiceService(AppServiceRepository appServiceRepository, AppServiceMapper appServiceMapper) {
        this.appServiceRepository = appServiceRepository;
        this.appServiceMapper = appServiceMapper;
    }

    /**
     * Save a appService.
     *
     * @param appServiceDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AppServiceDTO> save(AppServiceDTO appServiceDTO) {
        LOG.debug("Request to save AppService : {}", appServiceDTO);
        return appServiceRepository.save(appServiceMapper.toEntity(appServiceDTO)).map(appServiceMapper::toDto);
    }

    /**
     * Update a appService.
     *
     * @param appServiceDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AppServiceDTO> update(AppServiceDTO appServiceDTO) {
        LOG.debug("Request to update AppService : {}", appServiceDTO);
        return appServiceRepository.save(appServiceMapper.toEntity(appServiceDTO)).map(appServiceMapper::toDto);
    }

    /**
     * Partially update a appService.
     *
     * @param appServiceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<AppServiceDTO> partialUpdate(AppServiceDTO appServiceDTO) {
        LOG.debug("Request to partially update AppService : {}", appServiceDTO);

        return appServiceRepository
            .findById(appServiceDTO.getId())
            .map(existingAppService -> {
                appServiceMapper.partialUpdate(existingAppService, appServiceDTO);

                return existingAppService;
            })
            .flatMap(appServiceRepository::save)
            .map(appServiceMapper::toDto);
    }

    /**
     * Find appServices by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AppServiceDTO> findByCriteria(AppServiceCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all AppServices by Criteria");
        return appServiceRepository.findByCriteria(criteria, pageable).map(appServiceMapper::toDto);
    }

    /**
     * Find the count of appServices by criteria.
     * @param criteria filtering criteria
     * @return the count of appServices
     */
    public Mono<Long> countByCriteria(AppServiceCriteria criteria) {
        LOG.debug("Request to get the count of all AppServices by Criteria");
        return appServiceRepository.countByCriteria(criteria);
    }

    /**
     *  Get all the appServices where Manager is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AppServiceDTO> findAllWhereManagerIsNull() {
        LOG.debug("Request to get all appServices where Manager is null");
        return appServiceRepository.findAllWhereManagerIsNull().map(appServiceMapper::toDto);
    }

    /**
     * Returns the number of appServices available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return appServiceRepository.count();
    }

    /**
     * Get one appService by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<AppServiceDTO> findOne(Long id) {
        LOG.debug("Request to get AppService : {}", id);
        return appServiceRepository.findById(id).map(appServiceMapper::toDto);
    }

    /**
     * Delete the appService by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete AppService : {}", id);
        return appServiceRepository.deleteById(id);
    }
}
