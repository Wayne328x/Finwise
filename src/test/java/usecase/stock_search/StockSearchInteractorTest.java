package usecase.stock_search;

import data.stock.AlphaVantage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import usecase.stocksearch.StockSearchInputData;
import usecase.stocksearch.StockSearchInteractor;
import usecase.stocksearch.StockSearchOutputBoundary;
import usecase.stocksearch.StockSearchOutputData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockSearchInteractorTest {

    private MockAlphaVantageAPI mockApi;
    private MockOutputBoundary mockOutputBoundary;
    private StockSearchInteractor interactor;

    @BeforeEach
    void setUp() {
        mockApi = new MockAlphaVantageAPI();
        mockOutputBoundary = new MockOutputBoundary();
        interactor = new StockSearchInteractor(mockApi, mockOutputBoundary);
    }

    /**
     * Mock output boundary to verify that present() is called with correct data.
     */
    private static class MockOutputBoundary implements StockSearchOutputBoundary {
        private StockSearchOutputData lastPresentedOutput;
        private int presentCallCount = 0;

        @Override
        public void present(StockSearchOutputData output) {
            this.lastPresentedOutput = output;
            this.presentCallCount++;
        }

        public StockSearchOutputData getLastPresentedOutput() {
            return lastPresentedOutput;
        }

        public int getPresentCallCount() {
            return presentCallCount;
        }

        public void reset() {
            lastPresentedOutput = null;
            presentCallCount = 0;
        }
    }

    // Manual mock implementation to avoid Java 23 compatibility issues with Mockito
    private static class MockAlphaVantageAPI extends AlphaVantage {
        private List<StockSearchResult> searchResults;
        private IOException ioException;

        public MockAlphaVantageAPI() {
            super();
        }

        public void setSearchResults(List<StockSearchResult> results) {
            this.searchResults = results;
            this.ioException = null;
        }

        public void setIOException(IOException e) {
            this.ioException = e;
            this.searchResults = null;
        }

        @Override
        public List<StockSearchResult> searchStocks(String keywords) throws IOException {
            if (ioException != null) {
                throw ioException;
            }
            return searchResults != null ? searchResults : new ArrayList<>();
        }
    }

    @Test
    void testExecute_WithNullKeywords_ReturnsFailure() {
        StockSearchInputData input = new StockSearchInputData(null);
        mockOutputBoundary.reset();

        StockSearchOutputData output = interactor.execute(input);

        assertFalse(output.isSuccess());
        assertEquals("Search keywords cannot be empty", output.getMessage());
        assertTrue(output.getResults().isEmpty());
        
        assertEquals(1, mockOutputBoundary.getPresentCallCount());
        assertNotNull(mockOutputBoundary.getLastPresentedOutput());
        assertFalse(mockOutputBoundary.getLastPresentedOutput().isSuccess());
        assertEquals("Search keywords cannot be empty", 
            mockOutputBoundary.getLastPresentedOutput().getMessage());
    }

    @Test
    void testExecute_WithBlankKeywords_ReturnsFailure() {
        StockSearchInputData input = new StockSearchInputData("   ");
        mockOutputBoundary.reset();

        StockSearchOutputData output = interactor.execute(input);

        assertFalse(output.isSuccess());
        assertEquals("Search keywords cannot be empty", output.getMessage());
        assertTrue(output.getResults().isEmpty());
        
        assertEquals(1, mockOutputBoundary.getPresentCallCount());
        assertFalse(mockOutputBoundary.getLastPresentedOutput().isSuccess());
    }

    @Test
    void testExecute_WithEmptyKeywords_ReturnsFailure() {
        StockSearchInputData input = new StockSearchInputData("");
        mockOutputBoundary.reset();

        StockSearchOutputData output = interactor.execute(input);

        assertFalse(output.isSuccess());
        assertEquals("Search keywords cannot be empty", output.getMessage());
        assertTrue(output.getResults().isEmpty());
        
        assertEquals(1, mockOutputBoundary.getPresentCallCount());
        assertFalse(mockOutputBoundary.getLastPresentedOutput().isSuccess());
    }

    @Test
    void testExecute_WithValidKeywords_ReturnsSuccess() {
        String keywords = "AAPL";
        StockSearchInputData input = new StockSearchInputData(keywords);
        
        List<AlphaVantage.StockSearchResult> mockResults = new ArrayList<>();
        mockResults.add(new AlphaVantage.StockSearchResult(
            "AAPL", "Apple Inc.", "NASDAQ", "Equity", "United States", "USD"
        ));
        
        mockApi.setSearchResults(mockResults);
        mockOutputBoundary.reset();

        StockSearchOutputData output = interactor.execute(input);

        assertTrue(output.isSuccess());
        assertEquals("Search completed", output.getMessage());
        assertEquals(1, output.getResults().size());
        assertEquals("AAPL", output.getResults().get(0).getSymbol());
        
        assertEquals(1, mockOutputBoundary.getPresentCallCount());
        assertNotNull(mockOutputBoundary.getLastPresentedOutput());
        assertTrue(mockOutputBoundary.getLastPresentedOutput().isSuccess());
        assertEquals("Search completed", 
            mockOutputBoundary.getLastPresentedOutput().getMessage());
        assertEquals(1, mockOutputBoundary.getLastPresentedOutput().getResults().size());
        assertEquals("AAPL", 
            mockOutputBoundary.getLastPresentedOutput().getResults().get(0).getSymbol());
    }

    @Test
    void testExecute_WithEmptyResults_ReturnsFailure() {
        String keywords = "INVALID";
        StockSearchInputData input = new StockSearchInputData(keywords);
        
        mockApi.setSearchResults(new ArrayList<>());
        mockOutputBoundary.reset();

        StockSearchOutputData output = interactor.execute(input);

        assertFalse(output.isSuccess());
        assertEquals("No results for \"" + keywords + "\"", output.getMessage());
        assertTrue(output.getResults().isEmpty());
        
        assertEquals(1, mockOutputBoundary.getPresentCallCount());
        assertFalse(mockOutputBoundary.getLastPresentedOutput().isSuccess());
        assertEquals("No results for \"" + keywords + "\"", 
            mockOutputBoundary.getLastPresentedOutput().getMessage());
    }

    @Test
    void testExecute_WithIOException_ReturnsFailure() {
        String keywords = "AAPL";
        StockSearchInputData input = new StockSearchInputData(keywords);
        IOException ioException = new IOException("Network connection failed");
        
        mockApi.setIOException(ioException);
        mockOutputBoundary.reset();

        StockSearchOutputData output = interactor.execute(input);

        assertFalse(output.isSuccess());
        assertEquals("Network error: " + ioException.getMessage(), output.getMessage());
        assertTrue(output.getResults().isEmpty());
        
        assertEquals(1, mockOutputBoundary.getPresentCallCount());
        assertFalse(mockOutputBoundary.getLastPresentedOutput().isSuccess());
        assertEquals("Network error: " + ioException.getMessage(), 
            mockOutputBoundary.getLastPresentedOutput().getMessage());
    }


    @Test
    void testExecute_WithMultipleResults_ReturnsSuccess() {
        String keywords = "Apple";
        StockSearchInputData input = new StockSearchInputData(keywords);
        
        List<AlphaVantage.StockSearchResult> mockResults = new ArrayList<>();
        mockResults.add(new AlphaVantage.StockSearchResult(
            "AAPL", "Apple Inc.", "NASDAQ", "Equity", "United States", "USD"
        ));
        mockResults.add(new AlphaVantage.StockSearchResult(
            "APLE", "Apple Hospitality REIT", "NYSE", "Equity", "United States", "USD"
        ));
        
        mockApi.setSearchResults(mockResults);
        mockOutputBoundary.reset();

        StockSearchOutputData output = interactor.execute(input);

        assertTrue(output.isSuccess());
        assertEquals("Search completed", output.getMessage());
        assertEquals(2, output.getResults().size());
        
        assertEquals(1, mockOutputBoundary.getPresentCallCount());
        assertTrue(mockOutputBoundary.getLastPresentedOutput().isSuccess());
        assertEquals(2, mockOutputBoundary.getLastPresentedOutput().getResults().size());
    }

    @Test
    void testExecute_WithNullOutputBoundary_DoesNotThrowException() {
        StockSearchInteractor interactorWithNullBoundary = 
            new StockSearchInteractor(mockApi, null);
        String keywords = "AAPL";
        StockSearchInputData input = new StockSearchInputData(keywords);
        
        List<AlphaVantage.StockSearchResult> mockResults = new ArrayList<>();
        mockResults.add(new AlphaVantage.StockSearchResult(
            "AAPL", "Apple Inc.", "NASDAQ", "Equity", "United States", "USD"
        ));
        mockApi.setSearchResults(mockResults);

        StockSearchOutputData output = interactorWithNullBoundary.execute(input);

        assertTrue(output.isSuccess());
        assertEquals("Search completed", output.getMessage());
        assertEquals(1, output.getResults().size());
    }
}
