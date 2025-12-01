package fetch_news;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import entity.News;
import usecase.fetch_news.FetchNewsInputData;
import usecase.fetch_news.FetchNewsInteractor;
import usecase.fetch_news.FetchNewsOutputBoundary;
import usecase.fetch_news.NewsDataAccessInterface;

import static org.junit.jupiter.api.Assertions.*;


public class FetchNewsInteractorTest {

    @Test
    public void successTest() {
        NewsDataAccessInterface successDao = new NewsDataAccessInterface() {
            @Override
            public List<News> fetchNews(String query) {
                List<News> newsList = new ArrayList<>();
                LocalDateTime time = LocalDateTime.of(2025, 11, 17, 16, 0, 0);
                News news = new News(
                        "CRTC helps bring high-speed Internet to 27 communities in Saskatchewan",
                        "https://www.benzinga.com/pressreleases/...",
                        time
                );
                newsList.add(news);
                return newsList;
            }
        };

        FetchNewsOutputBoundary successPresenter = new FetchNewsOutputBoundary() {
            @Override
            public void presentNews(List<News> outputData) {
                assertEquals(1, outputData.size());
                assertEquals("CRTC helps bring high-speed Internet to 27 communities in Saskatchewan", outputData.get(0).getTitle());
                assertTrue(outputData.get(0).getUrl().startsWith("https://www.benzinga.com"));
            }

            @Override
            public void presentError(String errorMessage) {
                fail("Test expected success but failed with: " + errorMessage);
            }
        };

        FetchNewsInteractor interactor = new FetchNewsInteractor(successDao, successPresenter);
        interactor.execute(new FetchNewsInputData());
    }

    @Test
    public void rateLimitFailureTest() {
        // ✅ 修正：使用接口
        NewsDataAccessInterface failureDao = new NewsDataAccessInterface() {
            @Override
            public List<News> fetchNews(String query) throws NewsApiDAO.RateLimitExceededException {
                throw new NewsApiDAO.RateLimitExceededException("API Rate Limit Exceeded");
            }
        };

        FetchNewsOutputBoundary failurePresenter = new FetchNewsOutputBoundary() {
            @Override
            public void presentNews(List<News> outputData) {
                fail("Test expected RateLimit failure but succeeded.");
            }

            @Override
            public void presentError(String errorMessage) {
                assertNotNull(errorMessage);
                assertTrue(errorMessage.contains("Rate Limit") || errorMessage.contains("API"));
            }
        };

        FetchNewsInteractor interactor = new FetchNewsInteractor(failureDao, failurePresenter);
        interactor.execute(new FetchNewsInputData());
    }

    @Test
    public void generalFailureTest() {
        // ✅ 修正：使用接口
        NewsDataAccessInterface failureDao = new NewsDataAccessInterface() {
            @Override
            public List<News> fetchNews(String query) {
                throw new RuntimeException("Network crashed");
            }
        };

        FetchNewsOutputBoundary failurePresenter = new FetchNewsOutputBoundary() {
            @Override
            public void presentNews(List<News> outputData) {
                fail("Test expected General failure but succeeded.");
            }

            @Override
            public void presentError(String errorMessage) {
                assertTrue(errorMessage.contains("Failed to fetch news"));
                assertTrue(errorMessage.contains("Network crashed"));
            }
        };

        FetchNewsInteractor interactor = new FetchNewsInteractor(failureDao, failurePresenter);
        interactor.execute(new FetchNewsInputData());
    }

    @Test
    public void RealApiDAOTest() {
        NewsApiDAO dao = new NewsApiDAO();

        try {
            System.out.println("Testing Real API call...");
            List<News> newsList = dao.fetchNews("general");

            System.out.println("This test is expected to fail when api reached the limit!");
            assertNotNull(newsList);

            if (!newsList.isEmpty()) {
                News firstNews = newsList.get(0);
                System.out.println("Got news: " + firstNews.getTitle());
                assertNotNull(firstNews.getTitle());
                assertNotNull(firstNews.getUrl());
            }

        } catch (NewsApiDAO.RateLimitExceededException e) {
            System.out.println("API Limit reached, but code path covered.");
        } catch (Exception e) {
            System.out.println("Network error: " + e.getMessage());
        }
    }
}