package data.news;

import entity.News;
import usecase.fetch_news.NewsDataAccessInterface;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NewsApiDAO implements NewsDataAccessInterface {

    private static final String API_KEY = "W9M7RRT7GJDWDNQA";
    private static final String URL = "https://www.alphavantage.co/query?function=NEWS_SENTIMENT&apikey=" + API_KEY;

    private final OkHttpClient client;
    private final Gson gson;

    // This is for main
    public NewsApiDAO() {
        this(new OkHttpClient());
    }

    // This is for test purpose only, allowing us to mock when api calls fail.
    public NewsApiDAO(OkHttpClient client) {
        this.client = client;
        this.gson = new Gson();
    }

    @Override
    public List<News> fetchNews(String query) {
        List<News> newsList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(URL)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to fetch news: " + response);
            }

            String body = response.body().string();
            JsonObject json = gson.fromJson(body, JsonObject.class);

            // test the api limit
            if (json.has("Information")) {
                String infoText = json.get("Information").getAsString();
                if (infoText.contains("Please subscribe to any of the premium plans")) {
                    throw new NewsDataAccessInterface.DataFetchException(infoText);
                }
            }

            // Alpha Vantage returns a Json, what we need is in "feed"
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

        } catch (IOException | JsonParseException e) {
            throw new RuntimeException("Error fetching or parsing news: " + e.getMessage(), e);
        }

        return newsList;
    }
}
