package sn.sonatel.dsi.ins.imoc.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class EtatPaiementSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("reference", table, columnPrefix + "_reference"));
        columns.add(Column.aliased("payment_number", table, columnPrefix + "_payment_number"));
        columns.add(Column.aliased("payment_date", table, columnPrefix + "_payment_date"));
        columns.add(Column.aliased("amount", table, columnPrefix + "_amount"));
        columns.add(Column.aliased("act_code", table, columnPrefix + "_act_code"));
        columns.add(Column.aliased("payment_phone", table, columnPrefix + "_payment_phone"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("processing_date", table, columnPrefix + "_processing_date"));
        columns.add(Column.aliased("comments", table, columnPrefix + "_comments"));

        columns.add(Column.aliased("contrat_id", table, columnPrefix + "_contrat_id"));
        columns.add(Column.aliased("dfc_id", table, columnPrefix + "_dfc_id"));
        columns.add(Column.aliased("assistantgwtecreator_id", table, columnPrefix + "_assistantgwtecreator_id"));
        return columns;
    }
}
