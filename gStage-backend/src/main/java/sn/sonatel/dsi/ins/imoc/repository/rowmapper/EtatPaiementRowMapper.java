package sn.sonatel.dsi.ins.imoc.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.EtatPaiement;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.PaymentStatus;

/**
 * Converter between {@link Row} to {@link EtatPaiement}, with proper type conversions.
 */
@Service
public class EtatPaiementRowMapper implements BiFunction<Row, String, EtatPaiement> {

    private final ColumnConverter converter;

    public EtatPaiementRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link EtatPaiement} stored in the database.
     */
    @Override
    public EtatPaiement apply(Row row, String prefix) {
        EtatPaiement entity = new EtatPaiement();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setReference(converter.fromRow(row, prefix + "_reference", String.class));
        entity.setPaymentNumber(converter.fromRow(row, prefix + "_payment_number", String.class));
        entity.setPaymentDate(converter.fromRow(row, prefix + "_payment_date", LocalDate.class));
        entity.setAmount(converter.fromRow(row, prefix + "_amount", Double.class));
        entity.setActCode(converter.fromRow(row, prefix + "_act_code", String.class));
        entity.setPaymentPhone(converter.fromRow(row, prefix + "_payment_phone", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", PaymentStatus.class));
        entity.setProcessingDate(converter.fromRow(row, prefix + "_processing_date", LocalDate.class));
        entity.setComments(converter.fromRow(row, prefix + "_comments", String.class));
        entity.setContratId(converter.fromRow(row, prefix + "_contrat_id", Long.class));
        entity.setDfcId(converter.fromRow(row, prefix + "_dfc_id", Long.class));
        entity.setAssistantGWTECreatorId(converter.fromRow(row, prefix + "_assistantgwtecreator_id", Long.class));
        return entity;
    }
}
