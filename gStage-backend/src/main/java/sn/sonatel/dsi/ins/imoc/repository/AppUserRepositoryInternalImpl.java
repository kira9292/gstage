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
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.criteria.AppUserCriteria;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.AppUserRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ColumnConverter;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the AppUser entity.
 */
@SuppressWarnings("unused")
class AppUserRepositoryInternalImpl extends SimpleR2dbcRepository<AppUser, Long> implements AppUserRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AppUserRowMapper appuserMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("app_user", EntityManager.ENTITY_ALIAS);

    public AppUserRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AppUserRowMapper appuserMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(AppUser.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.appuserMapper = appuserMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<AppUser> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<AppUser> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AppUserSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, AppUser.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<AppUser> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<AppUser> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private AppUser process(Row row, RowMetadata metadata) {
        AppUser entity = appuserMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends AppUser> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<AppUser> findByCriteria(AppUserCriteria appUserCriteria, Pageable page) {
        return createQuery(page, buildConditions(appUserCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(AppUserCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(AppUserCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getUsername() != null) {
                builder.buildFilterConditionForField(criteria.getUsername(), entityTable.column("username"));
            }
            if (criteria.getEmail() != null) {
                builder.buildFilterConditionForField(criteria.getEmail(), entityTable.column("email"));
            }
            if (criteria.getPassword() != null) {
                builder.buildFilterConditionForField(criteria.getPassword(), entityTable.column("password"));
            }
            if (criteria.getName() != null) {
                builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
            }
            if (criteria.getFirstName() != null) {
                builder.buildFilterConditionForField(criteria.getFirstName(), entityTable.column("first_name"));
            }
        }
        return builder.buildConditions();
    }
}
