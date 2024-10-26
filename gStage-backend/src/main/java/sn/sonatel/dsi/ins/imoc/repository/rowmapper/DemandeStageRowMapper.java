package sn.sonatel.dsi.ins.imoc.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipStatus;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.InternshipType;

/**
 * Converter between {@link Row} to {@link DemandeStage}, with proper type conversions.
 */
@Service
public class DemandeStageRowMapper implements BiFunction<Row, String, DemandeStage> {

    private final ColumnConverter converter;

    public DemandeStageRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link DemandeStage} stored in the database.
     */
    @Override
    public DemandeStage apply(Row row, String prefix) {
        DemandeStage entity = new DemandeStage();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setReference(converter.fromRow(row, prefix + "_reference", String.class));
        entity.setCreationDate(converter.fromRow(row, prefix + "_creation_date", LocalDate.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", InternshipStatus.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setInternshipType(converter.fromRow(row, prefix + "_internship_type", InternshipType.class));
        entity.setStartDate(converter.fromRow(row, prefix + "_start_date", LocalDate.class));
        entity.setEndDate(converter.fromRow(row, prefix + "_end_date", LocalDate.class));
        entity.setResumeContentType(converter.fromRow(row, prefix + "_resume_content_type", String.class));
        entity.setResume(converter.fromRow(row, prefix + "_resume", byte[].class));
        entity.setCoverLetterContentType(converter.fromRow(row, prefix + "_cover_letter_content_type", String.class));
        entity.setCoverLetter(converter.fromRow(row, prefix + "_cover_letter", byte[].class));
        entity.setValidated(converter.fromRow(row, prefix + "_validated", Boolean.class));
        entity.setCandidatId(converter.fromRow(row, prefix + "_candidat_id", Long.class));
        entity.setAssistantGWTEId(converter.fromRow(row, prefix + "_assistantgwte_id", Long.class));
        entity.setManagerId(converter.fromRow(row, prefix + "_manager_id", Long.class));
        entity.setDepartementId(converter.fromRow(row, prefix + "_departement_id", Long.class));
        entity.setBusinessUnitId(converter.fromRow(row, prefix + "_business_unit_id", Long.class));
        return entity;
    }
}
