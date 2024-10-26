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
import sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaire;
import sn.sonatel.dsi.ins.imoc.domain.criteria.RestaurationStagiaireCriteria;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.CandidatRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ColumnConverter;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.RestaurationStagiaireRowMapper;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the RestaurationStagiaire entity.
 */
@SuppressWarnings("unused")
class RestaurationStagiaireRepositoryInternalImpl
    extends SimpleR2dbcRepository<RestaurationStagiaire, Long>
    implements RestaurationStagiaireRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CandidatRowMapper candidatMapper;
    private final RestaurationStagiaireRowMapper restaurationstagiaireMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("restauration_stagiaire", EntityManager.ENTITY_ALIAS);
    private static final Table candidatTable = Table.aliased("candidat", "candidat");

    public RestaurationStagiaireRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CandidatRowMapper candidatMapper,
        RestaurationStagiaireRowMapper restaurationstagiaireMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(RestaurationStagiaire.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.candidatMapper = candidatMapper;
        this.restaurationstagiaireMapper = restaurationstagiaireMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<RestaurationStagiaire> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<RestaurationStagiaire> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = RestaurationStagiaireSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CandidatSqlHelper.getColumns(candidatTable, "candidat"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(candidatTable)
            .on(Column.create("candidat_id", entityTable))
            .equals(Column.create("id", candidatTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, RestaurationStagiaire.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<RestaurationStagiaire> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<RestaurationStagiaire> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private RestaurationStagiaire process(Row row, RowMetadata metadata) {
        RestaurationStagiaire entity = restaurationstagiaireMapper.apply(row, "e");
        entity.setCandidat(candidatMapper.apply(row, "candidat"));
        return entity;
    }

    @Override
    public <S extends RestaurationStagiaire> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<RestaurationStagiaire> findByCriteria(RestaurationStagiaireCriteria restaurationStagiaireCriteria, Pageable page) {
        return createQuery(page, buildConditions(restaurationStagiaireCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(RestaurationStagiaireCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(RestaurationStagiaireCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getStartDate() != null) {
                builder.buildFilterConditionForField(criteria.getStartDate(), entityTable.column("start_date"));
            }
            if (criteria.getEndDate() != null) {
                builder.buildFilterConditionForField(criteria.getEndDate(), entityTable.column("end_date"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getCardNumber() != null) {
                builder.buildFilterConditionForField(criteria.getCardNumber(), entityTable.column("card_number"));
            }
            if (criteria.getCandidatId() != null) {
                builder.buildFilterConditionForField(criteria.getCandidatId(), candidatTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
