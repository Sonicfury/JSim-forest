package com.jsimforest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public interface DataBaseInterface {

    static void insert(String sql) throws SQLException {

        DataSource.connect();

        Statement statement = DataSource.connection.createStatement();

        statement.executeUpdate(sql);
    }

    static ResultSet select(String sql) throws SQLException{

        DataSource.connect();

        Statement statement = DataSource.connection.createStatement();

        return statement.executeQuery(sql);
    }

}
