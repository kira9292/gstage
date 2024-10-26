package sn.sonatel.dsi.ins.imoc.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaire;

/**
 * Converter between {@link Row} to {@link RestaurationStagiaire}, with proper type conversions.
 */
@Service
public class RestaurationStagiaireRowMapper implements BiFunction<Row, String, RestaurationStagiaire> {

    private final ColumnConverter converter;

    public RestaurationStagiaireRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RestaurationStagiaire} stored in the database.
     */
    @Override
    public RestaurationStagiaire apply(Row row, String prefix) {
        RestaurationStagiaire entity = new RestaurationStagiaire();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setStartDate(converter.fromRow(row, prefix + "_start_date", LocalDate.class));
        entity.setEndDate(converter.fromRow(row, prefix + "_end_date", LocalDate.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", Boolean.class));
        entity.setCardNumber(converter.fromRow(row, prefix + "_card_number", String.class));
        entity.setCandidatId(converter.fromRow(row, prefix + "_candidat_id", Long.class));
        return entity;
    }
}
