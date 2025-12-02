package data.usecase4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import data.AlphaVantage;
import entity.OrderRecord;
import entity.Holding;
import usecase.trading.TradingDataAccessInterface;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

public class JsonTradingDataAccess implements TradingDataAccessInterface{
    private final Map<String, Double> userCash = new HashMap<>();
    private final Map<String, Map<String, Holding>> userHoldings = new HashMap<>();
    private final List<OrderRecord> orderRecords = new ArrayList<>();
    private final Path file;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new com.google.gson.JsonSerializer<Instant>() {
                public com.google.gson.JsonElement serialize(
                        Instant src,
                        java.lang.reflect.Type typeOfSrc,
                        com.google.gson.JsonSerializationContext context) {
                    return new com.google.gson.JsonPrimitive(src.toString());
                }
            })
            .registerTypeAdapter(Instant.class, new com.google.gson.JsonDeserializer<Instant>() {
                public Instant deserialize(
                        com.google.gson.JsonElement json,
                        java.lang.reflect.Type typeOfT,
                        com.google.gson.JsonDeserializationContext context) {
                    return Instant.parse(json.getAsString());
                }
            })
            .setPrettyPrinting()
            .create();
    
    public JsonTradingDataAccess(Path file) {
        this.file = file;
        loadState();
    }

    private static class PersistedState {
        Map<String, Double> userCash = new HashMap<>();
        Map<String, Map<String, Holding>> userHoldings = new HashMap<>();
        List<OrderRecord> orderRecords = new ArrayList<>();
    }

    private void loadState() {
        if (!Files.exists(file)) return;
        try (Reader reader = Files.newBufferedReader(file)) {
            PersistedState state = gson.fromJson(reader, PersistedState.class);
            if (state != null) {
                userCash.clear();
                userCash.putAll(state.userCash);
                userHoldings.clear();
                userHoldings.putAll(state.userHoldings);
                orderRecords.clear();
                orderRecords.addAll(state.orderRecords);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load state from file", e);
        }
    }


    private synchronized void saveStateToFile() {
        PersistedState state = new PersistedState();
        state.userCash = userCash;
        state.userHoldings = userHoldings;
        state.orderRecords = orderRecords;
        try (Writer writer = Files.newBufferedWriter(file)) {
            gson.toJson(state, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save state to file", e);
        }
    }


    private void applyOrderRecord(OrderRecord record) {
        double price = record.getPrice();
        int shares = record.getShares();
        boolean isBuy = "BUY".equalsIgnoreCase(record.getAction());
        String username = record.getUsername();
        String symbol = record.getSymbol();

        Map<String, Holding> holdings = userHoldings.computeIfAbsent(username, k -> new HashMap<>());
        double cash = userCash.getOrDefault(username, 0.0);

        Holding holding = holdings.get(symbol);
        if (isBuy) {
            double totalCost = price * shares;
            int oldShares = holding == null ? 0 : holding.getShares();
            double oldAveragePrice = holding == null ? 0.0: holding.getAvgCost();
            int newShares = oldShares + shares;
            double newAveragePrice = (oldAveragePrice * oldShares + totalCost) / newShares;
            holdings.put(symbol, new Holding(symbol, newShares, newAveragePrice));
            cash -= totalCost;
        } else {
            if (holding != null && holding.getShares() >= shares) {
                int remainingShares = holding.getShares() - shares;
                cash += price * shares;
                if (remainingShares <= 0) {
                    holdings.remove(symbol);
                } else {
                    holdings.put(symbol, new Holding(symbol, remainingShares, holding.getAvgCost()));
                }
            }
        }
        userCash.put(username, cash);
    }

    //Trading Data Access Interface Methods
    public synchronized double getCash(String username) {
        return userCash.getOrDefault(username, 0.0);
    }
    public synchronized void updateCash(String username, double newCash) {
        userCash.put(username, newCash);
    }
    public synchronized Holding getHolding(String username, String symbol) {
        Map<String, Holding> holdings = userHoldings.get(username);
        return holdings == null ? null : holdings.get(symbol);
    }
    public synchronized void updateHolding(String username, Holding holding) {
        userHoldings.computeIfAbsent(username, k -> new HashMap<>())
                    .put(holding.getSymbol(), holding);
    }

    public synchronized void removeHolding(String username, String symbol) {
        Map<String, Holding> holdings = userHoldings.get(username);
        if (holdings != null) holdings.remove(symbol);
    }
    public synchronized List<Holding> getUserHoldings(String username) {
        Map<String, Holding> holdings = userHoldings.get(username);
        return holdings == null ? new ArrayList<>() : new ArrayList<>(holdings.values());
    }

//    public double getStockPrice(String symbol) { return 100.0; }

     public double getStockPrice(String symbol) {
         try {
             return new AlphaVantage().getQuote(symbol).getPrice();
         } catch (IOException e) {
             throw new RuntimeException("Failed to fetch stock price for " + symbol, e);
         }
     }
    
    public synchronized void saveOrder(OrderRecord orderRecord) {
        orderRecords.add(orderRecord);
        saveStateToFile();
    }

    public synchronized List<OrderRecord> findOrdersByUser(String username) {
        List<OrderRecord> result = new ArrayList<>();
        for (OrderRecord record : orderRecords) {
            if (record.getUsername().equals(username)) {
                result.add(record);
            }
        }
        return result;
    
    }
}