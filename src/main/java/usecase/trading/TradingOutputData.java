package usecase.trading;

public class TradingOutputData {
    private final String message;
    //private final boolean success;
    private final double cashAfterTrade;
    private final double averageCostAfterTrade;
    private final int totalSharesAfterTrade;
    private final double totalHoldingValueAfterTrade;

    public TradingOutputData(String message, boolean success,
        double cashAfterTrade, double averageCostAfterTrade,
        int totalSharesAfterTrade, double totalHoldingValueAfterTrade) {

        this.message = message;
        //this.success = success;
        this.cashAfterTrade = cashAfterTrade;
        this.averageCostAfterTrade = averageCostAfterTrade;
        this.totalSharesAfterTrade = totalSharesAfterTrade;
        this.totalHoldingValueAfterTrade = totalHoldingValueAfterTrade;
    }

    public String getMessage() {
        return message;
    }
    //public boolean isSuccess() {
    //    return success;
    //}
    public double getCashAfterTrade() {
        return cashAfterTrade;
    }
    public double getAverageCostAfterTrade() {
        return averageCostAfterTrade;
    }
    public int getTotalSharesAfterTrade() {
        return totalSharesAfterTrade;
    }
    public double getTotalHoldingValueAfterTrade() {
        return totalHoldingValueAfterTrade;
    }

}
