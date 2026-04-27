package client.ui;

import client.Client;
import server.user.UserProfile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfilePanel extends JPanel {
    private final Client client;

    private JLabel usernameLabel;
    private JLabel registeredLabel;
    private JLabel messageCountLabel;
    private JTextField displayNameField;
    private JTextArea bioArea;
    private JButton editButton;
    private JButton saveButton;
    private JButton cancelButton;
    private JLabel statusLabel;

    private boolean editing = false;
    private UserProfile currentProfile;
    private JPanel cardPanel;

    public ProfilePanel(Client client) {
        this.client = client;
        setLayout(new BorderLayout());
        setBackground(ColorTheme.BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        createUI();
    }

    private void createUI() {
        cardPanel = new JPanel(new CardLayout());
        cardPanel.setOpaque(false);

        JPanel viewPanel = createViewPanel();
        JPanel editPanel = createEditPanel();

        cardPanel.add(viewPanel, "view");
        cardPanel.add(editPanel, "edit");

        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        avatarLabel.setForeground(ColorTheme.ACCENT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(avatarLabel, gbc);

        usernameLabel = new JLabel("Имя пользователя: ");
        usernameLabel.setForeground(ColorTheme.TEXT);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 1;
        panel.add(usernameLabel, gbc);

        registeredLabel = new JLabel("Зарегистрирован: ");
        registeredLabel.setForeground(ColorTheme.TEXT);
        registeredLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 2;
        panel.add(registeredLabel, gbc);

        messageCountLabel = new JLabel("Сообщений: ");
        messageCountLabel.setForeground(ColorTheme.TEXT);
        messageCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy = 3;
        panel.add(messageCountLabel, gbc);

        editButton = createButton("Редактировать профиль", new Color(70, 130, 200));
        editButton.addActionListener(e -> startEditing());
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 8, 8, 8);
        panel.add(editButton, gbc);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(76, 175, 80));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 5;
        panel.add(statusLabel, gbc);

        return panel;
    }

    private JPanel createEditPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel titleLabel = new JLabel("Редактирование профиля");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ColorTheme.ACCENT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JLabel displayNameTitle = new JLabel("Отображаемое имя:");
        displayNameTitle.setForeground(ColorTheme.TEXT);
        displayNameTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(displayNameTitle, gbc);

        displayNameField = new JTextField();
        styleTextField(displayNameField);
        gbc.gridx = 1;
        panel.add(displayNameField, gbc);

        JLabel bioTitle = new JLabel("О себе:");
        bioTitle.setForeground(ColorTheme.TEXT);
        bioTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(bioTitle, gbc);

        bioArea = new JTextArea(5, 20);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setBackground(ColorTheme.INPUT_BG);
        bioArea.setForeground(ColorTheme.TEXT);
        bioArea.setCaretColor(ColorTheme.TEXT);
        bioArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ColorTheme.BORDER),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane bioScroll = new JScrollPane(bioArea);
        bioScroll.setOpaque(false);
        gbc.gridx = 1;
        panel.add(bioScroll, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        saveButton = createButton("Сохранить", new Color(70, 130, 200));
        cancelButton = createButton("Отмена", new Color(120, 120, 120));

        saveButton.addActionListener(e -> saveProfile());
        cancelButton.addActionListener(e -> cancelEditing());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        panel.add(buttonPanel, gbc);

        return panel;
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
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void loadProfile(String username) {
        client.requestProfile(username);
    }

    public void updateProfile(UserProfile profile) {
        this.currentProfile = profile;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        usernameLabel.setText("Имя пользователя: " + profile.getUsername());
        registeredLabel.setText("Зарегистрирован: " + sdf.format(new Date(profile.getRegisteredAt())));
        messageCountLabel.setText("Сообщений: " + profile.getMessageCount());

        displayNameField.setText(profile.getDisplayName());
        bioArea.setText(profile.getBio());

        if (!editing) {
            ((CardLayout) cardPanel.getLayout()).show(cardPanel, "view");
        }
    }

    private void startEditing() {
        editing = true;
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "edit");
    }

    private void saveProfile() {
        String displayName = displayNameField.getText().trim();
        String bio = bioArea.getText().trim();

        if (displayName.isEmpty()) {
            displayName = currentProfile.getUsername();
        }

        client.updateProfile(displayName, bio);
    }

    public void onUpdateSuccess() {
        editing = false;
        statusLabel.setText("Профиль успешно обновлён!");
        statusLabel.setForeground(new Color(76, 175, 80));

        Timer timer = new Timer(3000, e -> statusLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();

        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "view");
    }

    private void cancelEditing() {
        editing = false;
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "view");
    }

    public void applyTheme(String theme) {
        if ("Light".equals(theme)) {
            setBackground(Color.WHITE);
            editButton.setBackground(new Color(70, 130, 200));
            saveButton.setBackground(new Color(70, 130, 200));
            cancelButton.setBackground(new Color(120, 120, 120));
        } else {
            setBackground(ColorTheme.BACKGROUND);
            editButton.setBackground(new Color(70, 130, 200));
            saveButton.setBackground(new Color(70, 130, 200));
            cancelButton.setBackground(new Color(120, 120, 120));
        }
    }
}