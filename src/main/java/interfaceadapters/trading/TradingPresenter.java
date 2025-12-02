package interfaceadapters.trading;
import usecase.trading.TradingOutputBoundary;
import usecase.trading.TradingOutputData;
import usecase.trading.TradingViewModel;

public class TradingPresenter implements TradingOutputBoundary {
    private final TradingViewModel viewModel;

    public TradingPresenter(TradingViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentTradeResult(TradingOutputData output) {
        viewModel.setMessage(output.getMessage());
        viewModel.setCashAfterTrade(output.getCashAfterTrade());
        viewModel.setAverageCostAfterTrade(output.getAverageCostAfterTrade());
        viewModel.setTotalSharesAfterTrade(output.getTotalSharesAfterTrade());
        viewModel.setTotalHoldingValueAfterTrade(output.getTotalHoldingValueAfterTrade());
    }

    //public TradingViewModel getViewModel() {
    //    return viewModel;
    //}

}
