package usecase.list_expenses;

import java.util.List;

import data.ExpenseRepository;
import entity.Expense;

public class ListExpensesInteractor {
    private final ExpenseRepository expenseRepository;

    public ListExpensesInteractor(ExpenseRepository expenseRepository) {

        this.expenseRepository = expenseRepository;
    }
    /**
    * Find User in Database and output expense information.
     */

    public ListExpensesOutputData execute(ListExpensesInputData input) {
        final String username = input.getUsername();
        final List<Expense> expenses = expenseRepository.findByUsername(username);
        final double total = expenseRepository.getTotalForUser(username);
        return new ListExpensesOutputData(expenses, total);
    }
}
