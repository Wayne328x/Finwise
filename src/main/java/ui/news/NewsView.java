package ui.news;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import interfaceadapters.news.NewsController;
import usecase.fetch_news.NewsDataAccessInterface;

public class NewsView extends javax.swing.JFrame {

    private final int newsShownSize = 3;
    private final JLabel[] titleLabels = new JLabel[newsShownSize];
    private final JLabel[] timeLabels = new JLabel[newsShownSize];
    private final JButton prevButton;
    private final JButton nextButton;
    private NewsController controller;
    private final NewsComponentFactory componentFactory;

    public NewsView(NewsController controller) {
        this.controller = controller;
        this.componentFactory = new NewsComponentFactory();
        
        this.prevButton = componentFactory.createNavigationButton("previous page");
        this.nextButton = componentFactory.createNavigationButton("next page");

        initializeUi();
    }

    private void initializeUi() {
        final int titleFontWidth = 600;
        final int titleFontHeight = 400;
        setTitle("News");
        setSize(titleFontWidth, titleFontHeight);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        final JPanel mainPanel = componentFactory.createMainPanelWithGridLayout(4, 1);

        for (int i = 0; i < newsShownSize; i++) {
            final JPanel newsPanel = componentFactory.createNewsPanel();

            titleLabels[i] = componentFactory.createTitleLabel("Loading the titles...");

            timeLabels[i] = componentFactory.createTimeLabel("Time published: ");

            final JPanel textPanel = componentFactory.createVerticalStackingPanel();
            textPanel.add(titleLabels[i]);
            textPanel.add(timeLabels[i]);

            newsPanel.add(textPanel, BorderLayout.CENTER);
            mainPanel.add(newsPanel);
        }

        final JPanel buttonPanel = new JPanel();
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Update the view as the user goes to previous or next page.
     * @param viewModel is what needs to be updated. */
    public void updateView(NewsViewModel viewModel) {
        for (int i = 0; i < newsShownSize; i++) {

            final String title = viewModel.getTitles().get(i);
            final String time = viewModel.getPublishTimes().get(i);
            final String url = viewModel.getUrls().get(i);

            titleLabels[i].setText(title);
            timeLabels[i].setText(time);

            for (MouseListener ml : titleLabels[i].getMouseListeners()) {
                titleLabels[i].removeMouseListener(ml);
            }

            titleLabels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URI(url));
                    }
                    catch (NewsDataAccessInterface.DataFetchException | URISyntaxException | IOException exception) {
                        exception.printStackTrace();
                    }
                }
            });
        }

        prevButton.setEnabled(viewModel.isHasPrevPage());
        nextButton.setEnabled(viewModel.isHasNextPage());
    }

    /**
     * Show the error message.
     * @param message why the error happens. */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Failed to fetch news: ", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Set controller for turning pages.
     * @param controller . */
    public void setController(NewsController controller) {
        this.controller = controller;

        prevButton.addActionListener(event -> this.controller.goToPreviousPage());
        nextButton.addActionListener(event -> this.controller.goToNextPage());
    }

}
