package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.Drh;
import sn.sonatel.dsi.ins.imoc.domain.criteria.DrhCriteria;

/**
 * Spring Data R2DBC repository for the Drh entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DrhRepository extends ReactiveCrudRepository<Drh, Long>, DrhRepositoryInternal {
    Flux<Drh> findAllBy(Pageable pageable);

    @Override
    <S extends Drh> Mono<S> save(S entity);

    @Override
    Flux<Drh> findAll();

    @Override
    Mono<Drh> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DrhRepositoryInternal {
    <S extends Drh> Mono<S> save(S entity);

    Flux<Drh> findAllBy(Pageable pageable);

    Flux<Drh> findAll();

    Mono<Drh> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Drh> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Drh> findByCriteria(DrhCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(DrhCriteria criteria);
}
