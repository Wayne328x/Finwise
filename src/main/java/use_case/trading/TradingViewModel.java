package use_case.trading;

public class TradingViewModel {
    private String message = "";
    private double cashAfterTrade = 0.0;
    private double averageCostAfterTrade = 0.0;
    private int totalSharesAfterTrade = 0;
    private double totalHoldingValueAfterTrade = 0.0;
    
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    //
    public double getCashAfterTrade() {
        return cashAfterTrade;
    }
    public void setCashAfterTrade(double cashAfterTrade) {
        this.cashAfterTrade = cashAfterTrade;
    }
    //
    public double getAverageCostAfterTrade() {
        return averageCostAfterTrade;
    }
    public void setAverageCostAfterTrade(double averageCostAfterTrade) {
        this.averageCostAfterTrade = averageCostAfterTrade;
    }
    //
    public int getTotalSharesAfterTrade() {
        return totalSharesAfterTrade;
    }
    public void setTotalSharesAfterTrade(int totalSharesAfterTrade) {
        this.totalSharesAfterTrade = totalSharesAfterTrade;
    }
    //
    public double getTotalHoldingValueAfterTrade() {
        return totalHoldingValueAfterTrade;
    }
    public void setTotalHoldingValueAfterTrade(double totalHoldingValueAfterTrade) {
        this.totalHoldingValueAfterTrade = totalHoldingValueAfterTrade;
    }

}
