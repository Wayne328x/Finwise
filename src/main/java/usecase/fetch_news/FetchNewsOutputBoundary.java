package usecase.fetch_news;

import java.util.List;

import entity.News;

public interface FetchNewsOutputBoundary {
    /**
     * The interface of output boundary.
     * @param newsList list of news for output
     */
    void presentNews(List<News> newsList);

    /**
     * If failed to fetch the news, get the error message.
     * @param errorMessage error message
     */
    void presentError(String errorMessage);
}
