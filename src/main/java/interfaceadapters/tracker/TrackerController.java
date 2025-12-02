package interfaceadapters.tracker;

import usecase.add_expense.AddExpenseInputData;
import usecase.add_expense.AddExpenseInteractor;
import usecase.add_expense.AddExpenseOutputData;
import usecase.list_expenses.ListExpensesInputData;
import usecase.list_expenses.ListExpensesInteractor;
import usecase.list_expenses.ListExpensesOutputData;

/**
 * Controller for the Expense Tracker use cases.
 * Packages input into data objects and delegating to interactors.
 */
public class TrackerController {

    private final ListExpensesInteractor listInteractor;
    private final AddExpenseInteractor addInteractor;

    /**
     * Constructs a tracker controller with the required interactors.
     *
     * @param listInteractor interactor responsible for listing user expenses
     * @param addInteractor  interactor responsible for adding a new expense
     */
    public TrackerController(ListExpensesInteractor listInteractor,
                             AddExpenseInteractor addInteractor) {
        this.listInteractor = listInteractor;
        this.addInteractor = addInteractor;
    }

    /**
     * Loads all expenses associated with a given username.
     *
     * @param username the username whose expense history should be retrieved
     * @return output data containing the user's expenses and total
     */
    public ListExpensesOutputData loadExpenses(String username) {
        final ListExpensesInputData input = new ListExpensesInputData(username);
        return listInteractor.execute(input);
    }

    /**
     * Adds a new expense for a specific user.
     *
     * @param username   the user submitting the expense
     * @param datetime   the expense timestamp as a string
     * @param type       the category/type of expense
     * @param amountText the numeric amount as string input
     * @return output data containing success flag and displayable results
     */
    public AddExpenseOutputData addExpense(String username,
                                           String datetime,
                                           String type,
                                           String amountText) {
        final AddExpenseInputData input =
                new AddExpenseInputData(username, datetime, type, amountText);
        return addInteractor.execute(input);
    }
}
