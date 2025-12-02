package interfaceadapters.stocksearch;

import java.util.List;

import data.stock.WatchlistRepository;
import usecase.stocksearch.StockSearchInputBoundary;
import usecase.stocksearch.StockSearchInputData;
import usecase.stocksearch.StockSearchOutputData;

/**
 * Controller for stock search and watchlist actions.
 * Delegates search to the StockSearchInteractor and watch/unwatch
 * operations to the WatchlistRepository.
 */
public class StockSearchController {

    /**
     * The input boundary for stock search operations.
     */
    private final StockSearchInputBoundary stockSearchInteractor;

    /**
     * The repository for watchlist operations.
     */
    private final WatchlistRepository watchlistRepository;

    /**
     * Constructs a StockSearchController with the given dependencies.
     *
     * @param interactor the stock search input boundary
     * @param repository the watchlist repository
     */
    public StockSearchController(
            final StockSearchInputBoundary interactor,
            final WatchlistRepository repository) {
        this.stockSearchInteractor = interactor;
        this.watchlistRepository = repository;
    }

    /**
     * Performs a stock search based on the given keywords.
     *
     * @param keywords the search keywords
     * @return the search results
     */
    public StockSearchOutputData search(final String keywords) {
        final StockSearchInputData input = new StockSearchInputData(keywords);
        return stockSearchInteractor.execute(input);
    }

    /**
     * Returns true if the given symbol is in the user's watchlist.
     *
     * @param username the username
     * @param symbol the stock symbol
     * @return true if the symbol is watched, false otherwise
     */
    public boolean isWatched(final String username, final String symbol) {
        return watchlistRepository.isWatched(username, symbol);
    }

    /**
     * Adds or removes a stock from the user's watchlist.
     *
     * @param username the username
     * @param symbol the stock symbol
     * @param name the stock name
     * @param exchange the stock exchange
     * @param watched true to add to watchlist, false to remove
     */
    public void setWatched(
            final String username,
            final String symbol,
            final String name,
            final String exchange,
            final boolean watched) {
        if (watched) {
            watchlistRepository.addWatched(username, symbol, name, exchange);
        }
        else {
            watchlistRepository.removeWatched(username, symbol);
        }
    }

    /**
     * Gets all watched stock symbols for a user.
     *
     * @param username the username
     * @return list of watched stock symbols
     */
    public List<String> getWatchedSymbols(final String username) {
        return watchlistRepository.findSymbolsByUsername(username);
    }
}
