package use_case.add_expense;

import data.ExpenseRepository;

public class AddExpenseInteractor {

    private final ExpenseRepository expenseRepository;

    public AddExpenseInteractor(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public AddExpenseOutputData execute(AddExpenseInputData input) {
        String username = input.getUsername();
        String datetime = input.getDatetime();
        String type = input.getType();
        String amountText = input.getAmountText();

        if (username == null || username.isBlank() ||
                datetime == null || datetime.isBlank() ||
                type == null || type.isBlank() ||
                amountText == null || amountText.isBlank()) {
            return new AddExpenseOutputData(false, "Please fill up all fields!",
                    null, null, null);
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            return new AddExpenseOutputData(false, "Please enter a valid number!",
                    null, null, null);
        }

        try {
            expenseRepository.add(username, datetime, type, amount);
            return new AddExpenseOutputData(true, "Expense added!",
                    datetime, type, amount);
        } catch (Exception e) {
            return new AddExpenseOutputData(false, "Failed to add expense!",
                    null, null, null);
        }
    }
}
