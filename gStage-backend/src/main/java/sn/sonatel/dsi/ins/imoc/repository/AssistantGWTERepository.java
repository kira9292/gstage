package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.AssistantGWTE;
import sn.sonatel.dsi.ins.imoc.domain.criteria.AssistantGWTECriteria;

/**
 * Spring Data R2DBC repository for the AssistantGWTE entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AssistantGWTERepository extends ReactiveCrudRepository<AssistantGWTE, Long>, AssistantGWTERepositoryInternal {
    Flux<AssistantGWTE> findAllBy(Pageable pageable);

    @Override
    <S extends AssistantGWTE> Mono<S> save(S entity);

    @Override
    Flux<AssistantGWTE> findAll();

    @Override
    Mono<AssistantGWTE> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AssistantGWTERepositoryInternal {
    <S extends AssistantGWTE> Mono<S> save(S entity);

    Flux<AssistantGWTE> findAllBy(Pageable pageable);

    Flux<AssistantGWTE> findAll();

    Mono<AssistantGWTE> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<AssistantGWTE> findAllBy(Pageable pageable, Criteria criteria);
    Flux<AssistantGWTE> findByCriteria(AssistantGWTECriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(AssistantGWTECriteria criteria);
}
