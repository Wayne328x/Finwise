package usecase.trading;

import entity.Holding;
import entity.OrderRecord;
import java.util.List;

/**
 * Data access interface for trading-related information.
 */
public interface TradingDataAccessInterface {
    /**
     * Returns the current cash balance for the given user.
     *
     * @param username the user's username
     * @return cash balance of the user
     */
    double getCash(String username);

    /**
     * Updates the cash balance for the given user.
     *
     * @param username the user's username
     * @param newCash  new cash balance
     */
    void updateCash(String username, double newCash);

    /**
     * Returns the holding of the given symbol for the specified user.
     *
     * @param username the user's username
     * @param symbol   the stock symbol
     * @return the holding, or {@code null} if none exists
     */
    Holding getHolding(String username, String symbol);

    /**
     * Updates the given holding for the specified user.
     *
     * @param username the user's username
     * @param holding  the holding to store
     */
    void updateHolding(String username, Holding holding);

    /**
     * Removes the holding for the given symbol and user.
     *
     * @param username the user's username
     * @param symbol   the stock symbol
     */
    void removeHolding(String username, String symbol);

    /**
     * Returns the current stock price for the given symbol.
     *
     * @param symbol the stock symbol
     * @return the current stock price
     */
    double getStockPrice(String symbol);

    /**
     * Returns all holdings for the given user.
     *
     * @param username the user's username
     * @return list of holdings
     */
    List<Holding> getUserHoldings(String username);

    /**
     * Persists the given order.
     *
     * @param orderRecord the order record to save
     */
    void saveOrder(OrderRecord orderRecord);

    /**
     * Returns all orders created by the given user.
     *
     * @param username the user's username
     * @return list of order records
     */
    List<OrderRecord> findOrdersByUser(String username);

}
