package data.portfolio;

import entity.PricePoint;

import java.time.LocalDate;
import java.util.*;

/**
 * Simple in-memory implementation of PriceHistoryRepository.
 * Provides hard-coded historical prices for a few example symbols.
 * This is only for demo or test.
 * It can be replaced with a JDBC or API-based implementation later.
 */
public class InMemoryPriceHistoryRepository implements PriceHistoryRepository {

    // symbol -> list of price points (oldest → newest)
    private final Map<String, List<PricePoint>> prices = new HashMap<>();

    public InMemoryPriceHistoryRepository() {
        LocalDate today = LocalDate.now();

        prices.put("AAPL", List.of(
                new PricePoint(today.minusDays(4), 145.0),
                new PricePoint(today.minusDays(3), 148.0),
                new PricePoint(today.minusDays(2), 150.0),
                new PricePoint(today.minusDays(1), 152.0),
                new PricePoint(today,            155.0)
        ));

        prices.put("GOOG", List.of(
                new PricePoint(today.minusDays(4), 110.0),
                new PricePoint(today.minusDays(3), 112.0),
                new PricePoint(today.minusDays(2), 115.0),
                new PricePoint(today.minusDays(1), 117.0),
                new PricePoint(today,            120.0)
        ));

        prices.put("TSLA", List.of(
                new PricePoint(today.minusDays(4), 190.0),
                new PricePoint(today.minusDays(3), 192.0),
                new PricePoint(today.minusDays(2), 195.0),
                new PricePoint(today.minusDays(1), 198.0),
                new PricePoint(today,            200.0)
        ));
    }

    @Override
    public List<PricePoint> getPriceHistory(String symbol) {
        // If symbol not found, return empty list
        return prices.getOrDefault(symbol, Collections.emptyList());
    }
}
