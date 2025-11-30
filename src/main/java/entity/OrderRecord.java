package entity;

import java.time.Instant;

public class OrderRecord {
    private final Instant timestamp;
    private final String username;
    private final String symbol;
    private final String action;
    private final int shares;
    private final double price;
    private final double totalAmount;

    public OrderRecord(Instant timestamp, String username, String symbol, String action, int shares, double price, double totalAmount) {
        this.timestamp = timestamp;
        this.username = username;
        this.symbol = symbol;
        this.action = action;
        this.shares = shares;
        this.price = price;
        this.totalAmount = totalAmount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
    public String getUsername() {
        return username;
    }
    public String getSymbol() {
        return symbol;
    }
    public String getAction() {
        return action;
    }
    public int getShares() {
        return shares;
    }
    public double getPrice() {
        return price;
    }
    public double getTotalAmount() {
        return totalAmount;
    }

}
