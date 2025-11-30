package usecase.trends;

import entity.Expense;
import java.util.List;

/**
 * Data Access (use case interface) for Trends use case.
 */
public interface TrendsDataAccess {
    List<Expense> getExpenses(String username);
}
