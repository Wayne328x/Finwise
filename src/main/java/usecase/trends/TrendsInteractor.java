package usecase.trends;

import entity.Expense;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

/**
 * Interactor for the Trends use case.
 */
public class TrendsInteractor implements TrendsInputBoundary {

    private final TrendsDataAccess dataAccess;
    private final TrendsOutputBoundary presenter;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * @param dataAccess
     * @param presenter
     */
    public TrendsInteractor(TrendsDataAccess dataAccess, TrendsOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    /**
     * Executes the Trends use case.
     * @param inputData the input data (e.g. username, start date, end date)
     */
    @Override
    public void execute(TrendsInputData inputData) {
        //Load expenses for this user
        List<Expense> expenses = dataAccess.getExpenses(inputData.getUsername());

        Map<LocalDate, Map<String, Double>> totalExpenses = new TreeMap<>();

        for (Expense expense : expenses) {
            //Retrieving the expense date
            LocalDateTime dateTime = LocalDateTime.parse(expense.getDatetime(), formatter);
            LocalDate date = dateTime.toLocalDate();

            //Date check
            if (inputData.getUsername().equals(expense.getUsername()) && !date.isBefore(inputData.getStartDate()) && !date.isAfter(inputData.getEndDate())) {
                //Retrieving the expense type
                String type = expense.getType();
                if (!totalExpenses.containsKey(date)) {
                    totalExpenses.put(date, new TreeMap<>());
                }
                //Adding the expense into the correct bucket
                totalExpenses.get(date).put(type, totalExpenses.get(date).getOrDefault(type, 0.0) + expense.getAmount());
            }
        }

        //Output data
        TrendsOutputData outputData = new TrendsOutputData(totalExpenses);
        presenter.present(outputData);
    }
}
