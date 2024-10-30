package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;

/**
 * Spring Data JPA repository for the DemandeStage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DemandeStageRepository extends JpaRepository<DemandeStage, Long> {}
