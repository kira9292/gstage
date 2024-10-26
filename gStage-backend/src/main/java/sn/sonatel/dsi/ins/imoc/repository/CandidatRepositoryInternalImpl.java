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
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.criteria.CandidatCriteria;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.CandidatRowMapper;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ColumnConverter;
import sn.sonatel.dsi.ins.imoc.repository.rowmapper.ManagerRowMapper;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the Candidat entity.
 */
@SuppressWarnings("unused")
class CandidatRepositoryInternalImpl extends SimpleR2dbcRepository<Candidat, Long> implements CandidatRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ManagerRowMapper managerMapper;
    private final CandidatRowMapper candidatMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("candidat", EntityManager.ENTITY_ALIAS);
    private static final Table managerTable = Table.aliased("manager", "manager");

    public CandidatRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ManagerRowMapper managerMapper,
        CandidatRowMapper candidatMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Candidat.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.managerMapper = managerMapper;
        this.candidatMapper = candidatMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Candidat> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Candidat> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CandidatSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ManagerSqlHelper.getColumns(managerTable, "manager"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(managerTable)
            .on(Column.create("manager_id", entityTable))
            .equals(Column.create("id", managerTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Candidat.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Candidat> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Candidat> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Candidat process(Row row, RowMetadata metadata) {
        Candidat entity = candidatMapper.apply(row, "e");
        entity.setManager(managerMapper.apply(row, "manager"));
        return entity;
    }

    @Override
    public <S extends Candidat> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Candidat> findByCriteria(CandidatCriteria candidatCriteria, Pageable page) {
        return createQuery(page, buildConditions(candidatCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(CandidatCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(CandidatCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getFirstName() != null) {
                builder.buildFilterConditionForField(criteria.getFirstName(), entityTable.column("first_name"));
            }
            if (criteria.getLastName() != null) {
                builder.buildFilterConditionForField(criteria.getLastName(), entityTable.column("last_name"));
            }
            if (criteria.getBirthDate() != null) {
                builder.buildFilterConditionForField(criteria.getBirthDate(), entityTable.column("birth_date"));
            }
            if (criteria.getNationality() != null) {
                builder.buildFilterConditionForField(criteria.getNationality(), entityTable.column("nationality"));
            }
            if (criteria.getBirthPlace() != null) {
                builder.buildFilterConditionForField(criteria.getBirthPlace(), entityTable.column("birth_place"));
            }
            if (criteria.getIdNumber() != null) {
                builder.buildFilterConditionForField(criteria.getIdNumber(), entityTable.column("id_number"));
            }
            if (criteria.getAddress() != null) {
                builder.buildFilterConditionForField(criteria.getAddress(), entityTable.column("address"));
            }
            if (criteria.getEmail() != null) {
                builder.buildFilterConditionForField(criteria.getEmail(), entityTable.column("email"));
            }
            if (criteria.getPhone() != null) {
                builder.buildFilterConditionForField(criteria.getPhone(), entityTable.column("phone"));
            }
            if (criteria.getEducationLevel() != null) {
                builder.buildFilterConditionForField(criteria.getEducationLevel(), entityTable.column("education_level"));
            }
            if (criteria.getSchool() != null) {
                builder.buildFilterConditionForField(criteria.getSchool(), entityTable.column("school"));
            }
            if (criteria.getRegistrationNumber() != null) {
                builder.buildFilterConditionForField(criteria.getRegistrationNumber(), entityTable.column("registration_number"));
            }
            if (criteria.getCurrentEducation() != null) {
                builder.buildFilterConditionForField(criteria.getCurrentEducation(), entityTable.column("current_education"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getManagerId() != null) {
                builder.buildFilterConditionForField(criteria.getManagerId(), managerTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
