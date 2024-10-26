package sn.sonatel.dsi.ins.imoc.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ContratSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("reference", table, columnPrefix + "_reference"));
        columns.add(Column.aliased("start_date", table, columnPrefix + "_start_date"));
        columns.add(Column.aliased("end_date", table, columnPrefix + "_end_date"));
        columns.add(Column.aliased("compensation", table, columnPrefix + "_compensation"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("assignment_site", table, columnPrefix + "_assignment_site"));
        columns.add(Column.aliased("signature_date", table, columnPrefix + "_signature_date"));
        columns.add(Column.aliased("comments", table, columnPrefix + "_comments"));

        columns.add(Column.aliased("attestation_fin_stage_id", table, columnPrefix + "_attestation_fin_stage_id"));
        columns.add(Column.aliased("drh_id", table, columnPrefix + "_drh_id"));
        columns.add(Column.aliased("assistantgwtecreator_id", table, columnPrefix + "_assistantgwtecreator_id"));
        columns.add(Column.aliased("candidat_id", table, columnPrefix + "_candidat_id"));
        return columns;
    }
}
