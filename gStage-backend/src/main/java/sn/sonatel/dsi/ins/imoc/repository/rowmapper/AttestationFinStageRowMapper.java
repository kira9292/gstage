package sn.sonatel.dsi.ins.imoc.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage;

/**
 * Converter between {@link Row} to {@link AttestationFinStage}, with proper type conversions.
 */
@Service
public class AttestationFinStageRowMapper implements BiFunction<Row, String, AttestationFinStage> {

    private final ColumnConverter converter;

    public AttestationFinStageRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link AttestationFinStage} stored in the database.
     */
    @Override
    public AttestationFinStage apply(Row row, String prefix) {
        AttestationFinStage entity = new AttestationFinStage();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setReference(converter.fromRow(row, prefix + "_reference", String.class));
        entity.setIssueDate(converter.fromRow(row, prefix + "_issue_date", LocalDate.class));
        entity.setSignatureDate(converter.fromRow(row, prefix + "_signature_date", LocalDate.class));
        entity.setComments(converter.fromRow(row, prefix + "_comments", String.class));
        return entity;
    }
}
