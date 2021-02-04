package com.jsimforest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface DataBaseInterface {

    static int insert(String sql) throws SQLException {

        Statement statement = DataSource.connection.createStatement();

        int lastId;
        statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
        ResultSet lastResult = statement.getGeneratedKeys();
        if (lastResult.next()) {
            lastId = lastResult.getInt(1);
        } else {
            throw new SQLException("Impossible d'obtenir le dernier identifiant");
        }
        return lastId;
    }

    static ResultSet select(String sql) throws SQLException {

        Statement statement = DataSource.connection.createStatement();

        return statement.executeQuery(sql);
    }

}
