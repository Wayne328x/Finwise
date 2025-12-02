package usecase.list_expenses;

import data.expense.ExpenseRepository;
import entity.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ListExpensesInteractor}.
 */
class ListExpensesInteractorTest {

    private FakeExpenseRepository fakeRepo;
    private ListExpensesInteractor interactor;

    @BeforeEach
    void setUp() {
        fakeRepo = new FakeExpenseRepository();
        interactor = new ListExpensesInteractor(fakeRepo);
    }

    /**
     * Happy path: repository returns some expenses and a total,
     * and the interactor forwards them correctly.
     */
    @Test
    void testListExpensesWithResults() {
        String username = "alice";

        // Prepare fake data
        List<Expense> expenses = new ArrayList<>();
        expenses.add(new Expense(1, username, "2025-11-30 14:30", "Food", 10.50));
        expenses.add(new Expense(2, username, "2025-11-30 15:00", "Transport", 5.25));

        fakeRepo.expensesForUser = expenses;
        fakeRepo.totalForUser = 15.75;

        ListExpensesInputData input = new ListExpensesInputData(username);
        ListExpensesOutputData output = interactor.execute(input);

        // Verify output
        assertEquals(expenses, output.getExpenses(), "Expenses list should match repository data");
        assertEquals(15.75, output.getTotal(), 1e-6, "Total should match repository total");

        // Verify repository was called with correct username
        assertEquals(username, fakeRepo.lastFindByUsername, "findByUsername should be called with correct username");
        assertEquals(username, fakeRepo.lastGetTotalForUser, "getTotalForUser should be called with correct username");
    }

    /**
     * Edge case: user has no expenses. Repository returns an empty list and zero total.
     */
    @Test
    void testListExpensesNoResults() {
        String username = "bob";

        fakeRepo.expensesForUser = Collections.emptyList();
        fakeRepo.totalForUser = 0.0;

        ListExpensesInputData input = new ListExpensesInputData(username);
        ListExpensesOutputData output = interactor.execute(input);

        assertNotNull(output.getExpenses(), "Expenses list should not be null");
        assertTrue(output.getExpenses().isEmpty(), "Expenses list should be empty");
        assertEquals(0.0, output.getTotal(), 1e-6, "Total should be zero when no expenses");
    }

    /**
     * Edge case: blank username.
     * We simply verify that the interactor still forwards this to the repository;
     * any further validation would be a separate concern.
     */
    @Test
    void testListExpensesWithBlankUsernameStillCallsRepository() {
        String username = "   "; // blank/whitespace

        fakeRepo.expensesForUser = Collections.emptyList();
        fakeRepo.totalForUser = 0.0;

        ListExpensesInputData input = new ListExpensesInputData(username);
        interactor.execute(input);

        assertEquals(username, fakeRepo.lastFindByUsername);
        assertEquals(username, fakeRepo.lastGetTotalForUser);
    }

    /**
     * Edge case: repository throws (e.g. DB failure). We expect the interactor
     * to propagate the exception rather than swallow it silently.
     */
    @Test
    void testListExpensesRepositoryFailure() {
        fakeRepo.throwOnFind = true;

        ListExpensesInputData input = new ListExpensesInputData("alice");

        assertThrows(RuntimeException.class,
                () -> interactor.execute(input),
                "Interactor should propagate repository exceptions");
    }

    /**
     * Simple fake in-memory repository for testing.
     */
    private static class FakeExpenseRepository implements ExpenseRepository {

        List<Expense> expensesForUser = Collections.emptyList();
        double totalForUser = 0.0;

        String lastFindByUsername;
        String lastGetTotalForUser;

        boolean throwOnFind = false;

        @Override
        public void add(String username, String datetime, String type, double amount) {
            // not needed in these tests
            throw new UnsupportedOperationException("add() not used in ListExpensesInteractorTest");
        }

        @Override
        public List<Expense> findByUsername(String username) {
            lastFindByUsername = username;
            if (throwOnFind) {
                throw new RuntimeException("DB error on findByUsername");
            }
            return expensesForUser;
        }

        @Override
        public double getTotalForUser(String username) {
            lastGetTotalForUser = username;
            return totalForUser;
        }
    }
}
