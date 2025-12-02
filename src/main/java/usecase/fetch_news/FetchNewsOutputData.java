package usecase.fetch_news;

import java.util.List;

import entity.News;

/**
 * Including two types of outcomes.
 * successful: newsList is non-empty, and errorMessage is null
 * failed: newsList is empty, and errorMessage has something
 */
public class FetchNewsOutputData {

    private final List<News> newsList;
    private final String errorMessage;

    /**
     * When succeeded.
     * @param newsList is what it got from interactor.
     */
    public FetchNewsOutputData(List<News> newsList) {
        this.newsList = newsList;
        this.errorMessage = null;
    }

    /**
     * When failed.
     * @param errorMessage is the message it got from interactor.
     */
    public FetchNewsOutputData(String errorMessage) {
        this.newsList = null;
        this.errorMessage = errorMessage;
    }

    public List<News> getNewsList() {
        return newsList;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
