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
import sn.sonatel.dsi.ins.imoc.domain.AssistantGWTE;
import sn.sonatel.dsi.ins.imoc.domain.criteria.AssistantGWTECriteria;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.AssistantGWTERowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ColumnConverter;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the AssistantGWTE entity.
 */
@SuppressWarnings("unused")
class AssistantGWTERepositoryInternalImpl extends SimpleR2dbcRepository<AssistantGWTE, Long> implements AssistantGWTERepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AssistantGWTERowMapper assistantgwteMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("assistant_gwte", EntityManager.ENTITY_ALIAS);

    public AssistantGWTERepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AssistantGWTERowMapper assistantgwteMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(AssistantGWTE.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.assistantgwteMapper = assistantgwteMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<AssistantGWTE> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<AssistantGWTE> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AssistantGWTESqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, AssistantGWTE.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<AssistantGWTE> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<AssistantGWTE> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private AssistantGWTE process(Row row, RowMetadata metadata) {
        AssistantGWTE entity = assistantgwteMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends AssistantGWTE> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<AssistantGWTE> findByCriteria(AssistantGWTECriteria assistantGWTECriteria, Pageable page) {
        return createQuery(page, buildConditions(assistantGWTECriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(AssistantGWTECriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(AssistantGWTECriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getPhone() != null) {
                builder.buildFilterConditionForField(criteria.getPhone(), entityTable.column("phone"));
            }
        }
        return builder.buildConditions();
    }
}
