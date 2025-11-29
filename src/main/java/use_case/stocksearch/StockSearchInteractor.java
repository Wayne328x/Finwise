package use_case.stocksearch;

import data.AlphaVantageAPI;
import data.AlphaVantageAPI.StockSearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactor for stock search operations.
 * Handles the business logic for searching stocks using the AlphaVantage API.
 */
public class StockSearchInteractor {
  /**
   * The API client for fetching stock data.
   */
  private final AlphaVantageAPI api;

  /**
   * Constructs a StockSearchInteractor with the given API client.
   *
   * @param apiClient the AlphaVantage API client
   */
  public StockSearchInteractor(final AlphaVantageAPI apiClient) {
    this.api = apiClient;
  }

  /**
   * Executes a stock search operation based on the provided input.
   *
   * @param input the search input containing keywords
   * @return output data containing search results or error information
   */
  public StockSearchOutputData execute(final StockSearchInputData input) {
    String keywords = input.getKeywords();

    if (keywords == null || keywords.isBlank()) {
      return new StockSearchOutputData(
          false,
          "Search keywords cannot be empty",
          new ArrayList<>());
    }

    try {
      List<StockSearchResult> results = api.searchStocks(keywords);
      if (results.isEmpty()) {
        return new StockSearchOutputData(
            false,
            "No results for \"" + keywords + "\"",
            new ArrayList<>());
      }
      return new StockSearchOutputData(true, "Search completed", results);
    } catch (IOException e) {
      return new StockSearchOutputData(
          false,
          "Network error: " + e.getMessage(),
          new ArrayList<>());
    } catch (Exception e) {
      return new StockSearchOutputData(
          false,
          "Error: " + e.getMessage(),
          new ArrayList<>());
    }
  }
}
