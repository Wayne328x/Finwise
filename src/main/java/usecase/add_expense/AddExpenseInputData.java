package usecase.add_expense;

public class AddExpenseInputData {
    private final String username;
    private final String datetime;
    private final String type;
    private final String amountText;

    public AddExpenseInputData(String username, String datetime, String type, String amountText) {
        this.username = username;
        this.datetime = datetime;
        this.type = type;
        this.amountText = amountText;
    }

    public String getUsername() {
        return username;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getType() {
        return type;
    }

    public String getAmountText() {
        return amountText;
    }
}
