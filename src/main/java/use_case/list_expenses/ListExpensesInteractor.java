package use_case.list_expenses;

import data.ExpenseRepository;
import entity.Expense;

import java.util.List;

public class ListExpensesInteractor {
    private final ExpenseRepository expenseRepository;

    public ListExpensesInteractor(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public ListExpensesOutputData execute(ListExpensesInputData input) {
        String username = input.getUsername();
        List<Expense> expenses = expenseRepository.findByUsername(username);
        double total = expenseRepository.getTotalForUser(username);
        return new ListExpensesOutputData(expenses, total);
    }
}
