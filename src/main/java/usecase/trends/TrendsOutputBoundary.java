package usecase.trends;

/**
 * Output boundary (use case interface) for Trends use case.
 */
public interface TrendsOutputBoundary {

    /**
     * Present the result of the Trends use case.
     *
     * @param outputData the output data
     */
    void present(TrendsOutputData outputData);
}
