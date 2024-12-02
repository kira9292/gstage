package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.AttestationPresence;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;

import java.util.List;

/**
 * Spring Data JPA repository for the AttestationPresence entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttestationPresenceRepository extends JpaRepository<AttestationPresence, Long> {
    List<AttestationPresence> findByCandidat(Candidat ca);
}
