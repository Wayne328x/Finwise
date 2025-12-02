package data;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Factory class for creating DataSource instances.
 * Provides methods to create SQLite database connections.
 */
public final class DataSourceFactory {
    /**
    * Default maximum pool size for SQLite connections.
    */
    private static final int DEFAULT_MAX_POOL_SIZE = 3;

    /**
    * Creates a SQLite DataSource for the given database file.
    *
    * @param dbPath the path to the SQLite database file
    * @return a configured DataSource for SQLite
    */
    public static DataSource sqlite(String dbPath) {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + dbPath);
        config.setMaximumPoolSize(DEFAULT_MAX_POOL_SIZE);
        config.setPoolName("sqlite-pool");
        return new HikariDataSource(config);
    }
}
