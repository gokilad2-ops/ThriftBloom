package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages JDBC connections to MySQL for the thrift application.
 */
public class DatabaseConnection {

    static {
        try {
            Class.forName(DatabaseConfig.getDriver());
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL: MySQL JDBC Driver not found on classpath!");
            e.printStackTrace();
        }
    }

    /**
     * Obtains a new connection to the campus_thrift database.
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.getUrl(),
                DatabaseConfig.getUser(),
                DatabaseConfig.getPassword()
        );
    }

    /**
     * Obtains a connection to the MySQL server root (without selecting a database).
     * Used for creating the database if it doesn't exist yet.
     */
    public static Connection getServerConnection() throws SQLException {
        return DriverManager.getConnection(
                DatabaseConfig.getServerBaseUrl(),
                DatabaseConfig.getUser(),
                DatabaseConfig.getPassword()
        );
    }

    /**
     * Tests if the database is reachable with the current configuration.
     * @return true if connection succeeds, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
