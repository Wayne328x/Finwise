package interface_adapters.controllers;
import use_case.trading.TradingInputBoundary;
import use_case.trading.TradingInputData;
import use_case.trading.TradingViewModel;

public class TradingController {

    private final TradingInputBoundary interactor;
    private final TradingViewModel viewModel;

    public TradingController(TradingInputBoundary interactor, TradingViewModel viewModel) {
        this.interactor = interactor;
        this.viewModel = viewModel;
    }

    public TradingViewModel placeOrder(String username, String symbol, int shares, TradingInputData.Action action) {
        TradingInputData inputData = new TradingInputData(username, symbol, action, shares);
        interactor.placeOrder(inputData);
        return viewModel;
    }

    public TradingViewModel getViewModel() {
       return viewModel;
    }
}
