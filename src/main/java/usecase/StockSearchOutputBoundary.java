package usecase;

/**
 * Output boundary for stock search operations.
 * Implemented by presenters in the interface-adapters layer.
 */
public interface StockSearchOutputBoundary {

  /**
   * Presents the result of a stock search operation.
   *
   * @param output the output data produced by the use case
   */
  void present(StockSearchOutputData output);
}


