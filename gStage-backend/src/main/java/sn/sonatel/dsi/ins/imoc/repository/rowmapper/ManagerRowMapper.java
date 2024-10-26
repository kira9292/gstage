package sn.sonatel.dsi.ins.imoc.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.Manager;

/**
 * Converter between {@link Row} to {@link Manager}, with proper type conversions.
 */
@Service
public class ManagerRowMapper implements BiFunction<Row, String, Manager> {

    private final ColumnConverter converter;

    public ManagerRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Manager} stored in the database.
     */
    @Override
    public Manager apply(Row row, String prefix) {
        Manager entity = new Manager();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        entity.setServiceId(converter.fromRow(row, prefix + "_service_id", Long.class));
        return entity;
    }
}
