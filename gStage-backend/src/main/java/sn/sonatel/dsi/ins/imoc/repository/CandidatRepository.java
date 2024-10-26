package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.criteria.CandidatCriteria;

/**
 * Spring Data R2DBC repository for the Candidat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CandidatRepository extends ReactiveCrudRepository<Candidat, Long>, CandidatRepositoryInternal {
    Flux<Candidat> findAllBy(Pageable pageable);

    @Query("SELECT * FROM candidat entity WHERE entity.id not in (select demande_stage_id from demande_stage)")
    Flux<Candidat> findAllWhereDemandeStageIsNull();

    @Query("SELECT * FROM candidat entity WHERE entity.manager_id = :id")
    Flux<Candidat> findByManager(Long id);

    @Query("SELECT * FROM candidat entity WHERE entity.manager_id IS NULL")
    Flux<Candidat> findAllWhereManagerIsNull();

    @Override
    <S extends Candidat> Mono<S> save(S entity);

    @Override
    Flux<Candidat> findAll();

    @Override
    Mono<Candidat> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CandidatRepositoryInternal {
    <S extends Candidat> Mono<S> save(S entity);

    Flux<Candidat> findAllBy(Pageable pageable);

    Flux<Candidat> findAll();

    Mono<Candidat> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Candidat> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Candidat> findByCriteria(CandidatCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(CandidatCriteria criteria);
}
