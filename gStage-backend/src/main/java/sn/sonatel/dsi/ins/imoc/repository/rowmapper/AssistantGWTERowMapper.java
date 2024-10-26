package sn.sonatel.dsi.ins.imoc.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.AssistantGWTE;

/**
 * Converter between {@link Row} to {@link AssistantGWTE}, with proper type conversions.
 */
@Service
public class AssistantGWTERowMapper implements BiFunction<Row, String, AssistantGWTE> {

    private final ColumnConverter converter;

    public AssistantGWTERowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link AssistantGWTE} stored in the database.
     */
    @Override
    public AssistantGWTE apply(Row row, String prefix) {
        AssistantGWTE entity = new AssistantGWTE();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        return entity;
    }
}
