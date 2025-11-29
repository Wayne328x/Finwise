package data.usecase4;
import entity.usecase5.Holding;
import use_case.trading.TradingDataAccessInterface;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTradingDataAccess implements TradingDataAccessInterface{

    private final Map<String, Double> userCash;
    private final Map<String, Map<String, Holding>> userHoldings;
    
    public InMemoryTradingDataAccess() {
        this.userCash = new HashMap<>();
        this.userHoldings = new HashMap<>();
    }

    @Override
    public double getCash(String username) {
        return userCash.getOrDefault(username, 0.0);
    }

    @Override
    public void updateCash(String username, double newCash) {
        userCash.put(username, newCash);
    }

    @Override
    public Holding getHolding(String username, String symbol) {
        Map<String, Holding> holdings = userHoldings.get(username);
        if (holdings == null) {
            return null;
        }
        return holdings.get(symbol);
    }
    @Override
    public void updateHolding(String username, Holding holding) {
        userHoldings.computeIfAbsent(username, k -> new HashMap<>()).put(holding.getSymbol(), holding);
}

    @Override
    public void removeHolding(String username, String symbol) {
        Map<String, Holding> holdings = userHoldings.get(username);
        if (holdings != null) {
            holdings.remove(symbol);
        }
    }

    @Override
    public double getStockPrice(String symbol) {
        // returning fixed price just for testing (need to replace with API call)
        return 100.0;

        // AlphaVantageAPI api = new AlphaVantageAPI();
        // try {
        //     return api.getQuote(symbol).getPrice();
        // } catch (IOException e) {
        //     // Fallback to a safe value or rethrow
        //     throw new RuntimeException("Failed to fetch price for " + symbol, e);
        // }
    }
}
