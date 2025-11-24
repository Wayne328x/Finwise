package ui;
import interface_adapters.controllers.TradingController;
import use_case.trading.TradingInputData;
import use_case.trading.TradingViewModel;
import javax.swing.*;
import java.awt.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;



public class TradingView extends JFrame {
    private final TradingController controller;
    private final String username;

    private final JTextField symbolField = new JTextField(10);
    private final JSpinner sharesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    private final JRadioButton buyRadioButton = new JRadioButton("Buy", true);
    private final JLabel cashLabel = new JLabel("Cash: $0.00");
    private final JLabel holdingLabel = new JLabel("Holdings: 0 shares at $0.00 average cost");
    private final JLabel valueLabel = new JLabel("Total Value: $0.00");
    private final JLabel messageLabel = new JLabel("");

    public TradingView(TradingController controller, String username) {
        this.controller = controller;
        this.username = username;

        setTitle("Trading");
        setSize(420, 260);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        ButtonGroup actionGroup = new ButtonGroup();
        actionGroup.add(buyRadioButton);
        JRadioButton sellRadioButton = new JRadioButton("Sell");
        actionGroup.add(sellRadioButton);

        JPanel form = new JPanel();
        form.setLayout(new GridLayout(6, 2, 6, 6));
        form.add(new JLabel("Symbol:"));
        form.add(symbolField);
        form.add(new JLabel("Action:"));
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.add(buyRadioButton);
        actionPanel.add(sellRadioButton);
        form.add(actionPanel);
        form.add(new JLabel("Shares:"));
        form.add(sharesSpinner);
        form.add(cashLabel);
        form.add(holdingLabel);
        form.add(valueLabel);
        JButton placeOrderButton = new JButton("Place Order");
        form.add(placeOrderButton);

        setLayout(new BorderLayout(8, 8));
        add(form, BorderLayout.CENTER);
        add(messageLabel, BorderLayout.SOUTH);
        placeOrderButton.addActionListener(e -> placeOrder());
    }
    private void placeOrder() {
        String symbol = symbolField.getText().trim();
        int shares = (int) sharesSpinner.getValue();
        TradingInputData.Action action = buyRadioButton.isSelected() ?
                TradingInputData.Action.BUY : TradingInputData.Action.SELL;

        TradingViewModel viewModel = controller.placeOrder(username, symbol, shares, action);
        
        cashLabel.setText(String.format("Cash: $%.2f", viewModel.getCashAfterTrade()));
        holdingLabel.setText(String.format("Holdings: %d shares at $%.2f average cost",
                viewModel.getTotalSharesAfterTrade(), viewModel.getAverageCostAfterTrade()));
        valueLabel.setText(String.format("Total Value: $%.2f", viewModel.getTotalHoldingValueAfterTrade()));
        messageLabel.setText(viewModel.getMessage());
    }

}
