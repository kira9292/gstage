package sn.sonatel.dsi.ins.imoc.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ValidationSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("reference", table, columnPrefix + "_reference"));
        columns.add(Column.aliased("validation_date", table, columnPrefix + "_validation_date"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("comments", table, columnPrefix + "_comments"));
        columns.add(Column.aliased("validated_by", table, columnPrefix + "_validated_by"));

        columns.add(Column.aliased("attestation_presence_id", table, columnPrefix + "_attestation_presence_id"));
        columns.add(Column.aliased("contrat_id", table, columnPrefix + "_contrat_id"));
        columns.add(Column.aliased("attestation_fin_stage_id", table, columnPrefix + "_attestation_fin_stage_id"));
        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        return columns;
    }
}
