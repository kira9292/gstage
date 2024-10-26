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
import sn.sonatel.dsi.ins.imoc.domain.EtatPaiement;
import sn.sonatel.dsi.ins.imoc.domain.criteria.EtatPaiementCriteria;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.AssistantGWTERowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ColumnConverter;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ContratRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.DfcRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.EtatPaiementRowMapper;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the EtatPaiement entity.
 */
@SuppressWarnings("unused")
class EtatPaiementRepositoryInternalImpl extends SimpleR2dbcRepository<EtatPaiement, Long> implements EtatPaiementRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ContratRowMapper contratMapper;
    private final DfcRowMapper dfcMapper;
    private final AssistantGWTERowMapper assistantgwteMapper;
    private final EtatPaiementRowMapper etatpaiementMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("etat_paiement", EntityManager.ENTITY_ALIAS);
    private static final Table contratTable = Table.aliased("contrat", "contrat");
    private static final Table dfcTable = Table.aliased("dfc", "dfc");
    private static final Table assistantGWTECreatorTable = Table.aliased("assistant_gwte", "assistantGWTECreator");

    public EtatPaiementRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ContratRowMapper contratMapper,
        DfcRowMapper dfcMapper,
        AssistantGWTERowMapper assistantgwteMapper,
        EtatPaiementRowMapper etatpaiementMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(EtatPaiement.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.contratMapper = contratMapper;
        this.dfcMapper = dfcMapper;
        this.assistantgwteMapper = assistantgwteMapper;
        this.etatpaiementMapper = etatpaiementMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<EtatPaiement> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<EtatPaiement> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = EtatPaiementSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ContratSqlHelper.getColumns(contratTable, "contrat"));
        columns.addAll(DfcSqlHelper.getColumns(dfcTable, "dfc"));
        columns.addAll(AssistantGWTESqlHelper.getColumns(assistantGWTECreatorTable, "assistantGWTECreator"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(contratTable)
            .on(Column.create("contrat_id", entityTable))
            .equals(Column.create("id", contratTable))
            .leftOuterJoin(dfcTable)
            .on(Column.create("dfc_id", entityTable))
            .equals(Column.create("id", dfcTable))
            .leftOuterJoin(assistantGWTECreatorTable)
            .on(Column.create("assistantgwtecreator_id", entityTable))
            .equals(Column.create("id", assistantGWTECreatorTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, EtatPaiement.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<EtatPaiement> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<EtatPaiement> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private EtatPaiement process(Row row, RowMetadata metadata) {
        EtatPaiement entity = etatpaiementMapper.apply(row, "e");
        entity.setContrat(contratMapper.apply(row, "contrat"));
        entity.setDfc(dfcMapper.apply(row, "dfc"));
        entity.setAssistantGWTECreator(assistantgwteMapper.apply(row, "assistantGWTECreator"));
        return entity;
    }

    @Override
    public <S extends EtatPaiement> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<EtatPaiement> findByCriteria(EtatPaiementCriteria etatPaiementCriteria, Pageable page) {
        return createQuery(page, buildConditions(etatPaiementCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(EtatPaiementCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(EtatPaiementCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getReference() != null) {
                builder.buildFilterConditionForField(criteria.getReference(), entityTable.column("reference"));
            }
            if (criteria.getPaymentNumber() != null) {
                builder.buildFilterConditionForField(criteria.getPaymentNumber(), entityTable.column("payment_number"));
            }
            if (criteria.getPaymentDate() != null) {
                builder.buildFilterConditionForField(criteria.getPaymentDate(), entityTable.column("payment_date"));
            }
            if (criteria.getAmount() != null) {
                builder.buildFilterConditionForField(criteria.getAmount(), entityTable.column("amount"));
            }
            if (criteria.getActCode() != null) {
                builder.buildFilterConditionForField(criteria.getActCode(), entityTable.column("act_code"));
            }
            if (criteria.getPaymentPhone() != null) {
                builder.buildFilterConditionForField(criteria.getPaymentPhone(), entityTable.column("payment_phone"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getProcessingDate() != null) {
                builder.buildFilterConditionForField(criteria.getProcessingDate(), entityTable.column("processing_date"));
            }
            if (criteria.getComments() != null) {
                builder.buildFilterConditionForField(criteria.getComments(), entityTable.column("comments"));
            }
            if (criteria.getContratId() != null) {
                builder.buildFilterConditionForField(criteria.getContratId(), contratTable.column("id"));
            }
            if (criteria.getDfcId() != null) {
                builder.buildFilterConditionForField(criteria.getDfcId(), dfcTable.column("id"));
            }
            if (criteria.getAssistantGWTECreatorId() != null) {
                builder.buildFilterConditionForField(criteria.getAssistantGWTECreatorId(), assistantGWTECreatorTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
