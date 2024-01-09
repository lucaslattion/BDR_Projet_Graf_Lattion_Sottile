package ch.heigvd.auth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;

    private Connection conn;
    public DbConnection(String jdbcUrl, String dbUsername, String dbPassword) throws SQLException {
        this.jdbcUrl = jdbcUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
    }

    public Connection getConnection() throws SQLException {
        return conn;
    }
}
