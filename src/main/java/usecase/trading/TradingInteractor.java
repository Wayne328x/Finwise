package usecase.trading;

import java.time.Instant;

import entity.Holding;
import entity.OrderRecord;

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
        final String username = input.getUsername();
        final String symbol = input.getSymbol();
        final TradingInputData.Action action = input.getAction();
        final int shares = input.getShares();

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

        final double stockPrice = dataAccess.getStockPrice(symbol);
        final double cash = dataAccess.getCash(username);
        final Holding holding = dataAccess.getHolding(username, symbol);

        if (action == TradingInputData.Action.BUY) {
            final double totalCost = stockPrice * shares;
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
            final int oldShares = holding == null ? 0 : holding.getShares();
            final double oldAverageCost = holding == null ? 0 : holding.getAvgCost();
            final int newShares = oldShares + shares;
            final double newAverageCost = ((oldAverageCost * oldShares) + totalCost) / newShares;

            final Holding newHolding = new Holding(symbol, newShares, newAverageCost);
            dataAccess.updateCash(username, cash - totalCost);
            dataAccess.updateHolding(username, newHolding);

        }
        else {
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

            final double proceeds = stockPrice * shares;
            final int remaining = holding.getShares() - shares;
            dataAccess.updateCash(username, cash + proceeds);

            if (remaining == 0) {
                dataAccess.removeHolding(username, symbol);
            }
            else {
                final Holding updated = new Holding(symbol, remaining, holding.getAvgCost());
                dataAccess.updateHolding(username, updated);
            }
        }

        final double updatedCash = dataAccess.getCash(username);
        final Holding updatedHolding = dataAccess.getHolding(username, symbol);
        final int totalSharesAfterTrade = updatedHolding == null ? 0 : updatedHolding.getShares();
        final double averageCostAfterTrade = updatedHolding == null ? 0 : updatedHolding.getAvgCost();
        final double totalHoldingValueAfterTrade = totalSharesAfterTrade * stockPrice;

        // record the order
        final double executedPrice = stockPrice;
        final OrderRecord orderRecord = new OrderRecord(
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
