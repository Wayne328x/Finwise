package entity;

/**
 * Represents a single stock holding in the user's portfolio.
 * This is a domain entity (Entity layer in Clean Architecture).
 * It contains only business data and simple invariants, no UI or persistence logic.
 */
public class Holding {

    private final String symbol;   // e.g., "AAPL"
    private final int shares;      // number of shares held
    private final double avgCost;  // average cost per share

    /**
     * Creates a new Holding.
     * @param symbol  stock ticker symbol (must not be null or blank)
     * @param shares  number of shares (must be > 0)
     * @param avgCost average cost per share (must be >= 0)
     */
    public Holding(String symbol, int shares, double avgCost) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol must not be null or blank.");
        }
        if (shares <= 0) {
            throw new IllegalArgumentException("Shares must be positive.");
        }
        if (avgCost < 0) {
            throw new IllegalArgumentException("Average cost must not be negative.");
        }

        this.symbol = symbol;
        this.shares = shares;
        this.avgCost = avgCost;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getShares() {
        return shares;
    }

    public double getAvgCost() {
        return avgCost;
    }

    /**
     * @return the total cost for this holding: shares * avgCost
     */
    public double getTotalCost() {
        return shares * avgCost;
    }
}
