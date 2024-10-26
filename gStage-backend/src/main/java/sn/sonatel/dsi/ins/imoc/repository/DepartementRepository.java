package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.Departement;
import sn.sonatel.dsi.ins.imoc.domain.criteria.DepartementCriteria;

/**
 * Spring Data R2DBC repository for the Departement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DepartementRepository extends ReactiveCrudRepository<Departement, Long>, DepartementRepositoryInternal {
    Flux<Departement> findAllBy(Pageable pageable);

    @Override
    <S extends Departement> Mono<S> save(S entity);

    @Override
    Flux<Departement> findAll();

    @Override
    Mono<Departement> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DepartementRepositoryInternal {
    <S extends Departement> Mono<S> save(S entity);

    Flux<Departement> findAllBy(Pageable pageable);

    Flux<Departement> findAll();

    Mono<Departement> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Departement> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Departement> findByCriteria(DepartementCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(DepartementCriteria criteria);
}
