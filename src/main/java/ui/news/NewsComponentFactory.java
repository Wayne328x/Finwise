package ui.news;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A factory class responsible for creating and styling UI components for the NewsView.
 * This helps to reduce the coupling of NewsView to specific Swing component implementations
 * and adheres to CheckStyle rules by encapsulating component creation logic.
 */
public class NewsComponentFactory {

    /**
     * Creates a styled JLabel for displaying news titles.
     *
     * @param initialText The initial text to set for the label.
     * @return A styled JLabel instance.
     */
    public JLabel createTitleLabel(String initialText) {
        final int fontSize = 18;
        final JLabel titleLabel = new JLabel(initialText);
        titleLabel.setFont(new Font("Arial", Font.BOLD, fontSize));
        titleLabel.setForeground(Color.BLUE);
        titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return titleLabel;
    }

    /**
     * Creates a styled JLabel for displaying news publication times.
     *
     * @param initialText The initial text to set for the label.
     * @return A styled JLabel instance.
     */
    public JLabel createTimeLabel(String initialText) {
        final int fontSize = 12;
        final JLabel timeLabel = new JLabel(initialText);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, fontSize));
        timeLabel.setForeground(Color.DARK_GRAY);
        return timeLabel;
    }

    /**
     * Creates a styled JButton for navigation (e.g., previous/next page).
     *
     * @param text The text to display on the button.
     * @return A styled JButton instance.
     */
    public JButton createNavigationButton(String text) {
        return new JButton(text);
    }

    /**
     * Creates a JPanel with BorderLayout.
     *
     * @return A JPanel with BorderLayout.
     */
    public JPanel createNewsPanel() {
        final JPanel newsPanel = new JPanel();
        newsPanel.setLayout(new BorderLayout());
        return newsPanel;
    }

    /**
     * Creates a JPanel that stacks title and time vertically and aligns to the left.
     * Uses GridLayout(2, 1).
     *
     * @return A stacking JPanel.
     */
    public JPanel createVerticalStackingPanel() {
        final JPanel textPanel = new JPanel(new java.awt.GridLayout(2, 1));
        return textPanel;
    }

    // TODO 假如还有api的话试着把UI改回原来的样子并且录屏
//    public JPanel createVerticalStackingPanel() {
//        JPanel textPanel = new JPanel();
//
//        // 1. 设置 BoxLayout，沿 Y 轴 (垂直) 堆叠
//        textPanel.setLayout(new javax.swing.BoxLayout(textPanel, javax.swing.BoxLayout.Y_AXIS));
//
//        // 2. 关键：强制组件在 X 轴上左对齐，以避免 FlowLayout 居中残留的影响
//        //    (这确保了标题和时间不会被推到面板中间)
//        textPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
//
//        return textPanel;
//    }

    /**
     * Creates a JPanel with a GridLayout for the main news display.
     *
     * @param rows The number of rows for the grid layout.
     * @param cols The number of columns for the grid layout.
     * @return A JPanel with GridLayout.
     */
    public JPanel createMainPanelWithGridLayout(int rows, int cols) {
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new java.awt.GridLayout(rows, cols));
        return mainPanel;
    }
}
