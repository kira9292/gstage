package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaire;
import sn.sonatel.dsi.ins.imoc.domain.criteria.RestaurationStagiaireCriteria;

/**
 * Spring Data R2DBC repository for the RestaurationStagiaire entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RestaurationStagiaireRepository
    extends ReactiveCrudRepository<RestaurationStagiaire, Long>, RestaurationStagiaireRepositoryInternal {
    Flux<RestaurationStagiaire> findAllBy(Pageable pageable);

    @Query("SELECT * FROM restauration_stagiaire entity WHERE entity.candidat_id = :id")
    Flux<RestaurationStagiaire> findByCandidat(Long id);

    @Query("SELECT * FROM restauration_stagiaire entity WHERE entity.candidat_id IS NULL")
    Flux<RestaurationStagiaire> findAllWhereCandidatIsNull();

    @Override
    <S extends RestaurationStagiaire> Mono<S> save(S entity);

    @Override
    Flux<RestaurationStagiaire> findAll();

    @Override
    Mono<RestaurationStagiaire> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RestaurationStagiaireRepositoryInternal {
    <S extends RestaurationStagiaire> Mono<S> save(S entity);

    Flux<RestaurationStagiaire> findAllBy(Pageable pageable);

    Flux<RestaurationStagiaire> findAll();

    Mono<RestaurationStagiaire> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<RestaurationStagiaire> findAllBy(Pageable pageable, Criteria criteria);
    Flux<RestaurationStagiaire> findByCriteria(RestaurationStagiaireCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(RestaurationStagiaireCriteria criteria);
}
