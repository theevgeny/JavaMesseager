package client.ui;

import client.Client;
import server.message.Message;
import server.user.UserProfile;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class MainWindow {
    private final Client client;
    private JFrame frame;
    private ChatPanel chatPanel;
    private UserListPanel userListPanel;
    private ProfilePanel profilePanel;
    private JLabel statusLabel;
    private JPanel rightPanel;
    private JPanel headerPanel;
    private JLabel headerTitleLabel;
    private JButton backButton;
    private String currentView = "chat";
    private JPanel contentPanel;

    public MainWindow(Client client) {
        this.client = client;
        createAndShowGUI();
        applySettings();
    }

    private void createAndShowGUI() {
        frame = new JFrame("Мессенджер - " + client.getUsername());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new Dimension(800, 500));

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.logout();
            }
        });

        JMenuBar menuBar = createMenuBar();
        frame.setJMenuBar(menuBar);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(220);
        splitPane.setBackground(ColorTheme.BACKGROUND);

        userListPanel = new UserListPanel(client);
        chatPanel = new ChatPanel(client);
        profilePanel = new ProfilePanel(client);

        userListPanel.setChatPanel(chatPanel);

        splitPane.setLeftComponent(userListPanel);

        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(chatPanel, "chat");
        contentPanel.add(profilePanel, "profile");

        rightPanel = createRightPanel();

        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.add(rightPanel, BorderLayout.NORTH);
        rightWrapper.add(contentPanel, BorderLayout.CENTER);

        splitPane.setRightComponent(rightWrapper);
        frame.add(splitPane);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(ColorTheme.PANEL);

        JMenu fileMenu = new JMenu("Меню");
        fileMenu.setForeground(ColorTheme.TEXT);

        JMenuItem profileItem = new JMenuItem("Мой профиль");
        profileItem.addActionListener(e -> showProfile());

        JMenuItem settingsItem = new JMenuItem("Настройки");
        settingsItem.addActionListener(e -> showSettings());

        JMenuItem logoutItem = new JMenuItem("Выйти");
        logoutItem.addActionListener(e -> client.logout());

        JMenuItem exitItem = new JMenuItem("Закрыть");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(profileItem);
        fileMenu.add(settingsItem);
        fileMenu.addSeparator();
        fileMenu.add(logoutItem);
        fileMenu.add(exitItem);

        JMenu chatMenu = new JMenu("Чат");
        chatMenu.setForeground(ColorTheme.TEXT);

        JMenuItem clearHistoryItem = new JMenuItem("Очистить текущий чат");
        clearHistoryItem.addActionListener(e -> {
            if (chatPanel != null) chatPanel.clearCurrentHistory();
        });
        chatMenu.add(clearHistoryItem);

        menuBar.add(fileMenu);
        menuBar.add(chatMenu);

        return menuBar;
    }

    private void showProfile() {
        currentView = "profile";
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "profile");
        profilePanel.loadProfile(client.getUsername());
        updateHeaderTitle("Мой профиль");
        backButton.setVisible(true);
    }

    private void showChat() {
        currentView = "chat";
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "chat");
        updateHeaderTitle("Выберите контакт для чата");
        backButton.setVisible(false);
    }

    public void onProfileReceived(UserProfile profile) {
        if (profilePanel != null && profile.getUsername().equals(client.getUsername())) {
            profilePanel.updateProfile(profile);
        }
    }

    public void onProfileUpdateSuccess() {
        profilePanel.onUpdateSuccess();
        client.requestProfile(client.getUsername());
    }

    public void onHistoryReceived(ArrayList<Message> messages, int totalCount, int page, boolean hasMore) {
        if (chatPanel != null) {
            chatPanel.showHistory(messages, totalCount, page, hasMore, client.getUsername());
        }
    }

    public void show() {
        frame.setVisible(true);
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorTheme.BACKGROUND);

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorTheme.PANEL);
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftHeaderPanel.setOpaque(false);

        backButton = new JButton("← Назад");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        backButton.setBackground(new Color(70, 130, 200));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> showChat());
        backButton.setVisible(false);

        headerTitleLabel = new JLabel("Выберите контакт для чата");
        headerTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerTitleLabel.setForeground(ColorTheme.TEXT);

        leftHeaderPanel.add(backButton);
        leftHeaderPanel.add(headerTitleLabel);

        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);

        statusLabel = new JLabel("Готов");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(150, 150, 150));
        headerPanel.add(statusLabel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);
        return panel;
    }

    private void updateHeaderTitle(String title) {
        if (headerTitleLabel != null) {
            headerTitleLabel.setText(title);
        }
    }

    public void appendMessage(String sender, String message, long timestamp, boolean isSent) {
        if (chatPanel != null && "chat".equals(currentView)) {
            chatPanel.appendMessage(sender, message, timestamp, isSent);
        }
    }

    public void updateUserList(ArrayList<String> users) {
        if (userListPanel != null) {
            userListPanel.updateUsers(users, client.getUsername());
        }
    }

    public void updateStatus(String status) {
        if (statusLabel != null) {
            statusLabel.setText(status);
            Timer timer = new Timer(3000, e -> statusLabel.setText("Готов"));
            timer.setRepeats(false);
            timer.start();
        }
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public void close() {
        if (frame != null) {
            frame.dispose();
        }
    }

    public void onNewMessageReceived(String sender, String message) {
        SettingsManager.showNotification("Новое сообщение от " + sender, message);
        SettingsManager.playMessageSound();
        if (!"chat".equals(currentView)) {
            updateStatus("Новое сообщение от " + sender);
        }
    }

    private void showSettings() {
        SettingsDialog dialog = new SettingsDialog(frame, client);
        dialog.show();
    }

    public void applySettings() {
        SettingsManager.applyFontSize(frame);
        String theme = SettingsManager.getTheme();
        if ("Light".equals(theme)) {
            frame.getContentPane().setBackground(Color.WHITE);
            if (rightPanel != null) rightPanel.setBackground(Color.WHITE);
            if (headerPanel != null) headerPanel.setBackground(new Color(240, 240, 240));
            if (statusLabel != null) statusLabel.setForeground(Color.DARK_GRAY);
            if (chatPanel != null) chatPanel.applyTheme("Light");
            if (userListPanel != null) userListPanel.applyTheme("Light");
            if (profilePanel != null) profilePanel.applyTheme("Light");
            backButton.setBackground(new Color(70, 130, 200));
            backButton.setForeground(Color.WHITE);
        } else {
            frame.getContentPane().setBackground(ColorTheme.BACKGROUND);
            if (rightPanel != null) rightPanel.setBackground(ColorTheme.BACKGROUND);
            if (headerPanel != null) headerPanel.setBackground(ColorTheme.PANEL);
            if (statusLabel != null) statusLabel.setForeground(new Color(150,150,150));
            if (chatPanel != null) chatPanel.applyTheme("Dark");
            if (userListPanel != null) userListPanel.applyTheme("Dark");
            if (profilePanel != null) profilePanel.applyTheme("Dark");
            backButton.setBackground(new Color(70, 130, 200));
            backButton.setForeground(Color.WHITE);
        }
        frame.repaint();
    }
}