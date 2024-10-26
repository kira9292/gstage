package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.Manager;
import sn.sonatel.dsi.ins.imoc.domain.criteria.ManagerCriteria;

/**
 * Spring Data R2DBC repository for the Manager entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ManagerRepository extends ReactiveCrudRepository<Manager, Long>, ManagerRepositoryInternal {
    Flux<Manager> findAllBy(Pageable pageable);

    @Query("SELECT * FROM manager entity WHERE entity.service_id = :id")
    Flux<Manager> findByService(Long id);

    @Query("SELECT * FROM manager entity WHERE entity.service_id IS NULL")
    Flux<Manager> findAllWhereServiceIsNull();

    @Override
    <S extends Manager> Mono<S> save(S entity);

    @Override
    Flux<Manager> findAll();

    @Override
    Mono<Manager> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ManagerRepositoryInternal {
    <S extends Manager> Mono<S> save(S entity);

    Flux<Manager> findAllBy(Pageable pageable);

    Flux<Manager> findAll();

    Mono<Manager> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Manager> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Manager> findByCriteria(ManagerCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ManagerCriteria criteria);
}
