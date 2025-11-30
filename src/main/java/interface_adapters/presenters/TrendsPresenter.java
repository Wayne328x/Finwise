package interface_adapters.presenters;

import usecase.trends.TrendsOutputBoundary;
import usecase.trends.TrendsOutputData;
import ui.trends.TrendsViewModel;

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
