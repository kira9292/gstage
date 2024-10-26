package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.AppService;
import sn.sonatel.dsi.ins.imoc.domain.criteria.AppServiceCriteria;

/**
 * Spring Data R2DBC repository for the AppService entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppServiceRepository extends ReactiveCrudRepository<AppService, Long>, AppServiceRepositoryInternal {
    Flux<AppService> findAllBy(Pageable pageable);

    @Query("SELECT * FROM app_service entity WHERE entity.business_unit_id = :id")
    Flux<AppService> findByBusinessUnit(Long id);

    @Query("SELECT * FROM app_service entity WHERE entity.business_unit_id IS NULL")
    Flux<AppService> findAllWhereBusinessUnitIsNull();

    @Query("SELECT * FROM app_service entity WHERE entity.id not in (select manager_id from manager)")
    Flux<AppService> findAllWhereManagerIsNull();

    @Query("SELECT * FROM app_service entity WHERE entity.departemen_id = :id")
    Flux<AppService> findByDepartemen(Long id);

    @Query("SELECT * FROM app_service entity WHERE entity.departemen_id IS NULL")
    Flux<AppService> findAllWhereDepartemenIsNull();

    @Override
    <S extends AppService> Mono<S> save(S entity);

    @Override
    Flux<AppService> findAll();

    @Override
    Mono<AppService> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AppServiceRepositoryInternal {
    <S extends AppService> Mono<S> save(S entity);

    Flux<AppService> findAllBy(Pageable pageable);

    Flux<AppService> findAll();

    Mono<AppService> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<AppService> findAllBy(Pageable pageable, Criteria criteria);
    Flux<AppService> findByCriteria(AppServiceCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(AppServiceCriteria criteria);
}
