package usecase.list_expenses;

public class ListExpensesInputData {
    private final String username;

    public ListExpensesInputData(String username) {

        this.username = username;
    }

    public String getUsername() {

        return username;
    }
}
