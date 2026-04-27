package client.ui;

import client.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class UserListPanel extends JPanel {
    private final Client client;
    private DefaultListModel<String> listModel;
    private JList<String> userList;
    private ChatPanel chatPanel;

    public UserListPanel(Client client) {
        this.client = client;
        setLayout(new BorderLayout());
        setBackground(ColorTheme.PANEL);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        createTitle();
        createUserList();
    }

    public void setChatPanel(ChatPanel chatPanel) {
        this.chatPanel = chatPanel;
    }

    private void createTitle() {
        JLabel titleLabel = new JLabel("Контакты");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(ColorTheme.TEXT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
    }

    private void createUserList() {
        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        userList.setBackground(ColorTheme.PANEL);
        userList.setForeground(ColorTheme.TEXT);
        userList.setSelectionBackground(ColorTheme.ACCENT);
        userList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userList.setFixedCellHeight(30);
        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = userList.getSelectedValue();
                if (selected != null && chatPanel != null) {
                    chatPanel.setCurrentRecipient(selected);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        scrollPane.getViewport().setBackground(ColorTheme.PANEL);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateUsers(ArrayList<String> users, String myUsername) {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            for (String user : users) {
                if (!user.equals(myUsername)) {
                    listModel.addElement(user);
                }
            }
            java.util.List<String> temp = new ArrayList<>();
            for (int i = 0; i < listModel.size(); i++) {
                temp.add(listModel.getElementAt(i));
            }
            Collections.sort(temp);
            listModel.clear();
            for (String s : temp) {
                listModel.addElement(s);
            }
        });
    }

    public void applyTheme(String theme) {
        if ("Light".equals(theme)) {
            setBackground(new Color(240, 240, 240));
            userList.setBackground(new Color(240, 240, 240));
            userList.setForeground(Color.BLACK);
        } else {
            setBackground(ColorTheme.PANEL);
            userList.setBackground(ColorTheme.PANEL);
            userList.setForeground(ColorTheme.TEXT);
        }
    }
}