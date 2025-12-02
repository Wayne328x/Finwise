package usecase.fetch_news;

public interface FetchNewsInputBoundary {
    /**
     * The input boundary interface.
     * @param inputData for future extension, e.g. search news by keywords or filter by category
     */
    void execute(FetchNewsInputData inputData);
}
