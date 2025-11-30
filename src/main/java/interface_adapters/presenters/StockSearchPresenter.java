package interface_adapters.presenters;

import usecase.stocksearch.StockSearchOutputBoundary;
import usecase.stocksearch.StockSearchOutputData;

/**
 * Presenter for stock search results.
 * Currently stores the latest output; the view can pull it if needed.
 */
public class StockSearchPresenter implements StockSearchOutputBoundary {

  private StockSearchOutputData lastOutput;

  @Override
  public void present(final StockSearchOutputData output) {
    this.lastOutput = output;
  }

  /**
   * Returns the last output presented by this presenter.
   *
   * @return the last StockSearchOutputData or null if none presented yet
   */
  public StockSearchOutputData getLastOutput() {
    return lastOutput;
  }
}


