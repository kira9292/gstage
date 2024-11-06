package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.Jwt;

/**
 * Spring Data JPA repository for the Jwt entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JwtRepository extends JpaRepository<Jwt, Long> {}
