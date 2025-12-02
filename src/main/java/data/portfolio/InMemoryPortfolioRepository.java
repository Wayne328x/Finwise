package data.portfolio;

import entity.Holding;

import java.util.*;

/**
 * Simple in-memory implementation of PortfolioRepository.
 * This is only for demo or test.
 * It can be replaced with a JDBC or API-based implementation later.
 */
public class InMemoryPortfolioRepository implements PortfolioRepository {

    // Map: username -> list of holdings
    private final Map<String, List<Holding>> store = new HashMap<>();

    public InMemoryPortfolioRepository() {
        // Example data: can change symbols / shares / costs as like
        store.put("hhc", List.of(
                new Holding("AAPL", 10, 150.0),
                new Holding("GOOG", 5, 120.0),
                new Holding("TSLA", 8, 100.0)
        ));

        store.put("alice", List.of(
                new Holding("TSLA", 8, 200.0)
        ));
        // users with no entry in the map will be treated as "no investments"
    }

    @Override
    public List<Holding> findHoldingsByUser(String username) {
        return store.getOrDefault(username, Collections.emptyList());
    }
}

