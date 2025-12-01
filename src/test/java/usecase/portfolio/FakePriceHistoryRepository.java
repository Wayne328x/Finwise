package usecase.portfolio;

import data.usecase5.PriceHistoryRepository;
import entity.PricePoint;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakePriceHistoryRepository implements PriceHistoryRepository {
    private Map<String, List<PricePoint>> history = new HashMap<>();

    public void setHistory(String symbol, List<PricePoint> data) {
        history.put(symbol, data);
    }

    @Override
    public List<PricePoint> getPriceHistory(String symbol) {
        return history.getOrDefault(symbol, Collections.emptyList());
    }
}