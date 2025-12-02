package usecase.trends;

import entity.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrendsInteractorTest {

    private FakeTrendsDataAccess fakeRepo;
    private FakePresenter fakePresenter;
    private TrendsInteractor interactor;

    @BeforeEach
    void setUp() {
        fakeRepo = new FakeTrendsDataAccess();
        fakePresenter = new FakePresenter();
        interactor = new TrendsInteractor(fakeRepo, fakePresenter);
    }

    /**
     * Normal case:
     *  - expenses within date range included
     *  - aggregation by date + type is correct
     *  - presenter is called once
     */
    @Test
    void testTrendsSuccess() {
        fakeRepo.expensesToReturn = List.of(
                new Expense(0, "alice", "2025-01-01 12:00", "Food", 10.0),
                new Expense(0, "alice", "2025-01-01 12:00", "Food", 5.0),
                new Expense(0, "alice", "2025-01-02 12:00", "Transport", 7.5)
        );

        TrendsInputData input = new TrendsInputData(
                "alice",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 2)
        );

        interactor.execute(input);

        assertTrue(fakePresenter.presentCalled, "Presenter called");

        Map<LocalDate, Map<String, Double>> totals = fakePresenter.lastOutput.getTotalExpenses();

        assertEquals(2, totals.size(), "Two dates present");
        assertEquals(15.0, totals.get(LocalDate.of(2025, 1, 1)).get("Food"), 1e-6);
        assertEquals(7.5, totals.get(LocalDate.of(2025, 1, 2)).get("Transport"), 1e-6);
    }

    /**
     * Expenses belonging to another username ignored.
     */
    @Test
    void testDifferentUsernameIgnored() {
        fakeRepo.expensesToReturn = List.of(
                new Expense(0, "bob", "2025-01-01 12:00", "Food", 10.0), // should be ignored
                new Expense(0, "alice", "2025-01-01 12:00", "Food", 5.0) // included
        );

        TrendsInputData input = new TrendsInputData(
                "alice",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 2)
        );

        interactor.execute(input);

        Map<LocalDate, Map<String, Double>> totals = fakePresenter.lastOutput.getTotalExpenses();

        assertEquals(1, totals.size());
        assertEquals(5.0, totals.get(LocalDate.of(2025, 1, 1)).get("Food"), 1e-6);
    }

    /**
     * Date outside range ignored.
     */
    @Test
    void testOutOfRangeDateIgnored() {
        fakeRepo.expensesToReturn = List.of(
                new Expense(0, "alice", "2024-12-31 12:00", "Food", 10.0), // before start
                new Expense(0, "alice", "2025-01-03 12:00", "Food", 20.0), // after end
                new Expense(0, "alice", "2025-01-02 12:00", "Food", 7.0)   // included
        );

        TrendsInputData input = new TrendsInputData(
                "alice",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 2)
        );

        interactor.execute(input);

        Map<LocalDate, Map<String, Double>> totals = fakePresenter.lastOutput.getTotalExpenses();

        assertEquals(1, totals.size());
        assertEquals(7.0, totals.get(LocalDate.of(2025, 1, 2)).get("Food"), 1e-6);
    }

    /**
     * Empty list means presenter still called with empty map.
     */
    @Test
    void testEmptyExpensesList() {
        fakeRepo.expensesToReturn = Collections.emptyList();

        TrendsInputData input = new TrendsInputData(
                "alice",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 5)
        );

        interactor.execute(input);

        assertTrue(fakePresenter.presentCalled);
        assertTrue(fakePresenter.lastOutput.getTotalExpenses().isEmpty());
    }

    /**
     * Repository throws means we propagate exceptions.
     */
    @Test
    void testRepositoryThrows() {
        fakeRepo.throwOnGet = true;

        TrendsInputData input = new TrendsInputData(
                "alice",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 5)
        );

        assertThrows(RuntimeException.class, () -> interactor.execute(input));
    }

    /**
     * Fake Data Access.
     */
    private static class FakeTrendsDataAccess implements TrendsDataAccess {

        List<Expense> expensesToReturn = Collections.emptyList();
        boolean throwOnGet = false;
        String lastUsername;

        @Override
        public List<Expense> getExpenses(String username) {
            lastUsername = username;
            if (throwOnGet) {
                throw new RuntimeException("DB error");
            }
            return expensesToReturn;
        }
    }

    /**
     * Fake Presenter.
     */
    private static class FakePresenter implements TrendsOutputBoundary {

        boolean presentCalled = false;
        TrendsOutputData lastOutput;

        @Override
        public void present(TrendsOutputData outputData) {
            presentCalled = true;
            lastOutput = outputData;
        }
    }
}
