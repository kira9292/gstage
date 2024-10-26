package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.AttestationPresence;
import sn.sonatel.dsi.ins.imoc.domain.criteria.AttestationPresenceCriteria;

/**
 * Spring Data R2DBC repository for the AttestationPresence entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttestationPresenceRepository
    extends ReactiveCrudRepository<AttestationPresence, Long>, AttestationPresenceRepositoryInternal {
    Flux<AttestationPresence> findAllBy(Pageable pageable);

    @Query("SELECT * FROM attestation_presence entity WHERE entity.contrat_id = :id")
    Flux<AttestationPresence> findByContrat(Long id);

    @Query("SELECT * FROM attestation_presence entity WHERE entity.contrat_id IS NULL")
    Flux<AttestationPresence> findAllWhereContratIsNull();

    @Query("SELECT * FROM attestation_presence entity WHERE entity.manager_id = :id")
    Flux<AttestationPresence> findByManager(Long id);

    @Query("SELECT * FROM attestation_presence entity WHERE entity.manager_id IS NULL")
    Flux<AttestationPresence> findAllWhereManagerIsNull();

    @Query("SELECT * FROM attestation_presence entity WHERE entity.etat_paiement_id = :id")
    Flux<AttestationPresence> findByEtatPaiement(Long id);

    @Query("SELECT * FROM attestation_presence entity WHERE entity.etat_paiement_id IS NULL")
    Flux<AttestationPresence> findAllWhereEtatPaiementIsNull();

    @Override
    <S extends AttestationPresence> Mono<S> save(S entity);

    @Override
    Flux<AttestationPresence> findAll();

    @Override
    Mono<AttestationPresence> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AttestationPresenceRepositoryInternal {
    <S extends AttestationPresence> Mono<S> save(S entity);

    Flux<AttestationPresence> findAllBy(Pageable pageable);

    Flux<AttestationPresence> findAll();

    Mono<AttestationPresence> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<AttestationPresence> findAllBy(Pageable pageable, Criteria criteria);
    Flux<AttestationPresence> findByCriteria(AttestationPresenceCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(AttestationPresenceCriteria criteria);
}
