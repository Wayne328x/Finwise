package usecase.stock_search;

import data.AlphaVantage;
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
        private RuntimeException runtimeException;

        public MockAlphaVantageAPI() {
            super();
        }

        public void setSearchResults(List<StockSearchResult> results) {
            this.searchResults = results;
            this.ioException = null;
            this.runtimeException = null;
        }

        public void setIOException(IOException e) {
            this.ioException = e;
            this.searchResults = null;
            this.runtimeException = null;
        }

        public void setRuntimeException(RuntimeException e) {
            this.runtimeException = e;
            this.searchResults = null;
            this.ioException = null;
        }

        @Override
        public List<StockSearchResult> searchStocks(String keywords) throws IOException {
            if (ioException != null) {
                throw ioException;
            }
            if (runtimeException != null) {
                throw runtimeException;
            }
            return searchResults != null ? searchResults : new ArrayList<>();
        }
    }

    @Test
    void testExecute_WithNullKeywords_ReturnsFailure() {
        // Arrange
        StockSearchInputData input = new StockSearchInputData(null);
        mockOutputBoundary.reset();

        // Act
        StockSearchOutputData output = interactor.execute(input);

        // Assert
        assertFalse(output.isSuccess());
        assertEquals("Search keywords cannot be empty", output.getMessage());
        assertTrue(output.getResults().isEmpty());
        
        // Verify output boundary was called
        assertEquals(1, mockOutputBoundary.getPresentCallCount());
        assertNotNull(mockOutputBoundary.getLastPresentedOutput());
        assertFalse(mockOutputBoundary.getLastPresentedOutput().isSuccess());
        assertEquals("Search keywords cannot be empty", 
            mockOutputBoundary.getLastPresentedOutput().getMessage());
    }

    @Test
    void testExecute_WithBlankKeywords_ReturnsFailure() {
        // Arrange
        StockSearchInputData input = new StockSearchInputData("   ");
        mockOutputBoundary.reset();

        // Act
        StockSearchOutputData output = interactor.execute(input);

        // Assert
        assertFalse(output.isSuccess());
        assertEquals("Search keywords cannot be empty", output.getMessage());
        assertTrue(output.getResults().isEmpty());
        
        // Verify output boundary was called
        assertEquals(1, mockOutputBoundary.getPresentCallCount());
        assertFalse(mockOutputBoundary.getLastPresentedOutput().isSuccess());
    }

    @Test
    void testExecute_WithEmptyKeywords_ReturnsFailure() {
        // Arrange
        StockSearchInputData input = new StockSearchInputData("");
        mockOutputBoundary.reset();

        // Act
        StockSearchOutputData output = interactor.execute(input);

        // Assert
        assertFalse(output.isSuccess());
        assertEquals("Search keywords cannot be empty", output.getMessage());
        assertTrue(output.getResults().isEmpty());
        
        // Verify output boundary was called
        assertEquals(1, mockOutputBoundary.getPresentCallCount());
        assertFalse(mockOutputBoundary.getLastPresentedOutput().isSuccess());
    }

    @Test
    void testExecute_WithValidKeywords_ReturnsSuccess() {
        // Arrange
        String keywords = "AAPL";
        StockSearchInputData input = new StockSearchInputData(keywords);
        
        List<AlphaVantage.StockSearchResult> mockResults = new ArrayList<>();
        mockResults.add(new AlphaVantage.StockSearchResult(
            "AAPL", "Apple Inc.", "NASDAQ", "Equity", "United States", "USD"
        ));
        
        mockApi.setSearchResults(mockResults);
        mockOutputBoundary.reset();

        // Act
        StockSearchOutputData output = interactor.execute(input);

        // Assert
        assertTrue(output.isSuccess());
        assertEquals("Search completed", output.getMessage());
        assertEquals(1, output.getResults().size());
        assertEquals("AAPL", output.getResults().get(0).getSymbol());
        
        // Verify output boundary was called with correct data
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
        // Arrange
        String keywords = "INVALID";
        StockSearchInputData input = new StockSearchInputData(keywords);
        
        mockApi.setSearchResults(new ArrayList<>());
        mockOutputBoundary.reset();

        // Act
        StockSearchOutputData output = interactor.execute(input);

        // Assert
        assertFalse(output.isSuccess());
        assertEquals("No results for \"" + keywords + "\"", output.getMessage());
        assertTrue(output.getResults().isEmpty());
        
        // Verify output boundary was called
        assertEquals(1, mockOutputBoundary.getPresentCallCount());
        assertFalse(mockOutputBoundary.getLastPresentedOutput().isSuccess());
        assertEquals("No results for \"" + keywords + "\"", 
            mockOutputBoundary.getLastPresentedOutput().getMessage());
    }

    @Test
    void testExecute_WithIOException_ReturnsFailure() {
        // Arrange
        String keywords = "AAPL";
        StockSearchInputData input = new StockSearchInputData(keywords);
        IOException ioException = new IOException("Network connection failed");
        
        mockApi.setIOException(ioException);
        mockOutputBoundary.reset();

        // Act
        StockSearchOutputData output = interactor.execute(input);

        // Assert
        assertFalse(output.isSuccess());
        assertEquals("Network error: " + ioException.getMessage(), output.getMessage());
        assertTrue(output.getResults().isEmpty());
        
        // Verify output boundary was called with error data
        assertEquals(1, mockOutputBoundary.getPresentCallCount());
        assertFalse(mockOutputBoundary.getLastPresentedOutput().isSuccess());
        assertEquals("Network error: " + ioException.getMessage(), 
            mockOutputBoundary.getLastPresentedOutput().getMessage());
    }

    @Test
    void testExecute_WithGenericException_ReturnsFailure() {
        // Arrange
        String keywords = "AAPL";
        StockSearchInputData input = new StockSearchInputData(keywords);
        RuntimeException runtimeException = new RuntimeException("Unexpected error");
        
        mockApi.setRuntimeException(runtimeException);
        mockOutputBoundary.reset();

        // Act
        StockSearchOutputData output = interactor.execute(input);

        // Assert
        assertFalse(output.isSuccess());
        assertEquals("Error: " + runtimeException.getMessage(), output.getMessage());
        assertTrue(output.getResults().isEmpty());
        
        // Verify output boundary was called with error data
        assertEquals(1, mockOutputBoundary.getPresentCallCount());
        assertFalse(mockOutputBoundary.getLastPresentedOutput().isSuccess());
        assertEquals("Error: " + runtimeException.getMessage(), 
            mockOutputBoundary.getLastPresentedOutput().getMessage());
    }

    @Test
    void testExecute_WithMultipleResults_ReturnsSuccess() {
        // Arrange
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

        // Act
        StockSearchOutputData output = interactor.execute(input);

        // Assert
        assertTrue(output.isSuccess());
        assertEquals("Search completed", output.getMessage());
        assertEquals(2, output.getResults().size());
        
        // Verify output boundary was called with correct data
        assertEquals(1, mockOutputBoundary.getPresentCallCount());
        assertTrue(mockOutputBoundary.getLastPresentedOutput().isSuccess());
        assertEquals(2, mockOutputBoundary.getLastPresentedOutput().getResults().size());
    }

    @Test
    void testExecute_WithNullOutputBoundary_DoesNotThrowException() {
        // Arrange - Create interactor with null output boundary to test the null check branch
        StockSearchInteractor interactorWithNullBoundary = 
            new StockSearchInteractor(mockApi, null);
        String keywords = "AAPL";
        StockSearchInputData input = new StockSearchInputData(keywords);
        
        List<AlphaVantage.StockSearchResult> mockResults = new ArrayList<>();
        mockResults.add(new AlphaVantage.StockSearchResult(
            "AAPL", "Apple Inc.", "NASDAQ", "Equity", "United States", "USD"
        ));
        mockApi.setSearchResults(mockResults);

        // Act - Should not throw NullPointerException
        StockSearchOutputData output = interactorWithNullBoundary.execute(input);

        // Assert - Should still return valid output even with null boundary
        assertTrue(output.isSuccess());
        assertEquals("Search completed", output.getMessage());
        assertEquals(1, output.getResults().size());
    }
}
