package use_case.stocksearch;

import data.AlphaVantageAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StockSearchInteractorTest {

    private MockAlphaVantageAPI mockApi;
    private StockSearchInteractor interactor;

    @BeforeEach
    void setUp() {
        mockApi = new MockAlphaVantageAPI();
        interactor = new StockSearchInteractor(mockApi);
    }

    // Manual mock implementation to avoid Java 23 compatibility issues with Mockito
    private static class MockAlphaVantageAPI extends AlphaVantageAPI {
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

        // Act
        StockSearchOutputData output = interactor.execute(input);

        // Assert
        assertFalse(output.isSuccess());
        assertEquals("Search keywords cannot be empty", output.getMessage());
        assertTrue(output.getResults().isEmpty());
    }

    @Test
    void testExecute_WithBlankKeywords_ReturnsFailure() {
        // Arrange
        StockSearchInputData input = new StockSearchInputData("   ");

        // Act
        StockSearchOutputData output = interactor.execute(input);

        // Assert
        assertFalse(output.isSuccess());
        assertEquals("Search keywords cannot be empty", output.getMessage());
        assertTrue(output.getResults().isEmpty());
    }

    @Test
    void testExecute_WithEmptyKeywords_ReturnsFailure() {
        // Arrange
        StockSearchInputData input = new StockSearchInputData("");

        // Act
        StockSearchOutputData output = interactor.execute(input);

        // Assert
        assertFalse(output.isSuccess());
        assertEquals("Search keywords cannot be empty", output.getMessage());
        assertTrue(output.getResults().isEmpty());
    }

    @Test
    void testExecute_WithValidKeywords_ReturnsSuccess() {
        // Arrange
        String keywords = "AAPL";
        StockSearchInputData input = new StockSearchInputData(keywords);
        
        List<AlphaVantageAPI.StockSearchResult> mockResults = new ArrayList<>();
        mockResults.add(new AlphaVantageAPI.StockSearchResult(
            "AAPL", "Apple Inc.", "NASDAQ", "Equity", "United States", "USD"
        ));
        
        mockApi.setSearchResults(mockResults);

        // Act
        StockSearchOutputData output = interactor.execute(input);

        // Assert
        assertTrue(output.isSuccess());
        assertEquals("Search completed", output.getMessage());
        assertEquals(1, output.getResults().size());
        assertEquals("AAPL", output.getResults().get(0).getSymbol());
    }

    @Test
    void testExecute_WithEmptyResults_ReturnsFailure() {
        // Arrange
        String keywords = "INVALID";
        StockSearchInputData input = new StockSearchInputData(keywords);
        
        mockApi.setSearchResults(new ArrayList<>());

        // Act
        StockSearchOutputData output = interactor.execute(input);

        // Assert
        assertFalse(output.isSuccess());
        assertEquals("No results for \"" + keywords + "\"", output.getMessage());
        assertTrue(output.getResults().isEmpty());
    }

    @Test
    void testExecute_WithIOException_ReturnsFailure() {
        // Arrange
        String keywords = "AAPL";
        StockSearchInputData input = new StockSearchInputData(keywords);
        IOException ioException = new IOException("Network connection failed");
        
        mockApi.setIOException(ioException);

        // Act
        StockSearchOutputData output = interactor.execute(input);

        // Assert
        assertFalse(output.isSuccess());
        assertEquals("Network error: " + ioException.getMessage(), output.getMessage());
        assertTrue(output.getResults().isEmpty());
    }

    @Test
    void testExecute_WithGenericException_ReturnsFailure() {
        // Arrange
        String keywords = "AAPL";
        StockSearchInputData input = new StockSearchInputData(keywords);
        RuntimeException runtimeException = new RuntimeException("Unexpected error");
        
        mockApi.setRuntimeException(runtimeException);

        // Act
        StockSearchOutputData output = interactor.execute(input);

        // Assert
        assertFalse(output.isSuccess());
        assertEquals("Error: " + runtimeException.getMessage(), output.getMessage());
        assertTrue(output.getResults().isEmpty());
    }

    @Test
    void testExecute_WithMultipleResults_ReturnsSuccess() {
        // Arrange
        String keywords = "Apple";
        StockSearchInputData input = new StockSearchInputData(keywords);
        
        List<AlphaVantageAPI.StockSearchResult> mockResults = new ArrayList<>();
        mockResults.add(new AlphaVantageAPI.StockSearchResult(
            "AAPL", "Apple Inc.", "NASDAQ", "Equity", "United States", "USD"
        ));
        mockResults.add(new AlphaVantageAPI.StockSearchResult(
            "APLE", "Apple Hospitality REIT", "NYSE", "Equity", "United States", "USD"
        ));
        
        mockApi.setSearchResults(mockResults);

        // Act
        StockSearchOutputData output = interactor.execute(input);

        // Assert
        assertTrue(output.isSuccess());
        assertEquals("Search completed", output.getMessage());
        assertEquals(2, output.getResults().size());
    }
}

