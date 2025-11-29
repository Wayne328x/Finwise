package ui;

import data.AlphaVantageAPI;
import interface_adapters.controllers.StockSearchController;
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
import use_case.stocksearch.StockSearchOutputData;

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
    private final AlphaVantageAPI api;
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
    private final JList<AlphaVantageAPI.StockSearchResult> suggestionsList =
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
    private final JButton refreshButton = new JButton("⟳");
    /**
     * Button to add/remove stock from watchlist.
     */
    private final JButton watchButton = new JButton("♡");
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
    private AlphaVantageAPI.StockSearchResult currentSelectedResult = null;
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
     * @param usernameParam the logged-in username
     */
    public StockSearchView(
            final StockSearchController controllerParam,
            final String usernameParam) {
        this.controller = controllerParam;
        this.api = new AlphaVantageAPI();
        this.username = usernameParam;

        setTitle("FinWise — Live Stock Prices");
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
     * @param usernameParam the logged-in username
     * @param initialSymbol the initial stock symbol to load
     */
    public StockSearchView(
            final StockSearchController controllerParam,
            final String usernameParam,
            final String initialSymbol) {
        this(controllerParam, usernameParam);

        if (initialSymbol == null || initialSymbol.isEmpty()) {
            return;
        }

        statusLabel.setText("Loading " + initialSymbol + " from watchlist...");

        // Run the search in the background, then select the matching result
        SwingWorker<StockSearchOutputData, Void> worker = new SwingWorker<>() {
            @Override
            protected StockSearchOutputData doInBackground() {
                return controller.search(initialSymbol);
            }

            @Override
            protected void done() {
                try {
                    StockSearchOutputData output = get();

                    if (!output.isSuccess()
                            || output.getResults() == null
                            || output.getResults().isEmpty()) {
                        statusLabel.setText("No data found for " + initialSymbol);
                        return;
                    }

                    // Try to find an exact symbol match, otherwise use first result
                    AlphaVantageAPI.StockSearchResult selected =
                            output.getResults().get(0);
                    for (AlphaVantageAPI.StockSearchResult r : output.getResults()) {
                        if (r.getSymbol().equalsIgnoreCase(initialSymbol)) {
                            selected = r;
                            break;
                        }
                    }

                    // Use the same path as when the user selects a suggestion:
                    selectSuggestion(selected);

                } catch (Exception e) {
                    statusLabel.setText(
                            "Failed to load " + initialSymbol + ": " + e.getMessage());
                    JOptionPane.showMessageDialog(
                            StockSearchView.this,
                            "Failed to load " + initialSymbol + ": " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }

    /**
     * Initializes the UI components.
     *
     * @param usernameParam the username to display
     */
    private void initializeUserInterface(final String usernameParam) {
        JPanel topBar = new JPanel(new BorderLayout());
        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userLabel.setText("Logged in as: " + usernameParam);
        leftTop.add(userLabel);
        topBar.add(leftTop, BorderLayout.WEST);
        JPanel searchCard = new JPanel();
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
        JPanel detailsCard = new JPanel();
        detailsCard.setLayout(new BoxLayout(detailsCard, BoxLayout.Y_AXIS));
        detailsCard.setBorder(BorderFactory.createEmptyBorder(
                BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));
        detailsCard.setBackground(Color.WHITE);
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
        JPanel headerLeft = new JPanel();
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        headerLeft.setOpaque(false);
        headerLeft.add(companyNameLabel);
        headerLeft.add(symbolLabel);
        JPanel headerRight = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, RANGE_BUTTON_SPACING, 0));
        headerRight.setOpaque(false);
        headerRight.add(refreshButton);
        headerRight.add(watchButton);
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        JPanel pricePanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, PRICE_PANEL_SPACING, 0));
        pricePanel.setOpaque(false);
        pricePanel.add(priceLabel);
        pricePanel.add(changeLabel);
        headerRow.add(headerLeft, BorderLayout.WEST);
        headerRow.add(pricePanel, BorderLayout.CENTER);
        headerRow.add(headerRight, BorderLayout.EAST);
        detailsCard.add(headerRow);
        rangePanel.setOpaque(false);
        addRangeButton("1D", true);
        addRangeButton("5D", false);
        addRangeButton("1M", false);
        addRangeButton("6M", false);
        addRangeButton("1Y", false);
        addRangeButton("5Y", false);
        JPanel chartCard = new JPanel();
        chartCard.setLayout(new BorderLayout());
        chartCard.setBorder(BorderFactory.createEmptyBorder(
                BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING));
        chartCard.setBackground(Color.WHITE);
        JLabel chartTitle = new JLabel("Price Chart");
        chartTitle.setFont(chartTitle.getFont().deriveFont(
                Font.BOLD, CHART_TITLE_FONT_SIZE));
        JPanel chartHeader = new JPanel(new BorderLayout());
        chartHeader.setOpaque(false);
        chartHeader.add(chartTitle, BorderLayout.WEST);
        chartHeader.add(rangePanel, BorderLayout.EAST);
        chartPanel.setPreferredSize(new Dimension(CHART_WIDTH, CHART_HEIGHT));
        chartPanel.setBackground(
                new Color(COLOR_BG_R, COLOR_BG_G, COLOR_BG_B));
        chartCard.add(chartHeader, BorderLayout.NORTH);
        chartCard.add(chartPanel, BorderLayout.CENTER);
        JPanel centerPanel = new JPanel();
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
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(
                SMALL_BORDER_PADDING, BORDER_PADDING,
                SMALL_BORDER_PADDING, BORDER_PADDING));
        statusBar.add(statusLabel, BorderLayout.WEST);
        setLayout(new BorderLayout());
        add(topBar, BorderLayout.NORTH);
        add(new JScrollPane(centerPanel), BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * Adds a time range button to the range panel.
     *
     * @param label the button label
     * @param selected whether the button should be selected initially
     */
    private void addRangeButton(final String label, final boolean selected) {
        JToggleButton btn = new JToggleButton(label);
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
        btn.addActionListener(e -> onRangeSelected(label, btn));
        rangeGroup.add(btn);
        rangePanel.add(btn);
    }

    /**
     * Initializes event listeners for UI components.
     */
    private void initListeners() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                onSearchTextChanged();
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                onSearchTextChanged();
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                onSearchTextChanged();
            }
        });
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        && suggestionsList.getModel().getSize() > 0) {
                    AlphaVantageAPI.StockSearchResult sel =
                            suggestionsList.getModel().getElementAt(0);
                    selectSuggestion(sel);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    suggestionsScroll.setVisible(false);
                }
            }
        });
        suggestionsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() >= 1) {
                    AlphaVantageAPI.StockSearchResult sel =
                            suggestionsList.getSelectedValue();
                    selectSuggestion(sel);
                }
            }
        });
        refreshButton.addActionListener(e -> {
            if (currentSelectedResult != null) {
                loadQuote(currentSelectedResult);
            }
        });
        watchButton.addActionListener(e -> {
            if (currentSelectedResult == null
                    || currentSymbol == null
                    || currentSymbol.isEmpty()) {
                return; // nothing selected
            }

            // new state = toggled from current text
            boolean newWatched = "♡".equals(watchButton.getText());

            try {
                controller.setWatched(
                        username,
                        currentSymbol,
                        currentSelectedResult.getName(),
                        currentSelectedResult.getExchange(),
                        newWatched
                );
                // only update UI if DB operation succeeded
                watchButton.setText(newWatched ? "♥" : "♡");
                statusLabel.setText(newWatched
                        ? "Added to watchlist."
                        : "Removed from watchlist.");
            } catch (Exception ex) {
                // if DB fails, show error and don't change button text
                JOptionPane.showMessageDialog(
                        StockSearchView.this,
                        "Failed to update watchlist: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }

    /**
     * Handles search text changes and triggers async search.
     */
    private void onSearchTextChanged() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            suggestionsScroll.setVisible(false);
            suggestionsList.setListData(
                    new AlphaVantageAPI.StockSearchResult[0]);
            statusLabel.setText("Enter keywords to search.");
            return;
        }
        performAsyncSearch(text);
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
        currentSearchWorker = new SwingWorker<StockSearchOutputData, Void>() {
            @Override
            protected StockSearchOutputData doInBackground() {
                return controller.search(query);
            }

            @Override
            protected void done() {
                if (isCancelled()) {
                    return;
                }
                try {
                    StockSearchOutputData output = get();
                    if (!searchField.getText().trim().equals(query)) {
                        return;
                    }
                    statusLabel.setText(output.getMessage());
                    if (!output.isSuccess()) {
                        suggestionsList.setListData(
                                new AlphaVantageAPI.StockSearchResult[0]);
                        suggestionsScroll.setVisible(true);
                        return;
                    }
                    List<AlphaVantageAPI.StockSearchResult> results =
                            output.getResults();
                    suggestionsList.setListData(
                            results.toArray(new AlphaVantageAPI.StockSearchResult[0]));
                    suggestionsScroll.setVisible(true);
                } catch (CancellationException ignored) {
                    // Ignore cancellation
                } catch (InterruptedException | ExecutionException e) {
                    statusLabel.setText("Search failed: " + e.getMessage());
                    JOptionPane.showMessageDialog(
                            StockSearchView.this,
                            "Search failed: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                revalidate();
                repaint();
            }
        };
        currentSearchWorker.execute();
    }

    /**
     * Selects a search suggestion and loads its quote.
     *
     * @param s the search result to select
     */
    private void selectSuggestion(
            final AlphaVantageAPI.StockSearchResult s) {
        if (s == null) {
            return;
        }
        searchField.setText(s.getSymbol());
        suggestionsList.setListData(
                new AlphaVantageAPI.StockSearchResult[0]);
        suggestionsScroll.setVisible(false);
        currentSelectedResult = s;
        loadQuote(s);
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
    private void loadQuote(final AlphaVantageAPI.StockSearchResult result) {
        if (currentQuoteWorker != null && !currentQuoteWorker.isDone()) {
            currentQuoteWorker.cancel(true);
        }
        statusLabel.setText("Loading quote for " + result.getSymbol() + " ...");
        currentQuoteWorker = new SwingWorker<AlphaVantageAPI.StockQuote, Void>() {
            @Override
            protected AlphaVantageAPI.StockQuote doInBackground() {
                try {
                    return api.getQuote(result.getSymbol());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void done() {
                if (isCancelled()) {
                    return;
                }
                try {
                    AlphaVantageAPI.StockQuote quote = get();
                    companyNameLabel.setText(result.getName());
                    symbolLabel.setText(
                            result.getSymbol() + " • " + result.getExchange());
                    priceLabel.setText(String.format("$%.2f", quote.getPrice()));
                    double change = quote.getChange();
                    double changePct = quote.getChangePercent();
                    changeLabel.setText(
                            String.format("%+.2f (%+.2f%%)", change, changePct));
                    changeLabel.setForeground(change >= 0
                            ? new Color(COLOR_GREEN_R, COLOR_GREEN_G, COLOR_GREEN_B)
                            : new Color(COLOR_RED_R, COLOR_RED_G, COLOR_RED_B));

                    currentSymbol = result.getSymbol();

                    updateWatchButtonState();

                    String range = getSelectedRangeOrDefault();
                    fetchAndRenderSeries(currentSymbol, range);

                    statusLabel.setText("Quote loaded for " + currentSymbol);
                } catch (CancellationException ignored) {
                    // Ignore cancellation
                } catch (InterruptedException | ExecutionException e) {
                    statusLabel.setText("Failed to load quote: " + e.getMessage());
                    JOptionPane.showMessageDialog(
                            StockSearchView.this,
                            "Failed to load quote: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        currentQuoteWorker.execute();
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
            watchButton.setText("♡");
            return;
        }

        // Ask controller if this stock is already watched
        boolean watched = controller.isWatched(username, currentSymbol);

        if (watched) {
            watchButton.setText("♥");
        } else {
            watchButton.setText("♡");
        }
    }

    /**
     * Gets the selected time range or returns the default.
     *
     * @return the selected range or default
     */
    private String getSelectedRangeOrDefault() {
        for (AbstractButton b : Collections.list(rangeGroup.getElements())) {
            if (b.isSelected()) {
                return b.getText();
            }
        }
        return DEFAULT_RANGE;
    }

    /**
     * Handles time range selection.
     *
     * @param range the selected range
     * @param btn the button that was clicked
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
            } else {
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
     * @param range the time range
     */
    private void fetchAndRenderSeries(final String symbol, final String range) {
        if (currentSeriesWorker != null && !currentSeriesWorker.isDone()) {
            currentSeriesWorker.cancel(true);
        }
        statusLabel.setText(
                "Loading chart (" + range + ") for " + symbol + " ...");
        chartPanel.setSeries(null);
        chartPanel.repaint();
        currentSeriesWorker =
                new SwingWorker<List<AlphaVantageAPI.StockPriceData>, Void>() {
                    @Override
                    protected List<AlphaVantageAPI.StockPriceData> doInBackground() {
                        try {
                            return api.getTimeSeries(symbol, range);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    protected void done() {
                        if (isCancelled()) {
                            return;
                        }
                        try {
                            List<AlphaVantageAPI.StockPriceData> seriesData = get();
                            chartPanel.setSeries(seriesData);
                            chartPanel.repaint();
                            statusLabel.setText(
                                    "Chart loaded for " + symbol + " (" + range + ")");
                        } catch (CancellationException ignored) {
                            // Ignore cancellation
                        } catch (InterruptedException | ExecutionException e) {
                            statusLabel.setText("Failed to load chart: " + e.getMessage());
                            JOptionPane.showMessageDialog(
                                    StockSearchView.this,
                                    "Failed to load chart: " + e.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                };
        currentSeriesWorker.execute();
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
            if (value instanceof AlphaVantageAPI.StockSearchResult) {
                AlphaVantageAPI.StockSearchResult item =
                        (AlphaVantageAPI.StockSearchResult) value;
                String html =
                        "<html><b>" + escapeHtml(item.getName()) + "</b><br/>"
                                + "<span style='color:#667085;font-size:11px;'>"
                                + escapeHtml(item.getSymbol())
                                + " • "
                                + escapeHtml(item.getExchange())
                                + "</span></html>";
                setText(html);
            }
            return this;
        }

        /**
         * Escapes HTML special characters in a string.
         *
         * @param s the string to escape
         * @return the escaped string
         */
        private String escapeHtml(final String s) {
            return s == null ? EMPTY_STRING
                    : s.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
        }
    }

    /**
     * Custom panel for rendering stock price charts.
     */
    private static final class ChartPanel extends JPanel {
        /**
         * The price data series to display.
         */
        private List<AlphaVantageAPI.StockPriceData> series;

        /**
         * Sets the price data series to display.
         *
         * @param seriesData the price data series
         */
        public void setSeries(
                final List<AlphaVantageAPI.StockPriceData> seriesData) {
            this.series = seriesData;
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            if (series == null || series.isEmpty()) {
                g.setColor(new Color(
                        COLOR_DARK_GRAY_R, COLOR_DARK_GRAY_G, COLOR_DARK_GRAY_B));
                g.drawString(
                        "No chart data. Select a stock to view its price history.",
                        CHART_TEXT_OFFSET_X, getHeight() / 2);
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            final int w = getWidth();
            final int h = getHeight();
            final int padding = CHART_PADDING;
            final int bottomPadding = CHART_BOTTOM_PADDING;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (AlphaVantageAPI.StockPriceData d : series) {
                double p = d.getPrice();
                min = Math.min(min, p);
                max = Math.max(max, p);
            }
            if (min == max) {
                min -= 1;
                max += 1;
            }
            g2.setColor(new Color(
                    COLOR_LIGHT_GRAY_R, COLOR_LIGHT_GRAY_G, COLOR_LIGHT_GRAY_B));
            int x0 = padding;
            int y0 = h - bottomPadding;
            int x1 = w - padding;
            int y1 = padding;
            g2.drawLine(x0, y0, x1, y0);
            g2.drawLine(x0, y0, x0, y1);
            int n = series.size();
            if (n > 1) {
                double stepX = (x1 - x0) * 1.0 / (n - 1);
                g2.setColor(
                        new Color(COLOR_BLUE_R, COLOR_BLUE_G, COLOR_BLUE_B));
                int prevX = -1;
                int prevY = -1;
                for (int i = 0; i < n; i++) {
                    double price = series.get(i).getPrice();
                    double normalized = (price - min) / (max - min);
                    int x = (int) (x0 + i * stepX);
                    int y = (int) (y0 - normalized * (y0 - y1));
                    if (prevX >= 0) {
                        g2.drawLine(prevX, prevY, x, y);
                    }
                    prevX = x;
                    prevY = y;
                }
            }
            g2.setColor(
                    new Color(COLOR_GRAY_R, COLOR_GRAY_G, COLOR_GRAY_B));
            String minStr = String.format("%.2f", min);
            String maxStr = String.format("%.2f", max);
            g2.drawString(minStr, CHART_LABEL_OFFSET_X, y0);
            g2.drawString(
                    maxStr,
                    CHART_LABEL_OFFSET_X,
                    y1 + CHART_MAX_LABEL_OFFSET_Y);
            g2.dispose();
        }
    }
}