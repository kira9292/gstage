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
import sn.sonatel.dsi.ins.imoc.domain.Contrat;
import sn.sonatel.dsi.ins.imoc.domain.criteria.ContratCriteria;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.AssistantGWTERowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.AttestationFinStageRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.CandidatRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ColumnConverter;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ContratRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.DrhRowMapper;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the Contrat entity.
 */
@SuppressWarnings("unused")
class ContratRepositoryInternalImpl extends SimpleR2dbcRepository<Contrat, Long> implements ContratRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AttestationFinStageRowMapper attestationfinstageMapper;
    private final DrhRowMapper drhMapper;
    private final AssistantGWTERowMapper assistantgwteMapper;
    private final CandidatRowMapper candidatMapper;
    private final ContratRowMapper contratMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("contrat", EntityManager.ENTITY_ALIAS);
    private static final Table attestationFinStageTable = Table.aliased("attestation_fin_stage", "attestationFinStage");
    private static final Table drhTable = Table.aliased("drh", "drh");
    private static final Table assistantGWTECreatorTable = Table.aliased("assistant_gwte", "assistantGWTECreator");
    private static final Table candidatTable = Table.aliased("candidat", "candidat");

    public ContratRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AttestationFinStageRowMapper attestationfinstageMapper,
        DrhRowMapper drhMapper,
        AssistantGWTERowMapper assistantgwteMapper,
        CandidatRowMapper candidatMapper,
        ContratRowMapper contratMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Contrat.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.attestationfinstageMapper = attestationfinstageMapper;
        this.drhMapper = drhMapper;
        this.assistantgwteMapper = assistantgwteMapper;
        this.candidatMapper = candidatMapper;
        this.contratMapper = contratMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Contrat> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Contrat> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ContratSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AttestationFinStageSqlHelper.getColumns(attestationFinStageTable, "attestationFinStage"));
        columns.addAll(DrhSqlHelper.getColumns(drhTable, "drh"));
        columns.addAll(AssistantGWTESqlHelper.getColumns(assistantGWTECreatorTable, "assistantGWTECreator"));
        columns.addAll(CandidatSqlHelper.getColumns(candidatTable, "candidat"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(attestationFinStageTable)
            .on(Column.create("attestation_fin_stage_id", entityTable))
            .equals(Column.create("id", attestationFinStageTable))
            .leftOuterJoin(drhTable)
            .on(Column.create("drh_id", entityTable))
            .equals(Column.create("id", drhTable))
            .leftOuterJoin(assistantGWTECreatorTable)
            .on(Column.create("assistantgwtecreator_id", entityTable))
            .equals(Column.create("id", assistantGWTECreatorTable))
            .leftOuterJoin(candidatTable)
            .on(Column.create("candidat_id", entityTable))
            .equals(Column.create("id", candidatTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Contrat.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Contrat> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Contrat> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Contrat process(Row row, RowMetadata metadata) {
        Contrat entity = contratMapper.apply(row, "e");
        entity.setAttestationFinStage(attestationfinstageMapper.apply(row, "attestationFinStage"));
        entity.setDrh(drhMapper.apply(row, "drh"));
        entity.setAssistantGWTECreator(assistantgwteMapper.apply(row, "assistantGWTECreator"));
        entity.setCandidat(candidatMapper.apply(row, "candidat"));
        return entity;
    }

    @Override
    public <S extends Contrat> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Contrat> findByCriteria(ContratCriteria contratCriteria, Pageable page) {
        return createQuery(page, buildConditions(contratCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(ContratCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(ContratCriteria criteria) {
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
            if (criteria.getCompensation() != null) {
                builder.buildFilterConditionForField(criteria.getCompensation(), entityTable.column("compensation"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getAssignmentSite() != null) {
                builder.buildFilterConditionForField(criteria.getAssignmentSite(), entityTable.column("assignment_site"));
            }
            if (criteria.getSignatureDate() != null) {
                builder.buildFilterConditionForField(criteria.getSignatureDate(), entityTable.column("signature_date"));
            }
            if (criteria.getComments() != null) {
                builder.buildFilterConditionForField(criteria.getComments(), entityTable.column("comments"));
            }
            if (criteria.getAttestationFinStageId() != null) {
                builder.buildFilterConditionForField(criteria.getAttestationFinStageId(), attestationFinStageTable.column("id"));
            }
            if (criteria.getDrhId() != null) {
                builder.buildFilterConditionForField(criteria.getDrhId(), drhTable.column("id"));
            }
            if (criteria.getAssistantGWTECreatorId() != null) {
                builder.buildFilterConditionForField(criteria.getAssistantGWTECreatorId(), assistantGWTECreatorTable.column("id"));
            }
            if (criteria.getCandidatId() != null) {
                builder.buildFilterConditionForField(criteria.getCandidatId(), candidatTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
