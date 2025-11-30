package usecase.list_expenses;

import java.util.List;

import data.ExpenseRepository;
import entity.Expense;

/**
 * Interactor for finding list of expenses for an User.
 */
public class ListExpensesInteractor {
    private final ExpenseRepository expenseRepository;

    public ListExpensesInteractor(ExpenseRepository expenseRepository) {

        this.expenseRepository = expenseRepository;
    }
    /**
     * Searches list of expense, calculates total.
     *
     * @param input the data needed to search for list of expenses.
     * @return the result of the operation, including the list of expenses and total amount.
     */

    public ListExpensesOutputData execute(ListExpensesInputData input) {
        final String username = input.getUsername();
        final List<Expense> expenses = expenseRepository.findByUsername(username);
        final double total = expenseRepository.getTotalForUser(username);
        return new ListExpensesOutputData(expenses, total);
    }
}
