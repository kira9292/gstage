package sn.sonatel.dsi.ins.imoc.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.Contrat;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.ContractStatus;

/**
 * Converter between {@link Row} to {@link Contrat}, with proper type conversions.
 */
@Service
public class ContratRowMapper implements BiFunction<Row, String, Contrat> {

    private final ColumnConverter converter;

    public ContratRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Contrat} stored in the database.
     */
    @Override
    public Contrat apply(Row row, String prefix) {
        Contrat entity = new Contrat();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setReference(converter.fromRow(row, prefix + "_reference", String.class));
        entity.setStartDate(converter.fromRow(row, prefix + "_start_date", LocalDate.class));
        entity.setEndDate(converter.fromRow(row, prefix + "_end_date", LocalDate.class));
        entity.setCompensation(converter.fromRow(row, prefix + "_compensation", Double.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", ContractStatus.class));
        entity.setAssignmentSite(converter.fromRow(row, prefix + "_assignment_site", String.class));
        entity.setSignatureDate(converter.fromRow(row, prefix + "_signature_date", LocalDate.class));
        entity.setComments(converter.fromRow(row, prefix + "_comments", String.class));
        entity.setAttestationFinStageId(converter.fromRow(row, prefix + "_attestation_fin_stage_id", Long.class));
        entity.setDrhId(converter.fromRow(row, prefix + "_drh_id", Long.class));
        entity.setAssistantGWTECreatorId(converter.fromRow(row, prefix + "_assistantgwtecreator_id", Long.class));
        entity.setCandidatId(converter.fromRow(row, prefix + "_candidat_id", Long.class));
        return entity;
    }
}
