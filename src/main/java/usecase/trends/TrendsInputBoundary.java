package usecase.trends;

/**
 * Input boundary (use case interface) for Trends use case.
 */
public interface TrendsInputBoundary {

    /**
     * Execute the Trends use case.
     * @param inputData
     */
    void execute(TrendsInputData inputData);
}
