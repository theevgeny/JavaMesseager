package client.ui;

import client.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsDialog {
    private JDialog dialog;
    private JCheckBox notificationsCheckbox;
    private JCheckBox soundCheckbox;
    private JComboBox<String> themeCombo;
    private JTextField fontSizeField;
    private final Client client;

    public SettingsDialog(JFrame parent, Client client) {
        this.client = client;
        dialog = new JDialog(parent, "Настройки", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ColorTheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        gbc.gridx = 0;
        gbc.gridy = 0;
        notificationsCheckbox = new JCheckBox("Включить уведомления");
        notificationsCheckbox.setBackground(ColorTheme.BACKGROUND);
        notificationsCheckbox.setForeground(ColorTheme.TEXT);
        notificationsCheckbox.setSelected(SettingsManager.areNotificationsEnabled());
        panel.add(notificationsCheckbox, gbc);

        gbc.gridy = 1;
        soundCheckbox = new JCheckBox("Включить звуки сообщений");
        soundCheckbox.setBackground(ColorTheme.BACKGROUND);
        soundCheckbox.setForeground(ColorTheme.TEXT);
        soundCheckbox.setSelected(SettingsManager.isSoundEnabled());
        panel.add(soundCheckbox, gbc);

        gbc.gridy = 2;
        panel.add(createLabel("Тема:"), gbc);
        gbc.gridx = 1;
        themeCombo = new JComboBox<>(new String[]{"Темная", "Светлая"});
        styleComboBox(themeCombo);
        String currentTheme = SettingsManager.getTheme();
        themeCombo.setSelectedItem(currentTheme.equals("Dark") ? "Темная" : "Светлая");
        panel.add(themeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(createLabel("Размер шрифта:"), gbc);
        gbc.gridx = 1;
        fontSizeField = new JTextField(String.valueOf(SettingsManager.getFontSize()));
        styleTextField(fontSizeField);
        panel.add(fontSizeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        JButton saveBtn = createButton("Сохранить", ColorTheme.ACCENT);
        JButton cancelBtn = createButton("Отмена", new Color(100, 100, 100));

        saveBtn.addActionListener(e -> saveSettings());
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
    }

    private void saveSettings() {
        boolean notif = notificationsCheckbox.isSelected();
        boolean sound = soundCheckbox.isSelected();
        String themeRaw = (String) themeCombo.getSelectedItem();
        String theme = themeRaw.equals("Темная") ? "Dark" : "Light";

        int fontSize;
        try {
            fontSize = Integer.parseInt(fontSizeField.getText().trim());
            if (fontSize < 8) fontSize = 8;
        } catch (NumberFormatException e) {
            fontSize = 13;
        }

        SettingsManager.setNotificationsEnabled(notif);
        SettingsManager.setSoundEnabled(sound);
        SettingsManager.setTheme(theme);
        SettingsManager.setFontSize(fontSize);
        SettingsManager.save();

        if (client.getMainWindow() != null) {
            client.getMainWindow().applySettings();
        }

        JOptionPane.showMessageDialog(dialog, "Настройки сохранены!", "Успех", JOptionPane.INFORMATION_MESSAGE);
        dialog.dispose();
    }

    public void show() {
        dialog.setVisible(true);
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

    private void styleComboBox(JComboBox<String> combo) {
        combo.setBackground(ColorTheme.INPUT_BG);
        combo.setForeground(ColorTheme.TEXT);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}