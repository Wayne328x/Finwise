package data.stock;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * API client for Alpha Vantage stock data service.
 * Provides methods to search stocks, get quotes, and retrieve time series data.
 */
public class AlphaVantage {

    /**
     * API key for Alpha Vantage service.
     */
    private static final String API_KEY = "XLMWAWCGL5OB2VF6";
    // API_KEY = "9P17BCLZ42787NSY"
    // Alternative API key: JTSZQWNFASUDRTW5 (hhc)
    // Alternative API key: 14X8NQ0MYS4OL7V6 (jim)
    // daily limit : 25

    /**
     * Base URL for Alpha Vantage API.
     */
    private static final String BASE_URL = "https://www.alphavantage.co/query";

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
     * API key parameter string for URL construction.
     */
    private static final String API_KEY_PARAM = "&apikey=";

    /**
     * JSON field name for API notes.
     */
    private static final String JSON_FIELD_NOTE = "Note";

    /**
     * JSON field name for error messages.
     */
    private static final String JSON_FIELD_ERROR_MESSAGE = "Error Message";

    /**
     * HTTP client for making API requests.
     */
    private final OkHttpClient client;

    /**
     * Constructs an AlphaVantageAPI instance.
     */
    public AlphaVantage() {
        this.client = new OkHttpClient();
    }

    /**
     * Search for stocks using SYMBOL_SEARCH endpoint.
     *
     * @param keywords the search keywords
     * @return list of stock search results
     * @throws IOException if the API call fails or response is invalid
     */
    public List<StockSearchResult> searchStocks(final String keywords)
            throws IOException {
        final String encodedKeywords =
                URLEncoder.encode(keywords, StandardCharsets.UTF_8);
        final String url = BASE_URL
                + "?function=SYMBOL_SEARCH&keywords="
                + encodedKeywords
                + API_KEY_PARAM
                + API_KEY;

        final Request request = new Request.Builder()
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

            final String responseBody = response.body().string();
            System.out.println("DEBUG searchStocks response = " + responseBody);

            final JsonObject json;
            try {
                json = JsonParser.parseString(responseBody).getAsJsonObject();
            }
            catch (com.google.gson.JsonSyntaxException jsonException) {
                throw new IOException(
                        "Failed to parse JSON from AlphaVantage: " + responseBody,
                        jsonException);
            }

            validateSearchResponse(json, responseBody);
            final JsonArray matches = json.getAsJsonArray("bestMatches");
            return parseSearchResults(matches);
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
        final String url = BASE_URL
                + "?function=GLOBAL_QUOTE&symbol="
                + symbol
                + API_KEY_PARAM
                + API_KEY;

        final Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            final String responseBody = response.body().string();
            final JsonObject json =
                    JsonParser.parseString(responseBody).getAsJsonObject();

            if (json.has(JSON_FIELD_NOTE)) {
                throw new IOException(
                        "API call frequency limit reached. "
                                + json.get(JSON_FIELD_NOTE).getAsString());
            }

            if (json.has(JSON_FIELD_ERROR_MESSAGE)) {
                throw new IOException(
                        "API Error: " + json.get(JSON_FIELD_ERROR_MESSAGE).getAsString());
            }

            final JsonObject quote = json.getAsJsonObject("Global Quote");
            if (quote == null || quote.size() == 0) {
                throw new IOException("No quote data found for symbol: " + symbol);
            }

            final String sym = quote.get("01. symbol").getAsString();
            final double price =
                    Double.parseDouble(quote.get("05. price").getAsString());
            final double change =
                    Double.parseDouble(quote.get("09. change").getAsString());
            final double changePercent = Double.parseDouble(
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
        final String function;
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

        final String url = BASE_URL
                + "?function="
                + function
                + "&symbol="
                + symbol
                + API_KEY_PARAM
                + API_KEY;

        final Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            final String responseBody = response.body().string();
            final JsonObject json =
                    JsonParser.parseString(responseBody).getAsJsonObject();

            if (json.has(JSON_FIELD_NOTE)) {
                throw new IOException(
                        "API call frequency limit reached. "
                                + json.get(JSON_FIELD_NOTE).getAsString());
            }

            if (json.has(JSON_FIELD_ERROR_MESSAGE)) {
                throw new IOException(
                        "API Error: " + json.get(JSON_FIELD_ERROR_MESSAGE).getAsString());
            }

            // Extract time series data based on function
            final String timeSeriesKey = getTimeSeriesKey(function);
            final JsonObject timeSeries = json.getAsJsonObject(timeSeriesKey);

            if (timeSeries == null) {
                throw new IOException(
                        "No time series data found for symbol: " + symbol);
            }

            final List<StockPriceData> data = extractTimeSeriesData(timeSeries);
            filterByInterval(data, interval);
            data.sort((firstPoint, secondPoint) -> {
                return firstPoint.getDate().compareTo(secondPoint.getDate());
            });

            return data;
        }
    }

