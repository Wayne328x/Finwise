package usecase.fetch_news;

import fetch_news.NewsApiDAO;
import entity.News;
import java.util.List;

/**
 * Interface for accessing news data.
 */
public interface NewsDataAccessInterface {

    /**
     * Fetch news from the data source.
     * @param query The search query or keyword. Can be empty for latest news.
     * @return A list of News entities
     */
    List<News> fetchNews(String query) throws NewsApiDAO.RateLimitExceededException;
}
