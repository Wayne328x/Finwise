package use_case.stocksearch;

/**
 * Input data for stock search operations.
 * Contains the search keywords provided by the user.
 */
public final class StockSearchInputData {
  /**
   * The search keywords.
   */
  private final String keywords;

  /**
   * Constructs a StockSearchInputData object with the given keywords.
   *
   * @param keywordsText the search keywords
   */
  public StockSearchInputData(final String keywordsText) {
    this.keywords = keywordsText;
  }

  /**
   * Returns the search keywords.
   *
   * @return the keywords
   */
  public String getKeywords() {
    return keywords;
  }
}
