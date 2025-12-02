package data.expense;

import java.util.List;

import data.database.RegisteredExpenseRepository;
import entity.Expense;
import usecase.trends.TrendsDataAccess;

/**
 * Data adapter for the Trends use case.
 */
public class TrendsAdapter implements TrendsDataAccess {

    private final RegisteredExpenseRepository repo;

    /**
     * Constructs a TrendsAdapter with the given expense repository.
     *
     * @param repo the entire repository of expenses
     */
    public TrendsAdapter(final RegisteredExpenseRepository repo) {
        this.repo = repo;
    }

    /**
     * Retrieves all expenses for the given username.
     *
     * @param username the username of the logged-in user
     * @return list of expenses for the user
     */
    @Override
    public List<Expense> getExpenses(final String username) {
        // Delegate to the existing repository
        return repo.findByUsername(username);
    }
}
