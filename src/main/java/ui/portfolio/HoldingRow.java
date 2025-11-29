package ui.portfolio;

public class HoldingRow {
    private final String symbol;
    private final int shares;
    private final double avgCost;
    private final double totalCost;

    public HoldingRow(String symbol, int shares, double avgCost, double totalCost) {
        this.symbol = symbol;
        this.shares = shares;
        this.avgCost = avgCost;
        this.totalCost = totalCost;
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

    public double getTotalCost() {
        return totalCost;
    }
}

