package sn.sonatel.dsi.ins.imoc.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.Dfc;

/**
 * Converter between {@link Row} to {@link Dfc}, with proper type conversions.
 */
@Service
public class DfcRowMapper implements BiFunction<Row, String, Dfc> {

    private final ColumnConverter converter;

    public DfcRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Dfc} stored in the database.
     */
    @Override
    public Dfc apply(Row row, String prefix) {
        Dfc entity = new Dfc();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        return entity;
    }
}
