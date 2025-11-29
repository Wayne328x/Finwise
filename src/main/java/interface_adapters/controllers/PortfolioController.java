package interface_adapters.controllers;

import use_case.case5.PortfolioViewModel;
import use_case.portfolio.PortfolioInputBoundary;
import use_case.portfolio.PortfolioInputData;

/**
 * Controller for the Portfolio Analysis use case (Use Case 5).
 * Interface Adapters layer
 */
public class PortfolioController {

    private final PortfolioInputBoundary interactor;
    private final PortfolioViewModel viewModel;

    /**
     * @param interactor use case input boundary (implemented by PortfolioInteractor)
     * @param viewModel  view model updated by the presenter
     */
    public PortfolioController(PortfolioInputBoundary interactor,
                               PortfolioViewModel viewModel) {
        this.interactor = interactor;
        this.viewModel = viewModel;
    }

    /**
     * Trigger the portfolio analysis use case for a given user.
     * @param  username the username of the logged-in user
     * @return ViewModel containing holdings, snapshots, message, etc.
     */
    public PortfolioViewModel analyze(String username) {
        viewModel.setUsername(username);

        PortfolioInputData inputData = new PortfolioInputData(username);
        interactor.execute(inputData);

        return viewModel;
    }
}


