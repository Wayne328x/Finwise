package ui;

import controllers.DashboardController;
import data.ExpenseRepository;

import javax.swing.*;
import java.awt.*;

public class DashboardView extends JFrame {

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
        setSize(500, 350);
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

        // When user selects a tab, open a new window and reset back to Home
        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            if (idx == HOME_TAB) return;

            switch (idx) {
                case NEWS_TAB -> SwingUtilities.invokeLater(() -> new ui.NewsView().setVisible(true));
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
        expensesBtn.addActionListener(e -> onTrackExpenses.run());
        trendsBtn.addActionListener(e -> onFinancialTrends.run());
        stockBtn.addActionListener(e -> onStockPrices.run());
        investBtn.addActionListener(e -> onSimulatedInvestment.run());
        portfolioBtn.addActionListener(e -> onPortfolioAnalysis.run());
        newsBtn.addActionListener(e -> onMarketNews.run());

        // Layout
        setLayout(new BorderLayout(8, 8));
        add(topBar, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHomePanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTextArea info = new JTextArea("""
                This is your Dashboard Home.

                Use the tabs above:
                • News    → opens the News window
                • Tracker → opens the Tracker window
                • Stock   → opens the Stock window
                """);
        info.setEditable(false);
        info.setMargin(new Insets(8, 8, 8, 8));
        p.add(info, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildTabPlaceholder(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.add(new JLabel(text));
        return p;
    }

    private JPanel buildHomePanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTextArea info = new JTextArea("""
                This is your Dashboard Home.

                Use the tabs above:
                • News    → opens the News window
                • Tracker → opens the Tracker window
                • Stock   → opens the Stock window
                """);
        info.setEditable(false);
        info.setMargin(new Insets(8, 8, 8, 8));
        p.add(info, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildTabPlaceholder(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.add(new JLabel(text));
        return p;
    }
}
