package com.revolut.transfermanager.db.util;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUtil {

    private static final Logger logger = Logger.getLogger(DBUtil.class.getSimpleName());

    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    private static boolean isInit;

    public static void init(String dbProps, String schema) {
        PreparedStatement statement = null;
        Connection connection = null;
        try {
            Properties properties = new Properties();
            properties.load(DBUtil.class.getClassLoader().getResourceAsStream(dbProps));

            dbUrl = properties.getProperty("url");
            dbUser = properties.getProperty("username");
            dbPassword = properties.getProperty("password");

            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            connection.setAutoCommit(false);

            if (schema != null && !schema.isEmpty()) {
                URL schemaUrl = DBUtil.class.getClassLoader().getResource(schema);
                if (schemaUrl != null) {
                    List<String> tables = Files.readAllLines(Paths.get(schemaUrl.toString().replace("file:", "")), StandardCharsets.UTF_8);
                    for (String table : tables) {
                        statement = connection.prepareStatement(table);
                        statement.execute();
                    }
                    connection.commit();
                }
            }

            isInit = true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Error opening file", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Error getting connection", e);
        } finally {
            close(statement);
            close(connection);
        }
    }

    public static Connection getConnection() {
        try {
            if (!isInit) {
                throw new RuntimeException("DB not initialized");
            }
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Error getting connection", e);
        }
    }

    public static void close(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Error closing connection", e);
        }
    }

    public static void close(ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Error closing result set", e);
        }
    }

    public static void close(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Error closing statement", e);
        }
    }
}
