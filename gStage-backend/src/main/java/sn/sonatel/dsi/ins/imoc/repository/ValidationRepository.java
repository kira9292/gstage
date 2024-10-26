package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.Validation;
import sn.sonatel.dsi.ins.imoc.domain.criteria.ValidationCriteria;

/**
 * Spring Data R2DBC repository for the Validation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ValidationRepository extends ReactiveCrudRepository<Validation, Long>, ValidationRepositoryInternal {
    Flux<Validation> findAllBy(Pageable pageable);

    @Query("SELECT * FROM validation entity WHERE entity.attestation_presence_id = :id")
    Flux<Validation> findByAttestationPresence(Long id);

    @Query("SELECT * FROM validation entity WHERE entity.attestation_presence_id IS NULL")
    Flux<Validation> findAllWhereAttestationPresenceIsNull();

    @Query("SELECT * FROM validation entity WHERE entity.contrat_id = :id")
    Flux<Validation> findByContrat(Long id);

    @Query("SELECT * FROM validation entity WHERE entity.contrat_id IS NULL")
    Flux<Validation> findAllWhereContratIsNull();

    @Query("SELECT * FROM validation entity WHERE entity.attestation_fin_stage_id = :id")
    Flux<Validation> findByAttestationFinStage(Long id);

    @Query("SELECT * FROM validation entity WHERE entity.attestation_fin_stage_id IS NULL")
    Flux<Validation> findAllWhereAttestationFinStageIsNull();

    @Query("SELECT * FROM validation entity WHERE entity.user_id = :id")
    Flux<Validation> findByUser(Long id);

    @Query("SELECT * FROM validation entity WHERE entity.user_id IS NULL")
    Flux<Validation> findAllWhereUserIsNull();

    @Override
    <S extends Validation> Mono<S> save(S entity);

    @Override
    Flux<Validation> findAll();

    @Override
    Mono<Validation> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ValidationRepositoryInternal {
    <S extends Validation> Mono<S> save(S entity);

    Flux<Validation> findAllBy(Pageable pageable);

    Flux<Validation> findAll();

    Mono<Validation> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Validation> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Validation> findByCriteria(ValidationCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ValidationCriteria criteria);
}
