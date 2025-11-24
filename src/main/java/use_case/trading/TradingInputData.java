package use_case.trading;

public class TradingInputData {
    public enum Action {
        BUY,
        SELL
    }

    private final Action action;
    private final String symbol;
    private final int shares;
    private final String username;

    public TradingInputData(String username, String symbol, Action action, int shares) {
        this.username = username;
        this.symbol = symbol;
        this.action = action;
        this.shares = shares;
        
    }

    public Action getAction() {
        return action;
    }
    public String getSymbol() {
        return symbol;
    }
    public int getShares() {
        return shares;
    }
    public String getUsername() {
        return username;
    }


}
