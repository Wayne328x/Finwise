package usecase.trading;


import entity.Holding;
import entity.OrderRecord;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TradingInteractorTest {

    static class FakeTradingDataAccess implements TradingDataAccessInterface {
        double cash = 0;
        Map<String, Map<String, Holding>> holdings = new HashMap<>();
        List<OrderRecord> savedOrders = new ArrayList<>();
        double stubPrice = 100.0;

        @Override public double getCash(String username) { return cash; }
        @Override public void updateCash(String username, double newCash) { cash = newCash; }

        @Override public Holding getHolding(String username, String symbol) {
            return holdings.getOrDefault(username, new HashMap<>()).get(symbol);
        }
        @Override public void updateHolding(String username, Holding holding) {
            holdings.computeIfAbsent(username, k -> new HashMap<>()).put(holding.getSymbol(), holding);
        }
        @Override public void removeHolding(String username, String symbol) {
            Map<String, Holding> h = holdings.get(username);
            if (h != null) h.remove(symbol);
        }

        @Override public double getStockPrice(String symbol) { return stubPrice; }
        @Override public List<Holding> getUserHoldings(String username) {
            return new ArrayList<>(holdings.getOrDefault(username, new HashMap<>()).values());
        }

        @Override public void saveOrder(OrderRecord orderRecord) { savedOrders.add(orderRecord); }
        @Override public List<OrderRecord> findOrdersByUser(String username) { return savedOrders; }
    }

    static class SpyPresenter implements TradingOutputBoundary {
        TradingOutputData last;

        @Override
        public void presentTradeResult(TradingOutputData output) {
            last = output;
        }
    }

    @Test
    void buy_withEnoughCash_updatesHoldingsAndSavesOrder() {
        FakeTradingDataAccess dao = new FakeTradingDataAccess();
        dao.cash = 1000.0;
        SpyPresenter presenter = new SpyPresenter();
        TradingInteractor interactor = new TradingInteractor(dao, presenter);

        interactor.placeOrder(new TradingInputData("u", "AAPL", TradingInputData.Action.BUY, 5));

        Holding h = dao.getHolding("u", "AAPL");
        assertNotNull(h);
        assertEquals(5, h.getShares());
        assertEquals(1000.0 - 5 * dao.stubPrice, dao.getCash("u"), 0.001);
        assertNotNull(presenter.last);
        //assertTrue(presenter.last.isSuccess());
        assertEquals(1, dao.savedOrders.size());
        assertEquals("BUY", dao.savedOrders.get(0).getAction());
    }

    @Test
    void sell_moreThanOwned_failsAndDoesNotSave() {
        FakeTradingDataAccess dao = new FakeTradingDataAccess();
        dao.cash = 1000.0;
        dao.updateHolding("u", new Holding("AAPL", 2, 50));
        SpyPresenter presenter = new SpyPresenter();
        TradingInteractor interactor = new TradingInteractor(dao, presenter);

        interactor.placeOrder(new TradingInputData("u", "AAPL", TradingInputData.Action.SELL, 5));

        // Holding unchanged
        assertEquals(2, dao.getHolding("u", "AAPL").getShares());
        // No new order saved
        assertEquals(0, dao.savedOrders.size());
        assertNotNull(presenter.last);
        //assertFalse(presenter.last.isSuccess());
    }
}
