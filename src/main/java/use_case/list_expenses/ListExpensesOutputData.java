package use_case.list_expenses;

import entity.Expense;
import java.util.List;

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
