package sn.sonatel.dsi.ins.imoc.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class DemandeStageSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("reference", table, columnPrefix + "_reference"));
        columns.add(Column.aliased("creation_date", table, columnPrefix + "_creation_date"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("internship_type", table, columnPrefix + "_internship_type"));
        columns.add(Column.aliased("start_date", table, columnPrefix + "_start_date"));
        columns.add(Column.aliased("end_date", table, columnPrefix + "_end_date"));
        columns.add(Column.aliased("resume", table, columnPrefix + "_resume"));
        columns.add(Column.aliased("resume_content_type", table, columnPrefix + "_resume_content_type"));
        columns.add(Column.aliased("cover_letter", table, columnPrefix + "_cover_letter"));
        columns.add(Column.aliased("cover_letter_content_type", table, columnPrefix + "_cover_letter_content_type"));
        columns.add(Column.aliased("validated", table, columnPrefix + "_validated"));

        columns.add(Column.aliased("candidat_id", table, columnPrefix + "_candidat_id"));
        columns.add(Column.aliased("assistantgwte_id", table, columnPrefix + "_assistantgwte_id"));
        columns.add(Column.aliased("manager_id", table, columnPrefix + "_manager_id"));
        columns.add(Column.aliased("departement_id", table, columnPrefix + "_departement_id"));
        columns.add(Column.aliased("business_unit_id", table, columnPrefix + "_business_unit_id"));
        return columns;
    }
}
