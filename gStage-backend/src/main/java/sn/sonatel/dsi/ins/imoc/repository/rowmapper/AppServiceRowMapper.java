package sn.sonatel.dsi.ins.imoc.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.AppService;

/**
 * Converter between {@link Row} to {@link AppService}, with proper type conversions.
 */
@Service
public class AppServiceRowMapper implements BiFunction<Row, String, AppService> {

    private final ColumnConverter converter;

    public AppServiceRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link AppService} stored in the database.
     */
    @Override
    public AppService apply(Row row, String prefix) {
        AppService entity = new AppService();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setBusinessUnitId(converter.fromRow(row, prefix + "_business_unit_id", Long.class));
        entity.setDepartemenId(converter.fromRow(row, prefix + "_departemen_id", Long.class));
        return entity;
    }
}
