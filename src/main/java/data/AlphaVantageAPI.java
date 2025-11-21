package data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AlphaVantageAPI {
    private static final String API_KEY = "9P17BCLZ42787NSY";
    // alternative : JTSZQWNFASUDRTW5 (hhc)
    // daily limit : 25
    private static final String BASE_URL = "https://www.alphavantage.co/query";
    private final OkHttpClient client;

    public AlphaVantageAPI() {
        this.client = new OkHttpClient();
    }

    /**
     * Search for stocks using SYMBOL_SEARCH endpoint
     */
    public List<StockSearchResult> searchStocks(String keywords) throws IOException {
        String encodedKeywords = java.net.URLEncoder.encode(keywords, java.nio.charset.StandardCharsets.UTF_8);
        String url = BASE_URL + "?function=SYMBOL_SEARCH&keywords=" + encodedKeywords + "&apikey=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected HTTP code " + response.code() + " when calling: " + url);
            }

            String responseBody = response.body().string();
            System.out.println("DEBUG searchStocks response = " + responseBody);

            JsonObject json;
            try {
                json = JsonParser.parseString(responseBody).getAsJsonObject();
            } catch (Exception e) {
                throw new IOException("Failed to parse JSON from AlphaVantage: " + responseBody, e);
            }

            if (json.has("Note")) {
                throw new IOException("API note: " + json.get("Note").getAsString());
            }
            if (json.has("Error Message")) {
                throw new IOException("API error: " + json.get("Error Message").getAsString());
            }
            if (json.has("Information")) {
                throw new IOException("API info: " + json.get("Information").getAsString());
            }
            if (!json.has("bestMatches")) {
                throw new IOException("Unexpected API response: no 'bestMatches' field. Body = " + responseBody);
            }

            JsonArray matches = json.getAsJsonArray("bestMatches");
            List<StockSearchResult> results = new ArrayList<>();

            if (matches != null) {
                for (int i = 0; i < matches.size() && i < 10; i++) {
                    JsonObject match = matches.get(i).getAsJsonObject();
                    String symbol = match.get("1. symbol").getAsString();
                    String name = match.get("2. name").getAsString();
                    String type = match.get("3. type").getAsString();
                    String region = match.get("4. region").getAsString();
                    String currency = match.get("8. currency").getAsString();

                    String exchange = extractExchange(region, symbol);
                    results.add(new StockSearchResult(symbol, name, exchange, type, region, currency));
                }
            }
            return results;
        }
    }

    /**
     * Get current stock quote using GLOBAL_QUOTE endpoint
     */
    public StockQuote getQuote(String symbol) throws IOException {
        String url = BASE_URL + "?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

            if (json.has("Note")) {
                throw new IOException("API call frequency limit reached. " + json.get("Note").getAsString());
            }

            if (json.has("Error Message")) {
                throw new IOException("API Error: " + json.get("Error Message").getAsString());
            }

            JsonObject quote = json.getAsJsonObject("Global Quote");
            if (quote == null || quote.size() == 0) {
                throw new IOException("No quote data found for symbol: " + symbol);
            }

            String sym = quote.get("01. symbol").getAsString();
            double price = Double.parseDouble(quote.get("05. price").getAsString());
            double change = Double.parseDouble(quote.get("09. change").getAsString());
            double changePercent = Double.parseDouble(quote.get("10. change percent").getAsString().replace("%", ""));

            return new StockQuote(sym, price, change, changePercent);
        }
    }

    /**
     * Get time series data for chart
     */
    public List<StockPriceData> getTimeSeries(String symbol, String interval) throws IOException {
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

        String url = BASE_URL + "?function=" + function + "&symbol=" + symbol + "&apikey=" + API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

            if (json.has("Note")) {
                throw new IOException("API call frequency limit reached. " + json.get("Note").getAsString());
            }

            if (json.has("Error Message")) {
                throw new IOException("API Error: " + json.get("Error Message").getAsString());
            }

            // Extract time series data based on function
            String timeSeriesKey = getTimeSeriesKey(function);
            JsonObject timeSeries = json.getAsJsonObject(timeSeriesKey);

            if (timeSeries == null) {
                throw new IOException("No time series data found for symbol: " + symbol);
            }

            List<StockPriceData> data = new ArrayList<>();
            for (Map.Entry<String, com.google.gson.JsonElement> entry : timeSeries.entrySet()) {
                String date = entry.getKey();
                JsonObject values = entry.getValue().getAsJsonObject();

                double open  = Double.parseDouble(values.get("1. open").getAsString());
                double high  = Double.parseDouble(values.get("2. high").getAsString());
                double low   = Double.parseDouble(values.get("3. low").getAsString());
                double close = Double.parseDouble(values.get("4. close").getAsString());

                data.add(new StockPriceData(date, close, high, low));
            }

            // Filter by interval length (except for intraday 1D which uses timestamps)
            if (!"1D".equals(interval)) {
                LocalDate today = LocalDate.now();
                LocalDate cutoff = null;

                switch (interval) {
                    case "5D":
                        // 5 trading days ≈ last 7 calendar days
                        cutoff = today.minusDays(7);
                        break;
                    case "1M":
                        cutoff = today.minusMonths(1);
                        break;
                    case "6M":
                        cutoff = today.minusMonths(6);
                        break;
                    case "1Y":
                        cutoff = today.minusYears(1);
                        break;
                    case "5Y":
                        cutoff = today.minusYears(5);
                        break;
                    default:
                        break;
                }

                if (cutoff != null) {
                    final LocalDate cutoffDate = cutoff;
                    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
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

    private String getTimeSeriesKey(String function) {
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

    private String extractExchange(String region, String symbol) {
        // Simple extraction - can be improved
        if (region.contains("United States")) {
            if (symbol.contains(".")) {
                return symbol.substring(symbol.indexOf(".") + 1);
            }
            return "NASDAQ";  // Default for US stocks
        }
        return region;
    }

    // Inner classes for API responses
    public static class StockSearchResult {
        private final String symbol;
        private final String name;
        private final String exchange;
        private final String type;
        private final String region;
        private final String currency;

        public StockSearchResult(String symbol, String name, String exchange,
                                 String type, String region, String currency) {
            this.symbol = symbol;
            this.name = name;
            this.exchange = exchange;
            this.type = type;
            this.region = region;
            this.currency = currency;
        }

        public String getSymbol() { return symbol; }
        public String getName() { return name; }
        public String getExchange() { return exchange; }
        public String getType() { return type; }
        public String getRegion() { return region; }
        public String getCurrency() { return currency; }
    }

    public static class StockQuote {
        private final String symbol;
        private final double price;
        private final double change;
        private final double changePercent;

        public StockQuote(String symbol, double price, double change, double changePercent) {
            this.symbol = symbol;
            this.price = price;
            this.change = change;
            this.changePercent = changePercent;
        }

        public String getSymbol() { return symbol; }
        public double getPrice() { return price; }
        public double getChange() { return change; }
        public double getChangePercent() { return changePercent; }
    }

    public static class StockPriceData {
        private final String date;
        private final double close;
        private final double high;
        private final double low;

        /**
         * Full constructor using close, high, and low.
         */
        public StockPriceData(String date, double close, double high, double low) {
            this.date = date;
            this.close = close;
            this.high = high;
            this.low = low;
        }

        /**
         * Backward-compatible constructor that only specifies close.
         * High/low default to the close price.
         */
        public StockPriceData(String date, double close) {
            this(date, close, close, close);
        }

        public String getDate() { return date; }

        /** Close price (used as the plotted value in the line chart). */
        public double getPrice() { return close; }

        public double getClose() { return close; }
        public double getHigh() { return high; }
        public double getLow()  { return low; }
    }
}
