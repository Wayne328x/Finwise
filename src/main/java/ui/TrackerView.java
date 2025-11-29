package ui;

import data.ExpenseRepository;
import data.RegisteredExpenseRepository;
import entity.Expense;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TrackerView extends JFrame {

    private final String username;
    private final ExpenseRepository expenseRepository;

    private final DefaultTableModel tableModel;
    private final JTable table;

    private final JLabel totalLabel = new JLabel("Total: $0.00");

    private final JTextField datetimeField = new JTextField(16);
    private final JComboBox<String> typeCombo = new JComboBox<>(new String[]{
            "Food", "Technology", "Leisure", "Transport", "Other"
    });
    private final JTextField amountField = new JTextField(8);

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TrackerView(String username, ExpenseRepository expenseRepository) {
        this.username = username;
        this.expenseRepository = expenseRepository;

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
        List<Expense> expenses = expenseRepository.findByUsername(username);
        for (Expense expense : expenses) {
            tableModel.addRow(new Object[] {
                    expense.getDatetime(),
                    expense.getType(),
                    String.format("%.2f", expense.getAmount())
            });
        } updateTotal();
    }

    private void updateTotal() {
        double total = expenseRepository.getTotalForUser(username);
        totalLabel.setText("Total: " + String.format("%.2f", total));
    }

    private void onAddExpense() {
        String datetime = datetimeField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        String amountText = amountField.getText().trim();
        double amount;

        if (datetime.isEmpty() || type == null || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill up all fields!",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a number!",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            expenseRepository.add(username, datetime, type, amount);
            tableModel.insertRow(0, new Object[] {
                    datetime,
                    type,
                    String.format("%.2f", amount)
            });
            updateTotal();

            datetimeField.setText(LocalDateTime.now().format(formatter));
            amountField.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to add expense!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
