package sn.sonatel.dsi.ins.imoc.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.Departement;

/**
 * Converter between {@link Row} to {@link Departement}, with proper type conversions.
 */
@Service
public class DepartementRowMapper implements BiFunction<Row, String, Departement> {

    private final ColumnConverter converter;

    public DepartementRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Departement} stored in the database.
     */
    @Override
    public Departement apply(Row row, String prefix) {
        Departement entity = new Departement();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
