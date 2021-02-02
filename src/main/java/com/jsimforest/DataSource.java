package com.jsimforest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;

public class DataSource {

    public static Connection connection;

    public static boolean connect() {
        JsimProp prop = new JsimProp("config.properties");

        String driver = prop.loadPropertiesFile("driver");
        String host = prop.loadPropertiesFile("host");
        String port = prop.loadPropertiesFile("port");
        String dbName = prop.loadPropertiesFile("dbName");
        String user = prop.loadPropertiesFile("user");
        String password = prop.loadPropertiesFile("pass");

        String url = MessageFormat.format("jdbc:{0}://{1}:{2}/{3}", driver, host, port, dbName);

        try {
            // Connexion to the database
            connection = DriverManager.getConnection(url, user, password);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }
    }
}