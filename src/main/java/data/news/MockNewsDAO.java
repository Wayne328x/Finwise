package data.news;

import entity.News;
import usecase.fetch_news.NewsDataAccessInterface;
import com.google.gson.*;

import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock DAO is used for local test
 * reads in sample Json data and then generate List<News>
 */
public class MockNewsDAO implements NewsDataAccessInterface {

    private final String filePath;

    /**
     * @param filePath file path of the local sample data
     */
    public MockNewsDAO(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<News> fetchNews(String query) {
        List<News> newsList = new ArrayList<>();
        try (FileReader reader = new FileReader(filePath)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray feed = json.getAsJsonArray("feed");

            if (feed != null) {
                for (JsonElement elem : feed) {
                    JsonObject newsObj = elem.getAsJsonObject();
                    String title = newsObj.get("title").getAsString();
                    String url = newsObj.get("url").getAsString();
                    String timeStr = newsObj.get("time_published").getAsString();

                    // adjust the time format
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
                    LocalDateTime datePublished = LocalDateTime.parse(timeStr, formatter);

                    newsList.add(new News(title, url, datePublished));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to read mock news file: " + e.getMessage(), e);
        }

        return newsList;
    }
}
