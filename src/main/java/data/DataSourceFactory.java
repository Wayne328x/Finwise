package data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

/**
 * Factory class for creating DataSource instances.
 * Provides methods to create SQLite database connections.
 */
public final class DataSourceFactory {

  /**
   * Private constructor to prevent instantiation of utility class.
   */
  private DataSourceFactory() {
    // Utility class - no instantiation
  }

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
  public static DataSource sqlite(final String dbPath) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + dbPath);
    config.setMaximumPoolSize(DEFAULT_MAX_POOL_SIZE);
        config.setPoolName("sqlite-pool");
        return new HikariDataSource(config);
    }
}
