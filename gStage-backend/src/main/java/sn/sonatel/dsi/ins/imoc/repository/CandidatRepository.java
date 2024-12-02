package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;

import java.util.List;
import java.util.Map;

/**
 * Spring Data JPA repository for the Candidat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Long> {
    Candidat findByValidationStatuscandidatCode(String code);


    Candidat findByEmail(String email);
}
