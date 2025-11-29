package ui;

import java.time.LocalDate;
import java.util.Map;

/**
 * View Model for the Trends use case.
 */
public class TrendsViewModel {

    private Map<LocalDate, Map<String, Double>> totalExpenses;

    /**
     * @return a map of the expenses.
     */
    public Map<LocalDate, Map<String, Double>> getTotalExpenses() {
        return totalExpenses;
    }

    /**
     * @param totalExpenses the expenses for this trend.
     */
    public void setTotalExpenses(Map<LocalDate, Map<String, Double>> totalExpenses) {
        this.totalExpenses = totalExpenses;
    }
}
