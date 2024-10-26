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
import sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage;
import sn.sonatel.dsi.ins.imoc.domain.criteria.AttestationFinStageCriteria;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.AttestationFinStageRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ColumnConverter;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the AttestationFinStage entity.
 */
@SuppressWarnings("unused")
class AttestationFinStageRepositoryInternalImpl
    extends SimpleR2dbcRepository<AttestationFinStage, Long>
    implements AttestationFinStageRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AttestationFinStageRowMapper attestationfinstageMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("attestation_fin_stage", EntityManager.ENTITY_ALIAS);

    public AttestationFinStageRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AttestationFinStageRowMapper attestationfinstageMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(AttestationFinStage.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.attestationfinstageMapper = attestationfinstageMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<AttestationFinStage> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<AttestationFinStage> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AttestationFinStageSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, AttestationFinStage.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<AttestationFinStage> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<AttestationFinStage> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private AttestationFinStage process(Row row, RowMetadata metadata) {
        AttestationFinStage entity = attestationfinstageMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends AttestationFinStage> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<AttestationFinStage> findByCriteria(AttestationFinStageCriteria attestationFinStageCriteria, Pageable page) {
        return createQuery(page, buildConditions(attestationFinStageCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(AttestationFinStageCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(AttestationFinStageCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getReference() != null) {
                builder.buildFilterConditionForField(criteria.getReference(), entityTable.column("reference"));
            }
            if (criteria.getIssueDate() != null) {
                builder.buildFilterConditionForField(criteria.getIssueDate(), entityTable.column("issue_date"));
            }
            if (criteria.getSignatureDate() != null) {
                builder.buildFilterConditionForField(criteria.getSignatureDate(), entityTable.column("signature_date"));
            }
            if (criteria.getComments() != null) {
                builder.buildFilterConditionForField(criteria.getComments(), entityTable.column("comments"));
            }
        }
        return builder.buildConditions();
    }
}
