package usecase.fetch_news;

import java.util.List;

import entity.News;

public interface NewsDataAccessInterface {

    /**
     * Fetch news from the data source.
     * @param query is for searching news by keywords or category, for example.
     * @return is a JSON file from API call.
     */
    List<News> fetchNews(String query);

    class DataFetchException extends RuntimeException {
        public DataFetchException(String message) {
            super(message);
        }

    }
}
