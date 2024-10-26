package sn.sonatel.dsi.ins.imoc.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.AttestationPresence;

/**
 * Converter between {@link Row} to {@link AttestationPresence}, with proper type conversions.
 */
@Service
public class AttestationPresenceRowMapper implements BiFunction<Row, String, AttestationPresence> {

    private final ColumnConverter converter;

    public AttestationPresenceRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link AttestationPresence} stored in the database.
     */
    @Override
    public AttestationPresence apply(Row row, String prefix) {
        AttestationPresence entity = new AttestationPresence();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setReference(converter.fromRow(row, prefix + "_reference", String.class));
        entity.setStartDate(converter.fromRow(row, prefix + "_start_date", LocalDate.class));
        entity.setEndDate(converter.fromRow(row, prefix + "_end_date", LocalDate.class));
        entity.setSignatureDate(converter.fromRow(row, prefix + "_signature_date", LocalDate.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", Boolean.class));
        entity.setComments(converter.fromRow(row, prefix + "_comments", String.class));
        entity.setContratId(converter.fromRow(row, prefix + "_contrat_id", Long.class));
        entity.setManagerId(converter.fromRow(row, prefix + "_manager_id", Long.class));
        entity.setEtatPaiementId(converter.fromRow(row, prefix + "_etat_paiement_id", Long.class));
        return entity;
    }
}
