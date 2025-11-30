package use_case.trading;
import entity.Holding;
import entity.OrderRecord;
import java.time.Instant;

public class TradingInteractor implements TradingInputBoundary {

    private final TradingDataAccessInterface dataAccess;
    private final TradingOutputBoundary presenter;

    public TradingInteractor(TradingDataAccessInterface dataAccess,
                             TradingOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void placeOrder(TradingInputData input) {
        String username = input.getUsername();
        String symbol = input.getSymbol();
        TradingInputData.Action action = input.getAction();
        int shares = input.getShares();

        // validating input
        if (symbol == null || symbol.isBlank() || shares <= 0 || action == null) {
            presenter.presentTradeResult(new TradingOutputData(
                    "Enter your symbol, action, and shares.",
                    false,
                    dataAccess.getCash(username),
                    0,
                    0,
                    0
            ));
            return;
        }


        double stockPrice = dataAccess.getStockPrice(symbol);
        double cash = dataAccess.getCash(username);
        Holding holding = dataAccess.getHolding(username, symbol);

        if (action == TradingInputData.Action.BUY) {
            double totalCost = stockPrice * shares;
            if (cash < totalCost) {
                presenter.presentTradeResult(new TradingOutputData(
                        "Not enough cash.",
                        false,
                        cash,
                        holding == null ? 0 : holding.getAvgCost(),
                        holding == null ? 0 : holding.getShares(),
                        holding == null ? 0 : holding.getTotalCost()
                ));
                return;
            }

            // new shares and average total cost
            int oldShares = holding == null ? 0 : holding.getShares();
            double oldAverageCost = holding == null ? 0 : holding.getAvgCost();
            int newShares = oldShares + shares;
            double newAverageCost = ((oldAverageCost * oldShares) + totalCost) / newShares;

            Holding newHolding = new Holding(symbol, newShares, newAverageCost);
            dataAccess.updateCash(username, cash - totalCost);
            dataAccess.updateHolding(username, newHolding);

        } else {
            // selling
            if (holding == null || shares > holding.getShares()) {
                presenter.presentTradeResult(new TradingOutputData(
                        "Not enough shares to sell.",
                        false,
                        cash,
                        holding == null ? 0 : holding.getAvgCost(),
                        holding == null ? 0 : holding.getShares(),
                        holding == null ? 0 : holding.getTotalCost()
                ));
                return;
            }

            double proceeds = stockPrice * shares;
            int remaining = holding.getShares() - shares;
            dataAccess.updateCash(username, cash + proceeds);

            if (remaining == 0) {
                dataAccess.removeHolding(username, symbol);
            } else {
                Holding updated = new Holding(symbol, remaining, holding.getAvgCost());
                dataAccess.updateHolding(username, updated);
            }
        }

        double updatedCash = dataAccess.getCash(username);
        Holding updatedHolding = dataAccess.getHolding(username, symbol);
        int totalSharesAfterTrade = updatedHolding == null ? 0 : updatedHolding.getShares();
        double averageCostAfterTrade = updatedHolding == null ? 0 : updatedHolding.getAvgCost();
        double totalHoldingValueAfterTrade = totalSharesAfterTrade * stockPrice;

        // record the order
        double executedPrice = stockPrice;
        OrderRecord orderRecord = new OrderRecord(
                Instant.now(),
                username,
                symbol,
                action == TradingInputData.Action.BUY ? "BUY" : "SELL",
                shares,
                executedPrice,
                shares * executedPrice
        );
        dataAccess.saveOrder(orderRecord);

        
        presenter.presentTradeResult(new TradingOutputData(
                "Order executed.",
                true,
                updatedCash,
                averageCostAfterTrade,
                totalSharesAfterTrade,
                totalHoldingValueAfterTrade
        ));
    }
}
