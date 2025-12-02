package data.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

/**
 * Utility class for initializing database tables.
 * Creates the necessary schema for users, expenses, and watchlist tables.
 */
public final class TableInitializer {

    /**
     * The DDL statements for creating database tables.
     */
    private static final String DDL = """
        PRAGMA foreign_keys = ON;
        CREATE TABLE IF NOT EXISTS users (
            id            INTEGER PRIMARY KEY AUTOINCREMENT,
            username      TEXT NOT NULL UNIQUE,
            password      TEXT NOT NULL,
            created_at    TEXT NOT NULL DEFAULT (datetime('now'))
        );
        CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
        
        CREATE TABLE IF NOT EXISTS expenses (
            id          INTEGER PRIMARY KEY AUTOINCREMENT,
            username    TEXT NOT NULL,
            datetime    TEXT NOT NULL,
            type        TEXT NOT NULL,
            amount      REAL NOT NULL
        );
        CREATE INDEX IF NOT EXISTS idx_expenses_username ON expenses(username);
        
        /*watchlist table */
        CREATE TABLE IF NOT EXISTS watched_stocks (
            id          INTEGER PRIMARY KEY AUTOINCREMENT,
            username    TEXT NOT NULL,
            symbol      TEXT NOT NULL,
            name        TEXT,
            exchange    TEXT,
            created_at  TEXT NOT NULL DEFAULT (datetime('now')),
            UNIQUE(username, symbol)          -- one row per user+symbol
        );
        CREATE INDEX IF NOT EXISTS idx_watchlist_username
            ON watched_stocks(username);
        """;

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private TableInitializer() {
        // Utility class - no instantiation
    }

    /**
     * Ensures the database schema is initialized.
     * Creates all necessary tables and indexes if they don't exist.
     *
     * @param dataSource the data source to use for database connections
     * @throws RuntimeException if schema initialization fails
     */
    public static void ensureSchema(final DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            statement.executeUpdate(DDL);
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(
                    "Failed to initialize new database schema!",
                    sqlException);
        }
    }
}
