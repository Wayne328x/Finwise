package interface_adapters.presenters;

import entity.Expense;
import use_case.trends.TrendsOutputBoundary;
import use_case.trends.TrendsOutputData;
import ui.TrendsViewModel;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

/**
 * Presenter for the Trends use case.
 */
public class TrendsPresenter implements TrendsOutputBoundary {

    private final TrendsViewModel trendsViewModel;

    /**
     * @param trendsViewModel the data to be viewed.
     */
    public TrendsPresenter(TrendsViewModel trendsViewModel) {
        this.trendsViewModel = trendsViewModel;
    }

    /**
     * @param outputData the data to be viewed.
     */
    @Override
    public void present(TrendsOutputData outputData) {
        trendsViewModel.setTotalExpenses(outputData.getTotalExpenses());
    }
}
