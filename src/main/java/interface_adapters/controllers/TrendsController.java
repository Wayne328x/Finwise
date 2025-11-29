package interface_adapters.controllers;

import use_case.trends.TrendsInputBoundary;
import use_case.trends.TrendsInputData;

import java.time.LocalDate;

/**
 * Controller for the Trends use case.
 */
public class TrendsController {

    private final TrendsInputBoundary trendsInteractor;

    /**
     * @param trendsInteractor case input boundary (implemented by trendsInteractor).
     */
    public TrendsController(TrendsInputBoundary trendsInteractor) {
        this.trendsInteractor = trendsInteractor;
    }

    /**
     * Trigger the trend use case for a given user.
     * @param  username the username of the logged-in user.
     * @param  startDate the starting date.
     * @param  endDate the end date.
     * @return ViewModel containing data of trends.
     */
    public void onViewTrends(String username, LocalDate startDate, LocalDate endDate) {
        TrendsInputData inputData = new TrendsInputData(username, startDate, endDate);
        trendsInteractor.execute(inputData);
    }
}
