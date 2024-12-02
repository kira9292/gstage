package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
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

    List<DemandeStage> findByAppUser(AppUser user);

    void deleteByAppUserId(Long id);

    List<DemandeStage> findAllByAppUserIsNotNull();



    @Query("SELECT d FROM DemandeStage d JOIN FETCH d.candidat c "
        + "LEFT JOIN FETCH c.attestationPresences "
        + "LEFT JOIN FETCH c.attestationFinStage "
        + "LEFT JOIN FETCH c.contrats")
    List<DemandeStage> findAllWithRelations();
}
