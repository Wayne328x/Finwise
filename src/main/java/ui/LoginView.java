package ui;

import use_case.login.LoginOutputData;

import javax.swing.*;

import interface_adapters.controllers.LoginController;

import java.awt.*;

public class LoginView extends JFrame {

    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JButton loginButton = new JButton("Login");
    private final JButton signUpButton = new JButton("Sign Up");

    public LoginView(LoginController loginController, Runnable showSignUpView,
                     java.util.function.Consumer<String> showDashboard) {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 180);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3 ,2, 5, 5));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(signUpButton);
        add(panel);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            LoginOutputData result = loginController.login(username, password);
            JOptionPane.showMessageDialog(this, result.getMessage());
            if (result.isSuccess()) {
                showDashboard.accept(username); // pass username up to Main
                dispose();
            }
        });

        signUpButton.addActionListener(e -> {
            showSignUpView.run();
            dispose();
        });

    }
}
