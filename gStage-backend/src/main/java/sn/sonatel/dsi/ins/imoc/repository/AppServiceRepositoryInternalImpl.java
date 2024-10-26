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
import sn.sonatel.dsi.ins.imoc.domain.AppService;
import sn.sonatel.dsi.ins.imoc.domain.criteria.AppServiceCriteria;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.AppServiceRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.BusinessUnitRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ColumnConverter;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.DepartementRowMapper;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the AppService entity.
 */
@SuppressWarnings("unused")
class AppServiceRepositoryInternalImpl extends SimpleR2dbcRepository<AppService, Long> implements AppServiceRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final BusinessUnitRowMapper businessunitMapper;
    private final DepartementRowMapper departementMapper;
    private final AppServiceRowMapper appserviceMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("app_service", EntityManager.ENTITY_ALIAS);
    private static final Table businessUnitTable = Table.aliased("business_unit", "businessUnit");
    private static final Table departemenTable = Table.aliased("departement", "departemen");

    public AppServiceRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        BusinessUnitRowMapper businessunitMapper,
        DepartementRowMapper departementMapper,
        AppServiceRowMapper appserviceMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(AppService.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.businessunitMapper = businessunitMapper;
        this.departementMapper = departementMapper;
        this.appserviceMapper = appserviceMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<AppService> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<AppService> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AppServiceSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(BusinessUnitSqlHelper.getColumns(businessUnitTable, "businessUnit"));
        columns.addAll(DepartementSqlHelper.getColumns(departemenTable, "departemen"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(businessUnitTable)
            .on(Column.create("business_unit_id", entityTable))
            .equals(Column.create("id", businessUnitTable))
            .leftOuterJoin(departemenTable)
            .on(Column.create("departemen_id", entityTable))
            .equals(Column.create("id", departemenTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, AppService.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<AppService> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<AppService> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private AppService process(Row row, RowMetadata metadata) {
        AppService entity = appserviceMapper.apply(row, "e");
        entity.setBusinessUnit(businessunitMapper.apply(row, "businessUnit"));
        entity.setDepartemen(departementMapper.apply(row, "departemen"));
        return entity;
    }

    @Override
    public <S extends AppService> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<AppService> findByCriteria(AppServiceCriteria appServiceCriteria, Pageable page) {
        return createQuery(page, buildConditions(appServiceCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(AppServiceCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(AppServiceCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getName() != null) {
                builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
            }
            if (criteria.getDescription() != null) {
                builder.buildFilterConditionForField(criteria.getDescription(), entityTable.column("description"));
            }
            if (criteria.getBusinessUnitId() != null) {
                builder.buildFilterConditionForField(criteria.getBusinessUnitId(), businessUnitTable.column("id"));
            }
            if (criteria.getDepartemenId() != null) {
                builder.buildFilterConditionForField(criteria.getDepartemenId(), departemenTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
