package ui.portfolio;

import java.time.LocalDate;

public class SnapshotRow {
    private final LocalDate date;
    private final double totalCost;
    private final double totalValue;
    private final double profit;
    private final double profitRate;

    public SnapshotRow(LocalDate date,
                       double totalCost,
                       double totalValue,
                       double profit,
                       double profitRate) {
        this.date = date;
        this.totalCost = totalCost;
        this.totalValue = totalValue;
        this.profit = profit;
        this.profitRate = profitRate;
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

