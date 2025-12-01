package usecase.stocksearch;

/**
 * Input boundary for stock search operations.
 * Defines the interface through which the controller invokes the use case.
 */
public interface StockSearchInputBoundary {

    /**
   * Executes a stock search operation based on the provided input.
   *
   * @param input the search input containing keywords
   * @return output data containing search results or error information
   */
    StockSearchOutputData execute(StockSearchInputData input);
}
