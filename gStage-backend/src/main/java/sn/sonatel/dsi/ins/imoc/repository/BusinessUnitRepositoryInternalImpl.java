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
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.BusinessUnit;
import sn.sonatel.dsi.ins.imoc.domain.criteria.BusinessUnitCriteria;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.BusinessUnitRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ColumnConverter;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the BusinessUnit entity.
 */
@SuppressWarnings("unused")
class BusinessUnitRepositoryInternalImpl extends SimpleR2dbcRepository<BusinessUnit, Long> implements BusinessUnitRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final BusinessUnitRowMapper businessunitMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("business_unit", EntityManager.ENTITY_ALIAS);

    public BusinessUnitRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        BusinessUnitRowMapper businessunitMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(BusinessUnit.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.businessunitMapper = businessunitMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<BusinessUnit> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<BusinessUnit> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = BusinessUnitSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, BusinessUnit.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<BusinessUnit> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<BusinessUnit> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private BusinessUnit process(Row row, RowMetadata metadata) {
        BusinessUnit entity = businessunitMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends BusinessUnit> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<BusinessUnit> findByCriteria(BusinessUnitCriteria businessUnitCriteria, Pageable page) {
        return createQuery(page, buildConditions(businessUnitCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(BusinessUnitCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(BusinessUnitCriteria criteria) {
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
            if (criteria.getCode() != null) {
                builder.buildFilterConditionForField(criteria.getCode(), entityTable.column("code"));
            }
        }
        return builder.buildConditions();
    }
}
