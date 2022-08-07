package com.example.study.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class Database {

    private static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MYSQL;DATABASE_TO_LOWER=TRUE;IGNORECASE=TRUE";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL);
    }

    public static void initialize() throws IOException, SQLException {
        String sql;
        try (InputStream in = Database.class.getResourceAsStream("/schema.sql");
                BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            sql = reader.lines().collect(Collectors.joining(""));
        }

        try (Connection conn = Database.getConnection()) {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        }
    }
}
