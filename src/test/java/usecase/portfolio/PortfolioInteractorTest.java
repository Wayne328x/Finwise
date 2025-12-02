package usecase.portfolio;

import entity.Holding;
import entity.PortfolioSnapshot;
import entity.PricePoint;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PortfolioInteractorTest {

    // Fake Dependencies

    static class FakePortfolioRepository implements PortfolioRepository {
        List<Holding> holdings = new ArrayList<>();
        public void setHoldings(List<Holding> h) { this.holdings = h; }

        @Override
        public List<Holding> findHoldingsByUser(String username) {
            return holdings;
        }
    }

    static class FakePriceHistoryRepository implements PriceHistoryRepository {
        Map<String, List<PricePoint>> history = new HashMap<>();

        public void setHistory(String symbol, List<PricePoint> list) {
            history.put(symbol, list);
        }

        @Override
        public List<PricePoint> getPriceHistory(String symbol) {
            return history.getOrDefault(symbol, Collections.emptyList());
        }
    }

    static class FakePresenter implements PortfolioOutputBoundary {
        PortfolioOutputData received;

        @Override
        public void present(PortfolioOutputData outputData) {
            this.received = outputData;
        }
    }

    // Tests Begin

    @Test
    void testEmptyHoldings() {
        FakePortfolioRepository pr = new FakePortfolioRepository();
        FakePriceHistoryRepository phr = new FakePriceHistoryRepository();
        FakePresenter presenter = new FakePresenter();

        pr.setHoldings(Collections.emptyList());

        PortfolioInteractor interactor =
                new PortfolioInteractor(pr, phr, presenter);

        interactor.execute(new PortfolioInputData("userA"));

        assertFalse(presenter.received.hasData());
        assertEquals("No holdings found for user: userA",
                presenter.received.getMessage());
        assertTrue(presenter.received.getSnapshots().isEmpty());
    }

    @Test
    void testMissingPriceHistory() {
        FakePortfolioRepository pr = new FakePortfolioRepository();
        FakePriceHistoryRepository phr = new FakePriceHistoryRepository();
        FakePresenter presenter = new FakePresenter();

        Holding h = new Holding("AAPL", 10, 1500);
        pr.setHoldings(List.of(h));

        phr.setHistory("AAPL", Collections.emptyList()); // triggers failure

        PortfolioInteractor interactor =
                new PortfolioInteractor(pr, phr, presenter);

        interactor.execute(new PortfolioInputData("userA"));

        assertFalse(presenter.received.hasData());
        assertEquals("Missing historical prices for: AAPL",
                presenter.received.getMessage());
        assertEquals(List.of(h), presenter.received.getHoldings());
    }

    @Test
    void testNormalFlowWithPriceOnOrBefore() {
        FakePortfolioRepository pr = new FakePortfolioRepository();
        FakePriceHistoryRepository phr = new FakePriceHistoryRepository();
        FakePresenter presenter = new FakePresenter();

        Holding h1 = new Holding("AAPL", 10, 1500);
        Holding h2 = new Holding("MSFT", 5, 500);

        pr.setHoldings(List.of(h1, h2));

        List<PricePoint> aaplHistory = List.of(
                new PricePoint(LocalDate.of(2024, 1, 1), 100),
                new PricePoint(LocalDate.of(2024, 1, 2), 110)
        );

        // MSFT intentionally unsorted to test sorting
        List<PricePoint> msftHistory = List.of(
                new PricePoint(LocalDate.of(2024, 1, 2), 210),
                new PricePoint(LocalDate.of(2024, 1, 1), 200)
        );

        phr.setHistory("AAPL", new ArrayList<>(aaplHistory));
        phr.setHistory("MSFT", new ArrayList<>(msftHistory));

        PortfolioInteractor interactor =
                new PortfolioInteractor(pr, phr, presenter);

        interactor.execute(new PortfolioInputData("userA"));

        assertTrue(presenter.received.hasData());
        assertEquals("Portfolio analysis completed successfully.",
                presenter.received.getMessage());

        List<PortfolioSnapshot> snaps = presenter.received.getSnapshots();
        assertEquals(2, snaps.size()); // two dates

        // snapshot[0]: date = 2024-01-01
        assertEquals(LocalDate.of(2024, 1, 1), snaps.get(0).getDate());
        // totalCost = 10*1500 + 5*500 = 15000 + 2500 = 17500
        // value     = 10*100  + 5*200 = 1000  + 1000 = 2000
        assertEquals(17500, snaps.get(0).getTotalCost());
        assertEquals(2000, snaps.get(0).getTotalValue());

        // snapshot[1]: date = 2024-01-02
        assertEquals(LocalDate.of(2024, 1, 2), snaps.get(1).getDate());
        // cost unchanged
        assertEquals(17500, snaps.get(1).getTotalCost());
        // value = 10*110 + 5*210 = 1100 + 1050 = 2150
        assertEquals(2150, snaps.get(1).getTotalValue());
    }

    @Test
    void testFindPriceWhenNoDateBefore() {
        FakePortfolioRepository pr = new FakePortfolioRepository();
        FakePriceHistoryRepository phr = new FakePriceHistoryRepository();
        FakePresenter presenter = new FakePresenter();

        // base symbol: timeline earlier than AAPL history
        Holding base = new Holding("BASE", 1, 0);
        Holding h = new Holding("AAPL", 10, 1500);

        pr.setHoldings(List.of(base, h));

        // BASE timeline (earlier dates)
        phr.setHistory("BASE", new ArrayList<>(List.of(
                new PricePoint(LocalDate.of(2024, 1, 1), 0),
                new PricePoint(LocalDate.of(2024, 1, 2), 0)
        )));

        // AAPL history (all later, so lastBefore == null should happen)
        phr.setHistory("AAPL", new ArrayList<>(List.of(
                new PricePoint(LocalDate.of(2024, 1, 3), 300),
                new PricePoint(LocalDate.of(2024, 1, 4), 400)
        )));

        PortfolioInteractor interactor =
                new PortfolioInteractor(pr, phr, presenter);

        interactor.execute(new PortfolioInputData("userA"));

        List<PortfolioSnapshot> snaps = presenter.received.getSnapshots();

        // BASE timeline yields 2 dates (1/1 and 1/2)
        assertEquals(2, snaps.size());

        // For 2024-01-01: AAPL price = earliest price = 300
        assertEquals(300 * 10, snaps.get(0).getTotalValue());

        // For 2024-01-02: still earliest = 300
        assertEquals(300 * 10, snaps.get(1).getTotalValue());
    }
}

