package ui.trading;
import interfaceadapters.controllers.TradingController;
import interfaceadapters.controllers.StockSearchController;
import usecase.trading.TradingInputData;
import usecase.trading.TradingViewModel;
import usecase.stocksearch.StockSearchOutputData;
import data.AlphaVantage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;



public class TradingView extends JFrame {
    private final TradingController controller;
    private final StockSearchController stockController;
    private final String username;

    private final JTextField symbolField = new JTextField(10);
    private final JList<AlphaVantage.StockSearchResult> suggestions = new JList<>();
    private final JScrollPane suggestionsScroll = new JScrollPane(suggestions);
    private SwingWorker<StockSearchOutputData, Void> currentSearchWorker;
    private final Timer searchTimer = new Timer(400, e -> runSearch());
    private final JSpinner sharesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    private final JRadioButton buyRadioButton = new JRadioButton("Buy", true);
    private final JLabel cashLabel = new JLabel("Cash: $0.00");
    private final JLabel holdingLabel = new JLabel("Holdings: 0 shares at $0.00 average cost");
    private final JLabel valueLabel = new JLabel("Total Value: $0.00");
    private final JLabel messageLabel = new JLabel("");
    private final JLabel priceLabel = new JLabel("Price: $");

    public TradingView(TradingController controller, StockSearchController stockSearchController, String username) {
        this.controller = controller;
        this.stockController = stockSearchController;
        this.username = username;

        setTitle("Trading");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        searchTimer.setRepeats(false);

        ButtonGroup actionGroup = new ButtonGroup();
        actionGroup.add(buyRadioButton);
        JRadioButton sellRadioButton = new JRadioButton("Sell");
        actionGroup.add(sellRadioButton);

        suggestionsScroll.setVisible(false);
        suggestionsScroll.setPreferredSize(new Dimension(700, 280));
        suggestionsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        suggestionsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        suggestionsScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        suggestions.setVisibleRowCount(8);
        suggestions.setFixedCellHeight(28);
        suggestions.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            DefaultListCellRenderer base = new DefaultListCellRenderer();
            JLabel lbl = (JLabel) base.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof AlphaVantage.StockSearchResult) {
                AlphaVantage.StockSearchResult r = (AlphaVantage.StockSearchResult) value;
                lbl.setText(r.getSymbol() + " — " + r.getName() + " (" + r.getExchange() + ")");
                lbl.setFont(lbl.getFont().deriveFont(13f));
            }
            return lbl;
        });
        symbolField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        symbolField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel symbolColumn = new JPanel();
        symbolColumn.setLayout(new BoxLayout(symbolColumn, BoxLayout.Y_AXIS));
        symbolColumn.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        JLabel symbolLabel = new JLabel("Symbol:");
        symbolLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        symbolColumn.add(symbolLabel);
        symbolColumn.add(Box.createVerticalStrut(4));
        symbolColumn.add(symbolField);
        symbolColumn.add(Box.createVerticalStrut(6));
        suggestionsScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        symbolColumn.add(suggestionsScroll);

        JPanel form = new JPanel();
        form.setLayout(new GridLayout(6, 2, 10, 14));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        form.add(new JLabel("Action:"));
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.add(buyRadioButton);
        actionPanel.add(sellRadioButton);
        form.add(actionPanel);
        form.add(new JLabel("Shares:"));
        form.add(sharesSpinner);
        form.add(new JLabel("Current Price:"));
        form.add(priceLabel);
        form.add(cashLabel);
        form.add(holdingLabel);
        form.add(valueLabel);
        JButton placeOrderButton = new JButton("Place Order");
        form.add(placeOrderButton);

        JPanel outer = new JPanel(new BorderLayout(0, 12));
        outer.add(symbolColumn, BorderLayout.NORTH);
        outer.add(form, BorderLayout.CENTER);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));
        messageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        outer.add(messagePanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(outer, BorderLayout.CENTER);
        placeOrderButton.addActionListener(e -> placeOrder());

        symbolField.addActionListener(e -> loadPrice(symbolField.getText().trim()));
        symbolField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                suggestionsScroll.setVisible(false);
            }
        });
        symbolField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                restartTimer();
            }
            public void removeUpdate(DocumentEvent e) {
                restartTimer();
            }
            public void changedUpdate(DocumentEvent e) {
                restartTimer();
            }
            private void restartTimer() { searchTimer.restart(); }
        });

        suggestions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    AlphaVantage.StockSearchResult selected = suggestions.getSelectedValue();
                    if (selected != null) {
                        symbolField.setText(selected.getSymbol());
                        loadPrice(selected.getSymbol());
                        suggestionsScroll.setVisible(false);
                    }
                }
            }
        });

        updateLabels(controller.getViewModel());
    }

    private void runSearch() {
        String query = symbolField.getText().trim();
        if (!symbolField.isFocusOwner() || query.length() < 2) {
            suggestionsScroll.setVisible(false);
            revalidate();
            repaint();
            return;
        }
        if (currentSearchWorker != null && !currentSearchWorker.isDone()) {
            currentSearchWorker.cancel(true);
        }
        currentSearchWorker = new SwingWorker<>() {
            @Override
            protected StockSearchOutputData doInBackground() {
                return stockController.search(query);
            }
            @Override
            protected void done() {
                if (isCancelled()) return;
                try {
                    StockSearchOutputData output = get();
                    if (!output.isSuccess() || output.getResults().isEmpty()) {
                        suggestionsScroll.setVisible(false);
                        return;
                    }
                    suggestions.setListData(output.getResults().toArray(new AlphaVantage.StockSearchResult[0]));
                    suggestionsScroll.setVisible(true);
                    revalidate();
                    repaint();
                } catch (Exception ex) {
                    suggestionsScroll.setVisible(false);
                }
            }
        };
        currentSearchWorker.execute();
    }

    private void loadPrice(String symbol) {
        if (symbol == null || symbol.isBlank()) {
            priceLabel.setText("Price: $");
            return;
        }
        priceLabel.setText("Price: loading...");
        new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() throws Exception {
                return new AlphaVantage().getQuote(symbol).getPrice();
            }
            @Override
            protected void done() {
                try {
                    double p = get();
                    priceLabel.setText(String.format("Price: $%.2f", p));
                } catch (Exception ex) {
                    priceLabel.setText("Price: currently unavailable");
                }
            }
        }.execute();
    }


    private void placeOrder() {
        String symbol = symbolField.getText().trim();
        int shares = (int) sharesSpinner.getValue();
        TradingInputData.Action action = buyRadioButton.isSelected() ?
                TradingInputData.Action.BUY : TradingInputData.Action.SELL;

        TradingViewModel viewModel = controller.placeOrder(username, symbol, shares, action);
        suggestionsScroll.setVisible(false);
        updateLabels(viewModel);
    }

    private void updateLabels(TradingViewModel vm) {
        cashLabel.setText(String.format("Cash: $%.2f", vm.getCashAfterTrade()));
        holdingLabel.setText(String.format("Holdings: %d shares at $%.2f average cost",
                vm.getTotalSharesAfterTrade(), vm.getAverageCostAfterTrade()));
        valueLabel.setText(String.format("Total Value: $%.2f", vm.getTotalHoldingValueAfterTrade()));
        messageLabel.setText(vm.getMessage());
    }


}
