package entity;

import java.time.LocalDate;

/**
 * Represents the overall portfolio performance at a specific date.
 *
 * It aggregates all holdings into a single snapshot for that day.
 */
public class PortfolioSnapshot {

    private final LocalDate date;
    private final double totalCost;    // total cost basis of the portfolio
    private final double totalValue;   // total market value at this date
    private final double profit;       // totalValue - totalCost
    private final double profitRate;   // profit / totalCost (0 if totalCost == 0)

    /**
     * Creates a snapshot using already-computed fields.
     * Usually you will use the static factory method "fromCostAndValue".
     */
    public PortfolioSnapshot(LocalDate date,
                             double totalCost,
                             double totalValue,
                             double profit,
                             double profitRate) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null.");
        }
        this.date = date;
        this.totalCost = totalCost;
        this.totalValue = totalValue;
        this.profit = profit;
        this.profitRate = profitRate;
    }

    /**
     * Factory method: construct a snapshot from cost and value.
     * Profit and profitRate are derived automatically.
     */
    public static PortfolioSnapshot fromCostAndValue(LocalDate date,
                                                     double totalCost,
                                                     double totalValue) {
        double profit = totalValue - totalCost;
        double profitRate = (totalCost == 0) ? 0.0 : profit / totalCost;
        return new PortfolioSnapshot(date, totalCost, totalValue, profit, profitRate);
    }

    public LocalDate getDate() {
        return date;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public double getProfit() {
        return profit;
    }

    public double getProfitRate() {
        return profitRate;
    }
}

