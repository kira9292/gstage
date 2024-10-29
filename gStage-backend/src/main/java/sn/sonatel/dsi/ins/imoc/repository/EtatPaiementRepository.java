package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.EtatPaiement;

/**
 * Spring Data JPA repository for the EtatPaiement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EtatPaiementRepository extends JpaRepository<EtatPaiement, Long> {}
