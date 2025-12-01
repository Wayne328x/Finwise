package usecase.portfolio;

import entity.Holding;
import entity.PortfolioSnapshot;

import java.util.List;

/**
 * Output data for the Portfolio Analysis use case.
 * The View layer will use this data to render tables, charts, and messages.
 */
public class PortfolioOutputData {

    private final List<PortfolioSnapshot> snapshots;
    private final List<Holding> holdings;
    private final boolean hasData;
    private final String message;

    /**
     * @param snapshots time-series snapshots of the portfolio performance
     * @param holdings  the current holdings of the user
     * @param hasData   indicates whether there is meaningful data to display
     * @param message   message for the user (e.g., "No data available", or success info)
     */
    public PortfolioOutputData(List<PortfolioSnapshot> snapshots,
                               List<Holding> holdings,
                               boolean hasData,
                               String message) {
        this.snapshots = snapshots;
        this.holdings = holdings;
        this.hasData = hasData;
        this.message = message;
    }

    public List<PortfolioSnapshot> getSnapshots() {
        return snapshots;
    }

    public List<Holding> getHoldings() {
        return holdings;
    }

    public boolean hasData() {
        return hasData;
    }

    public String getMessage() {
        return message;
    }
}