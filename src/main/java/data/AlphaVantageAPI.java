package data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * API client for Alpha Vantage stock data service.
 * Provides methods to search stocks, get quotes, and retrieve time series data.
 */
public class AlphaVantageAPI {

  /**
   * API key for Alpha Vantage service.
   */
  private static final String API_KEY = "9P17BCLZ42787NSY";
  // Alternative API key: JTSZQWNFASUDRTW5 (hhc)
  // daily limit : 25

  /**
   * Base URL for Alpha Vantage API.
   */
  private static final String BASE_URL = "https://www.alphavantage.co/query";

  /**
   * HTTP client for making API requests.
   */
  private final OkHttpClient client;

  /**
   * Maximum number of search results to return.
   */
  private static final int MAX_SEARCH_RESULTS = 10;

  /**
   * Number of days to subtract for 5D interval
   * (5 trading days ≈ 7 calendar days).
   */
  private static final int DAYS_FOR_5D = 7;

  /**
   * Number of months to subtract for 6M interval.
   */
  private static final int MONTHS_FOR_6M = 6;

  /**
   * Number of years to subtract for 5Y interval.
   */
  private static final int YEARS_FOR_5Y = 5;

  /**
   * Constructs an AlphaVantageAPI instance.
   */
  public AlphaVantageAPI() {
    this.client = new OkHttpClient();
  }

  /**
   * Search for stocks using SYMBOL_SEARCH endpoint.
   * @param keywords the search keywords
   * @return list of stock search results
   * @throws IOException if the API call fails or response is invalid
   */
  public List<StockSearchResult> searchStocks(final String keywords)
      throws IOException {
    String encodedKeywords =
        URLEncoder.encode(keywords, StandardCharsets.UTF_8);
    String url = BASE_URL
        + "?function=SYMBOL_SEARCH&keywords="
        + encodedKeywords
        + "&apikey="
        + API_KEY;

    Request request = new Request.Builder()
        .url(url)
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException(
            "Unexpected HTTP code "
                + response.code()
                + " when calling: "
                + url);
      }

      String responseBody = response.body().string();
      System.out.println("DEBUG searchStocks response = " + responseBody);

      JsonObject json;
      try {
        json = JsonParser.parseString(responseBody).getAsJsonObject();
      } catch (Exception e) {
        throw new IOException(
            "Failed to parse JSON from AlphaVantage: " + responseBody,
            e);
      }

      if (json.has("Note")) {
        throw new IOException(
            "API note: " + json.get("Note").getAsString());
      }
      if (json.has("Error Message")) {
        throw new IOException(
            "API error: " + json.get("Error Message").getAsString());
      }
      if (json.has("Information")) {
        throw new IOException(
            "API info: " + json.get("Information").getAsString());
      }
      if (!json.has("bestMatches")) {
        throw new IOException(
            "Unexpected API response: no 'bestMatches' field. Body = "
                + responseBody);
      }

      JsonArray matches = json.getAsJsonArray("bestMatches");
      List<StockSearchResult> results = new ArrayList<>();

