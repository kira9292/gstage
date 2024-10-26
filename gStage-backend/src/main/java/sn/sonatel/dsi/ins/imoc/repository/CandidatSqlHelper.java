package sn.sonatel.dsi.ins.imoc.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CandidatSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("first_name", table, columnPrefix + "_first_name"));
        columns.add(Column.aliased("last_name", table, columnPrefix + "_last_name"));
        columns.add(Column.aliased("birth_date", table, columnPrefix + "_birth_date"));
        columns.add(Column.aliased("nationality", table, columnPrefix + "_nationality"));
        columns.add(Column.aliased("birth_place", table, columnPrefix + "_birth_place"));
        columns.add(Column.aliased("id_number", table, columnPrefix + "_id_number"));
        columns.add(Column.aliased("address", table, columnPrefix + "_address"));
        columns.add(Column.aliased("email", table, columnPrefix + "_email"));
        columns.add(Column.aliased("phone", table, columnPrefix + "_phone"));
        columns.add(Column.aliased("education_level", table, columnPrefix + "_education_level"));
        columns.add(Column.aliased("school", table, columnPrefix + "_school"));
        columns.add(Column.aliased("registration_number", table, columnPrefix + "_registration_number"));
        columns.add(Column.aliased("current_education", table, columnPrefix + "_current_education"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));

        columns.add(Column.aliased("manager_id", table, columnPrefix + "_manager_id"));
        return columns;
    }
}
