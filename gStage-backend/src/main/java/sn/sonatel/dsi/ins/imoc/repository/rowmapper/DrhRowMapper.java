package sn.sonatel.dsi.ins.imoc.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.Drh;

/**
 * Converter between {@link Row} to {@link Drh}, with proper type conversions.
 */
@Service
public class DrhRowMapper implements BiFunction<Row, String, Drh> {

    private final ColumnConverter converter;

    public DrhRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Drh} stored in the database.
     */
    @Override
    public Drh apply(Row row, String prefix) {
        Drh entity = new Drh();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        return entity;
    }
}
