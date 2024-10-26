package sn.sonatel.dsi.ins.imoc.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class AttestationFinStageSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("reference", table, columnPrefix + "_reference"));
        columns.add(Column.aliased("issue_date", table, columnPrefix + "_issue_date"));
        columns.add(Column.aliased("signature_date", table, columnPrefix + "_signature_date"));
        columns.add(Column.aliased("comments", table, columnPrefix + "_comments"));

        return columns;
    }
}
