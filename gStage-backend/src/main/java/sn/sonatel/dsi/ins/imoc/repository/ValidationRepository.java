package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.sonatel.dsi.ins.imoc.domain.Validation;

/**
 * Spring Data JPA repository for the Validation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ValidationRepository extends JpaRepository<Validation, Long> {}
