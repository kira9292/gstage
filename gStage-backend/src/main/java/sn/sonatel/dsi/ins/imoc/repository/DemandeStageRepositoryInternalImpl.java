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
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.criteria.DemandeStageCriteria;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.AssistantGWTERowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.BusinessUnitRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.CandidatRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ColumnConverter;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.DemandeStageRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.DepartementRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ManagerRowMapper;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the DemandeStage entity.
 */
@SuppressWarnings("unused")
class DemandeStageRepositoryInternalImpl extends SimpleR2dbcRepository<DemandeStage, Long> implements DemandeStageRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CandidatRowMapper candidatMapper;
    private final AssistantGWTERowMapper assistantgwteMapper;
    private final ManagerRowMapper managerMapper;
    private final DepartementRowMapper departementMapper;
    private final BusinessUnitRowMapper businessunitMapper;
    private final DemandeStageRowMapper demandestageMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("demande_stage", EntityManager.ENTITY_ALIAS);
    private static final Table candidatTable = Table.aliased("candidat", "candidat");
    private static final Table assistantGWTETable = Table.aliased("assistant_gwte", "assistantGWTE");
    private static final Table managerTable = Table.aliased("manager", "manager");
    private static final Table departementTable = Table.aliased("departement", "departement");
    private static final Table businessUnitTable = Table.aliased("business_unit", "businessUnit");

    public DemandeStageRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CandidatRowMapper candidatMapper,
        AssistantGWTERowMapper assistantgwteMapper,
        ManagerRowMapper managerMapper,
        DepartementRowMapper departementMapper,
        BusinessUnitRowMapper businessunitMapper,
        DemandeStageRowMapper demandestageMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(DemandeStage.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.candidatMapper = candidatMapper;
        this.assistantgwteMapper = assistantgwteMapper;
        this.managerMapper = managerMapper;
        this.departementMapper = departementMapper;
        this.businessunitMapper = businessunitMapper;
        this.demandestageMapper = demandestageMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<DemandeStage> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<DemandeStage> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = DemandeStageSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CandidatSqlHelper.getColumns(candidatTable, "candidat"));
        columns.addAll(AssistantGWTESqlHelper.getColumns(assistantGWTETable, "assistantGWTE"));
        columns.addAll(ManagerSqlHelper.getColumns(managerTable, "manager"));
        columns.addAll(DepartementSqlHelper.getColumns(departementTable, "departement"));
        columns.addAll(BusinessUnitSqlHelper.getColumns(businessUnitTable, "businessUnit"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(candidatTable)
            .on(Column.create("candidat_id", entityTable))
            .equals(Column.create("id", candidatTable))
            .leftOuterJoin(assistantGWTETable)
            .on(Column.create("assistantgwte_id", entityTable))
            .equals(Column.create("id", assistantGWTETable))
            .leftOuterJoin(managerTable)
            .on(Column.create("manager_id", entityTable))
            .equals(Column.create("id", managerTable))
            .leftOuterJoin(departementTable)
            .on(Column.create("departement_id", entityTable))
            .equals(Column.create("id", departementTable))
            .leftOuterJoin(businessUnitTable)
            .on(Column.create("business_unit_id", entityTable))
            .equals(Column.create("id", businessUnitTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, DemandeStage.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<DemandeStage> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<DemandeStage> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private DemandeStage process(Row row, RowMetadata metadata) {
        DemandeStage entity = demandestageMapper.apply(row, "e");
        entity.setCandidat(candidatMapper.apply(row, "candidat"));
        entity.setAssistantGWTE(assistantgwteMapper.apply(row, "assistantGWTE"));
        entity.setManager(managerMapper.apply(row, "manager"));
        entity.setDepartement(departementMapper.apply(row, "departement"));
        entity.setBusinessUnit(businessunitMapper.apply(row, "businessUnit"));
        return entity;
    }

    @Override
    public <S extends DemandeStage> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<DemandeStage> findByCriteria(DemandeStageCriteria demandeStageCriteria, Pageable page) {
        return createQuery(page, buildConditions(demandeStageCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(DemandeStageCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(DemandeStageCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getReference() != null) {
                builder.buildFilterConditionForField(criteria.getReference(), entityTable.column("reference"));
            }
            if (criteria.getCreationDate() != null) {
                builder.buildFilterConditionForField(criteria.getCreationDate(), entityTable.column("creation_date"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getInternshipType() != null) {
                builder.buildFilterConditionForField(criteria.getInternshipType(), entityTable.column("internship_type"));
            }
            if (criteria.getStartDate() != null) {
                builder.buildFilterConditionForField(criteria.getStartDate(), entityTable.column("start_date"));
            }
            if (criteria.getEndDate() != null) {
                builder.buildFilterConditionForField(criteria.getEndDate(), entityTable.column("end_date"));
            }
            if (criteria.getValidated() != null) {
                builder.buildFilterConditionForField(criteria.getValidated(), entityTable.column("validated"));
            }
            if (criteria.getCandidatId() != null) {
                builder.buildFilterConditionForField(criteria.getCandidatId(), candidatTable.column("id"));
            }
            if (criteria.getAssistantGWTEId() != null) {
                builder.buildFilterConditionForField(criteria.getAssistantGWTEId(), assistantGWTETable.column("id"));
            }
            if (criteria.getManagerId() != null) {
                builder.buildFilterConditionForField(criteria.getManagerId(), managerTable.column("id"));
            }
            if (criteria.getDepartementId() != null) {
                builder.buildFilterConditionForField(criteria.getDepartementId(), departementTable.column("id"));
            }
            if (criteria.getBusinessUnitId() != null) {
                builder.buildFilterConditionForField(criteria.getBusinessUnitId(), businessUnitTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
