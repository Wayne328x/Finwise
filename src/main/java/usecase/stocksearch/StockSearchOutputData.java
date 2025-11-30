package usecase.stocksearch;

import data.AlphaVantageAPI.StockSearchResult;
import java.util.List;

/**
 * Output data for stock search operations.
 * Contains the search results and status information.
 */
public class StockSearchOutputData {
  /**
   * Indicates whether the search operation was successful.
   */
  private final boolean success;
  /**
   * Message describing the search result or error.
   */
  private final String message;
  /**
   * List of stock search results.
   */
  private final List<StockSearchResult> results;

  /**
   * Constructs a StockSearchOutputData object.
   *
   * @param successValue whether the search was successful
   * @param messageText status message
   * @param resultsList list of search results
   */
  public StockSearchOutputData(
      final boolean successValue,
      final String messageText,
      final List<StockSearchResult> resultsList) {
    this.success = successValue;
    this.message = messageText;
    this.results = resultsList;
  }

  /**
   * Returns whether the search operation was successful.
   *
   * @return true if successful, false otherwise
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Returns the status message.
   *
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Returns the list of search results.
   *
   * @return the results list
   */
  public List<StockSearchResult> getResults() {
    return results;
  }
}
