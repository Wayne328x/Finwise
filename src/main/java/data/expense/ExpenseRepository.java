package data.expense;

import java.util.List;

import entity.Expense;

/**
 * Repository interface for expense data operations.
 * Provides methods to find, add, and calculate expenses.
 */
public interface ExpenseRepository {

    /**
     * Finds all expenses for a given username.
     *
     * @param username the username to search for
     * @return a list of expenses for the user
     */
    List<Expense> findByUsername(String username);

    /**
     * Adds a new expense to the database.
     *
     * @param username the username
     * @param datetime the expense datetime
     * @param type     the expense type
     * @param amount   the expense amount
     */
    void add(String username, String datetime, String type, double amount);

    /**
     * Gets the total expense amount for a user.
     *
     * @param username the username
     * @return the total expense amount
     */
    double getTotalForUser(String username);
}
