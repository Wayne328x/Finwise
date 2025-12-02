package ui.tracker;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import entity.Expense;
import interfaceadapters.controllers.TrackerController;
import usecase.add_expense.AddExpenseOutputData;
import usecase.list_expenses.ListExpensesOutputData;

/**
 * View for expense tracking.
 */
@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:SuppressWarnings"})
public class TrackerView extends JFrame {

    private static final int VIEW_PANEL_WIDTH = 800;
    private static final int VIEW_PANEL_HEIGHT = 500;
    private static final String CENT_DECIMAL_COUNT = "%.2f";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String username;
    private final TrackerController trackerController;

    private final DefaultTableModel tableModel;

    private final JLabel totalLabel = new JLabel("Total: $0.00");

    private final JTextField datetimeField = new JTextField(16);
    private final JComboBox<String> typeCombo = new JComboBox<>(new String[]{
        "Food", "Technology", "Leisure", "Transport", "Other",
    });
    private final JTextField amountField = new JTextField(8);

    public TrackerView(String username, TrackerController controller) {
        this.username = username;
        this.trackerController = controller;

        setTitle("Expense Tracker");
        setSize(VIEW_PANEL_WIDTH, VIEW_PANEL_HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(new Object[]{"Date/Time", "Type", "Amount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        final JTable table = new JTable(tableModel);
        final JScrollPane scrollPane = new JScrollPane(table);

        final JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());

        final JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(totalLabel);

        final JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formPanel.add(new JLabel("Date/Time"));
        formPanel.add(datetimeField);
        formPanel.add(new JLabel("Type"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Amount"));
        formPanel.add(amountField);
        final JButton addButton = new JButton("Add");
        formPanel.add(addButton);

        bottomPanel.add(totalPanel, BorderLayout.NORTH);
        bottomPanel.add(formPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        datetimeField.setText(LocalDateTime.now().format(FORMATTER));

        loadExpenses();

        addButton.addActionListener(event -> onAddExpense());
    }

    private void loadExpenses() {
        tableModel.setRowCount(0);

        final ListExpensesOutputData output = trackerController.loadExpenses(username);
        for (Expense expense : output.getExpenses()) {
            tableModel.addRow(new Object[] {
                    expense.getDatetime(),
                    expense.getType(),
                    String.format(CENT_DECIMAL_COUNT, expense.getAmount()),
            });
        }

        totalLabel.setText("Total: " + String.format(CENT_DECIMAL_COUNT, output.getTotal()));
    }

    private void onAddExpense() {
        final String datetime = datetimeField.getText().trim();
        final String type = (String) typeCombo.getSelectedItem();
        final String amountText = amountField.getText().trim();

        final AddExpenseOutputData result =
                trackerController.addExpense(username, datetime, type, amountText);

        if (result.isSuccess()) {
            final double amount = result.getAmount();
            tableModel.insertRow(0, new Object[] {
                    result.getDatetime(),
                    result.getType(),
                    String.format(CENT_DECIMAL_COUNT, amount),
            });

            final ListExpensesOutputData listOutput = trackerController.loadExpenses(username);
            totalLabel.setText("Total: " + String.format(CENT_DECIMAL_COUNT, listOutput.getTotal()));

            // Reset fields
            datetimeField.setText(LocalDateTime.now().format(FORMATTER));
            amountField.setText("");
        }
        else {
            JOptionPane.showMessageDialog(this, result.getMessage(),
                    "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }
}
