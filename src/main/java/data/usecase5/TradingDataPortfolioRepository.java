package data.usecase5;

import entity.Holding;
import usecase.trading.TradingDataAccessInterface;

import java.util.List;

/**
 * Implementation of PortfolioRepository that fetches holdings
 * from the trading data access layer (Use Case 4).
 */
public class TradingDataPortfolioRepository implements PortfolioRepository {

    private final TradingDataAccessInterface tradingData;

    public TradingDataPortfolioRepository(TradingDataAccessInterface tradingData) {
        this.tradingData = tradingData;
    }

    @Override
    public List<Holding> findHoldingsByUser(String username) {
        return tradingData.getUserHoldings(username);
    }
}

