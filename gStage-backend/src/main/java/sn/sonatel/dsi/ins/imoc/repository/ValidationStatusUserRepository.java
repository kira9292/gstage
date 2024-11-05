package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatusUser;

import java.util.Optional;

/**
 * Spring Data JPA repository for the ValidationStatusUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ValidationStatusUserRepository extends JpaRepository<ValidationStatusUser, Long> {
    Optional<ValidationStatusUser> findByCode(String code);
}
