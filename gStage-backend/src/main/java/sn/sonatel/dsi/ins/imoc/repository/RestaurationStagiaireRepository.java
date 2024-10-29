package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaire;

/**
 * Spring Data JPA repository for the RestaurationStagiaire entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RestaurationStagiaireRepository extends JpaRepository<RestaurationStagiaire, Long> {}
