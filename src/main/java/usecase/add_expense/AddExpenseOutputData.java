package usecase.add_expense;

public class AddExpenseOutputData {
    private final boolean success;
    private final String message;
    private final String datetime;
    private final String type;
    private final double amount;

    public AddExpenseOutputData(boolean success, String message,
                                String datetime, String type, double amount) {
        this.success = success;
        this.message = message;
        this.datetime = datetime;
        this.type = type;
        this.amount = amount;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }
}
