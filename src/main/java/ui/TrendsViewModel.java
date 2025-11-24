package ui;

import java.time.LocalDate;
import java.util.Map;

public class TrendsViewModel {

    private Map<LocalDate, Map<String, Double>> totalExpenses;

    public Map<LocalDate, Map<String, Double>> getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(Map<LocalDate, Map<String, Double>> totalExpenses) {
        this.totalExpenses = totalExpenses;
    }
}
