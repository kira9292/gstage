package sn.sonatel.dsi.ins.imoc.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.AttestationPresence;
import sn.sonatel.dsi.ins.imoc.domain.criteria.AttestationPresenceCriteria;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.AttestationPresenceRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ColumnConverter;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ContratRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.EtatPaiementRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ManagerRowMapper;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the AttestationPresence entity.
 */
@SuppressWarnings("unused")
class AttestationPresenceRepositoryInternalImpl
    extends SimpleR2dbcRepository<AttestationPresence, Long>
    implements AttestationPresenceRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ContratRowMapper contratMapper;
    private final ManagerRowMapper managerMapper;
    private final EtatPaiementRowMapper etatpaiementMapper;
    private final AttestationPresenceRowMapper attestationpresenceMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("attestation_presence", EntityManager.ENTITY_ALIAS);
    private static final Table contratTable = Table.aliased("contrat", "contrat");
    private static final Table managerTable = Table.aliased("manager", "manager");
    private static final Table etatPaiementTable = Table.aliased("etat_paiement", "etatPaiement");

    public AttestationPresenceRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ContratRowMapper contratMapper,
        ManagerRowMapper managerMapper,
        EtatPaiementRowMapper etatpaiementMapper,
        AttestationPresenceRowMapper attestationpresenceMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(AttestationPresence.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.contratMapper = contratMapper;
        this.managerMapper = managerMapper;
        this.etatpaiementMapper = etatpaiementMapper;
        this.attestationpresenceMapper = attestationpresenceMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<AttestationPresence> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<AttestationPresence> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AttestationPresenceSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ContratSqlHelper.getColumns(contratTable, "contrat"));
        columns.addAll(ManagerSqlHelper.getColumns(managerTable, "manager"));
        columns.addAll(EtatPaiementSqlHelper.getColumns(etatPaiementTable, "etatPaiement"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(contratTable)
            .on(Column.create("contrat_id", entityTable))
            .equals(Column.create("id", contratTable))
            .leftOuterJoin(managerTable)
            .on(Column.create("manager_id", entityTable))
            .equals(Column.create("id", managerTable))
            .leftOuterJoin(etatPaiementTable)
            .on(Column.create("etat_paiement_id", entityTable))
            .equals(Column.create("id", etatPaiementTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, AttestationPresence.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<AttestationPresence> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<AttestationPresence> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private AttestationPresence process(Row row, RowMetadata metadata) {
        AttestationPresence entity = attestationpresenceMapper.apply(row, "e");
        entity.setContrat(contratMapper.apply(row, "contrat"));
        entity.setManager(managerMapper.apply(row, "manager"));
        entity.setEtatPaiement(etatpaiementMapper.apply(row, "etatPaiement"));
        return entity;
    }

    @Override
    public <S extends AttestationPresence> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<AttestationPresence> findByCriteria(AttestationPresenceCriteria attestationPresenceCriteria, Pageable page) {
        return createQuery(page, buildConditions(attestationPresenceCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(AttestationPresenceCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(AttestationPresenceCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getReference() != null) {
                builder.buildFilterConditionForField(criteria.getReference(), entityTable.column("reference"));
            }
            if (criteria.getStartDate() != null) {
                builder.buildFilterConditionForField(criteria.getStartDate(), entityTable.column("start_date"));
            }
            if (criteria.getEndDate() != null) {
                builder.buildFilterConditionForField(criteria.getEndDate(), entityTable.column("end_date"));
            }
            if (criteria.getSignatureDate() != null) {
                builder.buildFilterConditionForField(criteria.getSignatureDate(), entityTable.column("signature_date"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getComments() != null) {
                builder.buildFilterConditionForField(criteria.getComments(), entityTable.column("comments"));
            }
            if (criteria.getContratId() != null) {
                builder.buildFilterConditionForField(criteria.getContratId(), contratTable.column("id"));
            }
            if (criteria.getManagerId() != null) {
                builder.buildFilterConditionForField(criteria.getManagerId(), managerTable.column("id"));
            }
            if (criteria.getEtatPaiementId() != null) {
                builder.buildFilterConditionForField(criteria.getEtatPaiementId(), etatPaiementTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
