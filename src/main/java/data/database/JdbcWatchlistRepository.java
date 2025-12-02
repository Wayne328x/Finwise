package data.database;

import data.stock.WatchlistRepository;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

/**
 * JDBC implementation of the watchlist repository using the
 * `watched_stocks` table.
 *
 * <p>Expected schema (created in TableInitializer):
 *
 * <p>CREATE TABLE IF NOT EXISTS watched_stocks (
 *     id          INTEGER PRIMARY KEY AUTOINCREMENT,
 *     username    TEXT NOT NULL,
 *     symbol      TEXT NOT NULL,
 *     name        TEXT,
 *     exchange    TEXT,
 *     created_at  TEXT NOT NULL DEFAULT (datetime('now')),
 *     UNIQUE(username, symbol)
 * );
 */
public final class JdbcWatchlistRepository implements WatchlistRepository {

    /**
     * Index for username parameter in prepared statements.
     */
    private static final int PARAM_INDEX_USERNAME = 1;

    /**
     * Index for symbol parameter in prepared statements.
     */
    private static final int PARAM_INDEX_SYMBOL = 2;

    /**
     * Index for name parameter in prepared statements.
     */
    private static final int PARAM_INDEX_NAME = 3;

    /**
     * Index for exchange parameter in prepared statements.
     */
    private static final int PARAM_INDEX_EXCHANGE = 4;

    /**
     * String literal for symbol parameter in error messages.
     */
    private static final String SYMBOL_PARAM = " symbol=";

    /**
     * The data source for database connections.
     */
    private final DataSource dataSource;

    /**
     * Constructs a JdbcWatchlistRepository with the given data source.
     *
     * @param dataSourceParam the data source for database connections
     */
    public JdbcWatchlistRepository(final DataSource dataSourceParam) {
        this.dataSource = dataSourceParam;
    }

    /**
     * Checks if a stock symbol is in the user's watchlist.
     *
     * @param username the username
     * @param symbol the stock symbol
     * @return true if the symbol is watched, false otherwise
     */
    @Override
    public boolean isWatched(final String username, final String symbol) {
        final String sql =
                "SELECT 1 FROM watched_stocks WHERE username = ? "
                        + "AND symbol = ? LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(PARAM_INDEX_USERNAME, username);
            ps.setString(PARAM_INDEX_SYMBOL, symbol);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
        catch (SQLException sqlException) {
            // Wrap checked exception in an unchecked one so callers don't need
            // to deal with SQL here
            throw new RuntimeException(
                    "Failed to check watchlist for user=" + username
                            + SYMBOL_PARAM + symbol,
                    sqlException);
        }
    }

    /**
     * Adds a stock to the user's watchlist.
     *
     * @param username the username
     * @param symbol the stock symbol
     * @param name the stock name
     * @param exchange the stock exchange
     */
    @Override
    public void addWatched(
            final String username,
            final String symbol,
            final String name,
            final String exchange) {
        final String sql =
                "INSERT OR IGNORE INTO watched_stocks "
                        + "(username, symbol, name, exchange) "
                        + "VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(PARAM_INDEX_USERNAME, username);
            ps.setString(PARAM_INDEX_SYMBOL, symbol);
            ps.setString(PARAM_INDEX_NAME, name);
            ps.setString(PARAM_INDEX_EXCHANGE, exchange);
            ps.executeUpdate();
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(
                    "Failed to add watched stock for user=" + username
                            + SYMBOL_PARAM + symbol,
                    sqlException);
        }
    }

    /**
     * Removes a stock from the user's watchlist.
     *
     * @param username the username
     * @param symbol the stock symbol
     */
    @Override
    public void removeWatched(final String username, final String symbol) {
        final String sql =
                "DELETE FROM watched_stocks WHERE username = ? AND symbol = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(PARAM_INDEX_USERNAME, username);
            ps.setString(PARAM_INDEX_SYMBOL, symbol);
            ps.executeUpdate();
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(
                    "Failed to remove watched stock for user=" + username
                            + SYMBOL_PARAM + symbol,
                    sqlException);
        }
    }

    /**
     * Finds all watched stock symbols for a user.
     *
     * @param username the username
     * @return list of watched stock symbols, ordered by creation date descending
     */
    @Override
    public List<String> findSymbolsByUsername(final String username) {
        final String sql =
                "SELECT symbol FROM watched_stocks "
                        + "WHERE username = ? "
                        + "ORDER BY created_at DESC";

        final List<String> symbols = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(PARAM_INDEX_USERNAME, username);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    symbols.add(rs.getString("symbol"));
                }
            }
        }
        catch (SQLException sqlException) {
            throw new RuntimeException(
                    "Failed to load watched symbols for user=" + username,
                    sqlException);
        }

        return symbols;
    }
}
