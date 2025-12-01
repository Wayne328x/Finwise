package data.news;

import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import entity.News;
import usecase.fetch_news.FetchNewsInputData;
import usecase.fetch_news.FetchNewsInteractor;
import usecase.fetch_news.FetchNewsOutputBoundary;

public class FetchNewsInteractorTest {

    @Test
    public void successTest() {
        NewsApiDAO successDao = new NewsApiDAO() {
            @Override
            public List<News> fetchNews(String query) {
                List<News> newsList = new ArrayList<>();
                // 模拟 JSON 里的那条 Benzinga 新闻
                // 时间: 20251117T160000 -> 2025-11-17 16:00
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

        // 2. 准备 Presenter
        FetchNewsOutputBoundary successPresenter = new FetchNewsOutputBoundary() {
            @Override
            public void presentNews(List<News> outputData) {
                assertEquals(1, outputData.size());
                assertEquals("CRTC helps bring high-speed Internet to 27 communities in Saskatchewan", outputData.get(0).getTitle());
                // 验证 URL 是文章链接，不是 API 链接
                assertTrue(outputData.get(0).getUrl().startsWith("https://www.benzinga.com"));
            }

            @Override
            public void presentError(String errorMessage) {
                fail("Test expected success but failed with: " + errorMessage);
            }
        };

        // 3. 执行
        FetchNewsInteractor interactor = new FetchNewsInteractor(successDao, successPresenter);
        interactor.execute(new FetchNewsInputData());
    }

    // Test 2: 模拟 API 限流异常 (RateLimitExceededException)
    // ⚠️ 这是拿满 100% 覆盖率的关键，因为它覆盖了 Interactor 里的第一个 catch 块
    @Test
    public void rateLimitFailureTest() {
        NewsApiDAO failureDao = new NewsApiDAO() {
            @Override
            public List<News> fetchNews(String query) throws NewsApiDAO.RateLimitExceededException {
                // 模拟 API 返回 "Please subscribe to any of the premium plans"
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
                // 验证我们是否捕获了特定的限流异常
                // 注意：你需要检查 Interactor 里是怎么处理这个异常的消息的
                assertNotNull(errorMessage);
                // 假设 Interactor 直接传出了异常信息，或者加了前缀
                assertTrue(errorMessage.contains("Rate Limit") || errorMessage.contains("API"));
            }
        };

        FetchNewsInteractor interactor = new FetchNewsInteractor(failureDao, failurePresenter);
        interactor.execute(new FetchNewsInputData());
    }

    // Test 3: 模拟通用网络错误 (RuntimeException)
    // 覆盖 Interactor 里的第二个 catch (Exception e) 块
    @Test
    public void generalFailureTest() {
        NewsApiDAO failureDao = new NewsApiDAO() {
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
                // 验证通用错误处理
                assertTrue(errorMessage.contains("Failed to fetch news"));
                assertTrue(errorMessage.contains("Network crashed"));
            }
        };

        FetchNewsInteractor interactor = new FetchNewsInteractor(failureDao, failurePresenter);
        interactor.execute(new FetchNewsInputData());
    }
}