package sn.sonatel.dsi.ins.imoc.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.sonatel.dsi.ins.imoc.domain.Role;
import sn.sonatel.dsi.ins.imoc.repository.RoleRepository;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.Role}.
 */
@RestController
@RequestMapping("/api/roles")
@Transactional
public class RoleResource {

    private static final Logger LOG = LoggerFactory.getLogger(RoleResource.class);

    private static final String ENTITY_NAME = "backendRole";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RoleRepository roleRepository;

    public RoleResource(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * {@code POST  /roles} : Create a new role.
     *
     * @param role the role to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new role, or with status {@code 400 (Bad Request)} if the role has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Role> createRole(@RequestBody Role role) throws URISyntaxException {
        LOG.debug("REST request to save Role : {}", role);
        if (role.getId() != null) {
            throw new BadRequestAlertException("A new role cannot already have an ID", ENTITY_NAME, "idexists");
        }
        role = roleRepository.save(role);
        return ResponseEntity.created(new URI("/api/roles/" + role.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, role.getId().toString()))
            .body(role);
    }

    /**
     * {@code PUT  /roles/:id} : Updates an existing role.
     *
     * @param id the id of the role to save.
     * @param role the role to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated role,
     * or with status {@code 400 (Bad Request)} if the role is not valid,
     * or with status {@code 500 (Internal Server Error)} if the role couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable(value = "id", required = false) final Long id, @RequestBody Role role)
        throws URISyntaxException {
        LOG.debug("REST request to update Role : {}, {}", id, role);
        if (role.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, role.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        role = roleRepository.save(role);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, role.getId().toString()))
            .body(role);
    }

    /**
     * {@code PATCH  /roles/:id} : Partial updates given fields of an existing role, field will ignore if it is null
     *
     * @param id the id of the role to save.
     * @param role the role to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated role,
     * or with status {@code 400 (Bad Request)} if the role is not valid,
     * or with status {@code 404 (Not Found)} if the role is not found,
     * or with status {@code 500 (Internal Server Error)} if the role couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Role> partialUpdateRole(@PathVariable(value = "id", required = false) final Long id, @RequestBody Role role)
        throws URISyntaxException {
        LOG.debug("REST request to partial update Role partially : {}, {}", id, role);
        if (role.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, role.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Role> result = roleRepository
            .findById(role.getId())
            .map(existingRole -> {
                if (role.getName() != null) {
                    existingRole.setName(role.getName());
                }

                return existingRole;
            })
            .map(roleRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, role.getId().toString())
        );
    }

    /**
     * {@code GET  /roles} : get all the roles.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of roles in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Role>> getAllRoles(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Roles");
        Page<Role> page = roleRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /roles/:id} : get the "id" role.
     *
     * @param id the id of the role to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the role, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRole(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Role : {}", id);
        Optional<Role> role = roleRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(role);
    }

    /**
     * {@code DELETE  /roles/:id} : delete the "id" role.
     *
     * @param id the id of the role to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Role : {}", id);
        roleRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
