package sn.sonatel.dsi.ins.imoc.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.BusinessUnit;

/**
 * Converter between {@link Row} to {@link BusinessUnit}, with proper type conversions.
 */
@Service
public class BusinessUnitRowMapper implements BiFunction<Row, String, BusinessUnit> {

    private final ColumnConverter converter;

    public BusinessUnitRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link BusinessUnit} stored in the database.
     */
    @Override
    public BusinessUnit apply(Row row, String prefix) {
        BusinessUnit entity = new BusinessUnit();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        return entity;
    }
}
