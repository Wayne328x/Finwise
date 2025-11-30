package usecase.fetch_news;

import entity.News;

import java.util.List;

/**
 * when the news fetching is:
 * successful: newsList is non-empty, and errorMessage is null
 * failed: newsList is empty, and errorMessage has something
 */
public class FetchNewsOutputData {

    private final List<News> newsList;
    private final String errorMessage;

    /**
     * when succeeded
     */
    public FetchNewsOutputData(List<News> newsList) {
        this.newsList = newsList;
        this.errorMessage = null;
    }

    /**
     * when failed
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
