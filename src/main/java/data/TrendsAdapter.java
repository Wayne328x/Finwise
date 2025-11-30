package data;

import java.util.List;
import entity.Expense;
import usecase.trends.TrendsDataAccess;

/**
 * Data adapter for the Trends use case.
 */
public class TrendsAdapter implements TrendsDataAccess {

    private final RegisteredExpenseRepository repo;

    /**
     * @param repo the entire repository of expenses.
     */
    public TrendsAdapter(RegisteredExpenseRepository repo) {
        this.repo = repo;
    }

    /**
     * @param username the username of the logged-in user.
     */
    @Override
    public List<Expense> getExpenses(String username) {
        // Delegate to the existing repository
        return repo.findByUsername(username);
    }
}
