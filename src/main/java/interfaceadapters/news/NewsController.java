package interfaceadapters.news;

import usecase.fetch_news.FetchNewsInputBoundary;
import usecase.fetch_news.FetchNewsInputData;
import usecase.fetch_news.NewsDataAccessInterface;

public class NewsController {

    private final FetchNewsInputBoundary interactor;
    private final FetchNewsPresenter presenter;
    // for the "previous and next page"

    public NewsController(FetchNewsInputBoundary interactor, FetchNewsPresenter presenter) {
        this.interactor = interactor;
        this.presenter = presenter;
    }

    /** Fetch news and then use interactor. */
    public void fetchNews() {
        try {
            // if we let users input data in the future, pass it in here.
            final FetchNewsInputData inputData = new FetchNewsInputData();
            interactor.execute(inputData);
        }
        catch (NewsDataAccessInterface.DataFetchException exception) {
            presenter.presentError("Failed to fetch the news: " + exception.getMessage());
        }
    }

    /** Go to previous page. */
    public void goToPreviousPage() {
        presenter.prevPage();
    }

    /** Go to next page. */
    public void goToNextPage() {
        presenter.nextPage();
    }
}
