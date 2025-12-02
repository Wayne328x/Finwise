package usecase.trends;

/**
 * Input boundary (use case interface) for Trends use case.
 */
public interface TrendsInputBoundary {

    /**
     * Execute the Trends use case.
     * @param inputData the input data (e.g. username, start date, end date)
     */
    void execute(TrendsInputData inputData);
}
