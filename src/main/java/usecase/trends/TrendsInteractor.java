package usecase.trends;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import entity.Expense;

/**
 * Interactor for the Trends use case.
 */
public class TrendsInteractor implements TrendsInputBoundary {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final TrendsDataAccess dataAccess;
    private final TrendsOutputBoundary presenter;

    /**
     * Constructs a TrendsInteractor.
     *
     * @param dataAccess the data access object for retrieving expenses
     * @param presenter the output boundary that presents the results
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
        // Load expenses for this user
        final List<Expense> expenses = dataAccess.getExpenses(inputData.getUsername());
        final Map<LocalDate, Map<String, Double>> totalExpenses = new TreeMap<>();

        for (Expense expense : expenses) {
            // Retrieving the expense date
            final LocalDateTime dateTime = LocalDateTime.parse(expense.getDatetime(), FORMATTER);
            final LocalDate date = dateTime.toLocalDate();

            // Date check
            if (inputData.getUsername().equals(expense.getUsername())
                    && !date.isBefore(inputData.getStartDate())
                    && !date.isAfter(inputData.getEndDate())) {
                // Retrieving the expense type
                final String type = expense.getType();
                if (!totalExpenses.containsKey(date)) {
                    totalExpenses.put(date, new TreeMap<>());
                }
                // Adding the expense into the correct bucket
                totalExpenses.get(date).put(type,
                        totalExpenses.get(date).getOrDefault(type, 0.0) + expense.getAmount());
            }
        }

        // Output data
        final TrendsOutputData outputData = new TrendsOutputData(totalExpenses);
        presenter.present(outputData);
    }
}
