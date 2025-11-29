package ui;

import data.ExpenseRepository;
import interface_adapters.controllers.DashboardController;
import interface_adapters.controllers.StockSearchController;

import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardView extends JFrame {

    private final JList<String> watchedList = new JList<>(new DefaultListModel<>());
    private final DashboardController dashController;
    private final StockSearchController stockController;
//    private final PortfolioController portfolioController;
    private final Runnable onLogout;
    private final String username;
    private final ExpenseRepository expenseRepository;

    private final JTabbedPane tabs = new JTabbedPane();

    // Indices for tabs
    private static final int HOME_TAB = 0;
    private static final int NEWS_TAB = 1;
    private static final int TRACKER_TAB = 2;
    private static final int STOCK_TAB = 3;
    private static final int PORTFOLIO_TAB = 4;

    public DashboardView(DashboardController dashController,
                         StockSearchController stockController,
                         Runnable onLogout,
                         String username,
                         ExpenseRepository expenseRepository) {
        this.dashController = dashController;
        this.stockController = stockController;
//        this.portfolioController = portfolioController;
        this.onLogout = onLogout;
        this.username = username;
        this.expenseRepository = expenseRepository;

        setTitle("Dashboard");
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
        tabs.addTab("Portfolio", buildTabPlaceholder("Open the Portfolio window"));

        // When user selects a tab, open a new window and reset back to Home
        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            if (idx == HOME_TAB) {
                // Refresh watchlist when returning to Home tab
                refreshWatchlist();
                return;
            }

            switch (idx) {
                case NEWS_TAB -> SwingUtilities.invokeLater(() -> {
                    // Create NewsController similar to Main.showNewsView()
                    data.news.NewsApiDAO newsApiDAO = new data.news.NewsApiDAO();
                    ui.NewsView newsView = new ui.NewsView(null);
                    interface_adapters.presenters.FetchNewsPresenter presenter =
                        new interface_adapters.presenters.FetchNewsPresenter(newsView);
                    use_case.fetch_news.FetchNewsInteractor interactor =
                        new use_case.fetch_news.FetchNewsInteractor(newsApiDAO, presenter);
                    interface_adapters.controllers.NewsController newsController =
                        new interface_adapters.controllers.NewsController(interactor, presenter);
                    newsView.setController(newsController);
                    newsController.fetchNews();
                    newsView.setVisible(true);
                });
                case TRACKER_TAB -> SwingUtilities.invokeLater(() ->
                        new ui.TrackerView(username, expenseRepository).setVisible(true));
                case STOCK_TAB -> SwingUtilities.invokeLater(() ->
                        new ui.StockSearchView(stockController, username).setVisible(true));
//                case PORTFOLIO_TAB -> SwingUtilities.invokeLater(() ->
//                        new  ui.PortfolioView(portfolioController, username).setVisible(true));
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
