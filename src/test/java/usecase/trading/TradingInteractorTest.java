package usecase.trading;


import entity.Holding;
import entity.OrderRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TradingInteractorTest {

    private FakeTradingDataAccess dao;
    private SpyPresenter presenter;
    private TradingInteractor interactor;

    @BeforeEach
    void setUp() {
        dao = new FakeTradingDataAccess();
        presenter = new SpyPresenter();
        interactor = new TradingInteractor(dao, presenter);
    }

    static class FakeTradingDataAccess implements TradingDataAccessInterface {
        double cash = 0;
        Map<String, Map<String, Holding>> holdings = new HashMap<>();
        List<OrderRecord> savedOrders = new ArrayList<>();
        double stubPrice = 100.0;
        boolean throwOnQuote = false;

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

        @Override public double getStockPrice(String symbol) {
            if (throwOnQuote) throw new RuntimeException("quote fail");
            return stubPrice;
        }
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
        dao.cash = 1000.0;
        interactor.placeOrder(new TradingInputData("u", "AAPL", TradingInputData.Action.BUY, 5));

        Holding h = dao.getHolding("u", "AAPL");
        assertNotNull(h);
        assertEquals(5, h.getShares());
        assertEquals(1000.0 - 5 * dao.stubPrice, dao.getCash("u"), 0.001);
        assertNotNull(presenter.last);
        assertTrue(presenter.last.isSuccess());
        assertEquals(1, dao.savedOrders.size());
        assertEquals("BUY", dao.savedOrders.get(0).getAction());
    }

    @Test
    void sell_moreThanOwned_failsAndDoesNotSave() {
        dao.cash = 1000.0;
        dao.updateHolding("u", new Holding("AAPL", 2, 50));

        interactor.placeOrder(new TradingInputData("u", "AAPL", TradingInputData.Action.SELL, 5));

        // Holding unchanged
        assertEquals(2, dao.getHolding("u", "AAPL").getShares());
        // No new order saved
        assertEquals(0, dao.savedOrders.size());
        assertNotNull(presenter.last);
        assertFalse(presenter.last.isSuccess());
    }

    @Test
    void buy_existingHolding_recomputesAverageCost() {
        dao.cash = 1000.0;
        dao.updateHolding("u", new Holding("AAPL", 2, 100.0));

        interactor.placeOrder(new TradingInputData("u", "AAPL", TradingInputData.Action.BUY, 3));

        Holding h = dao.getHolding("u", "AAPL");
        assertNotNull(h);
        assertEquals(5, h.getShares());
        assertEquals(100.0, h.getAvgCost(), 0.001);
        assertEquals(1000.0 - 3 * dao.stubPrice, dao.getCash("u"), 0.001);
        assertTrue(presenter.last.isSuccess());
        assertEquals(1, dao.savedOrders.size());
    }

    @Test
    void sell_partialLeavesHolding() {
        dao.cash = 200.0;
        dao.updateHolding("u", new Holding("AAPL", 5, 50.0));
        dao.stubPrice = 120.0;

        interactor.placeOrder(new TradingInputData("u", "AAPL", TradingInputData.Action.SELL, 2));

        Holding h = dao.getHolding("u", "AAPL");
        assertNotNull(h);
        assertEquals(3, h.getShares());
        assertEquals(50.0, h.getAvgCost(), 0.001);
        assertEquals(200.0 + 2 * 120.0, dao.getCash("u"), 0.001);
        assertTrue(presenter.last.isSuccess());
        assertEquals(1, dao.savedOrders.size());
    }

    @Test
    void nullAction_fails() {
        dao.cash = 300.0;
        interactor.placeOrder(new TradingInputData("u", "AAPL", null, 1));
        assertNotNull(presenter.last);
        assertFalse(presenter.last.isSuccess());
        assertNull(dao.getHolding("u", "AAPL"));
        assertEquals(0, dao.savedOrders.size());
    }

    @Test
    void nullSymbol_fails() {
        dao.cash = 300.0;
        interactor.placeOrder(new TradingInputData("u", null, TradingInputData.Action.BUY, 1));
        assertNotNull(presenter.last);
        assertFalse(presenter.last.isSuccess());
        assertNull(dao.getHolding("u", null));
        assertEquals(0, dao.savedOrders.size());
    }

    @Test
    void buy_withoutEnoughCash_fails() {
        dao.cash = 100.0;
        interactor.placeOrder(new TradingInputData("u", "AAPL", TradingInputData.Action.BUY, 5));

        assertNull(dao.getHolding("u", "AAPL"));
        assertEquals(100.0, dao.getCash("u"), 0.001);
        assertNotNull(presenter.last);
        assertFalse(presenter.last.isSuccess());
        assertEquals(0, dao.savedOrders.size());
    }

    @Test
    void sell_exactShares_removesHoldingAndAddsCash() {
        dao.cash = 1000.0;
        dao.updateHolding("u", new Holding("AAPL", 2, 50));
        dao.stubPrice = 120.0;

        interactor.placeOrder(new TradingInputData("u", "AAPL", TradingInputData.Action.SELL, 2));

        assertNull(dao.getHolding("u", "AAPL"));
        assertEquals(1000.0 + 2 * 120.0, dao.getCash("u"), 0.001);
        assertEquals(1, dao.savedOrders.size());
        assertEquals("SELL", dao.savedOrders.get(0).getAction());
        assertTrue(presenter.last.isSuccess());
    }

    @Test
    void invalidInput_blankSymbol() {
        dao.cash = 500.0;
        interactor.placeOrder(new TradingInputData("u", "   ", TradingInputData.Action.BUY, 1));

        assertNull(dao.getHolding("u", "   "));
        assertEquals(500.0, dao.getCash("u"), 0.001);
        assertNotNull(presenter.last);
        assertFalse(presenter.last.isSuccess());
        assertEquals(0, dao.savedOrders.size());
    }

    @Test
    void invalidInput_nonPositiveShares() {
        dao.cash = 500.0;
        interactor.placeOrder(new TradingInputData("u", "AAPL", TradingInputData.Action.BUY, 0));

        assertNull(dao.getHolding("u", "AAPL"));
        assertEquals(500.0, dao.getCash("u"), 0.001);
        assertNotNull(presenter.last);
        assertFalse(presenter.last.isSuccess());
        assertEquals(0, dao.savedOrders.size());
    }

    @Test
    void quoteFailure_causesFail() {
        dao.cash = 500.0;
        dao.throwOnQuote = true;

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                interactor.placeOrder(new TradingInputData("u", "AAPL", TradingInputData.Action.BUY, 1))
        );
        assertEquals("quote fail", ex.getMessage());
        assertNull(dao.getHolding("u", "AAPL"));
        assertEquals(500.0, dao.getCash("u"), 0.001);
        assertEquals(0, dao.savedOrders.size());
    }

    @Test
    void outputData_getters() {
        TradingOutputData out = new TradingOutputData("messages", true, 10, 2.5, 3, 7.5);
        assertEquals("messages", out.getMessage());
        assertTrue(out.isSuccess());
        assertEquals(10, out.getCashAfterTrade());
        assertEquals(2.5, out.getAverageCostAfterTrade());
        assertEquals(3, out.getTotalSharesAfterTrade());
        assertEquals(7.5, out.getTotalHoldingValueAfterTrade());
    }

}
