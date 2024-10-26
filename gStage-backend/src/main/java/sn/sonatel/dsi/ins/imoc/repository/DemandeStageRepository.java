package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.criteria.DemandeStageCriteria;

/**
 * Spring Data R2DBC repository for the DemandeStage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DemandeStageRepository extends ReactiveCrudRepository<DemandeStage, Long>, DemandeStageRepositoryInternal {
    Flux<DemandeStage> findAllBy(Pageable pageable);

    @Query("SELECT * FROM demande_stage entity WHERE entity.candidat_id = :id")
    Flux<DemandeStage> findByCandidat(Long id);

    @Query("SELECT * FROM demande_stage entity WHERE entity.candidat_id IS NULL")
    Flux<DemandeStage> findAllWhereCandidatIsNull();

    @Query("SELECT * FROM demande_stage entity WHERE entity.assistantgwte_id = :id")
    Flux<DemandeStage> findByAssistantGWTE(Long id);

    @Query("SELECT * FROM demande_stage entity WHERE entity.assistantgwte_id IS NULL")
    Flux<DemandeStage> findAllWhereAssistantGWTEIsNull();

    @Query("SELECT * FROM demande_stage entity WHERE entity.manager_id = :id")
    Flux<DemandeStage> findByManager(Long id);

    @Query("SELECT * FROM demande_stage entity WHERE entity.manager_id IS NULL")
    Flux<DemandeStage> findAllWhereManagerIsNull();

    @Query("SELECT * FROM demande_stage entity WHERE entity.departement_id = :id")
    Flux<DemandeStage> findByDepartement(Long id);

    @Query("SELECT * FROM demande_stage entity WHERE entity.departement_id IS NULL")
    Flux<DemandeStage> findAllWhereDepartementIsNull();

    @Query("SELECT * FROM demande_stage entity WHERE entity.business_unit_id = :id")
    Flux<DemandeStage> findByBusinessUnit(Long id);

    @Query("SELECT * FROM demande_stage entity WHERE entity.business_unit_id IS NULL")
    Flux<DemandeStage> findAllWhereBusinessUnitIsNull();

    @Override
    <S extends DemandeStage> Mono<S> save(S entity);

    @Override
    Flux<DemandeStage> findAll();

    @Override
    Mono<DemandeStage> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DemandeStageRepositoryInternal {
    <S extends DemandeStage> Mono<S> save(S entity);

    Flux<DemandeStage> findAllBy(Pageable pageable);

    Flux<DemandeStage> findAll();

    Mono<DemandeStage> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<DemandeStage> findAllBy(Pageable pageable, Criteria criteria);
    Flux<DemandeStage> findByCriteria(DemandeStageCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(DemandeStageCriteria criteria);
}
