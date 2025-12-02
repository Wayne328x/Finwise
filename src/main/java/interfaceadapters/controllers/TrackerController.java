package interfaceadapters.controllers;

import usecase.add_expense.*;
import usecase.list_expenses.*;

public class TrackerController {

    private final ListExpensesInteractor listInteractor;
    private final AddExpenseInteractor addInteractor;

    public TrackerController(ListExpensesInteractor listInteractor,
                             AddExpenseInteractor addInteractor) {
        this.listInteractor = listInteractor;
        this.addInteractor = addInteractor;
    }

    public ListExpensesOutputData loadExpenses(String username) {
        ListExpensesInputData input = new ListExpensesInputData(username);
        return listInteractor.execute(input);
    }

    public AddExpenseOutputData addExpense(String username,
                                           String datetime,
                                           String type,
                                           String amountText) {
        AddExpenseInputData input =
                new AddExpenseInputData(username, datetime, type, amountText);
        return addInteractor.execute(input);
    }
}
