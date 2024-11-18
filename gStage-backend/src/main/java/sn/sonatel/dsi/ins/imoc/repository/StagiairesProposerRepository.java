package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.StagiairesProposer;

/**
 * Spring Data JPA repository for the StagiairesProposer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StagiairesProposerRepository extends JpaRepository<StagiairesProposer, Long> {}
