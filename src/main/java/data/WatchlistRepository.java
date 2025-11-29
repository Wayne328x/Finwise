package data;

import java.util.List;

/**
 * Repository interface for managing user watchlists.
 * Provides methods to check, add, remove, and query watched stocks.
 */
public interface WatchlistRepository {

  /**
   * Checks if a stock symbol is in the user's watchlist.
   *
   * @param username the username
   * @param symbol the stock symbol
   * @return true if the symbol is watched, false otherwise
   */
  boolean isWatched(String username, String symbol);

  /**
   * Adds a stock to the user's watchlist.
   *
   * @param username the username
   * @param symbol the stock symbol
   * @param name the stock name
   * @param exchange the stock exchange
   */
  void addWatched(String username,
      String symbol,
      String name,
      String exchange);

  /**
   * Removes a stock from the user's watchlist.
   *
   * @param username the username
   * @param symbol the stock symbol
   */
  void removeWatched(String username, String symbol);

  /**
   * Finds all watched stock symbols for a user.
   *
   * @param username the username
   * @return list of watched stock symbols
   */
  List<String> findSymbolsByUsername(String username);
}
