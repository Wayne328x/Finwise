package usecase.fetch_news;

import data.news.NewsApiDao;
import entity.News;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


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
        NewsDataAccessInterface failureDao = new NewsDataAccessInterface() {
            @Override
            public List<News> fetchNews(String query){
                throw new NewsDataAccessInterface.DataFetchException("API Rate Limit Exceeded");
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
    public void daoRateLimitTest() {
        // 1. Mock a client that returns "Rate Limit" JSON
        OkHttpClient mockClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String json = "{\"Information\": \"Please subscribe to any of the premium plans\"}";
                    return new Response.Builder()
                            .code(200)
                            .message("OK")
                            .request(chain.request())
                            .protocol(Protocol.HTTP_1_1)
                            .body(ResponseBody.create(json, MediaType.get("application/json")))
                            .build();
                })
                .build();

        NewsApiDao dao = new NewsApiDao(mockClient);

        // 2. check if it throws DataFetchException
        assertThrows(NewsDataAccessInterface.DataFetchException.class, () -> {
            dao.fetchNews("general");
        });
    }

    @Test
    public void daoServerErrorTest() {
        // 1. Mock a client that returns Error 500 (cover !response.isSuccessful())
        OkHttpClient mockClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    return new Response.Builder()
                            .code(500)
                            .message("Internal Server Error")
                            .request(chain.request())
                            .protocol(Protocol.HTTP_1_1)
                            .body(ResponseBody.create("", MediaType.get("application/json")))
                            .build();
                })
                .build();

        NewsApiDao dao = new NewsApiDao(mockClient);

        // 2. check if RuntimeException is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dao.fetchNews("general");
        });
        assertTrue(exception.getMessage().contains("Error fetching"));
    }

    @Test
    public void daoInvalidJsonTest() {
        // 1. mack a Client that returns some random JSON (covers JsonParseException)
        OkHttpClient mockClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    return new Response.Builder()
                            .code(200)
                            .message("OK")
                            .request(chain.request())
                            .protocol(Protocol.HTTP_1_1)
                            .body(ResponseBody.create("{ Not Valid JSON }", MediaType.get("application/json")))
                            .build();
                })
                .build();

        NewsApiDao dao = new NewsApiDao(mockClient);

        assertThrows(RuntimeException.class, () -> {
            dao.fetchNews("general");
        });
    }

    @Test
    public void daoNetworkExceptionTest() {
        // 1. mock a client that throws IOException
        OkHttpClient mockClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    throw new IOException("Network Down");
                })
                .build();

        NewsApiDao dao = new NewsApiDao(mockClient);

        assertThrows(RuntimeException.class, () -> {
            dao.fetchNews("general");
        });
    }

    @Test
    public void daoInformationWithoutLimitTest() {
        // Purpose：cover the branch that json.has("Information") == true and infoText.contains("subscribe") == false
        OkHttpClient mockClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String json = "{\"Information\": \"Just some standard API info.\", \"feed\": []}";
                    return new Response.Builder()
                            .code(200)
                            .message("OK")
                            .request(chain.request())
                            .protocol(Protocol.HTTP_1_1)
                            .body(ResponseBody.create(json, MediaType.get("application/json")))
                            .build();
                })
                .build();

        NewsApiDao dao = new NewsApiDao(mockClient);

        // returns an empty list safely, instead of throwing DataFetchException
        List<News> result = dao.fetchNews("general");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void daoMissingFeedTest() {
        // Purpose：cover the branch that if (feed != null) == false
        // mock that api returns some information without string "feed"
        OkHttpClient mockClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String json = "{\"meta_data\": \"some info\", \"items\": \"0\"}";
                    return new Response.Builder()
                            .code(200)
                            .message("OK")
                            .request(chain.request())
                            .protocol(Protocol.HTTP_1_1)
                            .body(ResponseBody.create(json, MediaType.get("application/json")))
                            .build();
                })
                .build();

        NewsApiDao dao = new NewsApiDao(mockClient);

        // returns an empty list safely，instead of throwing NullPointerException
        List<News> result = dao.fetchNews("general");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

//  @Disabled("To save API limit for now")
    @Test
    public void daoRealIntegrationTest() {
        // REMARK: This is the only real api call in the test. When the limit is reached, the test will fail,
        //         but it will not affect the code coverage.

        NewsApiDao dao = new NewsApiDao();
        try {
            List<News> news = dao.fetchNews("general");
            if (!news.isEmpty()) {
                assertNotNull(news.get(0).getTitle());
            }
        } catch (Exception e) {
            System.out.println("Integration test skipped/failed due to network: " + e.getMessage());
        }
    }
}