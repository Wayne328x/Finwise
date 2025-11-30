package usecase.portfolio;

/**
 * Output boundary for the Portfolio Analysis use case.
 */
public interface PortfolioOutputBoundary {

    /**
     * Present the result of the portfolio analysis.
     * @param outputData the data to be presented to the user
     */
    void present(PortfolioOutputData outputData);
}

