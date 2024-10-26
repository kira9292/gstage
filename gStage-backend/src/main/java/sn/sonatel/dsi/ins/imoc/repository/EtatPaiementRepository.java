package sn.sonatel.dsi.ins.imoc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.EtatPaiement;
import sn.sonatel.dsi.ins.imoc.domain.criteria.EtatPaiementCriteria;

/**
 * Spring Data R2DBC repository for the EtatPaiement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EtatPaiementRepository extends ReactiveCrudRepository<EtatPaiement, Long>, EtatPaiementRepositoryInternal {
    Flux<EtatPaiement> findAllBy(Pageable pageable);

    @Query("SELECT * FROM etat_paiement entity WHERE entity.contrat_id = :id")
    Flux<EtatPaiement> findByContrat(Long id);

    @Query("SELECT * FROM etat_paiement entity WHERE entity.contrat_id IS NULL")
    Flux<EtatPaiement> findAllWhereContratIsNull();

    @Query("SELECT * FROM etat_paiement entity WHERE entity.dfc_id = :id")
    Flux<EtatPaiement> findByDfc(Long id);

    @Query("SELECT * FROM etat_paiement entity WHERE entity.dfc_id IS NULL")
    Flux<EtatPaiement> findAllWhereDfcIsNull();

    @Query("SELECT * FROM etat_paiement entity WHERE entity.assistantgwtecreator_id = :id")
    Flux<EtatPaiement> findByAssistantGWTECreator(Long id);

    @Query("SELECT * FROM etat_paiement entity WHERE entity.assistantgwtecreator_id IS NULL")
    Flux<EtatPaiement> findAllWhereAssistantGWTECreatorIsNull();

    @Override
    <S extends EtatPaiement> Mono<S> save(S entity);

    @Override
    Flux<EtatPaiement> findAll();

    @Override
    Mono<EtatPaiement> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EtatPaiementRepositoryInternal {
    <S extends EtatPaiement> Mono<S> save(S entity);

    Flux<EtatPaiement> findAllBy(Pageable pageable);

    Flux<EtatPaiement> findAll();

    Mono<EtatPaiement> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<EtatPaiement> findAllBy(Pageable pageable, Criteria criteria);
    Flux<EtatPaiement> findByCriteria(EtatPaiementCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(EtatPaiementCriteria criteria);
}
