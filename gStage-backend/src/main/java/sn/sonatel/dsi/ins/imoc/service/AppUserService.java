package sn.sonatel.dsi.ins.imoc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.AppUserCriteria;
import sn.sonatel.dsi.ins.imoc.repository.AppUserRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.AppUserDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.AppUserMapper;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.AppUser}.
 */
@Service
@Transactional
public class AppUserService {

    private static final Logger LOG = LoggerFactory.getLogger(AppUserService.class);

    private final AppUserRepository appUserRepository;

    private final AppUserMapper appUserMapper;

    public AppUserService(AppUserRepository appUserRepository, AppUserMapper appUserMapper) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
    }

    /**
     * Save a appUser.
     *
     * @param appUserDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AppUserDTO> save(AppUserDTO appUserDTO) {
        LOG.debug("Request to save AppUser : {}", appUserDTO);
        return appUserRepository.save(appUserMapper.toEntity(appUserDTO)).map(appUserMapper::toDto);
    }

    /**
     * Update a appUser.
     *
     * @param appUserDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<AppUserDTO> update(AppUserDTO appUserDTO) {
        LOG.debug("Request to update AppUser : {}", appUserDTO);
        return appUserRepository.save(appUserMapper.toEntity(appUserDTO)).map(appUserMapper::toDto);
    }

    /**
     * Partially update a appUser.
     *
     * @param appUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<AppUserDTO> partialUpdate(AppUserDTO appUserDTO) {
        LOG.debug("Request to partially update AppUser : {}", appUserDTO);

        return appUserRepository
            .findById(appUserDTO.getId())
            .map(existingAppUser -> {
                appUserMapper.partialUpdate(existingAppUser, appUserDTO);

                return existingAppUser;
            })
            .flatMap(appUserRepository::save)
            .map(appUserMapper::toDto);
    }

    /**
     * Find appUsers by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<AppUserDTO> findByCriteria(AppUserCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all AppUsers by Criteria");
        return appUserRepository.findByCriteria(criteria, pageable).map(appUserMapper::toDto);
    }

    /**
     * Find the count of appUsers by criteria.
     * @param criteria filtering criteria
     * @return the count of appUsers
     */
    public Mono<Long> countByCriteria(AppUserCriteria criteria) {
        LOG.debug("Request to get the count of all AppUsers by Criteria");
        return appUserRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of appUsers available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return appUserRepository.count();
    }

    /**
     * Get one appUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<AppUserDTO> findOne(Long id) {
        LOG.debug("Request to get AppUser : {}", id);
        return appUserRepository.findById(id).map(appUserMapper::toDto);
    }

    /**
     * Delete the appUser by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete AppUser : {}", id);
        return appUserRepository.deleteById(id);
    }
}
