package usecase.add_expense;

import data.ExpenseRepository;
import entity.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AddExpenseInteractorTest {

    private FakeExpenseRepository fakeRepo;
    private AddExpenseInteractor interactor;

    @BeforeEach
    void setUp() {
        fakeRepo = new FakeExpenseRepository();
        interactor = new AddExpenseInteractor(fakeRepo);
    }

    /**
     * all fields valid -> success, repo.add called with correct values.
     */
    @Test
    void testAddExpenseSuccess() {
        AddExpenseInputData input = new AddExpenseInputData(
                "alice",
                "2025-11-30 14:30",
                "Food",
                "12.50"
        );

        AddExpenseOutputData output = interactor.execute(input);

        assertTrue(output.isSuccess(), "Expected success for valid expense");
        assertEquals("Expense added!", output.getMessage());
        assertEquals("2025-11-30 14:30", output.getDatetime());
        assertEquals("Food", output.getType());
        assertEquals(12.50, output.getAmount(), 1e-6);

        assertTrue(fakeRepo.addCalled, "Repository.add should have been called");
        assertEquals("alice", fakeRepo.lastUsername);
        assertEquals("2025-11-30 14:30", fakeRepo.lastDatetime);
        assertEquals("Food", fakeRepo.lastType);
        assertEquals(12.50, fakeRepo.lastAmount, 1e-6);
    }

    /**
     * Missing username -> fail, no repo call.
     */
    @Test
    void testAddExpenseMissingUsername() {
        AddExpenseInputData input = new AddExpenseInputData(
                " ",
                "2025-11-30 14:30",
                "Food",
                "10.00"
        );

        AddExpenseOutputData output = interactor.execute(input);

        assertFalse(output.isSuccess());
        assertEquals("Please fill up all fields!", output.getMessage());
        assertNull(output.getAmount());
        assertFalse(fakeRepo.addCalled, "Repo.add should not be called for invalid input");
    }

    /**
     * Missing datetime -> fail, no repo call.
     */
    @Test
    void testAddExpenseMissingDatetime() {
        AddExpenseInputData input = new AddExpenseInputData(
                "alice",
                "   ",
                "Food",
                "10.00"
        );

        AddExpenseOutputData output = interactor.execute(input);

        assertFalse(output.isSuccess());
        assertEquals("Please fill up all fields!", output.getMessage());
        assertNull(output.getAmount());
        assertFalse(fakeRepo.addCalled);
    }

    /**
     * Missing type -> fail.
     */
    @Test
    void testAddExpenseMissingType() {
        AddExpenseInputData input = new AddExpenseInputData(
                "alice",
                "2025-11-30 14:30",
                "   ",                         // blank type
                "10.00"
        );

        AddExpenseOutputData output = interactor.execute(input);

        assertFalse(output.isSuccess());
        assertEquals("Please fill up all fields!", output.getMessage());
        assertNull(output.getAmount());
        assertFalse(fakeRepo.addCalled);
    }

    /**
     * Missing amount -> fail.
     */
    @Test
    void testAddExpenseMissingAmount() {
        AddExpenseInputData input = new AddExpenseInputData(
                "alice",
                "2025-11-30 14:30",
                "Food",
                ""
        );

        AddExpenseOutputData output = interactor.execute(input);

        assertFalse(output.isSuccess());
        assertEquals("Please fill up all fields!", output.getMessage());
        assertNull(output.getAmount());
        assertFalse(fakeRepo.addCalled);
    }

    /**
     * Non-numeric amount -> fail, no repo call.
     */
    @Test
    void testAddExpenseInvalidAmountFormat() {
        AddExpenseInputData input = new AddExpenseInputData(
                "alice",
                "2025-11-30 14:30",
                "Food",
                "twelve point five"
        );

        AddExpenseOutputData output = interactor.execute(input);

        assertFalse(output.isSuccess());
        assertEquals("Please enter a valid number!", output.getMessage());
        assertNull(output.getAmount());
        assertFalse(fakeRepo.addCalled);
    }

    /**
     * Repository throws exception -> fail with "Failed to add expense!".
     */
    @Test
    void testAddExpenseRepositoryFailure() {
        fakeRepo.throwOnAdd = true;

        AddExpenseInputData input = new AddExpenseInputData(
                "alice",
                "2025-11-30 14:30",
                "Food",
                "12.50"
        );

        AddExpenseOutputData output = interactor.execute(input);

        assertFalse(output.isSuccess());
        assertEquals("Failed to add expense!", output.getMessage());
        assertNull(output.getAmount());
        assertTrue(fakeRepo.addCalled);
    }

    /**
     * fake repository – only add() is really used in these tests.
     */
    private static class FakeExpenseRepository implements ExpenseRepository {

        boolean addCalled = false;
        boolean throwOnAdd = false;

        String lastUsername;
        String lastDatetime;
        String lastType;
        double lastAmount;

        @Override
        public void add(String username, String datetime, String type, double amount) {
            addCalled = true;
            if (throwOnAdd) {
                throw new RuntimeException("DB error");
            }
            lastUsername = username;
            lastDatetime = datetime;
            lastType = type;
            lastAmount = amount;
        }

        @Override
        public List<Expense> findByUsername(String username) {
            return Collections.emptyList();
        }

        @Override
        public double getTotalForUser(String username) {
            return 0.0;
        }
    }
}

