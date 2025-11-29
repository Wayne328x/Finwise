package ui.portfolio;

import java.util.ArrayList;
import java.util.List;

public class PortfolioViewModel {

    private String username;
    private final List<HoldingRow> holdings = new ArrayList<>();
    private final List<SnapshotRow> snapshots = new ArrayList<>();
    private String message;
    private boolean hasData;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<HoldingRow> getHoldings() {
        return holdings;
    }

    public List<SnapshotRow> getSnapshots() {
        return snapshots;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean hasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }
}
