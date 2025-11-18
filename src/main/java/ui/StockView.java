package ui;

import javax.swing.*;
import java.awt.*;

public class StockView extends JFrame {
    public StockView() {
        setTitle("Stock");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea stub = new JTextArea("""
                Stock Section
                --------------
                TODO: Implement stock lookups/quotes here.
                """);
        stub.setEditable(false);
        stub.setMargin(new Insets(8, 8, 8, 8));

        add(new JScrollPane(stub), BorderLayout.CENTER);
    }
}
