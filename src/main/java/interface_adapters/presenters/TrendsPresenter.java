package interface_adapters.presenters;

import use_case.trends.TrendsOutputBoundary;
import use_case.trends.TrendsOutputData;
import ui.TrendsViewModel;

public class TrendsPresenter implements TrendsOutputBoundary {

    private final TrendsViewModel trendsViewModel;

    public TrendsPresenter(TrendsViewModel trendsViewModel) {
        this.trendsViewModel = trendsViewModel;
    }

    @Override
    public void present(TrendsOutputData outputData) {
        trendsViewModel.setTotalExpenses(outputData.getTotalExpenses());
    }
}
