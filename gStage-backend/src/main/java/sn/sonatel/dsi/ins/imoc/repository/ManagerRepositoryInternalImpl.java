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
import sn.sonatel.dsi.ins.imoc.domain.Manager;
import sn.sonatel.dsi.ins.imoc.domain.criteria.ManagerCriteria;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.AppServiceRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ColumnConverter;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ManagerRowMapper;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the Manager entity.
 */
@SuppressWarnings("unused")
class ManagerRepositoryInternalImpl extends SimpleR2dbcRepository<Manager, Long> implements ManagerRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AppServiceRowMapper appserviceMapper;
    private final ManagerRowMapper managerMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("manager", EntityManager.ENTITY_ALIAS);
    private static final Table serviceTable = Table.aliased("app_service", "service");

    public ManagerRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AppServiceRowMapper appserviceMapper,
        ManagerRowMapper managerMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Manager.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.appserviceMapper = appserviceMapper;
        this.managerMapper = managerMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Manager> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Manager> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ManagerSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AppServiceSqlHelper.getColumns(serviceTable, "service"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(serviceTable)
            .on(Column.create("service_id", entityTable))
            .equals(Column.create("id", serviceTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Manager.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Manager> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Manager> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Manager process(Row row, RowMetadata metadata) {
        Manager entity = managerMapper.apply(row, "e");
        entity.setService(appserviceMapper.apply(row, "service"));
        return entity;
    }

    @Override
    public <S extends Manager> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Manager> findByCriteria(ManagerCriteria managerCriteria, Pageable page) {
        return createQuery(page, buildConditions(managerCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(ManagerCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(ManagerCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getPhone() != null) {
                builder.buildFilterConditionForField(criteria.getPhone(), entityTable.column("phone"));
            }
            if (criteria.getServiceId() != null) {
                builder.buildFilterConditionForField(criteria.getServiceId(), serviceTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
