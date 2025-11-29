package use_case.trading;
import entity.usecase5.Holding;

public interface TradingDataAccessInterface {

    double getCash(String username);
    void updateCash(String username, double newCash);

    Holding getHolding(String username, String symbol);
    void updateHolding(String username, Holding holding);
    void removeHolding(String username, String symbol);

    //returns the current stock price by calling alpha vantage API (need to replace)
    double getStockPrice(String symbol);
}
