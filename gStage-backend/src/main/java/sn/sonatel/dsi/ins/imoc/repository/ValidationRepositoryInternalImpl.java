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
import sn.sonatel.dsi.ins.imoc.domain.Validation;
import sn.sonatel.dsi.ins.imoc.domain.criteria.ValidationCriteria;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.AppUserRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.AttestationFinStageRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.AttestationPresenceRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ColumnConverter;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ContratRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ValidationRowMapper;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the Validation entity.
 */
@SuppressWarnings("unused")
class ValidationRepositoryInternalImpl extends SimpleR2dbcRepository<Validation, Long> implements ValidationRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AttestationPresenceRowMapper attestationpresenceMapper;
    private final ContratRowMapper contratMapper;
    private final AttestationFinStageRowMapper attestationfinstageMapper;
    private final AppUserRowMapper appuserMapper;
    private final ValidationRowMapper validationMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("validation", EntityManager.ENTITY_ALIAS);
    private static final Table attestationPresenceTable = Table.aliased("attestation_presence", "attestationPresence");
    private static final Table contratTable = Table.aliased("contrat", "contrat");
    private static final Table attestationFinStageTable = Table.aliased("attestation_fin_stage", "attestationFinStage");
    private static final Table userTable = Table.aliased("app_user", "e_user");

    public ValidationRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AttestationPresenceRowMapper attestationpresenceMapper,
        ContratRowMapper contratMapper,
        AttestationFinStageRowMapper attestationfinstageMapper,
        AppUserRowMapper appuserMapper,
        ValidationRowMapper validationMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Validation.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.attestationpresenceMapper = attestationpresenceMapper;
        this.contratMapper = contratMapper;
        this.attestationfinstageMapper = attestationfinstageMapper;
        this.appuserMapper = appuserMapper;
        this.validationMapper = validationMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Validation> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Validation> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ValidationSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AttestationPresenceSqlHelper.getColumns(attestationPresenceTable, "attestationPresence"));
        columns.addAll(ContratSqlHelper.getColumns(contratTable, "contrat"));
        columns.addAll(AttestationFinStageSqlHelper.getColumns(attestationFinStageTable, "attestationFinStage"));
        columns.addAll(AppUserSqlHelper.getColumns(userTable, "user"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(attestationPresenceTable)
            .on(Column.create("attestation_presence_id", entityTable))
            .equals(Column.create("id", attestationPresenceTable))
            .leftOuterJoin(contratTable)
            .on(Column.create("contrat_id", entityTable))
            .equals(Column.create("id", contratTable))
            .leftOuterJoin(attestationFinStageTable)
            .on(Column.create("attestation_fin_stage_id", entityTable))
            .equals(Column.create("id", attestationFinStageTable))
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Validation.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Validation> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Validation> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Validation process(Row row, RowMetadata metadata) {
        Validation entity = validationMapper.apply(row, "e");
        entity.setAttestationPresence(attestationpresenceMapper.apply(row, "attestationPresence"));
        entity.setContrat(contratMapper.apply(row, "contrat"));
        entity.setAttestationFinStage(attestationfinstageMapper.apply(row, "attestationFinStage"));
        entity.setUser(appuserMapper.apply(row, "user"));
        return entity;
    }

    @Override
    public <S extends Validation> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Validation> findByCriteria(ValidationCriteria validationCriteria, Pageable page) {
        return createQuery(page, buildConditions(validationCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(ValidationCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(ValidationCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getReference() != null) {
                builder.buildFilterConditionForField(criteria.getReference(), entityTable.column("reference"));
            }
            if (criteria.getValidationDate() != null) {
                builder.buildFilterConditionForField(criteria.getValidationDate(), entityTable.column("validation_date"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getComments() != null) {
                builder.buildFilterConditionForField(criteria.getComments(), entityTable.column("comments"));
            }
            if (criteria.getValidatedBy() != null) {
                builder.buildFilterConditionForField(criteria.getValidatedBy(), entityTable.column("validated_by"));
            }
            if (criteria.getAttestationPresenceId() != null) {
                builder.buildFilterConditionForField(criteria.getAttestationPresenceId(), attestationPresenceTable.column("id"));
            }
            if (criteria.getContratId() != null) {
                builder.buildFilterConditionForField(criteria.getContratId(), contratTable.column("id"));
            }
            if (criteria.getAttestationFinStageId() != null) {
                builder.buildFilterConditionForField(criteria.getAttestationFinStageId(), attestationFinStageTable.column("id"));
            }
            if (criteria.getUserId() != null) {
                builder.buildFilterConditionForField(criteria.getUserId(), userTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
