package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URI;

import interface_adapters.controllers.NewsController;

public class NewsView extends JFrame {

    private final JLabel[] titleLabels = new JLabel[3];
    private final JLabel[] timeLabels = new JLabel[3];
    private final JButton prevButton = new JButton("previous page");
    private final JButton nextButton = new JButton("next page");

    private NewsController controller;

    public NewsView(NewsController controller) {
        this.controller = controller;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("News");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 1));

        for (int i = 0; i < 3; i++) {
            JPanel newsPanel = new JPanel();
            newsPanel.setLayout(new BorderLayout());

            titleLabels[i] = new JLabel("Loading the titles...");
            titleLabels[i].setFont(new Font("Arial", Font.BOLD, 18));
            titleLabels[i].setForeground(Color.BLUE);
            titleLabels[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            timeLabels[i] = new JLabel("Time published: ");
            timeLabels[i].setFont(new Font("Arial", Font.PLAIN, 12));
            timeLabels[i].setForeground(Color.DARK_GRAY);

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.add(titleLabels[i]);
            textPanel.add(timeLabels[i]);

            newsPanel.add(textPanel, BorderLayout.CENTER);
            mainPanel.add(newsPanel);
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void updateView(NewsViewModel vm) {
        for (int i = 0; i < 3; i++) {

            String title = vm.titles.get(i);
            String time = vm.publishTimes.get(i);
            String url = vm.urls.get(i);

            titleLabels[i].setText(title);
            timeLabels[i].setText(time);

            final String urlToOpen = url;

            for (MouseListener ml : titleLabels[i].getMouseListeners()) {
                titleLabels[i].removeMouseListener(ml);
            }

            titleLabels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URI(urlToOpen));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }

        prevButton.setEnabled(vm.hasPrevPage);
        nextButton.setEnabled(vm.hasNextPage);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void setController(NewsController controller) {
        this.controller = controller;

        prevButton.addActionListener(e -> this.controller.goToPreviousPage());
        nextButton.addActionListener(e -> this.controller.goToNextPage());
    }

}

