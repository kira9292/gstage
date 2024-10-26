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
import sn.sonatel.dsi.ins.imoc.domain.Dfc;
import sn.sonatel.dsi.ins.imoc.domain.criteria.DfcCriteria;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ColumnConverter;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.DfcRowMapper;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the Dfc entity.
 */
@SuppressWarnings("unused")
class DfcRepositoryInternalImpl extends SimpleR2dbcRepository<Dfc, Long> implements DfcRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final DfcRowMapper dfcMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("dfc", EntityManager.ENTITY_ALIAS);

    public DfcRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        DfcRowMapper dfcMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Dfc.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.dfcMapper = dfcMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Dfc> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Dfc> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = DfcSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Dfc.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Dfc> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Dfc> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Dfc process(Row row, RowMetadata metadata) {
        Dfc entity = dfcMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Dfc> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Dfc> findByCriteria(DfcCriteria dfcCriteria, Pageable page) {
        return createQuery(page, buildConditions(dfcCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(DfcCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(DfcCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
