package client.ui;

import client.Client;
import server.message.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ChatPanel extends JPanel {
    private final Client client;
    private JTextPane chatArea;
    private JTextField messageField;
    private String currentRecipient;
    private Set<String> clearedConversations = new HashSet<>();
    private int currentPage = 0;
    private int totalPages = 0;
    private int totalMessages = 0;
    private boolean historyAdded = false;
    private boolean loadingHistory = false;
    private JButton loadMoreButton;
    private JPanel historyControlPanel;

    public ChatPanel(Client client) {
        this.client = client;
        setLayout(new BorderLayout());
        setBackground(ColorTheme.BACKGROUND);
        createChatArea();
        createInputPanel();
        createHistoryControls();
    }

    private void createHistoryControls() {
        historyControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        historyControlPanel.setBackground(ColorTheme.PANEL);
        historyControlPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

        loadMoreButton = createButton("Загрузить старые сообщения", new Color(220, 220, 250));
        loadMoreButton.setForeground(Color.WHITE);
        loadMoreButton.addActionListener(e -> loadMoreHistory());
        loadMoreButton.setEnabled(false);

        historyControlPanel.add(loadMoreButton);
    }

    private void createChatArea() {
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(ColorTheme.BACKGROUND);
        chatArea.setForeground(ColorTheme.TEXT);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBorder(BorderFactory.createLineBorder(ColorTheme.PANEL));
        chatScroll.getViewport().setBackground(ColorTheme.BACKGROUND);
        add(chatScroll, BorderLayout.CENTER);
    }

    private void createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(ColorTheme.PANEL);
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        messageField = new JTextField();
        messageField.setEnabled(false);
        messageField.setBackground(ColorTheme.INPUT_BG);
        messageField.setForeground(ColorTheme.TEXT);
        messageField.setCaretColor(ColorTheme.TEXT);
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageField.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        messageField.addActionListener(e -> sendMessage());

        JButton sendButton = createButton("Send", ColorTheme.ACCENT);
        sendButton.addActionListener(e -> sendMessage());
        sendButton.setPreferredSize(new Dimension(80, 35));

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);
    }

    private void loadMoreHistory() {
        if (loadingHistory) return;
        if (currentPage + 1 >= totalPages) {
            loadMoreButton.setEnabled(false);
            return;
        }

        loadingHistory = true;
        currentPage++;
        client.requestConversation(currentRecipient, currentPage, 50);
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty() || currentRecipient == null) {
            if (currentRecipient == null) {
                JOptionPane.showMessageDialog(this, "Select a recipient first");
            }
            return;
        }
        client.sendMessage(currentRecipient, message);
        messageField.setText("");
    }

    public void appendMessage(String sender, String message, long timestamp, boolean isSent) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String time = sdf.format(new Date(timestamp));
            String displayName = isSent ? "You" : sender;
            String messageLine = time + " " + displayName + ": " + message + "\n";
            chatArea.getDocument().insertString(chatArea.getDocument().getLength(), messageLine, null);
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void showHistory(java.util.ArrayList<Message> messages, int totalCount, int page, boolean hasMore, String myUsername) {
        if (currentRecipient == null) return;

        loadingHistory = false;
        totalMessages = totalCount;
        totalPages = (int) Math.ceil((double) totalCount / 50);
        currentPage = page;

        loadMoreButton.setEnabled(hasMore && totalPages > page + 1);

        if (clearedConversations.contains(currentRecipient)) {
            chatArea.setText("");
            return;
        }

        if (page == 0) {
            chatArea.setText("");
        }

        java.util.ArrayList<Message> sorted = new java.util.ArrayList<>(messages);
        sorted.sort((a, b) -> Long.compare(a.sendTime, b.sendTime));

        String existingText = chatArea.getText();

        if (page > 0 && !existingText.isEmpty()) {
            int cursorPos = chatArea.getCaretPosition();
            for (Message msg : sorted) {
                if ((msg.sender.equals(currentRecipient) || msg.recipient.equals(currentRecipient)) &&
                        !existingText.contains(String.valueOf(msg.sendTime))) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        String time = sdf.format(new Date(msg.sendTime));
                        boolean isSent = msg.sender.equals(myUsername);
                        String displayName = isSent ? "You" : msg.sender;
                        String messageLine = time + " " + displayName + ": " + msg.message + "\n";
                        chatArea.getDocument().insertString(0, messageLine, null);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
            chatArea.setCaretPosition(cursorPos);
        } else {
            for (Message msg : sorted) {
                if (msg.sender.equals(currentRecipient) || msg.recipient.equals(currentRecipient)) {
                    boolean isSent = msg.sender.equals(myUsername);
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        String time = sdf.format(new Date(msg.sendTime));
                        String displayName = isSent ? "You" : msg.sender;
                        String messageLine = time + " " + displayName + ": " + msg.message + "\n";
                        chatArea.getDocument().insertString(chatArea.getDocument().getLength(), messageLine, null);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        }
    }

    public void setCurrentRecipient(String recipient) {
        this.currentRecipient = recipient;
        this.currentPage = 0;
        this.totalPages = 0;
        this.totalMessages = 0;
        messageField.setEnabled(true);
        messageField.requestFocus();
        chatArea.setText("");

        if (!historyAdded) {
            add(historyControlPanel, BorderLayout.NORTH);
            revalidate();
            historyAdded = true;
        }

        client.requestConversation(recipient, 0, 50);
    }

    public void clearCurrentHistory() {
        if (currentRecipient == null) return;
        chatArea.setText("");
        clearedConversations.add(currentRecipient);
        currentPage = 0;
        totalPages = 0;
        loadMoreButton.setEnabled(false);
    }

    public boolean isConversationCleared(String recipient) {
        return clearedConversations.contains(recipient);
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

    public void applyTheme(String theme) {
        if ("Light".equals(theme)) {
            setBackground(Color.WHITE);
            chatArea.setBackground(Color.WHITE);
            chatArea.setForeground(Color.BLACK);
            historyControlPanel.setBackground(new Color(240, 240, 240));
        } else {
            setBackground(ColorTheme.BACKGROUND);
            chatArea.setBackground(ColorTheme.BACKGROUND);
            chatArea.setForeground(ColorTheme.TEXT);
            historyControlPanel.setBackground(ColorTheme.PANEL);
        }
    }
}