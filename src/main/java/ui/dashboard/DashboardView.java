package ui.dashboard;

import data.ExpenseRepository;
import fetch_news.NewsApiDAO;
import interface_adapters.controllers.*;
import ui.portfolio.PortfolioView;
import ui.stock_search.StockSearchView;
import ui.trading.TradingView;
import ui.trends.TrendsViewModel;
import ui.news.NewsView;
import ui.trends.TrendsView;

import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class DashboardView extends JFrame {

    private final JList<String> watchedList = new JList<>(new DefaultListModel<>());
    private final DashboardController dashController;
    private final StockSearchController stockController;
    private final TradingController tradingController;
    private final TrendsController trendsController;
    private final TrendsViewModel trendsViewModel;
    private final PortfolioController portfolioController;
    private final Runnable onLogout;
    private final String username;
    private final ExpenseRepository expenseRepository;
    private final Consumer<String> showTrackerView;

    private final JTabbedPane tabs = new JTabbedPane();

    // Indices for tabs
    private static final int HOME_TAB = 0;
    private static final int NEWS_TAB = 1;
    private static final int TRACKER_TAB = 2;
    private static final int STOCK_TAB = 3;
    private static final int TRADING_TAB = 4;
    private static final int TRENDS_TAB = 5;
    private static final int PORTFOLIO_TAB = 6;

    public DashboardView(DashboardController dashController,
                         StockSearchController stockController,
                         TradingController tradingController,
                         TrendsController trendsController,
                         TrendsViewModel trendsViewModel,
                         PortfolioController portfolioController,
                         Runnable onLogout,
                         String username,
                         ExpenseRepository expenseRepository,
                         Consumer<String> showTrackerView) {
        this.dashController = dashController;
        this.stockController = stockController;
        this.tradingController = tradingController;
        this.trendsController = trendsController;
        this.trendsViewModel = trendsViewModel;
        this.portfolioController = portfolioController;
        this.onLogout = onLogout;
        this.username = username;
        this.expenseRepository = expenseRepository;
        this.showTrackerView = showTrackerView;

        setTitle("FinWise");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        // Top bar with greeting + logout
        JPanel topBar = new JPanel(new BorderLayout());
        JLabel greeting = new JLabel("Welcome, " + (username == null ? "User" : username) + "!");
        JButton logoutBtn = new JButton("Log out");
        logoutBtn.addActionListener(e -> {
            dispose();
            onLogout.run();
        });
        topBar.add(greeting, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);

        // Tabs: Home (placeholder), News, Tracker, Stock
        tabs.addTab("Home", buildHomePanel());
        tabs.addTab("News", buildTabPlaceholder("Open the News window…"));
        tabs.addTab("Tracker", buildTabPlaceholder("Open the Tracker window…"));
        tabs.addTab("Stock", buildTabPlaceholder("Open the Stock window…"));
        tabs.addTab("Trading", buildTabPlaceholder("Open the Trading window"));
        tabs.addTab("Trends", buildTabPlaceholder("Open the Trends window…"));
        tabs.addTab("Portfolio", buildTabPlaceholder("Open the Portfolio window"));

        // When user selects a tab, open a new window and reset back to Home
        tabs.addChangeListener(event -> {
            int idx = tabs.getSelectedIndex();
            if (idx == HOME_TAB) {
                // Refresh watchlist when returning to Home tab
                refreshWatchlist();
                return;
            }

            switch (idx) {
                case NEWS_TAB -> SwingUtilities.invokeLater(() -> {
                    // Create NewsController similar to Main.showNewsView()
                    NewsApiDAO newsApiDAO = new NewsApiDAO();
                    NewsView newsView = new NewsView(null);
                    interface_adapters.presenters.FetchNewsPresenter presenter =
                        new interface_adapters.presenters.FetchNewsPresenter(newsView);
                    usecase.fetch_news.FetchNewsInteractor interactor =
                        new usecase.fetch_news.FetchNewsInteractor(newsApiDAO, presenter);
                    interface_adapters.controllers.NewsController newsController =
                        new interface_adapters.controllers.NewsController(interactor, presenter);
                    newsView.setController(newsController);
                    newsController.fetchNews();
                    newsView.setVisible(true);
                });
                case TRACKER_TAB -> SwingUtilities.invokeLater(() ->
                        showTrackerView.accept(username));
                case STOCK_TAB -> SwingUtilities.invokeLater(() ->
                        new StockSearchView(stockController, username).setVisible(true));
                case TRADING_TAB -> SwingUtilities.invokeLater(() ->
                        new TradingView(tradingController, stockController, username).setVisible(true));
                case TRENDS_TAB -> SwingUtilities.invokeLater(() ->
                        new TrendsView(trendsController, trendsViewModel, username).setVisible(true));
                case PORTFOLIO_TAB -> SwingUtilities.invokeLater(() ->
                        new PortfolioView(portfolioController, username).setVisible(true));
                default -> {}
            }
            // Reset to Home to avoid repeated auto-opens on focus changes
            tabs.setSelectedIndex(HOME_TAB);
        });

        // Refresh watchlist when dashboard window gains focus
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                refreshWatchlist();
            }
        });

        // When user double-clicks a watched symbol, open the StockSearchView
        watchedList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // double-click
                    String symbol = watchedList.getSelectedValue();
                    if (symbol != null && !symbol.isEmpty()) {
                        SwingUtilities.invokeLater(() ->
                                new StockSearchView(stockController, username, symbol)
                                        .setVisible(true)
                        );
                    }
                }
            }
        });

        // Layout
        setLayout(new BorderLayout(8, 8));
        add(topBar, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHomePanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));

        // Left/center: info text
        JTextArea info = new JTextArea("""
                This is your Dashboard Home.

                Use the tabs above:
                • News    → opens the News window
                • Tracker → opens the Tracker window
                • Stock   → opens the Stock window
                • Trading → opens the Trading window
                • Portfolio → opens the Portfolio window

                On the right, you can see your watched stocks.
                Double-click one to open its details.
                """);
        info.setEditable(false);
        info.setMargin(new Insets(8, 8, 8, 8));
        p.add(info, BorderLayout.CENTER);

        // Right: watched stocks list
        JPanel watchPanel = new JPanel(new BorderLayout());
        watchPanel.setBorder(BorderFactory.createTitledBorder("Watched stocks"));

        // Populate the list from the controller
        refreshWatchlist();

        watchedList.setVisibleRowCount(10);

        JScrollPane scrollPane = new JScrollPane(watchedList);
        watchPanel.add(scrollPane, BorderLayout.CENTER);

        watchPanel.setPreferredSize(new Dimension(getWidth() / 5, getHeight()));
        p.add(watchPanel, BorderLayout.EAST);

        return p;
    }

    /**
     * Refreshes the watchlist display by fetching the latest watched stocks from the database.
     */
    private void refreshWatchlist() {
        DefaultListModel<String> model = (DefaultListModel<String>) watchedList.getModel();
        model.clear();
        for (String symbol : stockController.getWatchedSymbols(username)) {
            model.addElement(symbol);
        }
    }

    private JPanel buildTabPlaceholder(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.add(new JLabel(text));
        return p;
    }
}
