package sn.sonatel.dsi.ins.imoc.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.Validation;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ValidationStatus;

/**
 * Converter between {@link Row} to {@link Validation}, with proper type conversions.
 */
@Service
public class ValidationRowMapper implements BiFunction<Row, String, Validation> {

    private final ColumnConverter converter;

    public ValidationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Validation} stored in the database.
     */
    @Override
    public Validation apply(Row row, String prefix) {
        Validation entity = new Validation();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setReference(converter.fromRow(row, prefix + "_reference", String.class));
        entity.setValidationDate(converter.fromRow(row, prefix + "_validation_date", LocalDate.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", ValidationStatus.class));
        entity.setComments(converter.fromRow(row, prefix + "_comments", String.class));
        entity.setValidatedBy(converter.fromRow(row, prefix + "_validated_by", String.class));
        entity.setAttestationPresenceId(converter.fromRow(row, prefix + "_attestation_presence_id", Long.class));
        entity.setContratId(converter.fromRow(row, prefix + "_contrat_id", Long.class));
        entity.setAttestationFinStageId(converter.fromRow(row, prefix + "_attestation_fin_stage_id", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
