package ui.stock_search;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import data.stock.AlphaVantage;
import interfaceadapters.stocksearch.StockSearchController;
import usecase.stocksearch.StockSearchOutputData;

/**
 * Swing UI for Use Case 3: Stock Search + Quote + Time Series Chart.
 * Provides a graphical interface for searching stocks, viewing quotes,
 * and displaying price charts with various time ranges.
 */
public class StockSearchView extends JFrame {

    // Constants for magic numbers
    /**
     * Default window width in pixels.
     */
    private static final int WINDOW_WIDTH = 1000;
    /**
     * Default window height in pixels.
     */
    private static final int WINDOW_HEIGHT = 700;
    /**
     * Search field width in pixels.
     */
    private static final int SEARCH_FIELD_WIDTH = 300;
    /**
     * Search field height in pixels.
     */
    private static final int SEARCH_FIELD_HEIGHT = 32;
    /**
     * Standard border padding in pixels.
     */
    private static final int BORDER_PADDING = 12;
    /**
     * Small border padding in pixels.
     */
    private static final int SMALL_BORDER_PADDING = 4;
    /**
     * Number of visible rows in suggestions list.
     */
    private static final int SUGGESTIONS_VISIBLE_ROWS = 6;
    /**
     * Chart panel width in pixels.
     */
    private static final int CHART_WIDTH = 800;
    /**
     * Chart panel height in pixels.
     */
    private static final int CHART_HEIGHT = 360;
    /**
     * Chart padding in pixels.
     */
    private static final int CHART_PADDING = 40;
    /**
     * Chart bottom padding in pixels.
     */
    private static final int CHART_BOTTOM_PADDING = 30;
    /**
     * Button margin top in pixels.
     */
    private static final int BUTTON_MARGIN_TOP = 4;
    /**
     * Button margin sides in pixels.
     */
    private static final int BUTTON_MARGIN_SIDES = 10;
    /**
     * Button margin bottom in pixels.
     */
    private static final int BUTTON_MARGIN_BOTTOM = 4;
    /**
     * Range button spacing in pixels.
     */
    private static final int RANGE_BUTTON_SPACING = 6;
    /**
     * Price panel spacing in pixels.
     */
    private static final int PRICE_PANEL_SPACING = 8;
    /**
     * Company name font size.
     */
    private static final float COMPANY_NAME_FONT_SIZE = 20.0f;
    /**
     * Symbol font size.
     */
    private static final float SYMBOL_FONT_SIZE = 14.0f;
    /**
     * Price font size.
     */
    private static final float PRICE_FONT_SIZE = 24.0f;
    /**
     * Change font size.
     */
    private static final float CHANGE_FONT_SIZE = 16.0f;
    /**
     * Chart title font size.
     */
    private static final float CHART_TITLE_FONT_SIZE = 16.0f;
    /**
     * Gray color red component.
     */
    private static final int COLOR_GRAY_R = 0x66;
    /**
     * Gray color green component.
     */
    private static final int COLOR_GRAY_G = 0x70;
    /**
     * Gray color blue component.
     */
    private static final int COLOR_GRAY_B = 0x85;
    /**
     * Blue color red component.
     */
    private static final int COLOR_BLUE_R = 0x3b;
    /**
     * Blue color green component.
     */
    private static final int COLOR_BLUE_G = 0x82;
    /**
     * Blue color blue component.
     */
    private static final int COLOR_BLUE_B = 0xf6;
    /**
     * Light blue color red component.
     */
    private static final int COLOR_LIGHT_BLUE_R = 148;
    /**
     * Light blue color green component.
     */
    private static final int COLOR_LIGHT_BLUE_G = 180;
    /**
     * Light blue color blue component.
     */
    private static final int COLOR_LIGHT_BLUE_B = 244;
    /**
     * Background color red component.
     */
    private static final int COLOR_BG_R = 0xf7;
    /**
     * Background color green component.
     */
    private static final int COLOR_BG_G = 0xf8;
    /**
     * Background color blue component.
     */
    private static final int COLOR_BG_B = 0xfb;
    /**
     * Green color red component.
     */
    private static final int COLOR_GREEN_R = 0x22;
    /**
     * Green color green component.
     */
    private static final int COLOR_GREEN_G = 0xc5;
    /**
     * Green color blue component.
     */
    private static final int COLOR_GREEN_B = 0x5e;
    /**
     * Red color red component.
     */
    private static final int COLOR_RED_R = 0xef;
    /**
     * Red color green component.
     */
    private static final int COLOR_RED_G = 0x44;
    /**
     * Red color blue component.
     */
    private static final int COLOR_RED_B = 0x44;
    /**
     * Dark gray color red component.
     */
    private static final int COLOR_DARK_GRAY_R = 0x99;
    /**
     * Dark gray color green component.
     */
    private static final int COLOR_DARK_GRAY_G = 0x99;
    /**
     * Dark gray color blue component.
     */
    private static final int COLOR_DARK_GRAY_B = 0x99;
    /**
     * Light gray color red component.
     */
    private static final int COLOR_LIGHT_GRAY_R = 0xdd;
    /**
     * Light gray color green component.
     */
    private static final int COLOR_LIGHT_GRAY_G = 0xdd;
    /**
     * Light gray color blue component.
     */
    private static final int COLOR_LIGHT_GRAY_B = 0xdd;
    /**
     * Default time range.
     */
    private static final String DEFAULT_RANGE = "1D";
    /**
     * Empty string constant.
     */
    private static final String EMPTY_STRING = "";
    /**
     * Error message title constant.
     */
    private static final String ERROR_TITLE = "Error";
    /**
     * Heart symbol (unwatched) constant.
     */
    private static final String HEART_UNWATCHED = "\u2661";
    /**
     * Heart symbol (watched) constant.
     */
    private static final String HEART_WATCHED = "\u2665";
    /**
     * Text offset for chart message in pixels.
     */
    private static final int CHART_TEXT_OFFSET_X = 20;
    /**
     * Text offset for chart labels in pixels.
     */
    private static final int CHART_LABEL_OFFSET_X = 5;
    /**
     * Text offset for max label in pixels.
     */
    private static final int CHART_MAX_LABEL_OFFSET_Y = 5;

