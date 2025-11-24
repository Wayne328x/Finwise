package interface_adapters.controllers;

import use_case.trends.TrendsInputBoundary;
import use_case.trends.TrendsInputData;

import java.time.LocalDate;

public class TrendsController {

    private final TrendsInputBoundary trendsInteractor;

    public TrendsController(TrendsInputBoundary trendsInteractor) {
        this.trendsInteractor = trendsInteractor;
    }

    public void onViewTrends(String username, LocalDate startDate, LocalDate endDate) {
        TrendsInputData inputData = new TrendsInputData(username, startDate, endDate);
        trendsInteractor.execute(inputData);
    }
}
