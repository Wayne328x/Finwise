package usecase.portfolio;

/**
 * Input boundary (use case interface) for Portfolio Analysis.
 */
public interface PortfolioInputBoundary {

    /**
     * Execute the portfolio analysis use case.
     * @param inputData
     */
    void execute(PortfolioInputData inputData);
}

