package usecase.add_expense;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import data.ExpenseRepository;

public class AddExpenseInteractor {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final ExpenseRepository expenseRepository;

    public AddExpenseInteractor(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public AddExpenseOutputData execute(AddExpenseInputData input) {
        final String username = input.getUsername();
        final String datetime = input.getDatetime();
        final String type = input.getType();
        final String amountText = input.getAmountText();

        if (username == null || username.isBlank()
                || datetime == null || datetime.isBlank()
                || type == null || type.isBlank()
                || amountText == null || amountText.isBlank()) {
            return new AddExpenseOutputData(false, "Please fill up all fields!",
                    null, null, null);
        }

        final LocalDateTime parsedDateTime;
        try {
            parsedDateTime = LocalDateTime.parse(datetime, FORMATTER);
        }
        catch (DateTimeParseException event) {
            return new AddExpenseOutputData(
                    false,
                    "Please enter date/time as yyyy-MM-dd HH:mm",
                    null, null, null
            );
        }

        final String normalizedDatetime = parsedDateTime.format(FORMATTER);

        final double amount;
        try {
            amount = Double.parseDouble(amountText);
        }
        catch (NumberFormatException event) {
            return new AddExpenseOutputData(false, "Please enter a valid number!",
                    null, null, null);
        }
        try {
            expenseRepository.add(username, normalizedDatetime, type, amount);
            return new AddExpenseOutputData(true, "Expense added!",
                    normalizedDatetime, type, amount);
        }
        catch (Exception event) {
            return new AddExpenseOutputData(false, "Failed to add expense!",
                    null, null, null);
        }
    }
}
