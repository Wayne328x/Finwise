package usecase.fetch_news;

import entity.News;
import java.util.List;

public interface NewsDataAccessInterface {

    /**
     * Fetch news from the data source.
     */
    List<News> fetchNews(String query);

    class DataFetchException extends RuntimeException {
        public DataFetchException(String message) {
            super(message);
        }
    }
}