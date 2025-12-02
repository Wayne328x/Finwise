package usecase.trends;

import java.util.List;

import entity.Expense;

/**
 * Data Access (use case interface) for Trends use case.
 */
public interface TrendsDataAccess {
    /**
     * Retrieves all expenses for the given user.
     *
     * @param username The username whose expenses should be retrieved
     * @return A list of Expense objects belonging to the user
     */
    List<Expense> getExpenses(String username);
}
