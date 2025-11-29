package ui;

import use_case.signup.SignUpOutputData;

import javax.swing.*;

import interface_adapters.controllers.SignUpController;

import java.awt.*;

public class SignUpView extends JFrame {

    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);
    private final JButton signUpButton = new JButton("Sign Up");
    private final JButton backToLoginButton = new JButton("Back to Login");

    public SignUpView(SignUpController signUpController, Runnable showLoginView) {
        setTitle("Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 180);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(signUpButton);
        panel.add(backToLoginButton);
        add(panel);

        signUpButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            SignUpOutputData output = signUpController.signUp(username, password);
            JOptionPane.showMessageDialog(this, output.getMessage());

            if (output.isSuccess()) {
                usernameField.setText("");
                passwordField.setText("");
                showLoginView.run();
                dispose();
            }
        });

        backToLoginButton.addActionListener(e -> {
            showLoginView.run();
            dispose();
        });
    }
}
