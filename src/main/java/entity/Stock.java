package entity;

/**
 * Represents a stock with its basic information and price data.
 */
public final class Stock {

  /**
   * The stock symbol.
   */
  private final String symbol;

  /**
   * The stock name.
   */
  private final String name;

  /**
   * The stock exchange.
   */
  private final String exchange;

  /**
   * The current stock price.
   */
  private final double price;

  /**
   * The price change as a percentage.
   */
  private final double changePercent;

  /**
   * The price change in dollars.
   */
  private final double changeDollar;

  /**
   * Constructs a Stock with the given information.
   *
   * @param symbolValue the stock symbol
   * @param nameValue the stock name
   * @param exchangeValue the stock exchange
   * @param priceValue the current stock price
   * @param changeDollarValue the price change in dollars
   * @param changePercentValue the price change as a percentage
   */
  public Stock(
      final String symbolValue,
      final String nameValue,
      final String exchangeValue,
      final double priceValue,
      final double changeDollarValue,
      final double changePercentValue) {
    this.symbol = symbolValue;
    this.name = nameValue;
    this.exchange = exchangeValue;
    this.price = priceValue;
    this.changeDollar = changeDollarValue;
    this.changePercent = changePercentValue;
  }

  /**
   * Gets the stock symbol.
   *
   * @return the stock symbol
   */
  public String getSymbol() {
    return symbol;
  }

  /**
   * Gets the stock name.
   *
   * @return the stock name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the stock exchange.
   *
   * @return the stock exchange
   */
  public String getExchange() {
    return exchange;
  }

  /**
   * Gets the current stock price.
   *
   * @return the stock price
   */
  public double getPrice() {
    return price;
  }

  /**
   * Gets the price change as a percentage.
   *
   * @return the price change percentage
   */
  public double getChangePercent() {
    return changePercent;
  }

  /**
   * Gets the price change in dollars.
   *
   * @return the price change in dollars
   */
  public double getChangeDollar() {
    return changeDollar;
  }
}
