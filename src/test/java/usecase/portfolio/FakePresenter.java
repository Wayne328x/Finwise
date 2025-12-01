package usecase.portfolio;

public class FakePresenter implements PortfolioOutputBoundary {
    PortfolioOutputData received;

    @Override
    public void present(PortfolioOutputData outputData) {
        this.received = outputData;
    }
}