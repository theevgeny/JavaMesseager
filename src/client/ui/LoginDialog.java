package client.ui;

import client.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginDialog {
    private final Client client;
    private JFrame frame;
    private JTextField ipField, usernameField;
    private JTextField portField;
    private JPasswordField passwordField;

    public LoginDialog(Client client) {
        this.client = client;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Мессенджер - Вход");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ColorTheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Мессенджер");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ColorTheme.ACCENT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        ipField = addField(panel, gbc, "IP сервера:", "127.0.0.1", 1);
        portField = addField(panel, gbc, "Порт:", "25565", 2);
        usernameField = addField(panel, gbc, "Логин:", "", 3);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel passLabel = createLabel("Пароль:");
        panel.add(passLabel, gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField();
        styleTextField(passwordField);
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);

        JButton loginBtn = createButton("Войти", ColorTheme.ACCENT);
        JButton registerBtn = createButton("Регистрация", new Color(100, 100, 100));

        loginBtn.addActionListener(e -> attemptLogin(false));
        registerBtn.addActionListener(e -> attemptLogin(true));

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        panel.add(buttonPanel, gbc);

        frame.add(panel);
    }

    private JTextField addField(JPanel panel, GridBagConstraints gbc, String label, String defaultValue, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        panel.add(createLabel(label), gbc);
        gbc.gridx = 1;
        JTextField field = new JTextField(defaultValue);
        styleTextField(field);
        panel.add(field, gbc);
        return field;
    }

    private void attemptLogin(boolean isRegister) {
        String serverIp = ipField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            int port = Integer.parseInt(portField.getText().trim());

            if (username.isEmpty() || password.isEmpty()) {
                showError("Заполните все поля");
                return;
            }

            if (isRegister && password.length() < 3) {
                showError("Пароль должен быть не менее 3 символов");
                return;
            }

            client.attemptLogin(serverIp, port, username, password, isRegister);

        } catch (NumberFormatException ex) {
            showError("Неверный номер порта");
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(ColorTheme.TEXT);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setBackground(ColorTheme.INPUT_BG);
        field.setForeground(ColorTheme.TEXT);
        field.setCaretColor(ColorTheme.TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorTheme.BORDER),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void show() { frame.setVisible(true); }

    public void close() { frame.dispose(); }

    public void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(frame, message, "Информация", JOptionPane.INFORMATION_MESSAGE);
    }
}