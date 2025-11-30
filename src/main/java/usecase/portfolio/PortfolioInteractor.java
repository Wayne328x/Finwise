package usecase.portfolio;

import data.usecase5.PortfolioRepository;
import data.usecase5.PriceHistoryRepository;

import entity.Holding;
import entity.PortfolioSnapshot;
import entity.PricePoint;

import java.time.LocalDate;
import java.util.*;

/**
 * Interactor for the Portfolio Performance Diagnostics Use Case (Use Case 5).
 */
public class PortfolioInteractor implements PortfolioInputBoundary {

    private final PortfolioRepository portfolioRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final PortfolioOutputBoundary outputBoundary;

    public PortfolioInteractor(
            PortfolioRepository portfolioRepository,
            PriceHistoryRepository priceHistoryRepository,
            PortfolioOutputBoundary outputBoundary) {
        this.portfolioRepository = portfolioRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(PortfolioInputData input) {
        String username = input.getUsername();

        // Load holdings
        List<Holding> holdings = portfolioRepository.findHoldingsByUser(username);

        if (holdings.isEmpty()) {
            outputBoundary.present(new PortfolioOutputData(
                    Collections.emptyList(),
                    Collections.emptyList(),
                    false,
                    "No holdings found for user: " + username
            ));
            return;
        }

        //  Load all historical price data once (cache)
        Map<String, List<PricePoint>> historyCache = new HashMap<>();
        for (Holding h : holdings) {
            List<PricePoint> history = priceHistoryRepository.getPriceHistory(h.getSymbol());

            if (history.isEmpty()) {
                outputBoundary.present(new PortfolioOutputData(
                        Collections.emptyList(),
                        holdings,
                        false,
                        "Missing historical prices for: " + h.getSymbol()
                ));
                return;
            }

            // Ensure sorted ascending by date
            history.sort(Comparator.comparing(PricePoint::getDate));
            historyCache.put(h.getSymbol(), history);
        }

        // Use the first holding as base timeline
        String baseSymbol = holdings.get(0).getSymbol();
        List<PricePoint> baseHistory = historyCache.get(baseSymbol);

        List<PortfolioSnapshot> snapshots = new ArrayList<>();

        // For each date, compute totalCost & totalValue
        for (PricePoint basePoint : baseHistory) {
            LocalDate date = basePoint.getDate();

            double totalCost = 0.0;
            double totalValue = 0.0;

            for (Holding h : holdings) {
                totalCost += h.getTotalCost();

                List<PricePoint> history = historyCache.get(h.getSymbol());
                double priceOnDate = findPriceOnOrBefore(history, date);

                totalValue += h.getShares() * priceOnDate;
            }

            PortfolioSnapshot snapshot =
                    PortfolioSnapshot.fromCostAndValue(date, totalCost, totalValue);
            snapshots.add(snapshot);
        }

        // Return result
        outputBoundary.present(new PortfolioOutputData(
                snapshots,
                holdings,
                true,
                "Portfolio analysis completed successfully."
        ));
    }

    /**
     * Returns price at the latest date ≤ targetDate.
     * If all dates are after targetDate, return the earliest available.
     */
    private double findPriceOnOrBefore(List<PricePoint> history, LocalDate targetDate) {
        PricePoint lastBefore = null;

        for (PricePoint p : history) {
            if (!p.getDate().isAfter(targetDate)) {
                lastBefore = p;
            } else {
                break;
            }
        }

        // If we never saw a date ≤ targetDate, use the earliest
        if (lastBefore == null) {
            return history.get(0).getPrice();
        }
        return lastBefore.getPrice();
    }
}


