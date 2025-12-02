package usecase.add_expense;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import data.expense.ExpenseRepository;

/**
 * Interactor for adding a new expense.
 */
public class AddExpenseInteractor {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ExpenseRepository expenseRepository;

    public AddExpenseInteractor(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    /**
     * Validates the input, parses the date and amount, and adds the expense.
     *
     * @param input the data needed to add a new expense
     * @return the result of the operation, including success flag, message, and
     *         normalized values if successful
     */
    public AddExpenseOutputData execute(AddExpenseInputData input) {
        boolean success = false;
        String message;
        String normalizedDatetime = null;
        String type = null;
        Double amount = null;

        final String username = input.getUsername();
        final String datetime = input.getDatetime();
        final String rawType = input.getType();
        final String amountText = input.getAmountText();

        boolean hasMissingField = false;
        if (isBlank(username)) {
            hasMissingField = true;
        }
        else if (isBlank(datetime)) {
            hasMissingField = true;
        }
        else if (isBlank(rawType)) {
            hasMissingField = true;
        }
        else if (isBlank(amountText)) {
            hasMissingField = true;
        }

        if (hasMissingField) {
            message = "Please fill up all fields!";
        }
        else {
            final LocalDateTime parsedDateTime = parseDateTime(datetime);
            if (parsedDateTime == null) {
                message = "Please enter date/time as yyyy-MM-dd HH:mm";
            }
            else {
                normalizedDatetime = parsedDateTime.format(FORMATTER);
                type = rawType;

                final Double parsedAmount = parseAmount(amountText);
                if (parsedAmount == null) {
                    message = "Please enter a valid number!";
                    normalizedDatetime = null;
                    type = null;
                }
                else {
                    amount = parsedAmount;
                    try {
                        expenseRepository.add(username, normalizedDatetime, type, amount);
                        success = true;
                        message = "Expense added!";
                    }
                    catch (Exception event) {
                        message = "Failed to add expense!";
                        normalizedDatetime = null;
                        type = null;
                        amount = null;
                    }
                }
            }
        }

        return new AddExpenseOutputData(success, message,
                normalizedDatetime, type, amount);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private LocalDateTime parseDateTime(String datetime) {
        LocalDateTime result = null;
        try {
            result = LocalDateTime.parse(datetime, FORMATTER);
        }
        catch (DateTimeParseException event) {
            // return null to signal invalid datetime
        }
        return result;
    }

    private Double parseAmount(String amountText) {
        Double result = null;
        try {
            result = Double.parseDouble(amountText);
        }
        catch (NumberFormatException event) {
            // return null to signal invalid amount
        }
        return result;
    }
}
