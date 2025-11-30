package ui.tracker;

import data.ExpenseRepository;
import entity.Expense;
import interface_adapters.controllers.TrackerController;
import use_case.list_expenses.ListExpensesOutputData;
import use_case.add_expense.AddExpenseOutputData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TrackerView extends JFrame {

    private final String username;
    private final TrackerController trackerController;

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JLabel totalLabel = new JLabel("Total: $0.00");

    private final JTextField datetimeField = new JTextField(16);
    private final JComboBox<String> typeCombo = new JComboBox<>(new String[]{
            "Food", "Technology", "Leisure", "Transport", "Other"
    });
    private final JTextField amountField = new JTextField(8);

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TrackerView(String username, TrackerController trackerController) {
        this.username = username;
        this.trackerController = trackerController;

        setTitle("Expense Tracker");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(new Object[]{"Date/Time", "Type", "Amount"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout());

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(totalLabel);

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formPanel.add(new JLabel("Date/Time"));
        formPanel.add(datetimeField);
        formPanel.add(new JLabel("Type"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Amount"));
        formPanel.add(amountField);
        JButton addButton = new JButton("Add");
        formPanel.add(addButton);

        bottomPanel.add(totalPanel, BorderLayout.NORTH);
        bottomPanel.add(formPanel, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        datetimeField.setText(LocalDateTime.now().format(formatter));

        loadExpenses();

        addButton.addActionListener(e -> onAddExpense());
    }

    private void loadExpenses() {
        tableModel.setRowCount(0);

        ListExpensesOutputData output = trackerController.loadExpenses(username);
        for (Expense expense : output.getExpenses()) {
            tableModel.addRow(new Object[] {
                    expense.getDatetime(),
                    expense.getType(),
                    String.format("%.2f", expense.getAmount())
            });
        }

        // total from output
        totalLabel.setText("Total: " + String.format("%.2f", output.getTotal()));
    }

    private void onAddExpense() {
        String datetime = datetimeField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        String amountText = amountField.getText().trim();

        AddExpenseOutputData result =
                trackerController.addExpense(username, datetime, type, amountText);

        if (!result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage(),
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Success: add row + reload total
        double amount = result.getAmount();
        tableModel.insertRow(0, new Object[] {
                result.getDatetime(),
                result.getType(),
                String.format("%.2f", amount)
        });

        // Refresh total using use case again
        ListExpensesOutputData listOutput = trackerController.loadExpenses(username);
        totalLabel.setText("Total: " + String.format("%.2f", listOutput.getTotal()));

        // Reset fields
        datetimeField.setText(LocalDateTime.now().format(formatter));
        amountField.setText("");

        // Optional: success dialog
        JOptionPane.showMessageDialog(this, result.getMessage());
    }

}