    /**
     * Validates the search response JSON for errors.
     *
     * @param json the JSON response object
     * @param responseBody the raw response body for error messages
     * @throws IOException if validation fails
     */
    private void validateSearchResponse(
            final JsonObject json,
            final String responseBody) throws IOException {
        if (json.has(JSON_FIELD_NOTE)) {
            throw new IOException(
                    "API note: " + json.get(JSON_FIELD_NOTE).getAsString());
        }
        if (json.has(JSON_FIELD_ERROR_MESSAGE)) {
            throw new IOException(
                    "API error: " + json.get(JSON_FIELD_ERROR_MESSAGE).getAsString());
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
    }

    /**
     * Parses search results from JSON array.
     *
     * @param matches the JSON array of matches
     * @return list of stock search results
     */
    private List<StockSearchResult> parseSearchResults(final JsonArray matches) {
        final List<StockSearchResult> results = new ArrayList<>();
        if (matches != null) {
            for (int i = 0;
                    i < matches.size() && i < MAX_SEARCH_RESULTS;
                    i++) {
                final JsonObject match = matches.get(i).getAsJsonObject();
                final String symbol = match.get("1. symbol").getAsString();
                final String name = match.get("2. name").getAsString();
                final String type = match.get("3. type").getAsString();
                final String region = match.get("4. region").getAsString();
                final String currency = match.get("8. currency").getAsString();

                final String exchange = extractExchange(region, symbol);
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

    /**
     * Extracts time series data from JSON object.
     *
     * @param timeSeries the time series JSON object
     * @return list of stock price data points
     */
    private List<StockPriceData> extractTimeSeriesData(
            final JsonObject timeSeries) {
        final List<StockPriceData> data = new ArrayList<>();
        for (Map.Entry<String, com.google.gson.JsonElement> entry
                : timeSeries.entrySet()) {
            final String date = entry.getKey();
            final JsonObject values = entry.getValue().getAsJsonObject();

            final double high =
                    Double.parseDouble(values.get("2. high").getAsString());
            final double low =
                    Double.parseDouble(values.get("3. low").getAsString());
            final double close =
                    Double.parseDouble(values.get("4. close").getAsString());

            data.add(new StockPriceData(date, close, high, low));
        }
        return data;
    }

    /**
     * Filters time series data by interval.
     *
     * @param data the list of stock price data to filter
     * @param interval the time interval
     */
    private void filterByInterval(
            final List<StockPriceData> data,
            final String interval) {
        // Filter by interval length (except for intraday 1D which uses
        // timestamps)
        if (!"1D".equals(interval)) {
            final LocalDate cutoff = calculateCutoffDate(interval);
            if (cutoff != null) {
                final LocalDate cutoffDate = cutoff;
                final DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern("yyyy-MM-dd");
                data.removeIf(pricePoint -> {
                    final LocalDate pointDate =
                            LocalDate.parse(pricePoint.getDate(), formatter);
                    return pointDate.isBefore(cutoffDate);
                });
            }
        }
    }

    /**
     * Calculates the cutoff date for the given interval.
     *
     * @param interval the time interval
     * @return the cutoff date or null if not applicable
     */
    private LocalDate calculateCutoffDate(final String interval) {
        final LocalDate today = LocalDate.now();
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
        return cutoff;
    }

    /**
     * Gets the time series key from the function name.
     *
     * @param function the function name
     * @return the time series key
     */
    private String getTimeSeriesKey(final String function) {
        String key = "Time Series (Daily)";
        if (function.contains("INTRADAY")) {
            key = "Time Series (5min)";
        }
        else if (function.contains("DAILY")) {
            key = "Time Series (Daily)";
        }
        else if (function.contains("WEEKLY")) {
            key = "Weekly Time Series";
        }
        else if (function.contains("MONTHLY")) {
            key = "Monthly Time Series";
        }
        return key;
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
        String exchange = region;
        if (region.contains("United States")) {
            if (symbol.contains(".")) {
                exchange = symbol.substring(symbol.indexOf(".") + 1);
            }
            else {
                // Default for US stocks
                exchange = "NASDAQ";
            }
        }
        return exchange;
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