    /**
     * The controller for stock search operations.
     */
    private final StockSearchController controller;
    /**
     * The API client for fetching stock data.
     */
    private final AlphaVantage api;
    /**
     * The current logged-in username.
     */
    private final String username;

    /**
     * Label displaying the logged-in user.
     */
    private final JLabel userLabel = new JLabel();
    /**
     * Text field for entering search keywords.
     */
    private final JTextField searchField = new JTextField();
    /**
     * List displaying stock search suggestions.
     */
    private final JList<AlphaVantage.StockSearchResult> suggestionsList =
            new JList<>();
    /**
     * Scroll pane containing the suggestions list.
     */
    private final JScrollPane suggestionsScroll =
            new JScrollPane(suggestionsList);
    /**
     * Label displaying the company name.
     */
    private final JLabel companyNameLabel = new JLabel("Search for a stock");
    /**
     * Label displaying the stock symbol.
     */
    private final JLabel symbolLabel = new JLabel(EMPTY_STRING);
    /**
     * Label displaying the current stock price.
     */
    private final JLabel priceLabel = new JLabel(EMPTY_STRING);
    /**
     * Label displaying the price change.
     */
    private final JLabel changeLabel = new JLabel(EMPTY_STRING);
    /**
     * Button to refresh the current quote.
     */
    private final JButton refreshButton = new JButton("\u21BB");
    /**
     * Button to add/remove stock from watchlist.
     */
    private final JButton watchButton = new JButton(HEART_UNWATCHED);
    /**
     * Button group for time range selection.
     */
    private final ButtonGroup rangeGroup = new ButtonGroup();
    /**
     * Panel containing time range buttons.
     */
    private final JPanel rangePanel =
            new JPanel(new FlowLayout(FlowLayout.RIGHT));
    /**
     * Panel for displaying the price chart.
     */
    private final ChartPanel chartPanel = new ChartPanel();
    /**
     * Label displaying status messages.
     */
    private final JLabel statusLabel =
            new JLabel("Enter keywords to search.");
    /**
     * Currently selected stock symbol.
     */
    private String currentSymbol = EMPTY_STRING;
    /**
     * Currently selected search result.
     */
    private AlphaVantage.StockSearchResult currentSelectedResult;
    /**
     * Worker thread for search operations.
     */
    private SwingWorker<?, ?> currentSearchWorker;
    /**
     * Worker thread for quote operations.
     */
    private SwingWorker<?, ?> currentQuoteWorker;
    /**
     * Worker thread for time series operations.
     */
    private SwingWorker<?, ?> currentSeriesWorker;

    /**
     * Constructs a StockSearchView with the given controller and username.
     *
     * @param controllerParam the stock search controller
     * @param usernameParam   the logged-in username
     */
    public StockSearchView(
            final StockSearchController controllerParam,
            final String usernameParam) {
        this.controller = controllerParam;
        this.api = new AlphaVantage();
        this.username = usernameParam;

        setTitle("FinWise - Live Stock Prices");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        initializeUserInterface(usernameParam);
        initListeners();
        showEmptyState();
    }

    /**
     * Constructs a StockSearchView with an initial symbol to load.
     *
     * @param controllerParam the stock search controller
     * @param usernameParam   the logged-in username
     * @param initialSymbol   the initial stock symbol to load
     */
    public StockSearchView(
            final StockSearchController controllerParam,
            final String usernameParam,
            final String initialSymbol) {
        this(controllerParam, usernameParam);

        if (initialSymbol != null && !initialSymbol.isEmpty()) {
            statusLabel.setText("Loading " + initialSymbol + " from watchlist...");

            // Run the search in the background, then select the matching result
            final SwingWorker<StockSearchOutputData, Void> worker =
                    createInitialSymbolWorker(initialSymbol);
            worker.execute();
        }
    }

