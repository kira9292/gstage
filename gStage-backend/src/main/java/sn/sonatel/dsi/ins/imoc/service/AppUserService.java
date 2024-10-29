package sn.sonatel.dsi.ins.imoc.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.repository.AppUserRepository;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.AppUser}.
 */
@Service
@Transactional
public class AppUserService {

    private static final Logger LOG = LoggerFactory.getLogger(AppUserService.class);

    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    /**
     * Save a appUser.
     *
     * @param appUser the entity to save.
     * @return the persisted entity.
     */
    public AppUser save(AppUser appUser) {
        LOG.debug("Request to save AppUser : {}", appUser);
        return appUserRepository.save(appUser);
    }

    /**
     * Update a appUser.
     *
     * @param appUser the entity to save.
     * @return the persisted entity.
     */
    public AppUser update(AppUser appUser) {
        LOG.debug("Request to update AppUser : {}", appUser);
        return appUserRepository.save(appUser);
    }

    /**
     * Partially update a appUser.
     *
     * @param appUser the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AppUser> partialUpdate(AppUser appUser) {
        LOG.debug("Request to partially update AppUser : {}", appUser);

        return appUserRepository
            .findById(appUser.getId())
            .map(existingAppUser -> {
                if (appUser.getUsername() != null) {
                    existingAppUser.setUsername(appUser.getUsername());
                }
                if (appUser.getEmail() != null) {
                    existingAppUser.setEmail(appUser.getEmail());
                }
                if (appUser.getPassword() != null) {
                    existingAppUser.setPassword(appUser.getPassword());
                }
                if (appUser.getName() != null) {
                    existingAppUser.setName(appUser.getName());
                }
                if (appUser.getFirstName() != null) {
                    existingAppUser.setFirstName(appUser.getFirstName());
                }

                return existingAppUser;
            })
            .map(appUserRepository::save);
    }

    /**
     * Get all the appUsers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<AppUser> findAll(Pageable pageable) {
        LOG.debug("Request to get all AppUsers");
        return appUserRepository.findAll(pageable);
    }

    /**
     * Get all the appUsers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AppUser> findAllWithEagerRelationships(Pageable pageable) {
        return appUserRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one appUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AppUser> findOne(Long id) {
        LOG.debug("Request to get AppUser : {}", id);
        return appUserRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the appUser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AppUser : {}", id);
        appUserRepository.deleteById(id);
    }
}
