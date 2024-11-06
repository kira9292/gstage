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
import sn.sonatel.dsi.ins.imoc.domain.Jwt;
import sn.sonatel.dsi.ins.imoc.repository.JwtRepository;
import sn.sonatel.dsi.ins.imoc.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.sonatel.dsi.ins.imoc.domain.Jwt}.
 */
@RestController
@RequestMapping("/api/jwts")
@Transactional
public class JwtResource {

    private static final Logger LOG = LoggerFactory.getLogger(JwtResource.class);

    private static final String ENTITY_NAME = "backendJwt";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final JwtRepository jwtRepository;

    public JwtResource(JwtRepository jwtRepository) {
        this.jwtRepository = jwtRepository;
    }

    /**
     * {@code POST  /jwts} : Create a new jwt.
     *
     * @param jwt the jwt to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new jwt, or with status {@code 400 (Bad Request)} if the jwt has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Jwt> createJwt(@RequestBody Jwt jwt) throws URISyntaxException {
        LOG.debug("REST request to save Jwt : {}", jwt);
        if (jwt.getId() != null) {
            throw new BadRequestAlertException("A new jwt cannot already have an ID", ENTITY_NAME, "idexists");
        }
        jwt = jwtRepository.save(jwt);
        return ResponseEntity.created(new URI("/api/jwts/" + jwt.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, jwt.getId().toString()))
            .body(jwt);
    }

    /**
     * {@code PUT  /jwts/:id} : Updates an existing jwt.
     *
     * @param id the id of the jwt to save.
     * @param jwt the jwt to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jwt,
     * or with status {@code 400 (Bad Request)} if the jwt is not valid,
     * or with status {@code 500 (Internal Server Error)} if the jwt couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Jwt> updateJwt(@PathVariable(value = "id", required = false) final Long id, @RequestBody Jwt jwt)
        throws URISyntaxException {
        LOG.debug("REST request to update Jwt : {}, {}", id, jwt);
        if (jwt.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, jwt.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!jwtRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        jwt = jwtRepository.save(jwt);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, jwt.getId().toString()))
            .body(jwt);
    }

    /**
     * {@code PATCH  /jwts/:id} : Partial updates given fields of an existing jwt, field will ignore if it is null
     *
     * @param id the id of the jwt to save.
     * @param jwt the jwt to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jwt,
     * or with status {@code 400 (Bad Request)} if the jwt is not valid,
     * or with status {@code 404 (Not Found)} if the jwt is not found,
     * or with status {@code 500 (Internal Server Error)} if the jwt couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Jwt> partialUpdateJwt(@PathVariable(value = "id", required = false) final Long id, @RequestBody Jwt jwt)
        throws URISyntaxException {
        LOG.debug("REST request to partial update Jwt partially : {}, {}", id, jwt);
        if (jwt.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, jwt.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!jwtRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Jwt> result = jwtRepository
            .findById(jwt.getId())
            .map(existingJwt -> {
                if (jwt.getDesactive() != null) {
                    existingJwt.setDesactive(jwt.getDesactive());
                }
                if (jwt.getExpire() != null) {
                    existingJwt.setExpire(jwt.getExpire());
                }

                return existingJwt;
            })
            .map(jwtRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, jwt.getId().toString())
        );
    }

    /**
     * {@code GET  /jwts} : get all the jwts.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of jwts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Jwt>> getAllJwts(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Jwts");
        Page<Jwt> page = jwtRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /jwts/:id} : get the "id" jwt.
     *
     * @param id the id of the jwt to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the jwt, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Jwt> getJwt(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Jwt : {}", id);
        Optional<Jwt> jwt = jwtRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(jwt);
    }

    /**
     * {@code DELETE  /jwts/:id} : delete the "id" jwt.
     *
     * @param id the id of the jwt to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJwt(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Jwt : {}", id);
        jwtRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
