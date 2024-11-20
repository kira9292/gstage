package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;

import java.util.List;

/**
 * Spring Data JPA repository for the DemandeStage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DemandeStageRepository extends JpaRepository<DemandeStage, Long> {
    List<DemandeStage> findByStatus(InternshipStatus status);
}
