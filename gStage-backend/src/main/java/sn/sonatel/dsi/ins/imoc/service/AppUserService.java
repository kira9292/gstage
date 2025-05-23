package sn.sonatel.dsi.ins.imoc.service;

import java.time.Instant;
import java.util.*;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.Role;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatusUser;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ERole;
import sn.sonatel.dsi.ins.imoc.dto.CandidatToStagiaireDTO;
import sn.sonatel.dsi.ins.imoc.dto.ManagerDTO2;
import sn.sonatel.dsi.ins.imoc.dto.UserDTO;
import sn.sonatel.dsi.ins.imoc.dto.mapper.AppUserMapper;
import sn.sonatel.dsi.ins.imoc.exceptions.ResourceNotFoundException;
import sn.sonatel.dsi.ins.imoc.repository.AppUserRepository;
import sn.sonatel.dsi.ins.imoc.repository.CandidatRepository;
import sn.sonatel.dsi.ins.imoc.repository.RoleRepository;
import sn.sonatel.dsi.ins.imoc.repository.ServiceRepository;



/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.AppUser}.
 */
@Service
@Transactional
public class AppUserService implements UserDetailsService {


    private static final Logger LOG = LoggerFactory.getLogger(AppUserService.class);

    @Autowired
    private  AppUserRepository appUserRepository;
    @Autowired
    private ValidationUserService validationUserService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private AppUserMapper appUserMapper;
    @Autowired
    private CandidatRepository candidatRepository;


    public AppUserService() {
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return  this.appUserRepository.
            findByEmailWithRoles(username).
            orElseThrow(()-> new UsernameNotFoundException("Aucun n'utilisateur ne correspond"));
    }

    public void inscription (UserDTO appUser) {
        if (!appUser.getAppUser().getEmail().contains("@") || !appUser.getAppUser().getEmail().contains(".")){
            throw new RuntimeException("Invalid email address");
        }
        Optional<AppUser> useroptonal = this.appUserRepository.findByEmail(appUser.getAppUser().getEmail());

        if (useroptonal.isPresent()) {
            throw new RuntimeException("Email already in use");

        }
        String mdpCrypte = this.passwordEncoder.encode(appUser.getAppUser().getPassword());
        appUser.getAppUser().setPassword(mdpCrypte);
        appUser.getAppUser().setStatus(false);

        if (appUser.getRole().getName() == ERole.MANAGER) {
            if (appUser.getService() == null) {
                throw new RuntimeException("Service must be specified for Manager role");
            }
            sn.sonatel.dsi.ins.imoc.domain.Service service = appUser.getService();
            if (appUser.getService().getId() == null) {
                service = serviceRepository.save(service); // Save the service if it's new
            }
            appUser.getAppUser().setService(service);
        }


        Optional<Role> role = roleRepository.findByName(appUser.getRole().getName());
        appUser.getAppUser().setRole(role.get());

        this.appUserRepository.save(appUser.getAppUser());


    }

    public void activation(Map<String, String> activation) {
        ValidationStatusUser validation =  this.validationUserService.getUserByCode(activation.get("code"));

        if (Instant.now().isAfter(validation.getExpire())){
            throw new RuntimeException("code expired");
        }

        AppUser useractivate = this.appUserRepository.findById(validation.getAppUser().getId()).
            orElseThrow(()-> new RuntimeException("user inconnue"));
        useractivate.setStatus(true);
    }










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
                if (appUser.getPhone() != null) {
                    existingAppUser.setPhone(appUser.getPhone());
                }
                if (appUser.getFormation() != null) {
                    existingAppUser.setFormation(appUser.getFormation());
                }
                if (appUser.getNiveau() != null) {
                    existingAppUser.setNiveau(appUser.getNiveau());
                }
                if (appUser.getStatus() != null) {
                    existingAppUser.setStatus(appUser.getStatus());
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
     *  Get all the appUsers where ValidationStatusUser is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AppUser> findAllWhereValidationStatusUserIsNull() {
        LOG.debug("Request to get all appUsers where ValidationStatusUser is null");
        return StreamSupport.stream(appUserRepository.findAll().spliterator(), false)
            .filter(appUser -> appUser.getValidationStatusUser() == null)
            .toList();
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

    public ManagerDTO2 getUserById(Long id) {
        AppUser appUser = appUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));
        return appUserMapper.toManagerDTO(appUser);
    }
    @Transactional
    public void inscriptioncandidat(CandidatToStagiaireDTO user) {


        if (!user.appUser().getEmail().contains("@") || !user.appUser().getEmail().contains(".")){
            throw new RuntimeException("Invalid email address");
        }
        Optional<AppUser> useroptonal = this.appUserRepository.findByEmail(user.appUser().getEmail());

        if (useroptonal.isPresent()) {
            throw new RuntimeException("Email already in use");

        }
        String mdpCrypte = this.passwordEncoder.encode(user.appUser().getPassword());
        user.appUser().setPassword(mdpCrypte);
        user.appUser().setStatus(false);

        AppUser user1 =this.appUserRepository.save(user.appUser());
        Optional<Role> role =roleRepository.findByName(ERole.STAGIAIRE);

        Optional<Candidat> candidat = this.candidatRepository.findById(user.candidat().getId());


        candidat.get().setAppUser(user.appUser());

        this.candidatRepository.save(candidat.get());




    }
}

