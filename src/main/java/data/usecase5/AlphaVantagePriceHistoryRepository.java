package data.usecase5;

import data.AlphaVantageAPI;
import entity.PricePoint;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A real implementation of PriceHistoryRepository using AlphaVantageAPI.
 */
public class AlphaVantagePriceHistoryRepository implements PriceHistoryRepository {

    private final AlphaVantageAPI api;

    public AlphaVantagePriceHistoryRepository() {
        this.api = new AlphaVantageAPI();
    }

    @Override
    public List<PricePoint> getPriceHistory(String symbol) {
        try {
            List<AlphaVantageAPI.StockPriceData> series = api.getTimeSeries(symbol, "1Y");

            List<PricePoint> result = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (AlphaVantageAPI.StockPriceData point : series) {
                // Only use YYYY-MM-DD format dates
                String dateStr = point.getDate();
                LocalDate date;
                try {
                    date = LocalDate.parse(dateStr, formatter);
                } catch (Exception e) {
                    // Skip malformed date (e.g., intraday format)
                    continue;
                }
                result.add(new PricePoint(date, point.getClose()));
            }

            return result;
        } catch (IOException e) {
            System.err.println("Failed to fetch price history for " + symbol + ": " + e.getMessage());
            return List.of();    // fallback to empty list
        }
    }
}

