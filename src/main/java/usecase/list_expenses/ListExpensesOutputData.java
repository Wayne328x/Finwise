package usecase.list_expenses;

import java.util.List;

import entity.Expense;

public class ListExpensesOutputData {
    private final List<Expense> expenses;
    private final double total;

    public ListExpensesOutputData(List<Expense> expenses, double total) {
        this.expenses = expenses;
        this.total = total;
    }

    public List<Expense> getExpenses() {

        return expenses;
    }

    public double getTotal() {

        return total;
    }
}
