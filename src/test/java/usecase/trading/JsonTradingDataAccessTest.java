package usecase.trading;

import data.trading.JsonTradingDataAccess;
import entity.Holding;
import entity.OrderRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonTradingDataAccessTest {

    private Path tempFile;

    @BeforeEach
    void setUp() throws Exception {
        tempFile = Files.createTempFile("orders", ".json");
    }

    @AfterEach
    void cleanup() throws Exception {
        if (tempFile != null && Files.exists(tempFile)) {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void saveAndReloadOrders_rebuildsHoldings() {
        TradingDataAccessInterface dao = new JsonTradingDataAccess(tempFile);

        dao.updateCash("u", 800.0);
        dao.updateHolding("u", new Holding("AAPL", 2, 100.0));
        dao.saveOrder(new OrderRecord(Instant.now(), "u", "AAPL", "BUY", 2, 100.0, 200.0));

        TradingDataAccessInterface dao2 = new JsonTradingDataAccess(tempFile);

        Holding h = dao2.getHolding("u", "AAPL");
        assertNotNull(h);
        assertEquals(2, h.getShares());
        assertEquals(800.0, dao2.getCash("u"), 0.001);

        List<OrderRecord> orders = dao2.findOrdersByUser("u");
        assertEquals(1, orders.size());
        assertEquals("AAPL", orders.get(0).getSymbol());
    }

    @Test
    void emptyFileReturnsEmptyState() throws Exception {

        Files.deleteIfExists(tempFile);

        TradingDataAccessInterface dao = new JsonTradingDataAccess(tempFile);
        assertEquals(0.0, dao.getCash("someone"), 0.001);
        assertTrue(dao.getUserHoldings("someone").isEmpty());
        assertTrue(dao.findOrdersByUser("someone").isEmpty());
    }


    @Test
    void sellOrderUpdatesCashAndHoldings() {
        TradingDataAccessInterface dao = new JsonTradingDataAccess(tempFile);

        dao.updateCash("u", 740.0);
        dao.updateHolding("u", new Holding("MSFT", 3, 100.0));
        dao.saveOrder(new OrderRecord(Instant.now(), "u", "MSFT", "BUY", 5, 100.0, 500.0));
        dao.saveOrder(new OrderRecord(Instant.now(), "u", "MSFT", "SELL", 2, 120.0, 240.0));

        TradingDataAccessInterface dao2 = new JsonTradingDataAccess(tempFile);
        Holding h = dao2.getHolding("u", "MSFT");
        assertNotNull(h);
        assertEquals(3, h.getShares());
        assertEquals(740.0, dao2.getCash("u"), 0.001);
    }


    @Test
    void multipleUsersAreIsolated() {
        TradingDataAccessInterface dao = new JsonTradingDataAccess(tempFile);

        dao.updateCash("alice", 900.0);
        dao.updateHolding("alice", new Holding("AAPL", 1, 100.0));
        dao.saveOrder(new OrderRecord(Instant.now(), "alice", "AAPL", "BUY", 1, 100.0, 100.0));

        dao.updateCash("bob", 400.0);
        dao.updateHolding("bob", new Holding("TSLA", 2, 50.0));
        dao.saveOrder(new OrderRecord(Instant.now(), "bob", "TSLA", "BUY", 2, 50.0, 100.0));

        TradingDataAccessInterface dao2 = new JsonTradingDataAccess(tempFile);
        Holding hAlice = dao2.getHolding("alice", "AAPL");
        assertNotNull(hAlice);
        assertEquals(1, hAlice.getShares());
        assertEquals(900.0, dao2.getCash("alice"), 0.001);

        Holding hBob = dao2.getHolding("bob", "TSLA");
        assertNotNull(hBob);
        assertEquals(2, hBob.getShares());
        assertEquals(400.0, dao2.getCash("bob"), 0.001);
    }

    @Test
    void multipleOrdersAccumulate() {
        TradingDataAccessInterface dao = new JsonTradingDataAccess(tempFile);

        dao.updateCash("u", 880.0);
        dao.updateHolding("u", new Holding("MSFT", 1, 112.0));
        dao.saveOrder(new OrderRecord(Instant.now(), "u", "MSFT", "BUY", 2, 100.0, 200.0));
        dao.saveOrder(new OrderRecord(Instant.now(), "u", "MSFT", "BUY", 3, 120.0, 360.0));
        dao.saveOrder(new OrderRecord(Instant.now(), "u", "MSFT", "SELL", 4, 110.0, 440.0));

        TradingDataAccessInterface dao2 = new JsonTradingDataAccess(tempFile);
        Holding h = dao2.getHolding("u", "MSFT");
        assertNotNull(h);
        assertEquals(1, h.getShares());
        assertEquals(880.0, dao2.getCash("u"), 0.001);
    }


    @Test
    void multipleSymbolsPersisted() {
        TradingDataAccessInterface dao = new JsonTradingDataAccess(tempFile);
        dao.updateCash("u", 900.0);
        dao.updateHolding("u", new Holding("AAPL", 1, 100.0));
        dao.updateHolding("u", new Holding("TSLA", 2, 50.0));
        dao.saveOrder(new OrderRecord(Instant.now(), "u", "AAPL", "BUY", 1, 100.0, 100.0));
        dao.saveOrder(new OrderRecord(Instant.now(), "u", "TSLA", "BUY", 2, 50.0, 100.0));

        TradingDataAccessInterface dao2 = new JsonTradingDataAccess(tempFile);
        Holding aapl = dao2.getHolding("u", "AAPL");
        Holding tsla = dao2.getHolding("u", "TSLA");
        assertNotNull(aapl);
        assertNotNull(tsla);
        assertEquals(1, aapl.getShares());
        assertEquals(2, tsla.getShares());
        assertEquals(900.0, dao2.getCash("u"), 0.001);
    }
}
