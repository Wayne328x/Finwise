package interface_adapters.presenters;

import entity.Holding;
import entity.PortfolioSnapshot;
import ui.portfolio.HoldingRow;
import ui.portfolio.PortfolioViewModel;
import ui.portfolio.SnapshotRow;
import use_case.portfolio.PortfolioOutputBoundary;
import use_case.portfolio.PortfolioOutputData;

import java.util.List;

/**
 * Presenter for Use Case 5.
 */
public class PortfolioPresenter implements PortfolioOutputBoundary {

    private final PortfolioViewModel viewModel;

    public PortfolioPresenter(PortfolioViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(PortfolioOutputData outputData) {
        viewModel.setHasData(outputData.hasData());
        viewModel.setMessage(outputData.getMessage());

        viewModel.getHoldings().clear();
        List<Holding> holdings = outputData.getHoldings();
        for (Holding h : holdings) {
            viewModel.getHoldings().add(
                    new HoldingRow(
                            h.getSymbol(),
                            h.getShares(),
                            h.getAvgCost(),
                            h.getTotalCost()
                    )
            );
        }

        viewModel.getSnapshots().clear();
        List<PortfolioSnapshot> snapshots = outputData.getSnapshots();
        for (PortfolioSnapshot s : snapshots) {
            viewModel.getSnapshots().add(
                    new SnapshotRow(
                            s.getDate(),
                            s.getTotalCost(),
                            s.getTotalValue(),
                            s.getProfit(),
                            s.getProfitRate()
                    )
            );
        }
    }

    public PortfolioViewModel getViewModel() {
        return viewModel;
    }
}
