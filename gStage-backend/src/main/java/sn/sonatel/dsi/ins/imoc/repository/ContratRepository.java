package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.Contrat;
import sn.sonatel.dsi.ins.imoc.domain.criteria.ContratCriteria;

/**
 * Spring Data R2DBC repository for the Contrat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ContratRepository extends ReactiveCrudRepository<Contrat, Long>, ContratRepositoryInternal {
    Flux<Contrat> findAllBy(Pageable pageable);

    @Query("SELECT * FROM contrat entity WHERE entity.attestation_fin_stage_id = :id")
    Flux<Contrat> findByAttestationFinStage(Long id);

    @Query("SELECT * FROM contrat entity WHERE entity.attestation_fin_stage_id IS NULL")
    Flux<Contrat> findAllWhereAttestationFinStageIsNull();

    @Query("SELECT * FROM contrat entity WHERE entity.drh_id = :id")
    Flux<Contrat> findByDrh(Long id);

    @Query("SELECT * FROM contrat entity WHERE entity.drh_id IS NULL")
    Flux<Contrat> findAllWhereDrhIsNull();

    @Query("SELECT * FROM contrat entity WHERE entity.assistantgwtecreator_id = :id")
    Flux<Contrat> findByAssistantGWTECreator(Long id);

    @Query("SELECT * FROM contrat entity WHERE entity.assistantgwtecreator_id IS NULL")
    Flux<Contrat> findAllWhereAssistantGWTECreatorIsNull();

    @Query("SELECT * FROM contrat entity WHERE entity.candidat_id = :id")
    Flux<Contrat> findByCandidat(Long id);

    @Query("SELECT * FROM contrat entity WHERE entity.candidat_id IS NULL")
    Flux<Contrat> findAllWhereCandidatIsNull();

    @Override
    <S extends Contrat> Mono<S> save(S entity);

    @Override
    Flux<Contrat> findAll();

    @Override
    Mono<Contrat> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ContratRepositoryInternal {
    <S extends Contrat> Mono<S> save(S entity);

    Flux<Contrat> findAllBy(Pageable pageable);

    Flux<Contrat> findAll();

    Mono<Contrat> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Contrat> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Contrat> findByCriteria(ContratCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ContratCriteria criteria);
}