      if (matches != null) {
        for (int i = 0;
            i < matches.size() && i < MAX_SEARCH_RESULTS;
            i++) {
          JsonObject match = matches.get(i).getAsJsonObject();
          String symbol = match.get("1. symbol").getAsString();
          String name = match.get("2. name").getAsString();
          String type = match.get("3. type").getAsString();
          String region = match.get("4. region").getAsString();
          String currency = match.get("8. currency").getAsString();

          String exchange = extractExchange(region, symbol);
          results.add(new StockSearchResult(
              symbol,
              name,
              exchange,
              type,
              region,
              currency));
        }
      }
      return results;
    }
  }

  /**
   * Get current stock quote using GLOBAL_QUOTE endpoint.
   *
   * @param symbol the stock symbol
   * @return the stock quote
   * @throws IOException if the API call fails or response is invalid
   */
  public StockQuote getQuote(final String symbol) throws IOException {
    String url = BASE_URL
        + "?function=GLOBAL_QUOTE&symbol="
        + symbol
        + "&apikey="
        + API_KEY;

    Request request = new Request.Builder()
        .url(url)
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }

      String responseBody = response.body().string();
      JsonObject json =
          JsonParser.parseString(responseBody).getAsJsonObject();

      if (json.has("Note")) {
        throw new IOException(
            "API call frequency limit reached. "
                + json.get("Note").getAsString());
      }

      if (json.has("Error Message")) {
        throw new IOException(
            "API Error: " + json.get("Error Message").getAsString());
      }

      JsonObject quote = json.getAsJsonObject("Global Quote");
      if (quote == null || quote.size() == 0) {
        throw new IOException("No quote data found for symbol: " + symbol);
      }

      String sym = quote.get("01. symbol").getAsString();
      double price =
          Double.parseDouble(quote.get("05. price").getAsString());
      double change =
          Double.parseDouble(quote.get("09. change").getAsString());
      double changePercent = Double.parseDouble(
          quote.get("10. change percent").getAsString().replace("%", ""));

      return new StockQuote(sym, price, change, changePercent);
    }
  }

  /**
   * Get time series data for chart.
   *
   * @param symbol the stock symbol
   * @param interval the time interval (1D, 5D, 1M, 6M, 1Y, 5Y)
   * @return list of stock price data points
   * @throws IOException if the API call fails or response is invalid
   */
  public List<StockPriceData> getTimeSeries(
      final String symbol,
      final String interval) throws IOException {
    String function;
    switch (interval) {
      case "1D":
        function = "TIME_SERIES_INTRADAY&interval=5min";
        break;
      case "5D":
      case "1M":
        function = "TIME_SERIES_DAILY";
        break;
      case "6M":
      case "1Y":
        function = "TIME_SERIES_WEEKLY";
        break;
      case "5Y":
        function = "TIME_SERIES_MONTHLY";
        break;
      default:
        function = "TIME_SERIES_DAILY";
    }

    String url = BASE_URL
        + "?function="
        + function
        + "&symbol="
        + symbol
        + "&apikey="
        + API_KEY;

    Request request = new Request.Builder()
        .url(url)
        .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }

      String responseBody = response.body().string();
      JsonObject json =
          JsonParser.parseString(responseBody).getAsJsonObject();

      if (json.has("Note")) {
        throw new IOException(
            "API call frequency limit reached. "
                + json.get("Note").getAsString());
      }

      if (json.has("Error Message")) {
        throw new IOException(
            "API Error: " + json.get("Error Message").getAsString());
      }

      // Extract time series data based on function
      String timeSeriesKey = getTimeSeriesKey(function);
      JsonObject timeSeries = json.getAsJsonObject(timeSeriesKey);

      if (timeSeries == null) {
        throw new IOException(
            "No time series data found for symbol: " + symbol);
      }

      List<StockPriceData> data = new ArrayList<>();
      for (Map.Entry<String, com.google.gson.JsonElement> entry
          : timeSeries.entrySet()) {
        String date = entry.getKey();
        JsonObject values = entry.getValue().getAsJsonObject();

        double high =
            Double.parseDouble(values.get("2. high").getAsString());
        double low =
            Double.parseDouble(values.get("3. low").getAsString());
        double close =
            Double.parseDouble(values.get("4. close").getAsString());

        data.add(new StockPriceData(date, close, high, low));
      }

      // Filter by interval length (except for intraday 1D which uses
      // timestamps)
      if (!"1D".equals(interval)) {
        LocalDate today = LocalDate.now();
        LocalDate cutoff = null;

        switch (interval) {
          case "5D":
            // 5 trading days ≈ last 7 calendar days
            cutoff = today.minusDays(DAYS_FOR_5D);
            break;
          case "1M":
            cutoff = today.minusMonths(1);
            break;
          case "6M":
            cutoff = today.minusMonths(MONTHS_FOR_6M);
            break;
          case "1Y":
            cutoff = today.minusYears(1);
            break;
          case "5Y":
            cutoff = today.minusYears(YEARS_FOR_5Y);
            break;
          default:
            break;
        }

        if (cutoff != null) {
          final LocalDate cutoffDate = cutoff;
          final DateTimeFormatter formatter =
              DateTimeFormatter.ofPattern("yyyy-MM-dd");
          data.removeIf(p -> {
            LocalDate pointDate = LocalDate.parse(p.getDate(), formatter);
            return pointDate.isBefore(cutoffDate);
          });
        }
      }

      // Sort by date ascending
      data.sort((a, b) -> a.getDate().compareTo(b.getDate()));

      return data;
    }
  }

  /**
   * Gets the time series key from the function name.
   *
   * @param function the function name
   * @return the time series key
   */
  private String getTimeSeriesKey(final String function) {
    if (function.contains("INTRADAY")) {
      return "Time Series (5min)";
    } else if (function.contains("DAILY")) {
      return "Time Series (Daily)";
    } else if (function.contains("WEEKLY")) {
      return "Weekly Time Series";
    } else if (function.contains("MONTHLY")) {
      return "Monthly Time Series";
    }
    return "Time Series (Daily)";
  }

  /**
   * Extracts the exchange name from region and symbol.
   *
   * @param region the region string
   * @param symbol the stock symbol
   * @return the exchange name
   */
  private String extractExchange(final String region, final String symbol) {
    // Simple extraction - can be improved
    if (region.contains("United States")) {
      if (symbol.contains(".")) {
        return symbol.substring(symbol.indexOf(".") + 1);
      }
      return "NASDAQ";  // Default for US stocks
    }
    return region;
  }

  /**
   * Inner class representing a stock search result.
   */
  public static final class StockSearchResult {

    /**
     * The stock symbol.
     */
    private final String symbol;

    /**
     * The stock name.
     */
    private final String name;

    /**
     * The stock exchange.
     */
    private final String exchange;

    /**
     * The stock type.
     */
    private final String type;

    /**
     * The stock region.
     */
    private final String region;

    /**
     * The stock currency.
     */
    private final String currency;

    /**
     * Constructs a StockSearchResult.
     *
     * @param symbolValue the stock symbol
     * @param nameValue the stock name
     * @param exchangeValue the stock exchange
     * @param typeValue the stock type
     * @param regionValue the stock region
     * @param currencyValue the stock currency
     */
    public StockSearchResult(
        final String symbolValue,
        final String nameValue,
        final String exchangeValue,
        final String typeValue,
        final String regionValue,
        final String currencyValue) {
      this.symbol = symbolValue;
      this.name = nameValue;
      this.exchange = exchangeValue;
      this.type = typeValue;
      this.region = regionValue;
      this.currency = currencyValue;
    }

    /**
     * Gets the stock symbol.
     *
     * @return the stock symbol
     */
    public String getSymbol() {
      return symbol;
    }

    /**
     * Gets the stock name.
     *
     * @return the stock name
     */
    public String getName() {
      return name;
    }

    /**
     * Gets the stock exchange.
     *
     * @return the stock exchange
     */
    public String getExchange() {
      return exchange;
    }

    /**
     * Gets the stock type.
     *
     * @return the stock type
     */
    public String getType() {
      return type;
    }

    /**
     * Gets the stock region.
     *
     * @return the stock region
     */
    public String getRegion() {
      return region;
    }

    /**
     * Gets the stock currency.
     *
     * @return the stock currency
     */
    public String getCurrency() {
      return currency;
    }
  }

  /**
   * Inner class representing a stock quote.
   */
  public static final class StockQuote {

    /**
     * The stock symbol.
     */
    private final String symbol;

    /**
     * The stock price.
     */
    private final double price;

    /**
     * The price change.
     */
    private final double change;

    /**
     * The price change percentage.
     */
    private final double changePercent;

    /**
     * Constructs a StockQuote.
     *
     * @param symbolValue the stock symbol
     * @param priceValue the stock price
     * @param changeValue the price change
     * @param changePercentValue the price change percentage
     */
    public StockQuote(
        final String symbolValue,
        final double priceValue,
        final double changeValue,
        final double changePercentValue) {
      this.symbol = symbolValue;
      this.price = priceValue;
      this.change = changeValue;
      this.changePercent = changePercentValue;
    }

    /**
     * Gets the stock symbol.
     *
     * @return the stock symbol
     */
    public String getSymbol() {
      return symbol;
    }

    /**
     * Gets the stock price.
     *
     * @return the stock price
     */
    public double getPrice() {
      return price;
    }

    /**
     * Gets the price change.
     *
     * @return the price change
     */
    public double getChange() {
      return change;
    }

    /**
     * Gets the price change percentage.
     *
     * @return the price change percentage
     */
    public double getChangePercent() {
      return changePercent;
    }
  }

  /**
   * Inner class representing stock price data.
   */
  public static final class StockPriceData {

    /**
     * The date string.
     */
    private final String date;

    /**
     * The closing price.
     */
    private final double close;

    /**
     * The high price.
     */
    private final double high;

    /**
     * The low price.
     */
    private final double low;

    /**
     * Full constructor using close, high, and low.
     *
     * @param dateValue the date string
     * @param closeValue the closing price
     * @param highValue the high price
     * @param lowValue the low price
     */
    public StockPriceData(
        final String dateValue,
        final double closeValue,
        final double highValue,
        final double lowValue) {
      this.date = dateValue;
      this.close = closeValue;
      this.high = highValue;
      this.low = lowValue;
    }

    /**
     * Backward-compatible constructor that only specifies close.
     * High/low default to the close price.
     *
     * @param dateValue the date string
     * @param closeValue the closing price
     */
    public StockPriceData(
        final String dateValue,
        final double closeValue) {
      this(dateValue, closeValue, closeValue, closeValue);
    }

    /**
     * Gets the date string.
     *
     * @return the date string
     */
    public String getDate() {
      return date;
    }

    /**
     * Close price (used as the plotted value in the line chart).
     *
     * @return the close price
     */
    public double getPrice() {
      return close;
    }

    /**
     * Gets the closing price.
     *
     * @return the closing price
     */
    public double getClose() {
      return close;
    }

    /**
     * Gets the high price.
     *
     * @return the high price
     */
    public double getHigh() {
      return high;
    }

    /**
     * Gets the low price.
     *
     * @return the low price
     */
    public double getLow() {
      return low;
    }
  }
}
