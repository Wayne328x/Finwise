package data.news;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import entity.News;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import usecase.fetch_news.NewsDataAccessInterface;

public class NewsApiDao implements NewsDataAccessInterface {

    private static final String API_KEY = "W9M7RRT7GJDWDNQA";
    private static final String URL = "https://www.alphavantage.co/query?function=NEWS_SENTIMENT&apikey=" + API_KEY;

    private final OkHttpClient client;
    private final Gson gson;

    // This is for main
    public NewsApiDao() {
        this(new OkHttpClient());
    }

    // This is for test purpose only, allowing us to mock when api calls fail.
    public NewsApiDao(OkHttpClient client) {
        this.client = client;
        this.gson = new Gson();
    }

    @Override
    public List<News> fetchNews(String query) {
        final List<News> newsList = new ArrayList<>();

        final Request request = new Request.Builder()
                .url(URL)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to fetch news: " + response);
            }

            final String body = response.body().string();
            final JsonObject json = gson.fromJson(body, JsonObject.class);

            // test the api limit
            if (json.has("Information")) {
                final String infoText = json.get("Information").getAsString();
                if (infoText.contains("Please subscribe to any of the premium plans")) {
                    throw new NewsDataAccessInterface.DataFetchException(infoText);
                }
            }

            // Alpha Vantage returns a Json, what we need is in "feed"
            final JsonArray feed = json.getAsJsonArray("feed");
            if (feed != null) {
                for (JsonElement elem : feed) {
                    final JsonObject newsObj = elem.getAsJsonObject();
                    final String title = newsObj.get("title").getAsString();
                    final String url = newsObj.get("url").getAsString();
                    final String timeStr = newsObj.get("time_published").getAsString();

                    // adjust the time format
                    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
                    final LocalDateTime datePublished = LocalDateTime.parse(timeStr, formatter);

                    newsList.add(new News(title, url, datePublished));
                }
            }

        }
        catch (IOException | JsonParseException exception) {
            throw new DataFetchException("Error fetching or parsing news: " + exception.getMessage());
        }

        return newsList;
    }
}
