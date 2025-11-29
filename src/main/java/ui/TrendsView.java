package ui;

import interface_adapters.controllers.TrendsController;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Swing view for the Trends use case.
 */
public class TrendsView extends JFrame {

    private final TrendsController controller;
    private final TrendsViewModel viewModel;
    private final String username;

    private final JTextField startField = new JTextField(10);
    private final JTextField endField = new JTextField(10);
    private final JButton fetchBtn = new JButton("Fetch");

    private final ChartPanel chartPanel = new ChartPanel();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TrendsView(TrendsController controller,
                      TrendsViewModel viewModel,
                      String username) {

        this.controller = controller;
        this.viewModel = viewModel;
        this.username = username;

        setTitle("Trends");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Top panel: start/end date + fetch button
        JPanel top = new JPanel();
        top.add(new JLabel("Start (yyyy-MM-dd):"));
        top.add(startField);
        top.add(new JLabel("End:"));
        top.add(endField);
        top.add(fetchBtn);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);

        // Fetch button triggers use case
        fetchBtn.addActionListener(e -> fetchAndDraw());
    }

    private void fetchAndDraw() {
        try {
            LocalDate start = LocalDate.parse(startField.getText(), FMT);
            LocalDate end = LocalDate.parse(endField.getText(), FMT);

            controller.onViewTrends(username, start, end);
            chartPanel.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid date format. Use yyyy-MM-dd.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private class ChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Map<LocalDate, Map<String, Double>> data = viewModel.getTotalExpenses();

            if (data == null || data.isEmpty()) {
                g.drawString("No data", getWidth() / 2 - 20, getHeight() / 2);
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2f));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padding = 60;
            int width = getWidth() - 2 * padding;
            int height = getHeight() - 2 * padding;

            // Sorted dates
            List<LocalDate> dates = new ArrayList<>(data.keySet());
            Collections.sort(dates);
            int n = dates.size();

            // X positions
            Map<LocalDate, Integer> xMap = new HashMap<>();
            for (int i = 0; i < n; i++) {
                int x = padding + (int)((i / (double)(n - 1)) * width);
                xMap.put(dates.get(i), x);
            }

            // Y-axis scaling
            double maxAmount = data.values().stream()
                    .flatMap(m -> m.values().stream())
                    .max(Double::compareTo)
                    .orElse(1.0);

            int numYTicks = 5;
            for (int i = 0; i <= numYTicks; i++) {
                double value = i * maxAmount / numYTicks;
                int yPos = padding + height - (int)(value / maxAmount * height);
                g2.drawLine(padding - 5, yPos, padding, yPos); // tick
                g2.drawString(String.format("%.2f", value), padding - 50, yPos + 5);
            }

            // Expense types
            Set<String> expenseTypes = new HashSet<>();
            for (Map<String, Double> inner : data.values()) {
                expenseTypes.addAll(inner.keySet());
            }

            Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE};
            int colorIdx = 0;

            for (String type : expenseTypes) {
                g2.setColor(colors[colorIdx % colors.length]);
                colorIdx++;

                int prevX = -1, prevY = -1;
                for (LocalDate date : dates) {
                    double amount = data.get(date).getOrDefault(type, 0.0);
                    int x = xMap.get(date);
                    int y = padding + height - (int)(amount / maxAmount * height);

                    if (prevX != -1) g2.drawLine(prevX, prevY, x, y);

                    prevX = x;
                    prevY = y;
                }

                // Legend
                g2.drawString(type, getWidth() - 120, padding + (colorIdx * 15));
            }

            // Draw axes
            g2.setColor(Color.BLACK);
            g2.drawLine(padding, padding, padding, padding + height);           // Y-axis
            g2.drawLine(padding, padding + height, padding + width, padding + height); // X-axis

            // Draw X-axis labels
            for (int i = 0; i < n; i++) {
                LocalDate date = dates.get(i);
                int xPos = xMap.get(date);
                int yPos = padding + height + 20;

                String label = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                // Rotate long labels
                g2.rotate(-Math.PI / 4, xPos, yPos);
                g2.drawString(label, xPos - 20, yPos);
                g2.rotate(Math.PI / 4, xPos, yPos);

                // Tick
                g2.drawLine(xPos, padding + height, xPos, padding + height + 5);
            }
        }
    }
}
