package sn.sonatel.dsi.ins.imoc.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class RestaurationStagiaireSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("start_date", table, columnPrefix + "_start_date"));
        columns.add(Column.aliased("end_date", table, columnPrefix + "_end_date"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("card_number", table, columnPrefix + "_card_number"));

        columns.add(Column.aliased("candidat_id", table, columnPrefix + "_candidat_id"));
        return columns;
    }
}