    /**
     * Creates a SwingWorker for loading an initial symbol from watchlist.
     *
     * @param initialSymbol the symbol to load
     * @return the SwingWorker instance
     */
    private SwingWorker<StockSearchOutputData, Void> createInitialSymbolWorker(
            final String initialSymbol) {
        return new SwingWorker<>() {
            @Override
            protected StockSearchOutputData doInBackground() {
                return controller.search(initialSymbol);
            }

            @Override
            protected void done() {
                handleInitialSymbolLoaded(initialSymbol, this);
            }
        };
    }

    /**
     * Handles the loaded initial symbol data.
     *
     * @param initialSymbol the symbol that was loaded
     * @param worker the worker that completed the search
     */
    private void handleInitialSymbolLoaded(
            final String initialSymbol,
            final SwingWorker<StockSearchOutputData, Void> worker) {
        try {
            final StockSearchOutputData output = worker.get();

            if (output.isSuccess()
                    && output.getResults() != null
                    && !output.getResults().isEmpty()) {
                // Try to find an exact symbol match, otherwise use first result
                AlphaVantage.StockSearchResult selected =
                        output.getResults().get(0);
                for (AlphaVantage.StockSearchResult r : output.getResults()) {
                    if (r.getSymbol().equalsIgnoreCase(initialSymbol)) {
                        selected = r;
                        break;
                    }
                }

                // Use the same path as when the user selects a suggestion:
                chooseSuggestion(selected);
            }
            else {
                statusLabel.setText("No data found for " + initialSymbol);
            }

        }
        catch (InterruptedException | ExecutionException exception) {
            statusLabel.setText(
                    "Failed to load " + initialSymbol + ": " + exception.getMessage());
            JOptionPane.showMessageDialog(
                    StockSearchView.this,
                    "Failed to load " + initialSymbol + ": " + exception.getMessage(),
                    ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Initializes the UI components.
     *
     * @param usernameParam the username to display
     */
    private void initializeUserInterface(final String usernameParam) {
        final JPanel topBar = createTopBar(usernameParam);
        final JPanel searchCard = createSearchCard();
        final JPanel detailsCard = createDetailsCard();
        final JPanel chartCard = createChartCard();
        final JPanel centerPanel = createCenterPanel(searchCard, detailsCard, chartCard);
        final JPanel statusBar = createStatusBar();
        setLayout(new BorderLayout());
        add(topBar, BorderLayout.NORTH);
        add(new JScrollPane(centerPanel), BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * Creates the top bar with user label.
     *
     * @param usernameParam the username to display
     * @return the top bar panel
     */
    private JPanel createTopBar(final String usernameParam) {
        final JPanel topBar = new JPanel(new BorderLayout());
        final JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userLabel.setText("Logged in as: " + usernameParam);
        leftTop.add(userLabel);
        topBar.add(leftTop, BorderLayout.WEST);
        return topBar;
    }

    /**
     * Creates the search card panel.
     *
     * @return the search card panel
     */
    private JPanel createSearchCard() {
        final JPanel searchCard = new JPanel();
        searchCard.setLayout(new BoxLayout(searchCard, BoxLayout.Y_AXIS));
        searchCard.setBorder(BorderFactory.createEmptyBorder(
                BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));
        searchCard.setBackground(Color.WHITE);
        searchField.setPreferredSize(
                new Dimension(SEARCH_FIELD_WIDTH, SEARCH_FIELD_HEIGHT));
        searchField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, SEARCH_FIELD_HEIGHT));
        searchField.putClientProperty("JComponent.sizeVariant", "regular");
        searchField.setToolTipText("Search stocks (e.g., AAPL, Tesla)...");
        suggestionsList.setVisibleRowCount(SUGGESTIONS_VISIBLE_ROWS);
        suggestionsList.setCellRenderer(new SuggestionCellRenderer());
        suggestionsScroll.setVisible(false);
        suggestionsScroll.setBorder(BorderFactory.createEmptyBorder());
        searchCard.add(searchField);
        searchCard.add(Box.createVerticalStrut(SMALL_BORDER_PADDING));
        searchCard.add(suggestionsScroll);
        return searchCard;
    }

    /**
     * Creates the details card panel.
     *
     * @return the details card panel
     */
    private JPanel createDetailsCard() {
        final JPanel detailsCard = new JPanel();
        detailsCard.setLayout(new BoxLayout(detailsCard, BoxLayout.Y_AXIS));
        detailsCard.setBorder(BorderFactory.createEmptyBorder(
                BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));
        detailsCard.setBackground(Color.WHITE);
        configureDetailLabels();
        final JPanel headerRow = createDetailsHeaderRow();
        detailsCard.add(headerRow);
        initializeRangeButtons();
        return detailsCard;
    }

    /**
     * Configures the detail labels (company name, symbol, price, change).
     */
    private void configureDetailLabels() {
        companyNameLabel.setFont(companyNameLabel.getFont().deriveFont(
                Font.BOLD, COMPANY_NAME_FONT_SIZE));
        symbolLabel.setFont(symbolLabel.getFont().deriveFont(
                Font.PLAIN, SYMBOL_FONT_SIZE));
        symbolLabel.setForeground(
                new Color(COLOR_GRAY_R, COLOR_GRAY_G, COLOR_GRAY_B));
        priceLabel.setFont(priceLabel.getFont().deriveFont(
                Font.BOLD, PRICE_FONT_SIZE));
        changeLabel.setFont(changeLabel.getFont().deriveFont(
                Font.PLAIN, CHANGE_FONT_SIZE));
    }

    /**
     * Creates the header row for details card.
     *
     * @return the header row panel
     */
    private JPanel createDetailsHeaderRow() {
        final JPanel headerLeft = new JPanel();
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        headerLeft.setOpaque(false);
        headerLeft.add(companyNameLabel);
        headerLeft.add(symbolLabel);
        final JPanel headerRight = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, RANGE_BUTTON_SPACING, 0));
        headerRight.setOpaque(false);
        headerRight.add(refreshButton);
        headerRight.add(watchButton);
        final JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        final JPanel pricePanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, PRICE_PANEL_SPACING, 0));
        pricePanel.setOpaque(false);
        pricePanel.add(priceLabel);
        pricePanel.add(changeLabel);
        headerRow.add(headerLeft, BorderLayout.WEST);
        headerRow.add(pricePanel, BorderLayout.CENTER);
        headerRow.add(headerRight, BorderLayout.EAST);
        return headerRow;
    }

    /**
     * Initializes the range buttons.
     */
    private void initializeRangeButtons() {
        rangePanel.setOpaque(false);
        addRangeButton("1D", true);
        addRangeButton("5D", false);
        addRangeButton("1M", false);
        addRangeButton("6M", false);
        addRangeButton("1Y", false);
        addRangeButton("5Y", false);
    }

    /**
     * Creates the chart card panel.
     *
     * @return the chart card panel
     */
    private JPanel createChartCard() {
        final JPanel chartCard = new JPanel();
        chartCard.setLayout(new BorderLayout());
        chartCard.setBorder(BorderFactory.createEmptyBorder(
                BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));
        chartCard.setBackground(Color.WHITE);
        final JLabel chartTitle = new JLabel("Price Chart");
        chartTitle.setFont(chartTitle.getFont().deriveFont(
                Font.BOLD, CHART_TITLE_FONT_SIZE));
        final JPanel chartHeader = new JPanel(new BorderLayout());
        chartHeader.setOpaque(false);
        chartHeader.add(chartTitle, BorderLayout.WEST);
        chartHeader.add(rangePanel, BorderLayout.EAST);
        chartPanel.setPreferredSize(new Dimension(CHART_WIDTH, CHART_HEIGHT));
        chartPanel.setBackground(
                new Color(COLOR_BG_R, COLOR_BG_G, COLOR_BG_B));
        chartCard.add(chartHeader, BorderLayout.NORTH);
        chartCard.add(chartPanel, BorderLayout.CENTER);
        return chartCard;
    }

    /**
     * Creates the center panel containing search, details, and chart cards.
     *
     * @param searchCard  the search card
     * @param detailsCard the details card
     * @param chartCard   the chart card
     * @return the center panel
     */
    private JPanel createCenterPanel(final JPanel searchCard,
                                     final JPanel detailsCard,
                                     final JPanel chartCard) {
        final JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(
                new Color(COLOR_BG_R, COLOR_BG_G, COLOR_BG_B));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(
                BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));
        centerPanel.add(searchCard);
        centerPanel.add(Box.createVerticalStrut(BORDER_PADDING));
        centerPanel.add(detailsCard);
        centerPanel.add(Box.createVerticalStrut(BORDER_PADDING));
        centerPanel.add(chartCard);
        return centerPanel;
    }

    /**
     * Creates the status bar panel.
     *
     * @return the status bar panel
     */
    private JPanel createStatusBar() {
        final JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(
                SMALL_BORDER_PADDING, BORDER_PADDING,
                SMALL_BORDER_PADDING, BORDER_PADDING));
        statusBar.add(statusLabel, BorderLayout.WEST);
        return statusBar;
    }

    /**
     * Adds a time range button to the range panel.
     *
     * @param label    the button label
     * @param selected whether the button should be selected initially
     */
    private void addRangeButton(final String label, final boolean selected) {
        final JToggleButton btn = new JToggleButton(label);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(
                BUTTON_MARGIN_TOP, BUTTON_MARGIN_SIDES,
                BUTTON_MARGIN_BOTTOM, BUTTON_MARGIN_SIDES));
        if (selected) {
            btn.setSelected(true);
            btn.setBackground(
                    new Color(COLOR_BLUE_R, COLOR_BLUE_G, COLOR_BLUE_B));
            btn.setForeground(
                    new Color(COLOR_LIGHT_BLUE_R, COLOR_LIGHT_BLUE_G,
                            COLOR_LIGHT_BLUE_B));
        }
        btn.addActionListener(actionEvent -> onRangeSelected(label, btn));
        rangeGroup.add(btn);
        rangePanel.add(btn);
    }

    /**
     * Initializes event listeners for UI components.
     */
    private void initListeners() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent event) {
                onSearchTextChanged();
            }

            @Override
            public void removeUpdate(final DocumentEvent event) {
                onSearchTextChanged();
            }

            @Override
            public void changedUpdate(final DocumentEvent event) {
                onSearchTextChanged();
            }
        });
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER
                        && suggestionsList.getModel().getSize() > 0) {
                    final AlphaVantage.StockSearchResult sel =
                            suggestionsList.getModel().getElementAt(0);
                    chooseSuggestion(sel);
                }
                else if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    suggestionsScroll.setVisible(false);
                }
            }
        });
        suggestionsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() >= 1) {
                    final AlphaVantage.StockSearchResult sel =
                            suggestionsList.getSelectedValue();
                    chooseSuggestion(sel);
                }
            }
        });
        refreshButton.addActionListener(actionEvent -> {
            if (currentSelectedResult != null) {
                loadQuote(currentSelectedResult);
            }
        });
        watchButton.addActionListener(actionEvent -> handleWatchButtonClick());
    }

    /**
     * Handles watch button click event.
     */
    private void handleWatchButtonClick() {
        if (currentSelectedResult != null
                && currentSymbol != null
                && !currentSymbol.isEmpty()) {
            final boolean newWatched = HEART_UNWATCHED.equals(watchButton.getText());

            try {
                controller.setWatched(
                        username,
                        currentSymbol,
                        currentSelectedResult.getName(),
                        currentSelectedResult.getExchange(),
                        newWatched
                );
                if (newWatched) {
                    watchButton.setText(HEART_WATCHED);
                    statusLabel.setText("Added to watchlist.");
                }
                else {
                    watchButton.setText(HEART_UNWATCHED);
                    statusLabel.setText("Removed from watchlist.");
                }
            }
            catch (IllegalArgumentException | IllegalStateException exception) {
                JOptionPane.showMessageDialog(
                        StockSearchView.this,
                        "Failed to update watchlist: " + exception.getMessage(),
                        ERROR_TITLE,
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Handles search text changes and triggers async search.
     */
    private void onSearchTextChanged() {
        final String text = searchField.getText().trim();
        if (text.isEmpty()) {
            suggestionsScroll.setVisible(false);
            suggestionsList.setListData(
                    new AlphaVantage.StockSearchResult[0]);
            statusLabel.setText("Enter keywords to search.");
        }
        else {
            performAsyncSearch(text);
        }
    }

    /**
     * Performs an asynchronous stock search.
     *
     * @param query the search query
     */
    private void performAsyncSearch(final String query) {
        if (currentSearchWorker != null && !currentSearchWorker.isDone()) {
            currentSearchWorker.cancel(true);
        }
        statusLabel.setText("Searching for \"" + query + "\" ...");
        currentSearchWorker = createSearchWorker(query);
        currentSearchWorker.execute();
    }

    /**
     * Creates a SwingWorker for performing a stock search.
     *
     * @param query the search query
     * @return the SwingWorker instance
     */
    private SwingWorker<StockSearchOutputData, Void> createSearchWorker(
            final String query) {
        return new SwingWorker<>() {
            @Override
            protected StockSearchOutputData doInBackground() {
                return controller.search(query);
            }

            @Override
            protected void done() {
                if (!isCancelled()) {
                    handleSearchCompleted();
                }
            }
        };
    }

    /**
     * Handles the completed search operation.
     */
    private void handleSearchCompleted() {
        try {
            @SuppressWarnings("unchecked")
            final SwingWorker<StockSearchOutputData, Void> worker =
                    (SwingWorker<StockSearchOutputData, Void>) currentSearchWorker;
            final StockSearchOutputData output = worker.get();
            if (!output.isSuccess()
                    || output.getResults() == null
                    || output.getResults().isEmpty()) {
                suggestionsScroll.setVisible(false);
                suggestionsList.setListData(
                        new AlphaVantage.StockSearchResult[0]);
                statusLabel.setText("No results found.");
            }
            else {
                suggestionsList.setListData(
                        output.getResults().toArray(
                                new AlphaVantage.StockSearchResult[0]));
                suggestionsScroll.setVisible(true);
                statusLabel.setText(
                        "Found " + output.getResults().size() + " result(s).");
            }
        }
        catch (CancellationException ignored) {
            // Ignore cancellation
        }
        catch (InterruptedException | ExecutionException exception) {
            statusLabel.setText("Search failed: " + exception.getMessage());
            JOptionPane.showMessageDialog(
                    StockSearchView.this,
                    "Search failed: " + exception.getMessage(),
                    ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Selects a search suggestion and loads its quote.
     *
     * @param searchResult the search result to select
     */
    private void chooseSuggestion(
            final AlphaVantage.StockSearchResult searchResult) {
        if (searchResult != null) {
            searchField.setText(searchResult.getSymbol());
            suggestionsList.setListData(
                    new AlphaVantage.StockSearchResult[0]);
            suggestionsScroll.setVisible(false);
            currentSelectedResult = searchResult;
            loadQuote(searchResult);
        }
    }

    /**
     * Shows the empty state when no stock is selected.
     */
    private void showEmptyState() {
        currentSymbol = EMPTY_STRING;
        currentSelectedResult = null;
        companyNameLabel.setText("Search for a stock");
        symbolLabel.setText(EMPTY_STRING);
        priceLabel.setText(EMPTY_STRING);
        changeLabel.setText(EMPTY_STRING);
        chartPanel.setSeries(null);
        chartPanel.repaint();
    }

    /**
     * Loads and displays the quote for the given stock result.
     *
     * @param result the stock search result
     */
    private void loadQuote(final AlphaVantage.StockSearchResult result) {
        if (currentQuoteWorker != null && !currentQuoteWorker.isDone()) {
            currentQuoteWorker.cancel(true);
        }
        statusLabel.setText("Loading quote for " + result.getSymbol() + " ...");
        currentQuoteWorker = createQuoteWorker(result);
        currentQuoteWorker.execute();
    }

    /**
     * Creates a SwingWorker for loading a stock quote.
     *
     * @param result the stock search result
     * @return the SwingWorker instance
     */
    private SwingWorker<AlphaVantage.StockQuote, Void> createQuoteWorker(
            final AlphaVantage.StockSearchResult result) {
        return new SwingWorker<AlphaVantage.StockQuote, Void>() {
            @Override
            protected AlphaVantage.StockQuote doInBackground() {
                try {
                    return api.getQuote(result.getSymbol());
                }
                catch (IOException ioException) {
                    throw new RuntimeException("Failed to fetch quote: " + ioException.getMessage(), ioException);
                }
            }

            @Override
            protected void done() {
                if (!isCancelled()) {
                    handleQuoteLoaded(result);
                }
            }
        };
    }

    /**
     * Handles the loaded quote data.
     *
     * @param result the stock search result
     */
    private void handleQuoteLoaded(final AlphaVantage.StockSearchResult result) {
        try {
            @SuppressWarnings("unchecked")
            final SwingWorker<AlphaVantage.StockQuote, Void> worker =
                    (SwingWorker<AlphaVantage.StockQuote, Void>) currentQuoteWorker;
            final AlphaVantage.StockQuote quote = worker.get();
            companyNameLabel.setText(result.getName());
            symbolLabel.setText(
                    result.getSymbol() + " - " + result.getExchange());
            priceLabel.setText(String.format("$%.2f", quote.getPrice()));
            final double change = quote.getChange();
            final double changePct = quote.getChangePercent();
            changeLabel.setText(
                    String.format("%+.2f (%+.2f%%)", change, changePct));
            if (change >= 0) {
                changeLabel.setForeground(
                        new Color(COLOR_GREEN_R, COLOR_GREEN_G, COLOR_GREEN_B));
            }
            else {
                changeLabel.setForeground(
                        new Color(COLOR_RED_R, COLOR_RED_G, COLOR_RED_B));
            }

            currentSymbol = result.getSymbol();

            updateWatchButtonState();

            final String range = getSelectedRangeOrDefault();
            fetchAndRenderSeries(currentSymbol, range);

            statusLabel.setText("Quote loaded for " + currentSymbol);
        }
        catch (CancellationException ignored) {
            // Ignore cancellation
        }
        catch (InterruptedException | ExecutionException exception) {
            statusLabel.setText("Failed to load quote: " + exception.getMessage());
            JOptionPane.showMessageDialog(
                    StockSearchView.this,
                    "Failed to load quote: " + exception.getMessage(),
                    ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Updates the watch button state based on current selection.
     */
    private void updateWatchButtonState() {
        // Defensive: only if we have both user + symbol
        if (username == null
                || username.isEmpty()
                || currentSymbol == null
                || currentSymbol.isEmpty()) {
            watchButton.setText(HEART_UNWATCHED);
        }
        else {
            // Ask controller if this stock is already watched
            final boolean watched = controller.isWatched(username, currentSymbol);

            if (watched) {
                watchButton.setText(HEART_WATCHED);
            }
            else {
                watchButton.setText(HEART_UNWATCHED);
            }
        }
    }

    /**
     * Gets the selected time range or returns the default.
     *
     * @return the selected range or default
     */
    private String getSelectedRangeOrDefault() {
        String selectedRange = DEFAULT_RANGE;
        for (AbstractButton b : Collections.list(rangeGroup.getElements())) {
            if (b.isSelected()) {
                selectedRange = b.getText();
                break;
            }
        }
        return selectedRange;
    }

    /**
     * Handles time range selection.
     *
     * @param range the selected range
     * @param btn   the button that was clicked
     */
    private void onRangeSelected(
            final String range, final JToggleButton btn) {
        for (AbstractButton b : Collections.list(rangeGroup.getElements())) {
            if (b == btn) {
                b.setBackground(
                        new Color(COLOR_BLUE_R, COLOR_BLUE_G, COLOR_BLUE_B));
                b.setForeground(
                        new Color(COLOR_LIGHT_BLUE_R, COLOR_LIGHT_BLUE_G,
                                COLOR_LIGHT_BLUE_B));
            }
            else {
                b.setBackground(null);
                b.setForeground(Color.BLACK);
            }
        }
        if (!currentSymbol.isEmpty()) {
            fetchAndRenderSeries(currentSymbol, range);
        }
    }

    /**
     * Fetches and renders the time series chart for a symbol.
     *
     * @param symbol the stock symbol
     * @param range  the time range
     */
    private void fetchAndRenderSeries(final String symbol, final String range) {
        if (currentSeriesWorker != null && !currentSeriesWorker.isDone()) {
            currentSeriesWorker.cancel(true);
        }
        statusLabel.setText(
                "Loading chart (" + range + ") for " + symbol + " ...");
        chartPanel.setSeries(null);
        chartPanel.repaint();
        currentSeriesWorker = createSeriesWorker(symbol, range);
        currentSeriesWorker.execute();
    }

    /**
     * Creates a SwingWorker for loading time series data.
     *
     * @param symbol the stock symbol
     * @param range  the time range
     * @return the SwingWorker instance
     */
    private SwingWorker<List<AlphaVantage.StockPriceData>, Void> createSeriesWorker(
            final String symbol, final String range) {
        return new SwingWorker<List<AlphaVantage.StockPriceData>, Void>() {
            @Override
            protected List<AlphaVantage.StockPriceData> doInBackground() {
                try {
                    return api.getTimeSeries(symbol, range);
                }
                catch (IOException ioException) {
                    throw new RuntimeException("Failed to fetch time series: " + ioException.getMessage(), ioException);
                }
            }

            @Override
            protected void done() {
                if (!isCancelled()) {
                    handleSeriesLoaded(symbol, range);
                }
            }
        };
    }

    /**
     * Handles the loaded time series data.
     *
     * @param symbol the stock symbol
     * @param range  the time range
     */
    private void handleSeriesLoaded(final String symbol, final String range) {
        try {
            @SuppressWarnings("unchecked")
            final SwingWorker<List<AlphaVantage.StockPriceData>, Void> worker =
                    (SwingWorker<List<AlphaVantage.StockPriceData>, Void>) currentSeriesWorker;
            final List<AlphaVantage.StockPriceData> seriesData = worker.get();
            chartPanel.setSeries(seriesData);
            chartPanel.repaint();
            statusLabel.setText(
                    "Chart loaded for " + symbol + " (" + range + ")");
        }
        catch (CancellationException ignored) {
            // Ignore cancellation
        }
        catch (InterruptedException | ExecutionException exception) {
            statusLabel.setText("Failed to load chart: " + exception.getMessage());
            JOptionPane.showMessageDialog(
                    StockSearchView.this,
                    "Failed to load chart: " + exception.getMessage(),
                    ERROR_TITLE,
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Custom cell renderer for stock search suggestions.
     */
    private static final class SuggestionCellRenderer
            extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                final JList<?> list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            if (value instanceof AlphaVantage.StockSearchResult) {
                final AlphaVantage.StockSearchResult item =
                        (AlphaVantage.StockSearchResult) value;
                final String html =
                        "<html><b>" + escapeHtml(item.getName()) + "</b><br/>"
                                + "<span style='color:#667085;font-size:11px;'>"
                                + escapeHtml(item.getSymbol())
                                + " - "
                                + escapeHtml(item.getExchange())
                                + "</span></html>";
                setText(html);
            }
            return this;
        }

        /**
         * Escapes HTML special characters in a string.
         *
         * @param str the string to escape
         * @return the escaped string
         */
        private String escapeHtml(final String str) {
            String result = EMPTY_STRING;
            if (str != null) {
                result = str.replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;");
            }
            return result;
        }
    }

    /**
     * Custom panel for rendering stock price charts.
     */
    private static final class ChartPanel extends JPanel {
        /**
         * The price data series to display.
         */
        private List<AlphaVantage.StockPriceData> series;

        /**
         * Sets the price data series to display.
         *
         * @param seriesData the price data series
         */
        public void setSeries(
                final List<AlphaVantage.StockPriceData> seriesData) {
            this.series = seriesData;
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            if (series == null || series.isEmpty()) {
                drawEmptyChartMessage(g);
            }
            else {
                drawChart(g);
            }
        }

        /**
         * Draws the empty chart message.
         *
         * @param graphics the graphics context
         */
        private void drawEmptyChartMessage(final Graphics graphics) {
            graphics.setColor(new Color(
                    COLOR_DARK_GRAY_R, COLOR_DARK_GRAY_G, COLOR_DARK_GRAY_B));
            graphics.drawString(
                    "No chart data. Select a stock to view its price history.",
                    CHART_TEXT_OFFSET_X, getHeight() / 2);
        }

        /**
         * Draws the price chart.
         *
         * @param graphics the graphics context
         */
        private void drawChart(final Graphics graphics) {
            final Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            final int w = getWidth();
            final int h = getHeight();
            final int padding = CHART_PADDING;
            final int bottomPadding = CHART_BOTTOM_PADDING;
            final double[] priceRange = calculatePriceRange();
            final double min = priceRange[0];
            final double max = priceRange[1];
            drawChartAxes(g2, w, h, padding, bottomPadding);
            drawPriceLine(g2, w, h, padding, bottomPadding, min, max);
            drawPriceLabels(g2, h, padding, bottomPadding, min, max);
            g2.dispose();
        }

        /**
         * Calculates the min and max price values.
         *
         * @return array with [min, max]
         */
        private double[] calculatePriceRange() {
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (AlphaVantage.StockPriceData d : series) {
                final double p = d.getPrice();
                min = Math.min(min, p);
                max = Math.max(max, p);
            }
            if (min == max) {
                min -= 1;
                max += 1;
            }
            return new double[]{min, max};
        }

        /**
         * Draws the chart axes.
         *
         * @param graphics2D           the graphics context
         * @param width            the width
         * @param height            the height
         * @param padding      the padding
         * @param bottomPadding the bottom padding
         */
        private void drawChartAxes(final Graphics2D graphics2D,
                                   final int width,
                                   final int height,
                                   final int padding,
                                   final int bottomPadding) {
            graphics2D.setColor(new Color(
                    COLOR_LIGHT_GRAY_R, COLOR_LIGHT_GRAY_G, COLOR_LIGHT_GRAY_B));
            final int x0 = padding;
            final int y0 = height - bottomPadding;
            final int x1 = width - padding;
            final int y1 = padding;
            graphics2D.drawLine(x0, y0, x1, y0);
            graphics2D.drawLine(x0, y0, x0, y1);
        }

        /**
         * Draws the price line.
         *
         * @param graphics2D           the graphics context
         * @param width            the width
         * @param height            the height
         * @param padding      the padding
         * @param bottomPadding the bottom padding
         * @param min          the minimum price
         * @param max          the maximum price
         */
        private void drawPriceLine(final Graphics2D graphics2D,
                                   final int width,
                                   final int height,
                                   final int padding,
                                   final int bottomPadding,
                                   final double min,
                                   final double max) {
            final int n = series.size();
            if (n > 1) {
                final int x0 = padding;
                final int y0 = height - bottomPadding;
                final int x1 = width - padding;
                final int y1 = padding;
                final double stepX = (x1 - x0) * 1.0 / (n - 1);
                graphics2D.setColor(
                        new Color(COLOR_BLUE_R, COLOR_BLUE_G, COLOR_BLUE_B));
                int prevX = -1;
                int prevY = -1;
                for (int i = 0; i < n; i++) {
                    final double price = series.get(i).getPrice();
                    final double normalized = (price - min) / (max - min);
                    final int x = (int) (x0 + i * stepX);
                    final int y = (int) (y0 - normalized * (y0 - y1));
                    if (prevX >= 0) {
                        graphics2D.drawLine(prevX, prevY, x, y);
                    }
                    prevX = x;
                    prevY = y;
                }
            }
        }

        /**
         * Draws the price labels.
         *
         * @param graphics2D           the graphics context
         * @param height            the height
         * @param padding      the padding
         * @param bottomPadding the bottom padding
         * @param min          the minimum price
         * @param max          the maximum price
         */
        private void drawPriceLabels(final Graphics2D graphics2D,
                                     final int height,
                                     final int padding,
                                     final int bottomPadding,
                                     final double min,
                                     final double max) {
            graphics2D.setColor(
                    new Color(COLOR_GRAY_R, COLOR_GRAY_G, COLOR_GRAY_B));
            final String minStr = String.format("%.2f", min);
            final String maxStr = String.format("%.2f", max);
            final int y0 = height - bottomPadding;
            final int y1 = padding;
            graphics2D.drawString(minStr, CHART_LABEL_OFFSET_X, y0);
            graphics2D.drawString(
                    maxStr,
                    CHART_LABEL_OFFSET_X,
                    y1 + CHART_MAX_LABEL_OFFSET_Y);
        }
    }
}
