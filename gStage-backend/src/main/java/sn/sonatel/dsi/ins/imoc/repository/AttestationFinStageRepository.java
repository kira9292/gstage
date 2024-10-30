package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage;

/**
 * Spring Data JPA repository for the AttestationFinStage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttestationFinStageRepository extends JpaRepository<AttestationFinStage, Long> {}
