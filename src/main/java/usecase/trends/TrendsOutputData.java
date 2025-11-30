package usecase.trends;

import java.time.LocalDate;
import java.util.Map;

/**
 * Output data for the Trends use case.
 * The View layer will use this data to render a line graph.
 */
public class TrendsOutputData {

    private final Map<LocalDate, Map<String, Double>> totalExpenses;

    /**
     * @param totalExpenses the expenses, indexed by date and type.
     */
    public TrendsOutputData(Map<LocalDate, Map<String, Double>> totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    /**
     * @return the expense totals, indexed by date and type.
     */
    public Map<LocalDate, Map<String, Double>> getTotalExpenses() {
        return totalExpenses;
    }
}
