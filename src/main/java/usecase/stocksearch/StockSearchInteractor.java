package usecase.stocksearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import data.AlphaVantage;
import data.AlphaVantage.StockSearchResult;

/**
 * Interactor for stock search operations.
 * Handles the business logic for searching stocks using the AlphaVantage API.
 */
public final class StockSearchInteractor implements StockSearchInputBoundary {
    /**
     * The API client for fetching stock data.
     */
    private final AlphaVantage api;

    /**
     * The output boundary for presenting search results.
     */
    private final StockSearchOutputBoundary outputBoundary;

    /**
     * Constructs a StockSearchInteractor with the given API client and output boundary.
     *
     * @param apiClient the AlphaVantage API client
     * @param outputBoundaryParam the output boundary for presenting results
     */
    public StockSearchInteractor(final AlphaVantage apiClient,
            final StockSearchOutputBoundary outputBoundaryParam) {
        this.api = apiClient;
        this.outputBoundary = outputBoundaryParam;
    }

    /**
     * Executes a stock search operation based on the provided input.
     *
     * @param input the search input containing keywords
     * @return output data containing search results or error information
     */
    @Override
    public StockSearchOutputData execute(final StockSearchInputData input) {
        final String keywords = input.getKeywords();

        StockSearchOutputData result;

        if (keywords == null || keywords.isBlank()) {
            result = new StockSearchOutputData(
                    false,
                    "Search keywords cannot be empty",
                    new ArrayList<>()
            );
        }
        else {
            try {
                final List<StockSearchResult> results =
                        api.searchStocks(keywords);
                if (results.isEmpty()) {
                    result = new StockSearchOutputData(
                            false,
                            "No results for \"" + keywords + "\"",
                            new ArrayList<>()
                    );
                }
                else {
                    result = new StockSearchOutputData(
                            true, "Search completed", results);
                }
            }
            catch (IOException ioException) {
                result = new StockSearchOutputData(
                        false,
                        "Network error: " + ioException.getMessage(),
                        new ArrayList<>()
                );
            }
            catch (RuntimeException runtimeException) {

                result = new StockSearchOutputData(
                        false,
                        "Error: " + runtimeException.getMessage(),
                        new ArrayList<>()
                );
            }
        }

        if (outputBoundary != null) {
            outputBoundary.present(result);
        }

        return result;
    }
}
