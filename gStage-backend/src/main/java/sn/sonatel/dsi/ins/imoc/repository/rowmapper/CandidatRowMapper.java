package sn.sonatel.dsi.ins.imoc.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.CandidateStatus;
import sn.sonatel.dsi.ins.imoc.domain.enumeration.EducationLevel;

/**
 * Converter between {@link Row} to {@link Candidat}, with proper type conversions.
 */
@Service
public class CandidatRowMapper implements BiFunction<Row, String, Candidat> {

    private final ColumnConverter converter;

    public CandidatRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Candidat} stored in the database.
     */
    @Override
    public Candidat apply(Row row, String prefix) {
        Candidat entity = new Candidat();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setBirthDate(converter.fromRow(row, prefix + "_birth_date", LocalDate.class));
        entity.setNationality(converter.fromRow(row, prefix + "_nationality", String.class));
        entity.setBirthPlace(converter.fromRow(row, prefix + "_birth_place", String.class));
        entity.setIdNumber(converter.fromRow(row, prefix + "_id_number", String.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        entity.setEducationLevel(converter.fromRow(row, prefix + "_education_level", EducationLevel.class));
        entity.setSchool(converter.fromRow(row, prefix + "_school", String.class));
        entity.setRegistrationNumber(converter.fromRow(row, prefix + "_registration_number", String.class));
        entity.setCurrentEducation(converter.fromRow(row, prefix + "_current_education", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", CandidateStatus.class));
        entity.setManagerId(converter.fromRow(row, prefix + "_manager_id", Long.class));
        return entity;
    }
}
