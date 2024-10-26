package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage;
import sn.sonatel.dsi.ins.imoc.domain.criteria.AttestationFinStageCriteria;

/**
 * Spring Data R2DBC repository for the AttestationFinStage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttestationFinStageRepository
    extends ReactiveCrudRepository<AttestationFinStage, Long>, AttestationFinStageRepositoryInternal {
    Flux<AttestationFinStage> findAllBy(Pageable pageable);

    @Query("SELECT * FROM attestation_fin_stage entity WHERE entity.id not in (select contrat_id from contrat)")
    Flux<AttestationFinStage> findAllWhereContratIsNull();

    @Override
    <S extends AttestationFinStage> Mono<S> save(S entity);

    @Override
    Flux<AttestationFinStage> findAll();

    @Override
    Mono<AttestationFinStage> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AttestationFinStageRepositoryInternal {
    <S extends AttestationFinStage> Mono<S> save(S entity);

    Flux<AttestationFinStage> findAllBy(Pageable pageable);

    Flux<AttestationFinStage> findAll();

    Mono<AttestationFinStage> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<AttestationFinStage> findAllBy(Pageable pageable, Criteria criteria);
    Flux<AttestationFinStage> findByCriteria(AttestationFinStageCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(AttestationFinStageCriteria criteria);
}
