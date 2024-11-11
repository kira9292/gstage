package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidat;

import java.util.Optional;

/**
 * Spring Data JPA repository for the ValidationStatuscandidat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ValidationStatuscandidatRepository extends JpaRepository<ValidationStatuscandidat, Long> {
    Optional<ValidationStatuscandidat> findByCode(String code);
    ValidationStatuscandidat findTopByCandidatEmailOrderByCreationDesc(String email);

}
