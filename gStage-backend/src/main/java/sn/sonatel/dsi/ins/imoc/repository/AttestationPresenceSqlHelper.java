package sn.sonatel.dsi.ins.imoc.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class AttestationPresenceSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("reference", table, columnPrefix + "_reference"));
        columns.add(Column.aliased("start_date", table, columnPrefix + "_start_date"));
        columns.add(Column.aliased("end_date", table, columnPrefix + "_end_date"));
        columns.add(Column.aliased("signature_date", table, columnPrefix + "_signature_date"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("comments", table, columnPrefix + "_comments"));

        columns.add(Column.aliased("contrat_id", table, columnPrefix + "_contrat_id"));
        columns.add(Column.aliased("manager_id", table, columnPrefix + "_manager_id"));
        columns.add(Column.aliased("etat_paiement_id", table, columnPrefix + "_etat_paiement_id"));
        return columns;
    }
}
