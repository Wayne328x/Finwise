package ui;

import use_case.case5.PortfolioViewModel;
import use_case.case5.HoldingRow;
import use_case.case5.SnapshotRow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import interface_adapters.controllers.PortfolioController;

import java.awt.*;
import java.util.List;

/**
 * UI layer for Use Case 5: Portfolio Analysis.
 * This view displays:
 *  - A table of current holdings
 *  - A line chart of portfolio performance
 *  - A table of portfolio performance snapshots over time
 * It belongs to the UI (View) layer in Clean Architecture and only communicates with the PortfolioController.
 */
public class PortfolioView extends JFrame {

    /**
     * @param controller the controller that triggers the portfolio analysis use case
     * @param username   the currently logged-in user whose portfolio is analyzed
     */
    public PortfolioView(PortfolioController controller,
                         String username) {

        setTitle("Portfolio Analysis");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ===== Step 1: Call the use case via controller =====
        PortfolioViewModel vm = controller.analyze(username);

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Header
        JLabel headerLabel = new JLabel(
                "Portfolio Analysis for " + vm.getUsername(),
                SwingConstants.CENTER
        );
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Center panel: holdings table + line chart + snapshots table
        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        if (!vm.hasData()) {
            JLabel noDataLabel = new JLabel(vm.getMessage(), SwingConstants.CENTER);
            noDataLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
            centerPanel.add(noDataLabel);
        } else {
            // ===== Holdings table (top) =====
            JTable holdingsTable = createHoldingsTable(vm.getHoldings());
            JScrollPane holdingsScroll = new JScrollPane(holdingsTable);
            holdingsScroll.setBorder(BorderFactory.createTitledBorder("Current Holdings"));

            // ===== Line chart (middle) =====
            JPanel chartPanel = new PortfolioChartPanel(vm.getSnapshots());

            // ===== Snapshots table (bottom) =====
            JTable snapshotsTable = createSnapshotsTable(vm.getSnapshots());
            JScrollPane snapshotsScroll = new JScrollPane(snapshotsTable);
            snapshotsScroll.setBorder(
                    BorderFactory.createTitledBorder("Portfolio Performance Over Time")
            );

            centerPanel.add(holdingsScroll);
            centerPanel.add(chartPanel);
            centerPanel.add(snapshotsScroll);
        }

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ===== Bottom panel: message + Back button =====
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JLabel messageLabel = new JLabel(vm.getMessage(), SwingConstants.LEFT);
        bottomPanel.add(messageLabel, BorderLayout.WEST);

        JButton backButton = new JButton("Back");
        bottomPanel.add(backButton, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Builds a JTable for the user's holdings (using HoldingRow from ViewModel).
     */
    private JTable createHoldingsTable(List<HoldingRow> holdings) {
        String[] columnNames = {"Symbol", "Shares", "Average Cost", "Total Cost"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (HoldingRow h : holdings) {
            Object[] row = new Object[]{
                    h.getSymbol(),
                    h.getShares(),
                    h.getAvgCost(),
                    h.getTotalCost()
            };
            model.addRow(row);
        }

        return new JTable(model);
    }

    /**
     * Builds a JTable for portfolio snapshots (time-series performance).
     */
    private JTable createSnapshotsTable(List<SnapshotRow> snapshots) {
        String[] columnNames = {"Date", "Total Cost", "Total Value", "Profit", "Profit Rate"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (SnapshotRow s : snapshots) {
            Object[] row = new Object[]{
                    s.getDate().toString(),
                    s.getTotalCost(),
                    s.getTotalValue(),
                    s.getProfit(),
                    String.format("%.2f%%", s.getProfitRate() * 100)
            };
            model.addRow(row);
        }

        return new JTable(model);
    }

    /**
     * Line chart panel for portfolio performance over time.
     * Here we draw the profit rate (%) over time using SnapshotRow.
     */
    class PortfolioChartPanel extends JPanel {

        private final List<SnapshotRow> snapshots;

        public PortfolioChartPanel(List<SnapshotRow> snapshots) {
            this.snapshots = snapshots;
            setBorder(BorderFactory.createTitledBorder("Portfolio Profit Rate Over Time"));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (snapshots == null || snapshots.size() < 2) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            int leftPadding = 60;
            int rightPadding = 20;
            int topPadding = 20;
            int bottomPadding = 40;

            int chartWidth = width - leftPadding - rightPadding;
            int chartHeight = height - topPadding - bottomPadding;

            int xAxisY = height - bottomPadding;
            int yAxisX = leftPadding;

            // X/Y axes
            g2.drawLine(yAxisX, topPadding, yAxisX, xAxisY);
            g2.drawLine(yAxisX, xAxisY, width - rightPadding, xAxisY);

            // Compute min/max profit rate (%) for scaling
            double minValue = Double.MAX_VALUE;
            double maxValue = Double.MIN_VALUE;
            for (SnapshotRow s : snapshots) {
                double v = s.getProfitRate() * 100.0; // percentage
                minValue = Math.min(minValue, v);
                maxValue = Math.max(maxValue, v);
            }
            if (maxValue == minValue) {
                maxValue += 1;
                minValue -= 1;
            }

            int n = snapshots.size();

            int prevX = -1;
            int prevY = -1;

            for (int i = 0; i < n; i++) {
                double v = snapshots.get(i).getProfitRate() * 100.0;

                double xRatio = (n == 1) ? 0.0 : (double) i / (n - 1);
                int x = yAxisX + (int) (xRatio * chartWidth);

                double yRatio = (maxValue - v) / (maxValue - minValue);
                int y = topPadding + (int) (yRatio * chartHeight);

                if (i > 0) {
                    g2.drawLine(prevX, prevY, x, y);
                }
                g2.fillOval(x - 3, y - 3, 6, 6);

                prevX = x;
                prevY = y;
            }

            // Y-axis labels (max / min)
            g2.drawString(String.format("%.1f%%", maxValue), 5, topPadding + 10);
            g2.drawString(String.format("%.1f%%", minValue), 5, xAxisY);

            // X-axis labels: first and last date
            String firstDate = snapshots.get(0).getDate().toString();
            String lastDate = snapshots.get(n - 1).getDate().toString();

            g2.drawString(firstDate, yAxisX, xAxisY + 20);
            g2.drawString(lastDate,
                    yAxisX + chartWidth - g2.getFontMetrics().stringWidth(lastDate),
                    xAxisY + 20);
        }
    }
}