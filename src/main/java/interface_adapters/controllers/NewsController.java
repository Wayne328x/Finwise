package interface_adapters.controllers;

import use_case.fetch_news.FetchNewsInputBoundary;
import interface_adapters.presenters.FetchNewsPresenter;
import use_case.fetch_news.FetchNewsInputData;

public class NewsController {

    private final FetchNewsInputBoundary interactor; // UseCase
    private final FetchNewsPresenter presenter;           // for the "previous and next page"

    public NewsController(FetchNewsInputBoundary interactor, FetchNewsPresenter presenter) {
        this.interactor = interactor;
        this.presenter = presenter;
    }

    // fetch news and then use interactor
    public void fetchNews() {
        try {
            // if we let users input data in the future, pass it in here.
            FetchNewsInputData inputData = new FetchNewsInputData();
            interactor.execute(inputData);
        } catch (Exception e) {
            presenter.presentError("Failed to fetch the news: " + e.getMessage());
        }
    }

    public void goToPreviousPage() {
        presenter.prevPage();
    }

    public void goToNextPage() {
        presenter.nextPage();
    }
}
