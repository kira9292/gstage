package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.Role;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Role entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String s);
}
